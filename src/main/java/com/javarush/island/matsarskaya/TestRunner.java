package com.javarush.island.matsarskaya;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javarush.island.matsarskaya.config.ConfigData;
import com.javarush.island.matsarskaya.entity.organism.AnimalStats;
import com.javarush.island.matsarskaya.view.IconAnimal;


import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TestRunner {
    public static void main(String[] args) {

        try {
            ObjectMapper mapper = new ObjectMapper();

            InputStream input = TestRunner.class.getClassLoader()
                    .getResourceAsStream("config.json");
            if (input == null) {
                throw new IllegalStateException("config.json не найден!");
            }

            ConfigData config = mapper.readValue(input, ConfigData.class);

            // создаём map для удобного доступа
            Map<String, AnimalStats> statsMap = new HashMap<>();
            for (Map<String, AnimalStats> map : config.animalStats) {
                statsMap.putAll(map);
            }

            System.out.println("Данные :" + IconAnimal.FOX);
            System.out.println(statsMap.get("fox"));
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

//        System.out.println("Statistic: " + IconAnimal.WOLF + " x " + "100");
//
// test table
//        GameMap map = new GameMap(3, 2); // карта 3x2
//
//        map.getCell(0, 0).setContent(0, 0, IconAnimal.WOLF);
//        map.getCell(0, 0).setContent(1, 1, IconAnimal.FOX);
//        map.getCell(1, 0).setContent(0, 1, IconAnimal.MOUSE);
//        map.getCell(2, 1).setContent(1, 0, IconAnimal.BEAR);
//
//        displayMapCompact(map);
//    }
//    public static void displayMapCompact(GameMap map) {
//        int rows = map.getHeight();
//        int cols = map.getWidth();
//        int cellSize = map.getCell(0, 0).getContent().length;
//
//        System.out.print(IconTable.BORDER_TOP_LEFT.getSymbol());
//        for (int c = 0; c < cols; c++) {
//            System.out.print(IconTable.BORDER_HORIZONTAL.getSymbol().repeat(cellSize * 2));
//            if (c < cols - 1) System.out.print(IconTable.BORDER_TOP_SEPARATOR.getSymbol());
//        }
//        System.out.println(IconTable.BORDER_TOP_RIGHT.getSymbol());
//
//        for (int r = 0; r < rows; r++) {
//            for (int inner = 0; inner < cellSize; inner++) {
//                System.out.print(IconTable.BORDER_VERTICAL.getSymbol());
//                for (int c = 0; c < cols; c++) {
//                    for (int i = 0; i < cellSize; i++) {
//                        String emoji = map.getCell(c, r).getContent()[inner][i].get();
//                        System.out.print(emoji + " ");
//                    }
//                    System.out.print(IconTable.BORDER_VERTICAL.getSymbol());
//                }
//                System.out.println();
//            }
//
//            if (r < rows - 1) {
//                System.out.print(IconTable.BORDER_LEFT_SEPARATOR.getSymbol());
//                for (int c = 0; c < cols; c++) {
//                    System.out.print(IconTable.BORDER_HORIZONTAL.getSymbol().repeat(cellSize * 2));
//                    if (c < cols - 1) System.out.print(IconTable.BORDER_CENTER_SEPARATOR.getSymbol());
//                }
//                System.out.println(IconTable.BORDER_RIGHT_SEPARATOR.getSymbol());
//            }
//        }
//
//
//        System.out.print(IconTable.BORDER_BOTTOM_LEFT.getSymbol());
//        for (int c = 0; c < cols; c++) {
//            System.out.print(IconTable.BORDER_HORIZONTAL.getSymbol().repeat(cellSize * 2));
//            if (c < cols - 1) System.out.print(IconTable.BORDER_BOTTOM_SEPARATOR.getSymbol());
//        }
//        System.out.println(IconTable.BORDER_BOTTOM_RIGHT.getSymbol());
//    }
//}


//Парсер на animalEating
//ParserConfig parserConfig = new ParserConfig();
//        parserConfig.parse();
//
//        for (var entry : parserConfig.getAllAnimals().entrySet()) {
//        System.out.println(entry.getKey() + " -> " + entry.getValue());
//
//        }