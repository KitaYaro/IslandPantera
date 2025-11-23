package com.javarush.island.matsarskaya.organism.actions;

import com.javarush.island.matsarskaya.entity.Animal;
import com.javarush.island.matsarskaya.entity.Animals;
import com.javarush.island.matsarskaya.map.Cell;
import com.javarush.island.matsarskaya.map.GameMap;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class Reproduction implements Runnable {
    private static final double DEFAULT_MIN_WEIGHT_FOR_REPRODUCTION = 1.0;
    private static final double LIGHT_ANIMALS_MAX_WEIGHT_THRESHOLD = 5.0;
    private static final double LIGHT_ANIMALS_MIN_WEIGHT_FOR_REPRODUCTION = 0.2;
    private static final int HIGH_DENSITY_REPRODUCTION_CHANCE = 5;
    private static final int DEFAULT_REPRODUCTION_CHANCE = 100;
    private static final int REDUCED_REPRODUCTION_CHANCE = 40;
    private static final double PARENT_WEIGHT_LOSS_PERCENT = 0.1;
    private static final double OFFSPRING_WEIGHT_PERCENT = 0.1;
    private static final int POPULATION_MULTIPLIER = 5;

    private final Animals animal;
    private static final Random random = new Random();

    public Reproduction(Animals animal) {
        this.animal = animal;
    }

    @Override
    public void run() {
        performReproduction();
    }

    private void performReproduction() {
        if (!canReproduce()) return;

        Cell currentCell = animal.getGameMap().getCell(animal.getX(), animal.getY());
        if (currentCell == null) return;

        synchronized (currentCell) {
            if (isPopulationTooDense()) {
                if (random.nextInt(100) > HIGH_DENSITY_REPRODUCTION_CHANCE) {
                    return;
                }
            }

            int reproductionChance = calculateReproductionChance();
            if (random.nextInt(100) > reproductionChance) {
                return;
            }

            List<Animal> potentialPartners = findPotentialPartners(currentCell);
            Animal partnerAnimal = potentialPartners.stream()
                    .findFirst()
                    .orElse(null);

            if (partnerAnimal == null) {
                return;
            }
            if (isCellOvercrowded(currentCell)) {
                return;
            }
            Animals partner = (Animals) partnerAnimal;
            createOffspring(partner, currentCell);
        }
    }

    private boolean canReproduce() {
        return animal.isAlive()
                && animal.getGameMap() != null
                && animal.getWeight() >= getMinWeightForReproduction();
    }

    private double getMinWeightForReproduction() {
        if (animal.getMaxWeight() < LIGHT_ANIMALS_MAX_WEIGHT_THRESHOLD) {
            return LIGHT_ANIMALS_MIN_WEIGHT_FOR_REPRODUCTION;
        }
        return DEFAULT_MIN_WEIGHT_FOR_REPRODUCTION;
    }

    private boolean isPopulationTooDense() {
        long totalCount = countAnimalsOfType(animal.getClass());
        int maxTotalPopulation = animal.getMaxCountPerCell() * POPULATION_MULTIPLIER;
        return totalCount > maxTotalPopulation;
    }

    private int calculateReproductionChance() {
        return animal.isHasEaten() ? DEFAULT_REPRODUCTION_CHANCE : REDUCED_REPRODUCTION_CHANCE;
    }

    private List<Animal> findPotentialPartners(Cell currentCell) {
        return currentCell.getAnimalList().stream()
                .filter(a -> a != animal
                        && a.getClass() == animal.getClass()
                        && a.isAlive()).collect(Collectors.toList());
    }

    private boolean isCellOvercrowded(Cell currentCell) {
        long currentCount = currentCell.getAnimalList().stream()
                .filter(a -> a.getClass() == animal.getClass())
                .count();
        return currentCount >= animal.getMaxCountPerCell();
    }

    private void createOffspring(Animals partner, Cell currentCell) {
        try {
            Animals offspring = animal.getClass().getDeclaredConstructor().newInstance();
            double offspringWeight = animal.getWeight() * OFFSPRING_WEIGHT_PERCENT
                    + partner.getWeight() * OFFSPRING_WEIGHT_PERCENT;

            offspring.initializeFromConfig(
                    offspringWeight,
                    animal.getSpeed(),
                    animal.getFoodRequired(),
                    animal.getMaxCountPerCell(),
                    animal.getWeightLossPerStep()
            );

            offspring.setGameMap(animal.getGameMap());
            offspring.setCoordinates(animal.getX(), animal.getY());
            offspring.setAlive(true);

            animal.setWeight(animal.getWeight() * (1 - PARENT_WEIGHT_LOSS_PERCENT));
            partner.setWeight(partner.getWeight() * (1 - PARENT_WEIGHT_LOSS_PERCENT));

            currentCell.addAnimal(offspring);
        } catch (InstantiationException | IllegalAccessException e) {
            System.err.println("Error creating child element: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error while creating child: " + e.getMessage());
        }
    }

    private long countAnimalsOfType(Class<? extends Animal> animalType) {
        GameMap gameMap = animal.getGameMap();
        if (gameMap == null) return 0;

        return Arrays.stream(gameMap.getCells())
                .flatMap(Arrays::stream)
                .filter(Objects::nonNull)
                .mapToLong(cell -> cell.getAnimalList().stream()
                        .filter(a -> a.getClass() == animalType && a.isAlive())
                        .count())
                .sum();
    }
}

