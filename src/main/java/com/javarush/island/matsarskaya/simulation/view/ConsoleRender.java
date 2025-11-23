package com.javarush.island.matsarskaya.simulation.view;

import com.javarush.island.matsarskaya.map.Cell;
import com.javarush.island.matsarskaya.map.GameMap;
import com.javarush.island.matsarskaya.entity.Animal;
import com.javarush.island.matsarskaya.simulation.IconAnimals;
import com.javarush.island.matsarskaya.simulation.IconTable;

public class ConsoleRender {
    public void renderMap(GameMap gameMap) {
        System.out.print(IconTable.BORDER_TOP_LEFT.getSymbol());
        for (int y = 0; y < gameMap.getWidth(); y++) {
            System.out.print(IconTable.BORDER_HORIZONTAL.getSymbol() + IconTable.BORDER_HORIZONTAL.getSymbol());
        }
        System.out.println(IconTable.BORDER_TOP_RIGHT.getSymbol());

        for (int x = 0; x < gameMap.getHeight(); x++) {
            System.out.print(IconTable.BORDER_VERTICAL.getSymbol());
            for (int y = 0; y < gameMap.getWidth(); y++) {
                Cell cell = gameMap.getCell(x, y);
                if (cell != null && cell.getAnimalList() != null && !cell.getAnimalList().isEmpty()) {
                    Animal firstAnimal = cell.getAnimalList().getFirst();
                    String icon = getIconForAnimal(firstAnimal);
                    System.out.print(padRight(icon, 2));
                } else if (cell != null && !cell.getGrassList().isEmpty()) {
                    System.out.print(padRight(IconAnimals.PLANT.get(), 2));
                } else {
                    System.out.print(padRight(".", 2));
                }
            }
            System.out.println(IconTable.BORDER_VERTICAL.getSymbol());
        }

        System.out.print(IconTable.BORDER_BOTTOM_LEFT.getSymbol());
        for (int y = 0; y < gameMap.getWidth(); y++) {
            System.out.print(IconTable.BORDER_HORIZONTAL.getSymbol() + IconTable.BORDER_HORIZONTAL.getSymbol());
        }
        System.out.println(IconTable.BORDER_BOTTOM_RIGHT.getSymbol());
    }
    private String padRight(String s, int n) {
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < n) {
            sb.append(" ");
        }
        return sb.toString();
    }

    private String getIconForAnimal(Animal animal) {
        return switch (animal.getClass().getSimpleName()) {
            // Хищники
            case "Wolf" -> IconAnimals.WOLF.get();
            case "Boa" -> IconAnimals.BOA.get();
            case "Fox" -> IconAnimals.FOX.get();
            case "Bear" -> IconAnimals.BEAR.get();
            case "Eagle" -> IconAnimals.EAGLE.get();

            // Травоядные
            case "Horse" -> IconAnimals.HORSE.get();
            case "Deer" -> IconAnimals.DEER.get();
            case "Rabbit" -> IconAnimals.RABBIT.get();
            case "Mouse" -> IconAnimals.MOUSE.get();
            case "Goat" -> IconAnimals.GOAT.get();
            case "Sheep" -> IconAnimals.SHEEP.get();
            case "Boar" -> IconAnimals.BOAR.get();
            case "Buffalo" -> IconAnimals.BUFFALO.get();
            case "Duck" -> IconAnimals.DUCK.get();
            case "Caterpillar" -> IconAnimals.CATERPILLAR.get();

            default -> "?";
        };
    }
}
