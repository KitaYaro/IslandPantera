package com.javarush.island.matsarskaya.config;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
// Класс для хранения всей информации из config.json
public class IslandConfig {
    private List<Map<String, Map<String, Double>>> animalStats;
    private List<Map<String, Map<String, Integer>>> animalEating;
}
