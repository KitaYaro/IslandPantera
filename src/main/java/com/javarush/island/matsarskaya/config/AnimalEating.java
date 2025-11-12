package com.javarush.island.matsarskaya.config;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class AnimalEating {
    private Map<String, Map<String, Integer>> eatingProbabilities;
}
