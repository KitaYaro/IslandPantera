package com.javarush.island.matsarskaya.config;

import java.util.Map;

public class AnimalConfigService {
    private final IslandConfig islandConfig;

    public AnimalConfigService(IslandConfig islandConfig) {
        this.islandConfig = islandConfig;
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
                return animalStats;
            }
        }
        return null; // Если не найдено
    }
}
