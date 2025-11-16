package com.javarush.island.matsarskaya.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javarush.island.matsarskaya.config.IslandConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

// загружает данные
public class ConfigLoaderService {
    ObjectMapper objectMapper = new ObjectMapper();

    public IslandConfig loadConfig() throws IOException {
        // Используем classpath для доступа к ресурсам
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.json");
        if (inputStream == null) {
            throw new IOException("Configuration file not found in resources");
        }
        return objectMapper.readValue(inputStream, IslandConfig.class);
    }
}
