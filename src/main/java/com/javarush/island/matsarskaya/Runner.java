package com.javarush.island.matsarskaya;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javarush.island.matsarskaya.map.GameMap;
import com.javarush.island.matsarskaya.organism.herbivore.Rabbit;
import com.javarush.island.matsarskaya.organism.predator.Wolf;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Runner {
    public static void main(String[] args) {

        ObjectMapper mapper = new ObjectMapper();
        try {
            File configFile = new File("src/main/resources/config.json");
            // Чтение как Map для более простого доступа
            Map<String, Object> configMap = mapper.readValue(configFile, Map.class);

            System.out.println("Sections in config: " + configMap.keySet());

            List<?> eatingList = (List<?>) configMap.get("animalEating");
            List<?> statsList = (List<?>) configMap.get("animalStats");

            System.out.println("Number of eating configurations: " + eatingList.size());
            System.out.println("Number of stats configurations: " + statsList.size());

        } catch (IOException e) {
            e.printStackTrace();
        }
        GameMap gameMap = new GameMap(10, 10);
        Wolf wolf = new Wolf();
        Rabbit rabbit = new Rabbit();
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
    }
}