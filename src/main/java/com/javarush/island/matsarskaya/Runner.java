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
import java.util.concurrent.ThreadLocalRandom;


public class Runner {
    public static void main(String[] args) {
        // Загрузка конфигурации
        ConfigLoaderService configLoader = new ConfigLoaderService();
        IslandConfig config;
        try {
            config = configLoader.loadConfig();
            System.out.println("Конфигурация успешно загружена");
        } catch (IOException e) {
            System.err.println("Ошибка загрузки конфигурации: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        GameMap gameMap = new GameMap(100, 20);

        // Создаем сервис для работы с конфигурацией животных
        AnimalConfigService animalConfigService = new AnimalConfigService(config);

        // Добавление начальной травы на карту для тестирования
        addInitialGrass(gameMap);

        // Создаем животных на основе конфигурации
        createAnimalsFromConfig(gameMap, animalConfigService);

        // Создаем и запускаем менеджер симуляции
        SimulationManager simulationManager = new SimulationManager(gameMap, animalConfigService);
        simulationManager.startSimulation();
    }

    private static void addInitialGrass(GameMap gameMap) {
        // Добавление начальной травы на карту для тестирования
        for (int i = 0; i < 20; i++) {
            Grass grass = new Grass();
            gameMap.placeGrass(grass, 0, i); // Размещаем в первой строке
        }
    }

    private static void createAnimalsFromConfig(GameMap gameMap, AnimalConfigService animalConfigService) {
        // Список всех доступных типов животных
        List<String> animalTypes = Arrays.asList(
                "wolf", "boa", "fox", "bear", "eagle",         // Хищники
                "horse", "deer", "rabbit", "mouse", "goat",    // Травоядные
                "sheep", "boar", "buffalo", "duck", "caterpillar"
        );

        // Для каждого типа животных создаем несколько экземпляров
        for (String animalType : animalTypes) {
            AnimalStats stats = animalConfigService.getAnimalStats(animalType);
            if (stats != null) {
                // Создаем от 1 до 3 животных каждого типа для тестирования
                int count = ThreadLocalRandom.current().nextInt(30, 100);
                for (int i = 0; i < count; i++) {
                    Animals animal = createAnimalInstance(animalType);
                    if (animal != null) {
                        animal.initializeFromConfig(
                                stats.getWeight(),
                                stats.getSpeed(),
                                stats.getFoodRequired(),
                                stats.getMaxCountPerCell(),
                                stats.getWeightLossPerStep()
                        );
                        animal.setGameMap(gameMap);

                        // Размещаем животное в случайной ячейке
                        int x = ThreadLocalRandom.current().nextInt(0, gameMap.getHeight());
                        int y = ThreadLocalRandom.current().nextInt(0, gameMap.getWidth());
                        gameMap.placeAnimal(animal, x, y);
                    }
                }
            }
        }
    }

    private static Animals createAnimalInstance(String animalType) {
        switch (animalType.toLowerCase()) {
            // Хищники
            case "wolf": return new Wolf();
            case "boa": return new Boa();
            case "fox": return new Fox();
            case "bear": return new Bear();
            case "eagle": return new Eagle();

            // Травоядные
            case "horse": return new Horse();
            case "deer": return new Deer();
            case "rabbit": return new Rabbit();
            case "mouse": return new Mouse();
            case "goat": return new Goat();
            case "sheep": return new Sheep();
            case "boar": return new Boar();
            case "buffalo": return new Buffalo();
            case "duck": return new Duck();
            case "caterpillar": return new Caterpillar();

            default:
                System.err.println("Неизвестный тип животного: " + animalType);
                return null;
        }
    }
}

