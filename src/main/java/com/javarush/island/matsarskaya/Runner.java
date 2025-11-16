package com.javarush.island.matsarskaya;

import com.javarush.island.matsarskaya.config.AnimalConfigService;
import com.javarush.island.matsarskaya.config.AnimalStats;
import com.javarush.island.matsarskaya.services.ConfigLoaderService;
import com.javarush.island.matsarskaya.config.IslandConfig;
import com.javarush.island.matsarskaya.map.GameMap;
import com.javarush.island.matsarskaya.organism.herbivore.Rabbit;
import com.javarush.island.matsarskaya.organism.predator.Wolf;
import com.javarush.island.matsarskaya.services.SimulationManager;

import java.io.IOException;


public class Runner {
    public static void main(String[] args) {
        // Загрузка конфигурации
        ConfigLoaderService configLoader = new ConfigLoaderService();
        IslandConfig config = null;
        try {
            config = configLoader.loadConfig();
            System.out.println("Конфигурация успешно загружена");
        } catch (IOException e) {
            System.err.println("Ошибка загрузки конфигурации: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        GameMap gameMap = new GameMap(5, 7);
        Wolf wolf = new Wolf();
        Wolf wolf2 = new Wolf();
        Rabbit rabbit = new Rabbit();
        Rabbit rabbit2 = new Rabbit();

        // Создаем сервис для работы с конфигурацией животных
        AnimalConfigService animalConfigService = new AnimalConfigService(config);
        // Получаем параметры для волка
        AnimalStats wolfStats = animalConfigService.getAnimalStats("wolf");
        if (wolfStats != null) {
            wolf.initializeFromConfig(
                    wolfStats.getWeight(),
                    wolfStats.getSpeed(),
                    wolfStats.getFoodRequired(),
                    wolfStats.getMaxCountPerCell(),
                    wolfStats.getWeightLossPerStep());
        }

        // Аналогично для зайца
        AnimalStats rabbitStats = animalConfigService.getAnimalStats("rabbit");
        if (rabbitStats != null) {
            rabbit.initializeFromConfig(
                    rabbitStats.getWeight(),
                    rabbitStats.getSpeed(),
                    rabbitStats.getFoodRequired(),
                    rabbitStats.getMaxCountPerCell(),
                    rabbitStats.getWeightLossPerStep());
        }
        if (wolfStats != null) {
            wolf2.initializeFromConfig(
                    wolfStats.getWeight(),
                    wolfStats.getSpeed(),
                    wolfStats.getFoodRequired(),
                    wolfStats.getMaxCountPerCell(),
                    wolfStats.getWeightLossPerStep());
        }

        if (rabbitStats != null) {
            rabbit2.initializeFromConfig(
                    rabbitStats.getWeight(),
                    rabbitStats.getSpeed(),
                    rabbitStats.getFoodRequired(),
                    rabbitStats.getMaxCountPerCell(),
                    rabbitStats.getWeightLossPerStep());
        }
        wolf.setGameMap(gameMap);
        rabbit.setGameMap(gameMap);
        wolf2.setGameMap(gameMap);
        rabbit2.setGameMap(gameMap);
        gameMap.placeAnimal(wolf, 1, 2);
        gameMap.placeAnimal(rabbit, 2, 1);
        gameMap.placeAnimal(wolf2, 1, 1);
        gameMap.placeAnimal(rabbit2, 2, 2);

        // Создаем и запускаем менеджер симуляции вместо ручного цикла
        SimulationManager simulationManager = new SimulationManager(gameMap, animalConfigService);
        simulationManager.startSimulation();
    }
}
//        // Загрузка конфигурации
//        ConfigLoaderService configLoader = new ConfigLoaderService();
//        IslandConfig config = null;
//        try {
//            config = configLoader.loadConfig();
//            System.out.println("Конфигурация успешно загружена");
//        } catch (IOException e) {
//            System.err.println("Ошибка загрузки конфигурации: " + e.getMessage());
//            e.printStackTrace();
//            return;
//        }
//
//        GameMap gameMap = new GameMap(10, 10);
//        Wolf wolf = new Wolf();
//        Wolf wolf2 = new Wolf();
//        Rabbit rabbit = new Rabbit();
//        Rabbit rabbit2 = new Rabbit();
//
//        // Создаем сервис для работы с конфигурацией животных
//        AnimalConfigService animalConfigService = new AnimalConfigService(config);
//        // Получаем параметры для волка
//        AnimalStats wolfStats = animalConfigService.getAnimalStats("wolf");
//        if (wolfStats != null) {
//            wolf.initializeFromConfig(
//                    wolfStats.getWeight(),
//                    wolfStats.getSpeed(),
//                    wolfStats.getFoodRequired(),
//                    wolfStats.getMaxCountPerCell(),
//                    wolfStats.getWeightLossPerStep());
//        }
//
//        // Аналогично для зайца
//        AnimalStats rabbitStats = animalConfigService.getAnimalStats("rabbit");
//        if (rabbitStats != null) {
//            rabbit.initializeFromConfig(
//                    rabbitStats.getWeight(),
//                    rabbitStats.getSpeed(),
//                    rabbitStats.getFoodRequired(),
//                    rabbitStats.getMaxCountPerCell(),
//                    wolfStats.getWeightLossPerStep());
//        }
//        if (wolfStats != null) {
//            wolf2.initializeFromConfig(
//                    wolfStats.getWeight(),
//                    wolfStats.getSpeed(),
//                    wolfStats.getFoodRequired(),
//                    wolfStats.getMaxCountPerCell(),
//                    wolfStats.getWeightLossPerStep());
//        }
//
//        if (rabbitStats != null) {
//            rabbit2.initializeFromConfig(
//                    rabbitStats.getWeight(),
//                    rabbitStats.getSpeed(),
//                    rabbitStats.getFoodRequired(),
//                    rabbitStats.getMaxCountPerCell(),
//                    wolfStats.getWeightLossPerStep());
//        }
//        wolf.setGameMap(gameMap);
//        rabbit.setGameMap(gameMap);
//        wolf2.setGameMap(gameMap);
//        rabbit2.setGameMap(gameMap);
//        gameMap.placeAnimal(wolf, 1, 2);
//        gameMap.placeAnimal(rabbit, 2, 1);
//        gameMap.placeAnimal(wolf2, 1, 1);
//        gameMap.placeAnimal(rabbit2, 2, 2);
//
//        System.out.println("Начальная позиция волка: (" + wolf.getX() + ", " + wolf.getY() + ")");
//        System.out.println("Начальная позиция зайца: (" + rabbit.getX() + ", " + rabbit.getY() + ")");
//
//
//        // Проверяем новые позиции
//        System.out.println("Новая позиция волка: (" + wolf.getX() + ", " + wolf.getY() + ")");
//        System.out.println("Новая позиция зайца: (" + rabbit.getX() + ", " + rabbit.getY() + ")");
//
//        ConsoleRender renderer = new ConsoleRender();
//        System.out.println("Initial state:");
//        renderer.renderMap(gameMap);
//
//        for (int i = 0; i < 10; i++) { // Show 5 steps
//            System.out.println("\nStep " + (i + 1) + ":");
//            System.out.println("Total animals on map: " + gameMap.countAnimals());
//            System.out.println("BEFORE ACTIONS:");
//
//            // Подготовка к проверке питания
//            wolf.prepareForEatingCheck();
//            wolf2.prepareForEatingCheck();
//            rabbit.prepareForEatingCheck();
//            rabbit2.prepareForEatingCheck();
//
//            wolf.printStatus();
//            wolf2.printStatus();
//            rabbit.printStatus();
//            rabbit2.printStatus();
//            //  Добавляем этап передвижения
//            wolf.walking();
//            wolf2.walking();
//            rabbit.walking();
//            rabbit2.walking();
//            // Добавляем этап питания
//            wolf.eating(animalConfigService);
//            wolf2.eating(animalConfigService);
//            rabbit.eating(animalConfigService);
//            rabbit2.eating(animalConfigService);
//
//            // Выводим статистику после действий
//            System.out.println("AFTER ACTIONS:");
//            // Сначала все животные теряют вес
//            wolf.loseWeightOverTime();
//            wolf2.loseWeightOverTime();
//            rabbit.loseWeightOverTime();
//            rabbit2.loseWeightOverTime();
//
//            System.out.println("Total animals on map: " + gameMap.countAnimals());
//            renderer.renderMap(gameMap);
//
//            try {
//                Thread.sleep(1000); // 1 second pause
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                break;
//            }
//        }
