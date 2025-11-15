package com.javarush.island.matsarskaya.entity;

import com.javarush.island.matsarskaya.config.AnimalConfigService;
import com.javarush.island.matsarskaya.map.Cell;
import com.javarush.island.matsarskaya.map.GameMap;
import com.javarush.island.matsarskaya.organism.Eating;
import lombok.Getter;
import lombok.Setter;

import java.util.Random;

@Setter
@Getter
public abstract class Animals implements Animal {
    private int x;
    private int y;
    protected boolean alive = true;
    protected GameMap gameMap;
    protected double weight;
    protected int speed;
    protected double foodRequired;
    protected int maxCountPerCell;
    protected int weightLossPerStep = 20;
    protected boolean hasEaten = false;  // По умолчанию животное еще не ело
    private double weightBeforeEating;

    public void initializeFromConfig(double weight, int speed, double foodRequired, int maxCountPerCell, int weightLossPerStep) {
        this.weight = weight;
        this.speed = speed;
        this.foodRequired = foodRequired;
        this.maxCountPerCell = maxCountPerCell;
        this.weightLossPerStep = weightLossPerStep;

    }

    public void setCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void walking() {
        if (!isAlive() || gameMap == null) return;

        int direction = new Random().nextInt(4);
        int newX = this.x;
        int newY = this.y;

        switch (direction) {
            case 0:
                newX--;
                break;
            case 1:
                newX++;
                break;
            case 2:
                newY--;
                break;
            case 3:
                newY++;
                break;
        }

        if (gameMap.isValidPosition(newX, newY)) {
            Cell currentCell = gameMap.getCell(this.x, this.y);
            Cell newCell = gameMap.getCell(newX, newY);

            if (currentCell != null && newCell != null) {
                currentCell.removeAnimal(this);
                newCell.addAnimal(this);
                this.x = newX;
                this.y = newY;
            }
        }
    }

    @Override
    public void eating(AnimalConfigService animalConfigService) {
        this.hasEaten = true; // Помечаем, что животное поело
        Eating eatingTask = new Eating(this, animalConfigService);
        eatingTask.run();
    }
    @Override
    public void reproduction () {
    }

    @Override
    public boolean isAlive () {
        return alive;
    }

    public void prepareForEatingCheck() {
        this.weightBeforeEating = this.weight;
    }
    //Метод для потери веса
    public void loseWeightOverTime() {

            if (isAlive()  && this.weight == this.weightBeforeEating) { // Теряем вес только если не ели
                this.weight -= this.weightLossPerStep;
                System.out.println(this.getClass().getSimpleName() + " lost " + this.weightLossPerStep + " weight from starvation. Current weight: " + String.format("%.2f", this.weight));

                // Проверяем смерть от голода
                if (this.weight <= 0) {
                    this.alive = false;
                    System.out.println(this.getClass().getSimpleName() + " died from starvation");
                    // Удаляем мертвое животное с карты
                    Cell currentCell = gameMap.getCell(this.x, this.y);
                    if (currentCell != null) {
                        currentCell.removeAnimal(this);
                    }
                }
            }
        // Сбрасываем флаг для следующего такта
        this.hasEaten = false;
        }
    //метод для отображения статистики в класс Animals
    public void printStatus() {
        System.out.println(this.getClass().getSimpleName() +
                " at (" + this.x + "," + this.y + ")" +
                " Weight: " + String.format("%.2f", this.weight) +
                " Alive: " + this.alive +
                " HasEaten: " + this.hasEaten);
    }
}
