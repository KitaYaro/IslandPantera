package com.javarush.island.matsarskaya.organism.actions;

import com.javarush.island.matsarskaya.config.AnimalConfigService;
import com.javarush.island.matsarskaya.entity.Animal;
import com.javarush.island.matsarskaya.entity.Animals;
import com.javarush.island.matsarskaya.map.Cell;
import com.javarush.island.matsarskaya.organism.Grass;

import java.util.*;
import java.util.stream.Collectors;

public class Eating implements Runnable {
    private final Animals animal;
    private final AnimalConfigService animalConfigService;
    private static final Random random = new Random();

    public Eating(Animals animal, AnimalConfigService animalConfigService) {
        this.animal = animal;
        this.animalConfigService = animalConfigService;
    }

    @Override
    public void run() {
        performEating();
    }

    private void performEating() {
        if (!isEatingPossible()) return;

        Cell currentCell = animal.getGameMap().getCell(animal.getX(), animal.getY());
        if (currentCell == null) return;

        synchronized (currentCell) {
            Map<String, Integer> eatingProbabilities = getEatingProbabilities();
            List<Animal> potentialPrey = getPotentialPrey(currentCell);
            List<Grass> availableGrass = currentCell.getGrassList();

            Integer grassProbability = eatingProbabilities.get("grass");
            boolean canEatGrass = grassProbability != null && grassProbability > 0;

            if (canEatGrass && !availableGrass.isEmpty()) {
                tryEatGrass(availableGrass, grassProbability, currentCell);
            } else if (!potentialPrey.isEmpty()) {
                tryEatPrey(potentialPrey, eatingProbabilities, currentCell);
            }
        }
    }

    private boolean isEatingPossible() {
        return animal.isAlive()
                && animal.getGameMap() != null;
    }

    private Map<String, Integer> getEatingProbabilities() {
        String animalType = animal.getClass().getSimpleName().toLowerCase();
        return Optional.ofNullable(animalConfigService)
                .map(service -> service.getEatingProbabilities(animalType))
                .orElse(Collections.emptyMap());
    }

    private List<Animal> getPotentialPrey(Cell currentCell) {
        return currentCell.getAnimalList().stream()
                .filter(Objects::nonNull)
                .filter(prey -> prey != animal
                        && prey.isAlive()
                        && !prey.getClass().equals(animal.getClass()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void tryEatGrass(List<Grass> availableGrass, Integer grassProbability, Cell currentCell) {
        if (random.nextInt(100) < grassProbability) {
            // Поедаем одно растение
            availableGrass.stream()
                    .findFirst()
                    .ifPresent(grass -> {
                        double grassWeight = grass.getNutritionalValue();
                        animal.setWeight(animal.getWeight() + grassWeight);
                        animal.setHasEaten(true);
                        currentCell.removeGrass(grass);
                    });
        }
    }

    private void tryEatPrey(List<Animal> potentialPrey, Map<String, Integer> eatingProbabilities, Cell currentCell) {
        Animal prey = potentialPrey.get(random.nextInt(potentialPrey.size()));
        String preyType = prey.getClass().getSimpleName().toLowerCase();
        Integer probability = eatingProbabilities.get(preyType);

        if (probability != null && random.nextInt(100) < probability) {

            double preyWeight = prey instanceof Animals ? ((Animals) prey).getWeight() : 1.0;
            animal.setWeight(animal.getWeight() + preyWeight);
            animal.setHasEaten(true);

            currentCell.removeAnimal(prey);
            if (prey instanceof Animals) {
                ((Animals) prey).setAlive(false);
            }
        }
    }
}
