package com.javarush.island.matsarskaya.entity;

import com.javarush.island.matsarskaya.config.AnimalConfigService;

public interface Animal {
    void walking();
    void eating(AnimalConfigService animalConfigService);
    void reproduction();
    boolean isAlive();
}
