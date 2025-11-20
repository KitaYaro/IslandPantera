package com.javarush.island.matsarskaya.organism;

import com.javarush.island.matsarskaya.map.GameMap;
import lombok.Setter;

public class Grass {
    private int x;
    private int y;
    @Setter
    private double weight = 1.0;
    private GameMap gameMap;

    public Grass() {
    }

    public void setCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }
    // Метод для роста травы
    public void grow() {
        // Трава медленно растет со временем
        this.weight += 0.5;
        System.out.println("Трава в ячейке (" + (x+1) + "," + (y+1) + ") выросла. Новый вес: " + String.format("%.2f", weight));
    }
    // Метод для получения питательной ценности травы
    public double getNutritionalValue() {
        return this.weight;
    }
}
