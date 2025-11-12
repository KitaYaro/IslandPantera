package com.javarush.island.matsarskaya.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnimalStats {
    private double weight;
    private int maxCountPerCell;
    private int speed;
    private double foodRequired;
}
