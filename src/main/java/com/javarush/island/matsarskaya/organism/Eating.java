package com.javarush.island.matsarskaya.organism;

import com.javarush.island.matsarskaya.config.AnimalConfigService;
import com.javarush.island.matsarskaya.entity.Animal;
import com.javarush.island.matsarskaya.entity.Animals;
import com.javarush.island.matsarskaya.map.Cell;
import com.javarush.island.matsarskaya.map.GameMap;

import java.util.*;
import java.util.stream.Collectors;

public class Eating implements Runnable{
    private final Animals animal;
    private final AnimalConfigService animalConfigService;
    private Random eatRandom = new Random();

    public Eating(Animals animal, AnimalConfigService animalConfigService) {
        this.animal = animal;
        this.animalConfigService = animalConfigService;
    }
    @Override
    public void run() {
        if (!animal.isAlive()) return;

        GameMap gameMap = animal.getGameMap();
        if (gameMap == null) return;

        Cell currentCell = gameMap.getCell(animal.getX(), animal.getY());
        if (currentCell == null) return;

        List<Animal> animalsInCell = currentCell.getAnimalList().stream()
                .filter(Objects::nonNull)
                .filter(prey -> prey != animal && prey.isAlive())
                .collect(Collectors.toList());

        if (animalsInCell.isEmpty()) return;

        // Выбираем случайную жертву
        Animal prey = animalsInCell.get(new Random().nextInt(animalsInCell.size()));

        // Получаем вероятности поедания
        String predatorType = animal.getClass().getSimpleName().toLowerCase();
        Map<String, Integer> eatingProbabilities = Optional.ofNullable(animalConfigService)
                .map(service -> service.getEatingProbabilities(predatorType))
                .orElse(Collections.emptyMap());

        String preyType = prey.getClass().getSimpleName().toLowerCase();
        Integer probability = eatingProbabilities.get(preyType);

        if (probability != null && new Random().nextInt(100) < probability) {
            // Реальное поедание
            double preyWeight = prey instanceof Animals ? ((Animals) prey).getWeight() : 1.0;
            animal.setWeight(animal.getWeight() + preyWeight);

            // Удаляем жертву из ячейки и помечаем как мертвую
            currentCell.removeAnimal(prey);
            if (prey instanceof Animals) {
                ((Animals) prey).setAlive(false);
            }

            System.out.println(animal.getClass().getSimpleName() + " successfully ate " +
                    prey.getClass().getSimpleName() + " (+" + preyWeight + " weight)");
        } else {
            System.out.println(animal.getClass().getSimpleName() + " failed to eat " +
                    prey.getClass().getSimpleName());
        }
    }
}
