package com.javarush.island.matsarskaya.map;

import com.javarush.island.matsarskaya.organism.Animal;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Cell {
    protected List<Animal> animalList = new ArrayList<>();
    private int cellX;
    private int cellY;
    private GameMap gameMap;

    public Cell (int cellX, int cellY) {
        this.cellX = cellX;
        this.cellY = cellY;
    }
    public void addAnimal(Animal animal) {
        animalList.add(animal);
    }
    public void removeAnimal(Animal animal) {
        animalList.remove(animal);
    }
    public List<Animal> getAnimalList(){
        return new ArrayList<>(animalList);
    }
    public Cell getNeighboringCell(int x, int y) {
        return new Cell(x, y);
    }

}
