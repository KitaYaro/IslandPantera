package com.javarush.island.matsarskaya.config;

import java.util.Collections;
import java.util.Map;

// отвечает за получение данных из конфигурации
public class AnimalConfigService {
    private final IslandConfig islandConfig;

    public AnimalConfigService(IslandConfig islandConfig) {
        this.islandConfig = islandConfig;
    }
    public AnimalStats getPlantStats() {
        return getAnimalStats("plants");
    }
    public AnimalStats getAnimalStats(String animalType) {
        if (islandConfig == null || islandConfig.getAnimalStats() == null) {
            return null;
        }
        // Проходим по списку animalStats в конфигурации
        for (Map<String, Map<String, Double>> animalStatsMap : islandConfig.getAnimalStats()) {
            if (animalStatsMap.containsKey(animalType)) {
                Map<String, Double> stats = animalStatsMap.get(animalType);
                AnimalStats animalStats = new AnimalStats();
                animalStats.setWeight(stats.getOrDefault("weight", 1.0));
                animalStats.setMaxCountPerCell(stats.getOrDefault("maxCountPerCell", 1.0).intValue());
                animalStats.setSpeed(stats.getOrDefault("speed", 1.0).intValue());
                animalStats.setFoodRequired(stats.getOrDefault("foodRequired", 0.0));
                animalStats.setWeightLossPerStep(stats.getOrDefault("weightLossPerStep", 1.0).intValue());
                return animalStats;
            }
        }
        return null; // Если не найдено
    }
    // метод для получения данных поедания
    public Map<String, Integer> getEatingProbabilities(String animalEatingType) {
        if (islandConfig == null || islandConfig.getAnimalEating() == null) {
            return Collections.emptyMap();
        }
        // Проходим по списку animalEating в конфигурации
        for (Map<String, Map<String, Integer>> eatingEntry : islandConfig.getAnimalEating()) {
            if (eatingEntry.containsKey(animalEatingType)) {
                return eatingEntry.getOrDefault(animalEatingType, Collections.emptyMap());
            }
        }
        return Collections.emptyMap(); // Если не найдено
    }
}
