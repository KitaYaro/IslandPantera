package com.javarush.island.matsarskaya.simulation.view;

import com.javarush.island.matsarskaya.map.Cell;
import com.javarush.island.matsarskaya.map.GameMap;
import com.javarush.island.matsarskaya.entity.Animal;
import com.javarush.island.matsarskaya.organism.herbivore.*;
import com.javarush.island.matsarskaya.organism.predator.*;
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
                if (cell != null && cell.getAnimalList() != null && !cell.getAnimalList().isEmpty()) {
                    Animal firstAnimal = cell.getAnimalList().get(0);
                    String icon = getIconForAnimal(firstAnimal);
                    System.out.print(String.format("%-2s", icon));
                } else if (cell != null && !cell.getGrassList().isEmpty()) {
                    System.out.print(String.format("%-2s", IconAnimals.PLANT.get()));
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
        // Хищники
        if (animal instanceof Wolf) return IconAnimals.WOLF.get();
        if (animal instanceof Boa) return IconAnimals.BOA.get();
        if (animal instanceof Fox) return IconAnimals.FOX.get();
        if (animal instanceof Bear) return IconAnimals.BEAR.get();
        if (animal instanceof Eagle) return IconAnimals.EAGLE.get();

        // Травоядные
        if (animal instanceof Horse) return IconAnimals.HORSE.get();
        if (animal instanceof Deer) return IconAnimals.DEER.get();
        if (animal instanceof Rabbit) return IconAnimals.RABBIT.get();
        if (animal instanceof Mouse) return IconAnimals.MOUSE.get();
        if (animal instanceof Goat) return IconAnimals.GOAT.get();
        if (animal instanceof Sheep) return IconAnimals.SHEEP.get();
        if (animal instanceof Boar) return IconAnimals.BOAR.get();
        if (animal instanceof Buffalo) return IconAnimals.BUFFALO.get();
        if (animal instanceof Duck) return IconAnimals.DUCK.get();
        if (animal instanceof Caterpillar) return IconAnimals.CATERPILLAR.get();

        return "?";
    }

}
