package com.javarush.island.matsarskaya.organism;

import lombok.Getter;
import lombok.Setter;

public class Grass {
    @Getter
    private int x;
    @Getter
    private int y;
    @Getter
    @Setter
    private double weight = 1.0;

    public Grass() {
    }
    public void setCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public double getNutritionalValue() {
        return this.weight;
    }
}
