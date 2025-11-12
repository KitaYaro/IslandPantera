package com.javarush.island.matsarskaya.entity.organism.predator;

import com.javarush.island.matsarskaya.config.ConfigData;
import com.javarush.island.matsarskaya.entity.Animal;
import com.javarush.island.matsarskaya.entity.organism.AnimalStats;
import com.javarush.island.matsarskaya.entity.organism.Animals;
import com.javarush.island.matsarskaya.entity.organism.herbivores.Herbivores;

import java.util.List;
import java.util.Random;

public class Predator extends Animals {
    public Predator(ConfigData configData) {
        this.configData = configData;
    }

    @Override
    protected void eatSpecificFood() {
        if (currentCell != null && configData != null) {
            // Находим травоядных для охоты
            List<Animal> prey = currentCell.getAnimals().stream()
                    .filter(a -> a instanceof Herbivores)
                    .toList();

            boolean hunted = false;
            AnimalStats stats = configData.getAnimalStats(getClass().getSimpleName().toLowerCase());

            if (stats != null && !prey.isEmpty()) {
                // Охота на случайную жертву
                Animal victim = prey.get(new Random().nextInt(prey.size()));
                String preyType = victim.getClass().getSimpleName().toLowerCase();

                Integer chance = configData.getEatingProbability(getClass().getSimpleName().toLowerCase(), preyType);
                if (chance != null && new Random().nextInt(100) < chance) {
                    // Успешная охота
                    currentCell.removeAnimal(victim);
                    AnimalStats preyStats = configData.getAnimalStats(preyType);
                    if (preyStats != null) {
                        weight += preyStats.getWeight() * 0.3 * packSize;
                        hunted = true;
                    }
                }
            }

            // Если не удалось поохотиться, теряем вес
            if (!hunted) {
                weight -= stats.getFoodRequired() * packSize * 0.2;
            }

            // Проверка смерти
            if (weight <= 0) alive = false;
        }
    }
}

//Пример реализации метода для получения пищевых предпочтений:
//public Map<String, Integer> getEatingProbabilitiesForPredator(String predatorType) {
//    if (animalEating != null) {
//        for (Map<String, Map<String, Object>> predatorMap : animalEating) {
//            if (predatorMap.containsKey(predatorType.toLowerCase())) {
//                Map<String, Object> preyProbabilities = predatorMap.get(predatorType.toLowerCase());
//                Map<String, Integer> result = new HashMap<>();
//                for (Map.Entry<String, Object> entry : preyProbabilities.entrySet()) {
//                    if (entry.getValue() instanceof Number) {
//                        result.put(entry.getKey(), ((Number) entry.getValue()).intValue());
//                    }
//                }
//                return result;
//            }
//        }
//    }
//    return new HashMap<>();
//}