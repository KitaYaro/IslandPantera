package com.javarush.island.matsarskaya;

import com.javarush.island.matsarskaya.config.AnimalConfigService;
import com.javarush.island.matsarskaya.config.AnimalStats;
import com.javarush.island.matsarskaya.config.ConfigLoaderService;
import com.javarush.island.matsarskaya.config.IslandConfig;
import com.javarush.island.matsarskaya.map.GameMap;
import com.javarush.island.matsarskaya.organism.herbivore.Rabbit;
import com.javarush.island.matsarskaya.organism.predator.Wolf;
import com.javarush.island.matsarskaya.simulation.view.ConsoleRender;

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

        GameMap gameMap = new GameMap(10, 10);
        Wolf wolf = new Wolf();
        Rabbit rabbit = new Rabbit();

        // Создаем сервис для работы с конфигурацией животных
        AnimalConfigService animalConfigService = new AnimalConfigService(config);
        // Получаем параметры для волка
        AnimalStats wolfStats = animalConfigService.getAnimalStats("wolf");
        if (wolfStats != null) {
            wolf.initializeFromConfig(
                    wolfStats.getWeight(),
                    wolfStats.getSpeed(),
                    wolfStats.getFoodRequired(),
                    wolfStats.getMaxCountPerCell()
            );
        }

        // Аналогично для зайца
        AnimalStats rabbitStats = animalConfigService.getAnimalStats("rabbit");
        if (rabbitStats != null) {
            rabbit.initializeFromConfig(
                    rabbitStats.getWeight(),
                    rabbitStats.getSpeed(),
                    rabbitStats.getFoodRequired(),
                    rabbitStats.getMaxCountPerCell()
            );
        }
            wolf.setGameMap(gameMap);
            rabbit.setGameMap(gameMap);
            gameMap.placeAnimal(wolf, 1, 1);
            gameMap.placeAnimal(rabbit, 2, 2);

            System.out.println("Начальная позиция волка: (" + wolf.getX() + ", " + wolf.getY() + ")");
            System.out.println("Начальная позиция зайца: (" + rabbit.getX() + ", " + rabbit.getY() + ")");

            // Выполняем шаг перемещения
            wolf.walking();
            rabbit.walking();

            // Проверяем новые позиции
            System.out.println("Новая позиция волка: (" + wolf.getX() + ", " + wolf.getY() + ")");
            System.out.println("Новая позиция зайца: (" + rabbit.getX() + ", " + rabbit.getY() + ")");

            ConsoleRender renderer = new ConsoleRender();
            System.out.println("Initial state:");
            renderer.renderMap(gameMap);

            for (int i = 0; i < 5; i++) { // Show 5 steps
                System.out.println("\nStep " + (i + 1) + ":");
                wolf.walking();
                rabbit.walking();
                renderer.renderMap(gameMap);

                try {
                    Thread.sleep(1000); // 1 second pause
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }