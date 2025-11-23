package com.javarush.island.matsarskaya.map;

import com.javarush.island.matsarskaya.entity.Animal;
import com.javarush.island.matsarskaya.organism.Grass;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Cell {
    protected List<Animal> animalList = new ArrayList<>();
    protected List<Grass> grassList = new ArrayList<>();
    private final int cellX;
    private final int cellY;

    public Cell (int cellX, int cellY) {
        this.cellX = cellX;
        this.cellY = cellY;
    }
    public synchronized void addAnimal(Animal animal) {
        animalList.add(animal);
    }
    public synchronized void removeAnimal(Animal animal) {
        animalList.remove(animal);
    }
    public synchronized void addGrass(Grass grass){
        grassList.add(grass);
    }
    public synchronized void removeGrass(Grass grass){
        grassList.remove(grass);
    }

    public List<Animal> getAnimalList(){
        return new ArrayList<>(animalList);
    }
}
