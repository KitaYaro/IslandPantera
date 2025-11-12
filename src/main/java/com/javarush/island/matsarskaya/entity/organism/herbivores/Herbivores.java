package com.javarush.island.matsarskaya.entity.organism.herbivores;

import com.javarush.island.matsarskaya.entity.organism.AnimalStats;
import com.javarush.island.matsarskaya.entity.organism.Animals;

public class Herbivores extends Animals {
    @Override
    protected void eatSpecificFood() {
        if (currentCell != null && configData != null) {
            String animalType = this.getClass().getSimpleName().toLowerCase();
            AnimalStats stats = configData.getAnimalStats(animalType);

            if (stats != null) {
                double foodNeeded = stats.getFoodRequired();
                double totalFoodRequired = foodNeeded * packSize;

                if (currentCell.getGrassCount() > 0) {
                    weight += foodNeeded * 0.5 * packSize;
                    currentCell.removeGrass();
                } else {
                    weight -= totalFoodRequired * 0.3;
                }
                if (weight <= 0) {
                    alive = false;
                }
            }
        }
    }
}