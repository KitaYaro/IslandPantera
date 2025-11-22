package com.javarush.island.matsarskaya.services;

import com.javarush.island.matsarskaya.config.AnimalConfigService;
import com.javarush.island.matsarskaya.entity.Animal;
import com.javarush.island.matsarskaya.entity.Animals;
import com.javarush.island.matsarskaya.map.Cell;
import com.javarush.island.matsarskaya.map.GameMap;
import com.javarush.island.matsarskaya.organism.Eating;
import com.javarush.island.matsarskaya.organism.Growing;
import com.javarush.island.matsarskaya.organism.Reproduction;
import com.javarush.island.matsarskaya.organism.Walking;
import com.javarush.island.matsarskaya.simulation.view.ConsoleRender;
import com.javarush.island.matsarskaya.simulation.view.Rendering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// класс для управления симуляцией (потоки)
public class SimulationManager {
    private final GameMap gameMap;
    private final AnimalConfigService animalConfigService;
    private final ConsoleRender consoleRender;
    private final ScheduledExecutorService scheduler; // Пул потоков для действий
    private volatile boolean running = false;
    private List<Animal> herds; // Стаи животных
    private Map<String, Integer> initialAnimalCounts;
    private Map<String, Integer> previousAnimalCounts;
    private Map<String, Integer> bornCounts;
    private Map<String, Integer> diedCounts;
    private int tickNumber = 0;


    public SimulationManager(GameMap gameMap, AnimalConfigService animalConfigService) {
        this.gameMap = gameMap;
        this.animalConfigService = animalConfigService;
        this.consoleRender = new ConsoleRender();
        // Создаем пул из 4 потоков (для передвижения, питания, размножения, травы и отрисовки)
        this.scheduler = Executors.newScheduledThreadPool(5); // 4 для действия + 1 для симуляции
        // Инициализируем стаи один раз при создании SimulationManager
        this.herds = initializeHerds();
        // Инициализация статистики
        this.initialAnimalCounts = new HashMap<>();
        this.previousAnimalCounts = new HashMap<>();
        this.bornCounts = new HashMap<>();
        this.diedCounts = new HashMap<>();
    }

    /* Инициализирует стаи животных один раз при запуске
     * @return список стай
     */
    private List<Animal> initializeHerds() {
        // Пока возвращаем всех животных как есть
        // Позже здесь будет логика создания стай
        return getAllAnimals();
    }

    public void startSimulation() {
        // Сохраняем начальную статистику
        initialAnimalCounts = getCurrentAnimalCounts();
        previousAnimalCounts = new HashMap<>(initialAnimalCounts);

        System.out.println("Изначальное количество животных на карте:");
        printAnimalCounts(initialAnimalCounts);
        System.out.println("Симуляция запущена!");

        running = true;

        // Запускаем симуляцию с периодом 1 секунда
        scheduler.scheduleAtFixedRate(() -> {
            if (!running || gameMap.countAnimals() <= 40) {
                if (gameMap.countAnimals() <= 40) {
                    System.out.println("Симуляция завершена: на острове осталось " + gameMap.countAnimals() + " животных. Программа останавливается.");
                }
                stopSimulation();
                return;
            }
            runSimulationStep();
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void runSimulationStep() {
        tickNumber++;

        System.out.println("=== статистика до такта ===");
        Map<String, Integer> currentCounts = getCurrentAnimalCounts();
        System.out.println("Сейчас животных на карте:");
        printAnimalCounts(currentCounts);
        // Добавляем информацию о траве
        System.out.println("Травы на карте: " + gameMap.countGrass());

        // Подготовка к проверке питания (сохраняем вес до действий)
        for (Animal animal : herds) {
            if (animal.isAlive() && animal instanceof Animals) {
                ((Animals) animal).prepareForEatingCheck();
            }
        }
        // Рост растений
        Growing growingTask = new Growing(gameMap, animalConfigService);
        List<Runnable> growingTasks = new ArrayList<>();
        growingTasks.add(growingTask);
        executeTasks(growingTasks);

        // Передвижение
        List<Runnable> walkingTasks = new ArrayList<>();
        for (Animal animal : herds) {
            if (animal.isAlive()) {
                walkingTasks.add(new Walking((Animals) animal));
            }
        }//выполняем действие передвижения параллельно
        executeTasks(walkingTasks);

        // Питание
        List<Runnable> eatingTasks = new ArrayList<>();
        for (Animal animal : herds) {
            if (animal.isAlive()) {
                eatingTasks.add(new Eating((Animals) animal, animalConfigService));
            }
        }//выполняем действие питания параллельно
        executeTasks(eatingTasks);

        // Размножение
        List<Runnable> reproductionTasks = new ArrayList<>();
        for (Animal animal : herds) {
            if (animal.isAlive()) {
                reproductionTasks.add(new Reproduction((Animals) animal));
            }
        }//выполняем действие размножения параллельно
        executeTasks(reproductionTasks);

        // Потеря веса
        List<Runnable> weightLossTasks = new ArrayList<>();
        for (Animal animal : herds) {
            if (animal.isAlive()) {
                weightLossTasks.add(() -> animal.loseWeightOverTime());
            }
        }
        executeTasks(weightLossTasks);
        // Отрисовка в отдельном потоке
        List<Runnable> renderingTasks = new ArrayList<>();
        renderingTasks.add(new Rendering(gameMap));
        executeTasks(renderingTasks);

        System.out.println("Такт: " + tickNumber);
        // Обновляем список животных после всех действий
        updateHerdList();

        // Подсчет статистики после такта
        Map<String, Integer> newCounts = getCurrentAnimalCounts();
        updateStatistics(currentCounts, newCounts);

        System.out.println("===статистика после такта===");
        System.out.println("Сейчас животных на карте:");
        printAnimalCounts(newCounts);
        System.out.println("На данный момент родилось:");
        printAnimalCounts(bornCounts);
        System.out.println("На данный момент умерло:");
        printAnimalCounts(diedCounts);

        // Обновляем предыдущие значения для следующего такта
        previousAnimalCounts = newCounts;

    }

    // Обновляет список стай после каждого шага симуляции
    private void updateHerdList() {
        this.herds = getAllAnimals(); // Пока просто получаем всех животных
    }

    private List<Animal> getAllAnimals() {
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

    /*
     * Подсчитывает общее количество животных на карте
     * @return количество животных
     */
    private int countAllAnimals() {
        int count = 0;
        for (int x = 0; x < gameMap.getHeight(); x++) {
            for (int y = 0; y < gameMap.getWidth(); y++) {
                Cell cell = gameMap.getCell(x, y);
                if (cell != null) {
                    count += cell.getAnimalList().size();
                }
            }
        }
        return count;
    }

    private void executeTasks(List<Runnable> tasks) {
        List<java.util.concurrent.Future<?>> futures = new ArrayList<>();

        // Отправляем все задачи в пул потоков
        for (Runnable task : tasks) {
            futures.add(scheduler.submit(task));
        }

        // Ждем завершения всех задач
        for (java.util.concurrent.Future<?> future : futures) {
            try {
                future.get(); // Блокируем до завершения задачи
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Поток был прерван: " + e.getMessage());
            } catch (java.util.concurrent.ExecutionException e) {
                System.err.println("Ошибка выполнения задачи: " + e.getMessage());
            }
        }
    }

    public void stopSimulation() {
        running = false;
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    /**
     * Получает текущее количество животных по типам
     */
    private Map<String, Integer> getCurrentAnimalCounts() {
        Map<String, Integer> counts = new HashMap<>();
        for (Animal animal : getAllAnimals()) {
            if (animal.isAlive()) {
                String type = animal.getClass().getSimpleName();
                counts.put(type, counts.getOrDefault(type, 0) + 1);
            }
        }
        return counts;
    }

    /**
     * Обновляет статистику рождений и смертей
     */
    private void updateStatistics(Map<String, Integer> previous, Map<String, Integer> current) {
        bornCounts.clear();
        diedCounts.clear();

        // Собираем все типы животных
        Map<String, Integer> allTypes = new HashMap<>();
        allTypes.putAll(previous);
        allTypes.putAll(current);

        for (String type : allTypes.keySet()) {
            int prevCount = previous.getOrDefault(type, 0);
            int currentCount = current.getOrDefault(type, 0);

            if (currentCount > prevCount) {
                bornCounts.put(type, currentCount - prevCount);
                diedCounts.put(type, 0);
            } else if (currentCount < prevCount) {
                diedCounts.put(type, prevCount - currentCount);
                bornCounts.put(type, 0);
            } else {
                bornCounts.put(type, 0);
                diedCounts.put(type, 0);
            }
        }
    }

    /**
     * Выводит количество животных по типам
     */
    private void printAnimalCounts(Map<String, Integer> counts) {
        if (counts.isEmpty()) {
            System.out.println("(нет животных)");
            return;
        }

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(entry.getKey()).append(" = ").append(entry.getValue());
            first = false;
        }
        System.out.println(sb.toString());
    }
}
