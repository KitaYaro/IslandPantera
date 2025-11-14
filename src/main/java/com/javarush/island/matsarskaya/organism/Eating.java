package com.javarush.island.matsarskaya.organism;

import java.util.Random;

public class Eating implements Runnable{
    private final Animals animal;
    private Random eatRandom = new Random();

    public Eating(Animals animal) {
        this.animal = animal;
    }
    @Override
    public void run() {
        animal.eating();
    }
}
