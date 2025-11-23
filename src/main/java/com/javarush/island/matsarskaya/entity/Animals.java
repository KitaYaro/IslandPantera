package com.javarush.island.matsarskaya.entity;

import com.javarush.island.matsarskaya.config.AnimalConfigService;
import com.javarush.island.matsarskaya.config.AnimalParameters;
import com.javarush.island.matsarskaya.map.Cell;
import com.javarush.island.matsarskaya.map.GameMap;
import com.javarush.island.matsarskaya.organism.actions.Eating;
import com.javarush.island.matsarskaya.organism.actions.Reproduction;
import com.javarush.island.matsarskaya.organism.actions.Walking;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public abstract class Animals implements Animal {
    private static final double MIN_WEIGHT_RATIO_FOR_SURVIVAL = 0.05;
    private int x;
    private int y;
    protected boolean alive = true;
    protected boolean hasEaten = false;
    protected GameMap gameMap;
    private AnimalParameters parameters;
    private double maxWeight;
    private double weightBeforeEating;

    public void initializeFromConfig(double weight, int speed, double foodRequired,
                                     int maxCountPerCell, int weightLossPerStep) {
        this.parameters = new AnimalParameters(weight, speed, foodRequired, maxCountPerCell, weightLossPerStep);
        this.maxWeight = weight;
    }

    public double getWeight() {
        return parameters != null ? parameters.getWeight() : 0.0;
    }

    public void setWeight(double weight) {
        if (parameters != null) {
            parameters.setWeight(weight);
        }
    }

    public int getSpeed() {
        return parameters.getSpeed();
    }

    public double getFoodRequired() {
        return parameters.getFoodRequired();
    }

    public int getMaxCountPerCell() {
        return parameters.getMaxCountPerCell();
    }

    public int getWeightLossPerStep() {
        return parameters.getWeightLossPerStep();
    }

    public void setCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void walking() {
        Walking walkingTask = new Walking(this);
        walkingTask.run();
    }

    @Override
    public void eating(AnimalConfigService animalConfigService) {
        this.hasEaten = true;
        Eating eatingTask = new Eating(this, animalConfigService);
        eatingTask.run();
    }

    @Override
    public void reproduction() {
        Reproduction reproductionTask = new Reproduction(this);
        reproductionTask.run();
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    public void prepareForEatingCheck() {
        this.weightBeforeEating = this.getWeight();
    }

    @Override
    public void loseWeightOverTime() {
        if (this.hasEaten) {
            this.hasEaten = false;
            return;
        }

        double weightFactor = Math.log10(this.maxWeight + 1) / 10;
        double baseLossPercent = Math.min(0.5, 0.3 + weightFactor);

        double weightLoss = this.maxWeight * baseLossPercent;
        this.setWeight(this.getWeight() - weightLoss);

        if (this.getWeight() < this.maxWeight * MIN_WEIGHT_RATIO_FOR_SURVIVAL) {
            this.alive = false;
            removeAnimalFromCell();
            return;
        }

        this.hasEaten = false;
    }

    private void removeAnimalFromCell() {
        Cell currentCell = gameMap.getCell(this.x, this.y);
        if (currentCell != null) {
            synchronized (currentCell) {
                currentCell.removeAnimal(this);
            }
        }
    }
}


