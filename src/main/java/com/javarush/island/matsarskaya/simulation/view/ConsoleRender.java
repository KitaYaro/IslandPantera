package com.javarush.island.matsarskaya.simulation.view;

import com.javarush.island.matsarskaya.map.Cell;
import com.javarush.island.matsarskaya.map.GameMap;
import com.javarush.island.matsarskaya.entity.Animal;
import com.javarush.island.matsarskaya.organism.herbivore.Rabbit;
import com.javarush.island.matsarskaya.organism.predator.Wolf;
import com.javarush.island.matsarskaya.simulation.IconAnimals;
import com.javarush.island.matsarskaya.simulation.IconTable;

public class ConsoleRender {
    public void renderMap(GameMap gameMap) {
        // Верхняя граница
        System.out.print(IconTable.BORDER_TOP_LEFT.getSymbol());
        for (int y = 0; y < gameMap.getWidth(); y++) {
            System.out.print(IconTable.BORDER_HORIZONTAL.getSymbol() + IconTable.BORDER_HORIZONTAL.getSymbol());
        }
        System.out.println(IconTable.BORDER_TOP_RIGHT.getSymbol());

        // Содержимое карты
        for (int x = 0; x < gameMap.getHeight(); x++) {
            System.out.print(IconTable.BORDER_VERTICAL.getSymbol());
            for (int y = 0; y < gameMap.getWidth(); y++) {
                Cell cell = gameMap.getCell(x, y);
                if (cell != null && !cell.getAnimalList().isEmpty()) {
                    Animal firstAnimal = cell.getAnimalList().get(0);
                    String icon = getIconForAnimal(firstAnimal);
                    System.out.print(String.format("%-2s", icon));
                } else {
                    System.out.print(String.format("%-2s", "."));
                }
            }
            System.out.println(IconTable.BORDER_VERTICAL.getSymbol());
        }

        // Нижняя граница
        System.out.print(IconTable.BORDER_BOTTOM_LEFT.getSymbol());
        for (int y = 0; y < gameMap.getWidth(); y++) {
            System.out.print(IconTable.BORDER_HORIZONTAL.getSymbol() + IconTable.BORDER_HORIZONTAL.getSymbol());
        }
        System.out.println(IconTable.BORDER_BOTTOM_RIGHT.getSymbol());
    }

    private String getIconForAnimal(Animal animal) {
        if (animal instanceof Wolf) {
            return IconAnimals.WOLF.get();
        } else if (animal instanceof Rabbit) {
            return IconAnimals.RABBIT.get();
        }
        return "?";

    }
}
