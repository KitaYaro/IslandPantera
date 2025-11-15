package com.javarush.island.matsarskaya.map;

import com.javarush.island.matsarskaya.entity.Animal;
import com.javarush.island.matsarskaya.entity.Animals;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GameMap {
    private final Cell[][] cells;
    protected final int width;
    protected final int height;
    protected List<Animal> animals = new ArrayList<>();

    public GameMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new Cell[height][width];
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                cells[x][y] = new Cell(x, y);
            }
        }
    }
    public Cell getCell(int x, int y) {
        if (isValidPosition(x, y)) {
            return cells[y][x];
        }
        return null;
    }

    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
    //Метод для размещения животных на карте:
    public void placeAnimal(Animal animal, int x, int y){
        if (isValidPosition(x, y)){
            Cell cell = getCell(x, y);
            if(cell != null){
                cell.addAnimal(animal);
                if(animal instanceof Animals){
                    ((Animals) animal).setCoordinates(x, y);
                }
            }
        }
    }
    // В GameMap.java
    public int countAnimals() {
        int count = 0;
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                Cell cell = cells[x][y];
                if (cell != null) {
                    count += cell.getAnimalList().size();
                }
            }
        }
        return count;
    }


}
