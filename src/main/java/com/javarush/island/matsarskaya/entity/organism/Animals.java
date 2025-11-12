package com.javarush.island.matsarskaya.entity.organism;

import com.javarush.island.matsarskaya.config.ConfigData;
import com.javarush.island.matsarskaya.entity.Animal;
import com.javarush.island.matsarskaya.entity.island.GameMap;
import com.javarush.island.matsarskaya.entity.island.MapCell;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Random;

@Setter
@Getter
public abstract class Animals implements Animal {

    protected ConfigData configData;
    protected MapCell currentCell;
    protected boolean alive = true;
    protected double weight;
    protected double foodRequired;
    protected int packSize = 1;
    protected GameMap gameMap;

    @Override
    public synchronized int eating() {
        if (alive && currentCell != null) {
            weight -= foodRequired * 0.1;
            eatSpecificFood();
            if (weight <= 0) {
                alive = false;
            }
        }
        return 0;
    }

    protected abstract void eatSpecificFood();

    @Override
    public synchronized void walking() {
        if (!alive || currentCell == null || configData == null) {
            return;
        }
        try {
            String animalType = this.getClass().getSimpleName().toLowerCase();
            AnimalStats stats = configData.getAnimalStats(animalType);

            if (stats == null || stats.getSpeed() <= 0) {
                return;
            }

            int maxSpeed = stats.getSpeed();
            int stepsToTake = new Random().nextInt(maxSpeed) + 1;

            if (gameMap == null) {
                return; // Нет доступа к карте
            }
            int currentX = 0, currentY = 0;
            List<MapCell> neighbors = gameMap.getNeighborsCells(currentX, currentY);

            if (neighbors.isEmpty()) {
                return;
            }
            MapCell targetCell = neighbors.get(new Random().nextInt(neighbors.size()));
            if (targetCell.canAddAnimal(this, stats.getMaxCountPerCell())) {
                synchronized (currentCell) {
                    synchronized (targetCell) {
                        currentCell.removeAnimal(this);
                        targetCell.addAnimal(this);
                        currentCell = targetCell;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error during walking: " + e.getMessage());
        }
    }

    @Override
public synchronized int reproduction() {
    return 0;
}

@Override
public synchronized boolean isAlive() {
    return alive;
}

private double calculateMinReproductionWeight() {
    // Минимальный вес для размножения = базовый вес * размер стаи * коэффициент
    return weight * packSize * 0.2;
}

protected boolean canReproduce() {
    // Проверяем, что вес стаи достаточен для размножения
    double minReproductionWeight = calculateMinReproductionWeight();
    return weight >= minReproductionWeight && alive;
}
}