package com.javarush.island.matsarskaya.organism.actions;

import com.javarush.island.matsarskaya.entity.Animals;
import com.javarush.island.matsarskaya.map.Cell;
import com.javarush.island.matsarskaya.map.GameMap;

import java.util.Random;

public class Walking implements Runnable {
    private static final int DIRECTIONS_COUNT = 4;
    private static final int MIN_COORDINATE = 0;
    private static final int UP = 0;
    private static final int DOWN = 1;
    private static final int LEFT = 2;
    private static final int RIGHT = 3;

    private final Animals animal;
    private static final Random random = new Random();

    public Walking(Animals animal) {
        this.animal = animal;
    }

    @Override
    public void run() {
        performWalking();
    }

    private void performWalking() {
        if (!canMove()) return;
        int direction = random.nextInt(DIRECTIONS_COUNT);
        int moveDistance = random.nextInt(animal.getSpeed()) + 1;

        int newX = animal.getX();
        int newY = animal.getY();

        switch (direction) {
            case UP -> newX = Math.max(MIN_COORDINATE, animal.getX() - moveDistance);
            case DOWN -> newX = Math.min(animal.getGameMap().getHeight() - 1, animal.getX() + moveDistance);
            case LEFT -> newY = Math.max(MIN_COORDINATE, animal.getY() - moveDistance);
            case RIGHT -> newY = Math.min(animal.getGameMap().getWidth() - 1, animal.getY() + moveDistance);
        }

        if ((newX != animal.getX() || newY != animal.getY())
                && animal.getGameMap().isValidPosition(newX, newY)) {
            executeMovement(newX, newY);
        }
    }

    private boolean canMove() {
        return animal.isAlive()
                && animal.getGameMap() != null
                && animal.getSpeed() > 0;
    }

    private void executeMovement(int newX, int newY) {
        GameMap gameMap = animal.getGameMap();
        Cell currentCell = gameMap.getCell(animal.getX(), animal.getY());
        Cell targetCell = gameMap.getCell(newX, newY);

        if (currentCell != null && targetCell != null) {
            Cell firstCell = currentCell.hashCode() < targetCell.hashCode() ? currentCell : targetCell;
            Cell secondCell = currentCell.hashCode() < targetCell.hashCode() ? targetCell : currentCell;

            synchronized (firstCell) {
                synchronized (secondCell) {
                    if (targetCell.getAnimalList().size() < animal.getMaxCountPerCell()) {
                        currentCell.removeAnimal(animal);
                        targetCell.addAnimal(animal);
                        animal.setX(newX);
                        animal.setY(newY);
                    }
                }
            }
        }
    }
}


