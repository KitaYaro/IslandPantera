package com.javarush.island.matsarskaya.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

// загружает данные
public class ConfigLoaderService {
    ObjectMapper objectMapper = new ObjectMapper();

    public IslandConfig loadConfig() throws IOException {
        File configFile = new File("src/main/resources/config.json");
        return objectMapper.readValue(configFile, IslandConfig.class);
    }
}
