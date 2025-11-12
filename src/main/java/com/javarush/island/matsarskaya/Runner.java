package com.javarush.island.matsarskaya;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Runner {
    public static void main(String[] args) {

        ObjectMapper mapper = new ObjectMapper();
        try {
            File configFile = new File("src/main/resources/config.json");
            // Чтение как Map для более простого доступа
            Map<String, Object> configMap = mapper.readValue(configFile, Map.class);

            System.out.println("Sections in config: " + configMap.keySet());

            List<?> eatingList = (List<?>) configMap.get("animalEating");
            List<?> statsList = (List<?>) configMap.get("animalStats");

            System.out.println("Number of eating configurations: " + eatingList.size());
            System.out.println("Number of stats configurations: " + statsList.size());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}