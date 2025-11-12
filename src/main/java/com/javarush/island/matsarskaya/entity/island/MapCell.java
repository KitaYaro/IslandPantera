package com.javarush.island.matsarskaya.entity.island;

import com.javarush.island.matsarskaya.entity.Animal;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;

@Getter
public class MapCell {
    private List<Animal> animals = new ArrayList<>();
    private int grassCount;

    public MapCell() {
    }
    public List<Animal> getAnimals(){
        return new ArrayList<>(animals);
    }

    public synchronized void addAnimal(Animal animal) {
        animals.add(animal);
    }
    public synchronized void removeAnimal(Animal animal) {
        animals.remove(animal);
    }
    public synchronized void addGrass() {
        grassCount++;
    }
    public synchronized void removeGrass() {
        if(grassCount > 0) {
            grassCount--;
        }
    }
    public synchronized int getAnimalCountByType(Class<? extends Animal> animalType){
        return (int) animals.stream()
                .filter(animalType::isInstance)
                .count();
    }
    public boolean canAddAnimal(Animal animal, int maxCount){
        int currentCount = getAnimalCountByType(animal.getClass());
        return currentCount < maxCount;
    }
    public boolean canAddGrass(int maxGrass){
        return grassCount < maxGrass;
    }

}
