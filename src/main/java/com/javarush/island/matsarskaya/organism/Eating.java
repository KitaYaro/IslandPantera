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

            // Сначала пробуем поесть животных (если это хищник)
            if (!potentialPrey.isEmpty() && (plantProbability == null || plantProbability < 100)) {
                // Выбираем случайную жертву
                Animal prey = potentialPrey.get(new Random().nextInt(potentialPrey.size()));

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

//                    System.out.println(animal.getClass().getSimpleName() + " съел " +
//                            prey.getClass().getSimpleName() + " (+" + String.format("%.2f", preyWeight) + " вес)");
//                } else {
//                    System.out.println(animal.getClass().getSimpleName() + " не смог съесть " +
//                            prey.getClass().getSimpleName());
//                }
                }
                // Если не удалось поесть животных или животное травоядное, пробуем есть растения
                else if (!availableGrass.isEmpty() && plantProbability != null && plantProbability > 0) {
                    // Логика поедания растений
                    if (new Random().nextInt(100) < plantProbability) {
                        // Поедаем одно растение
                        Grass grass = availableGrass.get(0);
                        double grassWeight = grass.getNutritionalValue();
                        animal.setWeight(animal.getWeight() + grassWeight);

                        currentCell.removeGrass(grass);

//                    System.out.println(animal.getClass().getSimpleName() + " съел растение (+" + String.format("%.2f", grassWeight) + " вес)");
//                } else {
//                    System.out.println(animal.getClass().getSimpleName() + " не захотел есть растения");
                    }
                }
            }
        }
    }
}