package com.javarush.island.matsarskaya.services;

import com.javarush.island.matsarskaya.config.AnimalConfigService;
import com.javarush.island.matsarskaya.config.SimulationStatistics;
import com.javarush.island.matsarskaya.config.TaskExecutor;
import com.javarush.island.matsarskaya.entity.Animal;
import com.javarush.island.matsarskaya.entity.Animals;
import com.javarush.island.matsarskaya.map.Cell;
import com.javarush.island.matsarskaya.map.GameMap;
import com.javarush.island.matsarskaya.organism.actions.Eating;
import com.javarush.island.matsarskaya.organism.actions.Growing;
import com.javarush.island.matsarskaya.organism.actions.Reproduction;
import com.javarush.island.matsarskaya.organism.actions.Walking;
import com.javarush.island.matsarskaya.simulation.view.Rendering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SimulationManager {
    private final GameMap gameMap;
    private final AnimalConfigService animalConfigService;
    private final TaskExecutor taskExecutor;
    private final SimulationStatistics statistics;
    private volatile boolean running = false;
    private List<Animal> herds;
    private ScheduledFuture<?> simulationTask;
    private int tickNumber = 0;

    public SimulationManager(GameMap gameMap, AnimalConfigService animalConfigService) {
        this.gameMap = gameMap;
        this.animalConfigService = animalConfigService;
        this.taskExecutor = new TaskExecutor();
        this.statistics = new SimulationStatistics();
        this.herds = initializeHerds();
    }

    private List<Animal> initializeHerds() {
        return getAllAnimals();
    }

    public void startSimulation() {
        Map<String, Integer> initialCounts = getCurrentAnimalCounts();
        statistics.initialize(initialCounts);

        System.out.println("Initial number of animals on the map:");
        printAnimalCounts(statistics.getInitialAnimalCounts());
        System.out.println("\n" +
                "The simulation has started!");

        running = true;

        simulationTask = taskExecutor.scheduleAtFixedRate(() -> {
            if (!running) {
                stopSimulation();
                return;
            }

            int animalCount = gameMap.countAnimals();
            if (animalCount <= 10) {
                System.out.println("\n" +
                        "Simulation completed: " + animalCount + " animals. The program stops.");
                stopSimulation();
                return;
            }
            runSimulationStep();
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void runSimulationStep() {
        tickNumber++;

        System.out.println("=== statistics before tick ===");
        Map<String, Integer> currentCounts = getCurrentAnimalCounts();
        System.out.println("Now on the map animals:");
        printAnimalCounts(currentCounts);
        System.out.println("Grass on the map: " + gameMap.countGrass());

        for (Animal animal : herds) {
            if (animal.isAlive() && animal instanceof Animals) {
                ((Animals) animal).prepareForEatingCheck();
            }
        }

        Growing growingTask = new Growing(gameMap, animalConfigService);
        List<Runnable> growingTasks = new ArrayList<>();
        growingTasks.add(growingTask);
        taskExecutor.executeTasks(growingTasks);

        List<Runnable> walkingTasks = new ArrayList<>();
        for (Animal animal : herds) {
            if (animal.isAlive()) {
                walkingTasks.add(new Walking((Animals) animal));
            }
        }
        taskExecutor.executeTasks(walkingTasks);

        List<Runnable> eatingTasks = new ArrayList<>();
        for (Animal animal : herds) {
            if (animal.isAlive()) {
                eatingTasks.add(new Eating((Animals) animal, animalConfigService));
            }
        }
        taskExecutor.executeTasks(eatingTasks);

        List<Runnable> reproductionTasks = new ArrayList<>();
        for (Animal animal : herds) {
            if (animal.isAlive()) {
                reproductionTasks.add(new Reproduction((Animals) animal));
            }
        }
        taskExecutor.executeTasks(reproductionTasks);

        List<Runnable> weightLossTasks = new ArrayList<>();
        for (Animal animal : herds) {
            if (animal.isAlive()) {
                weightLossTasks.add(() -> animal.loseWeightOverTime());
            }
        }
        taskExecutor.executeTasks(weightLossTasks);

        List<Runnable> renderingTasks = new ArrayList<>();
        renderingTasks.add(new Rendering(gameMap));
        taskExecutor.executeTasks(renderingTasks);

        System.out.println("Tick: " + tickNumber);
        updateHerdList();

        Map<String, Integer> newCounts = getCurrentAnimalCounts();
        statistics.updateStatistics(newCounts);

        System.out.println("===statistics after tick===");
        System.out.println("Now on the map animals:");
        printAnimalCounts(newCounts);
        System.out.println("Born now:");
        printAnimalCounts(statistics.getBornCounts());
        System.out.println("Not alive:");
        printAnimalCounts(statistics.getDiedCounts());
    }

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

    public void stopSimulation() {
        running = false;
        if (simulationTask != null) {
            simulationTask.cancel(false);
        }
        taskExecutor.shutdown();
    }

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

    private void printAnimalCounts(Map<String, Integer> counts) {
        if (counts.isEmpty()) {
            System.out.println("no animals)");
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
        System.out.println(sb);
    }
}
