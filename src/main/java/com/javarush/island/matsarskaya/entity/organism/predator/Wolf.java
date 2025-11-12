package com.javarush.island.matsarskaya.entity.organism.predator;

import com.javarush.island.matsarskaya.config.ConfigData;
import com.javarush.island.matsarskaya.entity.organism.Animals;

public class Wolf extends Predator {


    public Wolf(ConfigData configData) {
        super(configData);
        this.packSize = 10;
    }
}
