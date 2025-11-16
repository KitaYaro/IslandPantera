package com.javarush.island.matsarskaya.services;

import com.javarush.island.matsarskaya.config.AnimalConfigService;
import com.javarush.island.matsarskaya.entity.Animal;
import com.javarush.island.matsarskaya.entity.Animals;
import com.javarush.island.matsarskaya.map.Cell;
import com.javarush.island.matsarskaya.map.GameMap;
import com.javarush.island.matsarskaya.organism.Eating;
import com.javarush.island.matsarskaya.organism.Reproduction;
import com.javarush.island.matsarskaya.organism.Walking;
import com.javarush.island.matsarskaya.simulation.view.ConsoleRender;
import com.javarush.island.matsarskaya.simulation.view.Rendering;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// класс для управления симуляцией (потоки)
public class SimulationManager {
    private final GameMap gameMap;
    private final AnimalConfigService animalConfigService;
    private final ConsoleRender consoleRender;
    private final ExecutorService actionExecutor; // Пул потоков для действий
    private volatile boolean running = false;

    public SimulationManager(GameMap gameMap, AnimalConfigService animalConfigService) {
        this.gameMap = gameMap;
        this.animalConfigService = animalConfigService;
        this.consoleRender = new ConsoleRender();
        // Создаем пул из 4 потоков (для передвижения, питания, размножения и отрисовки)
        this.actionExecutor = Executors.newFixedThreadPool(4);
    }

    public void startSimulation() {
        running = true;
        int step = 0;

        while (running && gameMap.countAnimals() > 1) {
            try {
                while (running && gameMap.countAnimals() > 1) {
                    step++;
                    System.out.println("\nStep " + step + ":");
                    System.out.println("Total animals on map: " + gameMap.countAnimals());

                    // Выполняем один шаг симуляции
                    runSimulationStep();

                    try {
                        Thread.sleep(1000); // Пауза 1 секунда между шагами
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                System.out.println("Simulation ended: " + (gameMap.countAnimals() <= 1 ? "Too few animals left" : "Stopped by user"));
            } finally {
                stopSimulation(); // Обязательно останавливаем симуляцию
            }
        }
    }

        private void runSimulationStep () {
            List<Animal> allAnimals = getAllAnimals();

            // Подготовка к проверке питания (сохраняем вес до действий)
            for (Animal animal : allAnimals) {
                if (animal.isAlive() && animal instanceof Animals) {
                    ((Animals) animal).prepareForEatingCheck();
                }
            }
            // Вывод статистики до действий
            System.out.println("BEFORE ACTIONS:");
            for (Animal animal : allAnimals) {
                if (animal.isAlive() && animal instanceof Animals) {
                    ((Animals) animal).printStatus();
                }
            }
            // Передвижение
            List<Runnable> walkingTasks = new ArrayList<>();
            for (Animal animal : allAnimals) {
                if (animal.isAlive()) {
                    walkingTasks.add(new Walking(animal));
                }
            }//выполняем действие передвижения параллельно
            executeTasks(walkingTasks);

            // Питание
            List<Runnable> eatingTasks = new ArrayList<>();
            for (Animal animal : allAnimals) {
                if (animal.isAlive()) {
                    eatingTasks.add(new Eating((Animals) animal, animalConfigService));
                }
            }//выполняем действие питания параллельно
            executeTasks(eatingTasks);

            // Размножение
            List<Runnable> reproductionTasks = new ArrayList<>();
            for (Animal animal : allAnimals) {
                if (animal.isAlive()) {
                    reproductionTasks.add(new Reproduction(animal));
                }
            }//выполняем действие размножения параллельно
            executeTasks(reproductionTasks);

            // Потеря веса
            List<Runnable> weightLossTasks = new ArrayList<>();
            for (Animal animal : allAnimals) {
                if (animal.isAlive()) {
                    weightLossTasks.add(() -> animal.loseWeightOverTime());
                }
            }
            executeTasks(weightLossTasks);

            // Вывод статистики после действий
            System.out.println("AFTER ACTIONS:");
            for (Animal animal : allAnimals) {
                if (animal.isAlive() && animal instanceof Animals) {
                    ((Animals) animal).printStatus();
                }
            }
            // Отрисовка в отдельном потоке
            List<Runnable> renderingTasks = new ArrayList<>();
            renderingTasks.add(new Rendering(gameMap));
            executeTasks(renderingTasks);
//        // Отрисовка выполняется в основном потоке
//        consoleRender.renderMap(gameMap);
        }

        private List<Animal> getAllAnimals () {
            List<Animal> allAnimals = new ArrayList<>();
            for (int x = 0; x < gameMap.getHeight(); x++) {
                for (int y = 0; y < gameMap.getWidth(); y++) {
                    Cell cell = gameMap.getCell(x, y);
                    if (cell != null) {
                        allAnimals.addAll(cell.getAnimalList());
                    }
                }
            }
            return allAnimals;
        }

        private void executeTasks (List < Runnable > tasks) {
            List<java.util.concurrent.Future<?>> futures = new ArrayList<>();

            // Отправляем все задачи в пул потоков
            for (Runnable task : tasks) {
                futures.add(actionExecutor.submit(task));
            }

            // Ждем завершения всех задач
            for (java.util.concurrent.Future<?> future : futures) {
                try {
                    future.get(); // Блокируем до завершения задачи
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void stopSimulation () {
            running = false;
            actionExecutor.shutdown();
            try {
                if (!actionExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    actionExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                actionExecutor.shutdownNow();
            }
        }
    }
