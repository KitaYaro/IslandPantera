package com.javarush.island.matsarskaya.config;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AnimalConfigService {
    private final IslandConfig islandConfig;

    public AnimalConfigService(IslandConfig islandConfig) {
        this.islandConfig = islandConfig;
    }

    public AnimalStats getPlantStats() {
        return getAnimalStats("plants");
    }

    public AnimalStats getAnimalStats(String animalType) {
        return Optional.ofNullable(islandConfig)
                .map(IslandConfig::getAnimalStats)
                .stream()
                .flatMap(List::stream)
                .filter(animalStatsMap -> animalStatsMap.containsKey(animalType))
                .findFirst()
                .map(animalStatsMap -> createAnimalStats(animalStatsMap.get(animalType)))
                .orElse(null);
    }

    private AnimalStats createAnimalStats(Map<String, Double> stats) {
        AnimalStats animalStats = new AnimalStats();
        animalStats.setWeight(stats.getOrDefault("weight", 1.0));
        animalStats.setMaxCountPerCell(stats.getOrDefault("maxCountPerCell", 1.0).intValue());
        animalStats.setSpeed(stats.getOrDefault("speed", 1.0).intValue());
        animalStats.setFoodRequired(stats.getOrDefault("foodRequired", 0.0));
        animalStats.setWeightLossPerStep(stats.getOrDefault("weightLossPerStep", 1.0).intValue());
        return animalStats;
    }

    public Map<String, Integer> getEatingProbabilities(String animalEatingType) {
        if (islandConfig == null || islandConfig.getAnimalEating() == null) {
            return Collections.emptyMap();
        }

        return islandConfig.getAnimalEating().stream()
                .filter(eatingEntry -> eatingEntry.containsKey(animalEatingType))
                .findFirst()
                .map(eatingEntry -> eatingEntry.getOrDefault(animalEatingType, Collections.emptyMap()))
                .orElse(Collections.emptyMap());
    }
}
