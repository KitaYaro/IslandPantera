package com.javarush.island.matsarskaya.config;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class SimulationStatistics {
    @Getter
    private Map<String, Integer> initialAnimalCounts = new HashMap<>();
    private Map<String, Integer> previousAnimalCounts = new HashMap<>();
    @Getter
    private final Map<String, Integer> bornCounts = new HashMap<>();
    @Getter
    private final Map<String, Integer> diedCounts = new HashMap<>();

    public void initialize(Map<String, Integer> initialCounts) {
        this.initialAnimalCounts = new HashMap<>(initialCounts);
        this.previousAnimalCounts = new HashMap<>(initialCounts);
    }

    public void updateStatistics(Map<String, Integer> currentCounts) {
        bornCounts.clear();
        diedCounts.clear();

        // Собираем все типы животных
        Map<String, Integer> allTypes = new HashMap<>();
        allTypes.putAll(previousAnimalCounts);
        allTypes.putAll(currentCounts);

        for (String type : allTypes.keySet()) {
            int prevCount = previousAnimalCounts.getOrDefault(type, 0);
            int currentCount = currentCounts.getOrDefault(type, 0);

            if (currentCount > prevCount) {
                bornCounts.put(type, currentCount - prevCount);
                diedCounts.put(type, 0);
            } else if (currentCount < prevCount) {
                diedCounts.put(type, prevCount - currentCount);
                bornCounts.put(type, 0);
            } else {
                bornCounts.put(type, 0);
                diedCounts.put(type, 0);
            }
        }
        previousAnimalCounts = new HashMap<>(currentCounts);
    }
}
