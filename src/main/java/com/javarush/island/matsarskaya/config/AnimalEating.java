package com.javarush.island.matsarskaya.config;

import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter
//отвечает за выполнение логики питания
public class AnimalEating {
    private Map<String, Integer> eatingProbabilities;
}
