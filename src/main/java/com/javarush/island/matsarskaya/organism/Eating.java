package com.javarush.island.matsarskaya.organism;

import com.javarush.island.matsarskaya.config.AnimalConfigService;
import com.javarush.island.matsarskaya.entity.Animal;
import com.javarush.island.matsarskaya.entity.Animals;
import com.javarush.island.matsarskaya.map.Cell;
import com.javarush.island.matsarskaya.map.GameMap;

import java.util.*;
import java.util.stream.Collectors;

public class Eating implements Runnable {
    private final Animals animal;
    private final AnimalConfigService animalConfigService;

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

        // Получаем параметры питания для текущего животного
        String animalType = animal.getClass().getSimpleName().toLowerCase();
        Map<String, Integer> eatingProbabilities = Optional.ofNullable(animalConfigService)
                .map(service -> service.getEatingProbabilities(animalType))
                .orElse(Collections.emptyMap());

        // Определяем, может ли животное есть растения
        Integer plantProbability = eatingProbabilities.get("plants");

        // Синхронизация на уровне ячейки для выбора жертвы и поедания
        synchronized (currentCell) {
            // Получаем животных и растения из текущей ячейки
            List<Animal> potentialPrey = currentCell.getAnimalList().stream()
                    .filter(Objects::nonNull)
                    .filter(prey -> prey != animal && prey.isAlive() &&
                            !prey.getClass().equals(animal.getClass()))
                    .collect(Collectors.toList());

            List<Grass> availableGrass = currentCell.getGrassList();

            // Проверяем, может ли животное есть растения
            boolean canEatPlants = plantProbability != null && plantProbability > 0;

            // Для травоядных сразу пробуем есть растения
            if (canEatPlants && !availableGrass.isEmpty()) {
                // Логика поедания растений
                if (new Random().nextInt(100) < plantProbability) {
                    // Поедаем одно растение
                    Grass grass = availableGrass.get(0);
                    double grassWeight = grass.getNutritionalValue();
                    animal.setWeight(animal.getWeight() + grassWeight);
                    animal.setHasEaten(true); // Помечаем, что животное поело

                    currentCell.removeGrass(grass);
                }
            }
            // Для хищников пробуем поесть животных
            else if (!potentialPrey.isEmpty()) {
                // Выбираем случайную жертву
                Animal prey = potentialPrey.get(new Random().nextInt(potentialPrey.size()));

                String preyType = prey.getClass().getSimpleName().toLowerCase();
                Integer probability = eatingProbabilities.get(preyType);

                if (probability != null && new Random().nextInt(100) < probability) {
                    // Реальное поедание
                    double preyWeight = prey instanceof Animals ? ((Animals) prey).getWeight() : 1.0;
                    animal.setWeight(animal.getWeight() + preyWeight);
                    animal.setHasEaten(true); // Помечаем, что животное поело

                    // Удаляем жертву из ячейки и помечаем как мертвую
                    currentCell.removeAnimal(prey);
                    if (prey instanceof Animals) {
                        ((Animals) prey).setAlive(false);
                    }
                }
            }
        }
    }
}