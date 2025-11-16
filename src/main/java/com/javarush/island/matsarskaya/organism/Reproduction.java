package com.javarush.island.matsarskaya.organism;

import com.javarush.island.matsarskaya.entity.Animal;

public class Reproduction implements Runnable{
    private final Animal animal;

    public Reproduction(Animal animal) {
        this.animal = animal;
    }

    @Override
    public void run() {
        if (animal.isAlive()) {
            animal.reproduction();
        }
    }
}
