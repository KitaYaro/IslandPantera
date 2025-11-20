package com.javarush.island.matsarskaya.organism;

import com.javarush.island.matsarskaya.config.AnimalConfigService;
import com.javarush.island.matsarskaya.config.AnimalStats;
import com.javarush.island.matsarskaya.map.Cell;
import com.javarush.island.matsarskaya.map.GameMap;

import java.util.Random;

public class Growing implements Runnable{
    private final GameMap gameMap;
    private final int maxGrassParCell;
    private final double grassWeight;
    private final int grassPerStep;


    // метод для вычисления скорости появления новых растений
    public Growing(GameMap gameMap, AnimalConfigService configService) {
        this.gameMap = gameMap;
        AnimalStats plantStats = configService.getPlantStats();
        if (plantStats != null) {
            this.maxGrassParCell = plantStats.getMaxCountPerCell();
            this.grassWeight = plantStats.getWeight();
            // Вычисляем plantsPerStep на основе данных из конфигурации
            // Например, 5% от максимального количества растений в ячейке
            this.grassPerStep = Math.max(1, plantStats.getMaxCountPerCell() / 20);
        } else {
            // Значения по умолчанию, если конфигурация не найдена
            this.maxGrassParCell = 200;
            this.grassWeight = 1.0;
            this.grassPerStep = 10;
        }
    }

    @Override
    public void run() {
        try {
            Random random = new Random();
            // Создаем новые растения в случайных ячейках
            for (int i = 0; i < grassPerStep; i++) {
                int x = random.nextInt(gameMap.getHeight());
                int y = random.nextInt(gameMap.getWidth());

                Cell cell = gameMap.getCell(x, y);
                if (cell != null) {
                    // Проверяем, не превышено ли максимальное количество растений в ячейке
                    if (cell.getGrassList().size() < maxGrassParCell) {
                        Grass grass = new Grass();
                        grass.setWeight(grassWeight);
                        grass.setCoordinates(x, y);
                        cell.addGrass(grass);
                    }
                }
            }
        }  catch (IllegalArgumentException e) {
            System.err.println("Invalid arguments during grass growing: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.err.println("State error during grass growing: " + e.getMessage());
        }
    }
}
