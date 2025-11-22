package com.javarush.island.matsarskaya.entity;

import com.javarush.island.matsarskaya.config.AnimalConfigService;
import com.javarush.island.matsarskaya.config.AnimalParameters;
import com.javarush.island.matsarskaya.map.Cell;
import com.javarush.island.matsarskaya.map.GameMap;
import com.javarush.island.matsarskaya.organism.Eating;
import com.javarush.island.matsarskaya.organism.Reproduction;
import com.javarush.island.matsarskaya.organism.Walking;
import lombok.Getter;
import lombok.Setter;
import java.util.Random;


@Setter
@Getter
public abstract class Animals implements Animal {
    // Константы для улучшения читаемости
    private static final double MIN_WEIGHT_RATIO_FOR_SURVIVAL = 0.05; // 5% от максимального веса для выживания

    // Координаты
    private int x;
    private int y;

    // Состояние
    protected boolean alive = true;
    protected boolean hasEaten = false; // По умолчанию животное еще не ело

    // Ссылки
    protected GameMap gameMap;

    // Параметры животного
    private AnimalParameters parameters;
    private double maxWeight; // максимальный вес из конфигурации
    private double weightBeforeEating;

    private static final Random random = new Random();

    public void initializeFromConfig(double weight, int speed, double foodRequired,
                                     int maxCountPerCell, int weightLossPerStep) {
        this.parameters = new AnimalParameters(weight, speed, foodRequired, maxCountPerCell, weightLossPerStep);
        this.maxWeight = weight;
    }

    // Делегирование методов к параметрам
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
        // Делегируем выполнение отдельному классу
        Walking walkingTask = new Walking(this);
        walkingTask.run();
    }

    @Override
    public void eating(AnimalConfigService animalConfigService) {
        this.hasEaten = true; // Помечаем, что животное поело
        Eating eatingTask = new Eating(this, animalConfigService);
        eatingTask.run();
    }

    @Override
    public void reproduction() {
        // Делегируем выполнение отдельному классу
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

    /**
     * Метод для потери веса со временем
     * Животные теряют вес каждый такт, если не ели
     */
    @Override
    public void loseWeightOverTime() {
        // Если животное поело, оно не теряет вес в этом такте
        if (this.hasEaten) {
            this.hasEaten = false;
            return;
        }

        // Потеря веса пропорциональна максимальному весу
        // Используем логарифмическую шкалу для плавной зависимости
        double weightFactor = Math.log10(this.maxWeight + 1) / 10;
        // Базовая потеря от 5% до 30% в зависимости от максимального веса
        double baseLossPercent = Math.min(0.5, 0.3 + weightFactor);

        double weightLoss = this.maxWeight * baseLossPercent;
        this.setWeight(this.getWeight() - weightLoss);

        // Более строгая проверка смерти от голода
        if (this.getWeight() < this.maxWeight * MIN_WEIGHT_RATIO_FOR_SURVIVAL) {
            this.alive = false;
            removeAnimalFromCell();
            return;
        }

        this.hasEaten = false;
    }

    /**
     * Удаляет животное из ячейки при смерти
     */
    private void removeAnimalFromCell() {
        Cell currentCell = gameMap.getCell(this.x, this.y);
        if (currentCell != null) {
            synchronized (currentCell) {
                currentCell.removeAnimal(this);
            }
        }
    }
}


