package com.javarush.island.matsarskaya.organism.actions;

import com.javarush.island.matsarskaya.config.AnimalConfigService;
import com.javarush.island.matsarskaya.config.AnimalStats;
import com.javarush.island.matsarskaya.map.Cell;
import com.javarush.island.matsarskaya.map.GameMap;
import com.javarush.island.matsarskaya.organism.Grass;

import java.util.Random;

public class Growing implements Runnable{
    private final GameMap gameMap;
    private final int maxGrassParCell;
    private final double grassWeight;
    private final int grassPerStep;

    public Growing(GameMap gameMap, AnimalConfigService configService) {
        this.gameMap = gameMap;
        AnimalStats plantStats = configService.getPlantStats();
        if (plantStats != null) {
            this.maxGrassParCell = plantStats.getMaxCountPerCell();
            this.grassWeight = plantStats.getWeight();
            this.grassPerStep = Math.max(1, plantStats.getMaxCountPerCell() / 15);
        } else {
            this.maxGrassParCell = 200;
            this.grassWeight = 1.0;
            this.grassPerStep = 10;
        }
    }

    @Override
    public void run() {
        try {
            int totalGrass = gameMap.countGrass();

            if (totalGrass >= 200) {
                return;
            }
            Random random = new Random();
            for (int i = 0; i < grassPerStep; i++) {
                int x = random.nextInt(gameMap.getHeight());
                int y = random.nextInt(gameMap.getWidth());

                Cell cell = gameMap.getCell(x, y);
                if (cell != null) {
                    synchronized (cell) {
                        if (cell.getGrassList().size() < maxGrassParCell) {
                            Grass grass = new Grass();
                            grass.setWeight(grassWeight);
                            grass.setCoordinates(x, y);
                            cell.addGrass(grass);
                        }
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
