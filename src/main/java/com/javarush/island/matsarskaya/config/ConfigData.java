package com.javarush.island.matsarskaya.config;


import com.javarush.island.matsarskaya.entity.organism.AnimalStats;

import java.util.List;
import java.util.Map;

public class ConfigData {
    private Map<String, Map<String, Object>> predatorPreferences;
    public List<Map<String, Map<String, Object>>> animalEating;
    public List<Map<String, AnimalStats>> animalStats;

    //возвращает максимальное количество животных данного типа в одной ячейке
    public int getMaxCountPerCell(String animalType) {
        AnimalStats stats = getAnimalStats(animalType);
        if (stats != null) {
            return stats.getMaxCountPerCell();
        }
        throw new IllegalArgumentException("Animal type not found: " + animalType);
    }

    //возвращает статистику животных
    public AnimalStats getAnimalStats(String animalType) {
        if (animalStats != null) {
            for (Map<String, AnimalStats> statsMap : animalStats) {
                if (statsMap.containsKey(animalType.toLowerCase())) {
                    return statsMap.get(animalType.toLowerCase());
                }
            }
        }
        throw new IllegalArgumentException("Animal type not found: " + animalType);
    }

    //метод для получения вероятности съедания
    public Integer getEatingProbability(String predatorName, String preyName) {
        if (predatorPreferences != null) {
            Map<String, Object> preferences = predatorPreferences.get(predatorName.toLowerCase());
            if (preferences != null) {
                Object probability = preferences.get(preyName.toLowerCase());
                return probability instanceof Number ? ((Number) probability).intValue() : null;
            }
        }
        return null;
    }

}


