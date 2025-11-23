package com.javarush.island.matsarskaya.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnimalParameters {
    private double weight;
    private int speed;
    private double foodRequired;
    private int maxCountPerCell;
    private int weightLossPerStep;

    public AnimalParameters(double weight, int speed, double foodRequired,
                            int maxCountPerCell, int weightLossPerStep) {
        this.weight = weight;
        this.speed = speed;
        this.foodRequired = foodRequired;
        this.maxCountPerCell = maxCountPerCell;
        this.weightLossPerStep = weightLossPerStep;
    }
}
