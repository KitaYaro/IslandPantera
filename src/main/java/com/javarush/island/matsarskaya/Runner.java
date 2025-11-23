package com.javarush.island.matsarskaya;

import com.javarush.island.matsarskaya.config.AnimalConfigService;
import com.javarush.island.matsarskaya.config.AnimalStats;
import com.javarush.island.matsarskaya.entity.Animals;
import com.javarush.island.matsarskaya.organism.Grass;
import com.javarush.island.matsarskaya.organism.herbivore.*;
import com.javarush.island.matsarskaya.organism.predator.*;
import com.javarush.island.matsarskaya.services.ConfigLoaderService;
import com.javarush.island.matsarskaya.config.IslandConfig;
import com.javarush.island.matsarskaya.map.GameMap;
import com.javarush.island.matsarskaya.services.SimulationManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class Runner {
    public static void main(String[] args) {

        ConfigLoaderService configLoader = new ConfigLoaderService();
        IslandConfig config;
        try {
            config = configLoader.loadConfig();
            System.out.println("\n" +
                    "Configuration loaded successfully");
        } catch (IOException e) {
            System.err.println("\n" +
                    "Error loading configuration: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        GameMap gameMap = new GameMap(100, 20);
        AnimalConfigService animalConfigService = new AnimalConfigService(config);
        addInitialGrass(gameMap);
        createAnimalsFromConfig(gameMap, animalConfigService);
        Optional.of(new SimulationManager(gameMap, animalConfigService))
                .ifPresent(SimulationManager::startSimulation);
    }

    private static void addInitialGrass(GameMap gameMap) {
        for (int i = 0; i < 20; i++) {
            Grass grass = new Grass();
            gameMap.placeGrass(grass, 0, i);
        }
    }

    private static void createAnimalsFromConfig(GameMap gameMap, AnimalConfigService animalConfigService) {
        List<String> animalTypes = getAnimalTypes();
        animalTypes.parallelStream().forEach(animalType ->
                createAnimalsOfType(animalType, gameMap, animalConfigService));
    }

    private static List<String> getAnimalTypes() {
        return Arrays.asList(
                "wolf", "boa", "fox", "bear", "eagle",
                "horse", "deer", "rabbit", "mouse", "goat",
                "sheep", "boar", "buffalo", "duck", "caterpillar"
        );
    }
    private static void createAnimalsOfType(String animalType, GameMap gameMap, AnimalConfigService animalConfigService) {
        AnimalStats stats = animalConfigService.getAnimalStats(animalType);
        Optional.ofNullable(stats).ifPresent(s -> {
            int count = ThreadLocalRandom.current().nextInt(30, 100);
            IntStream.range(0, count).forEach(i -> createAndPlaceAnimal(animalType, gameMap, s));
        });
    }

    private static void createAndPlaceAnimal(String animalType, GameMap gameMap, AnimalStats stats) {
        Optional.ofNullable(createAnimalInstance(animalType)).ifPresent(animal -> {
            animal.initializeFromConfig(
                    stats.getWeight(),
                    stats.getSpeed(),
                    stats.getFoodRequired(),
                    stats.getMaxCountPerCell(),
                    stats.getWeightLossPerStep()
            );
            animal.setGameMap(gameMap);

            int x = ThreadLocalRandom.current().nextInt(0, gameMap.getHeight());
            int y = ThreadLocalRandom.current().nextInt(0, gameMap.getWidth());
            gameMap.placeAnimal(animal, x, y);
        });
    }

    private static Animals createAnimalInstance(String animalType) {
        return switch (animalType.toLowerCase()) {
            case "wolf" -> new Wolf();
            case "boa" -> new Boa();
            case "fox" -> new Fox();
            case "bear" -> new Bear();
            case "eagle" -> new Eagle();
            case "horse" -> new Horse();
            case "deer" -> new Deer();
            case "rabbit" -> new Rabbit();
            case "mouse" -> new Mouse();
            case "goat" -> new Goat();
            case "sheep" -> new Sheep();
            case "boar" -> new Boar();
            case "buffalo" -> new Buffalo();
            case "duck" -> new Duck();
            case "caterpillar" -> new Caterpillar();
            default -> {
                System.err.println("Unknown type of animal: " + animalType);
                yield null;
            }
        };
    }
}

