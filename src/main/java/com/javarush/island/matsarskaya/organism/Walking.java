package com.javarush.island.matsarskaya.organism;

import com.javarush.island.matsarskaya.entity.Animal;
import com.javarush.island.matsarskaya.entity.Animals;
import com.javarush.island.matsarskaya.map.Cell;
import com.javarush.island.matsarskaya.map.GameMap;

import java.util.Random;

public class Walking implements Runnable {
    private static final int DIRECTIONS_COUNT = 4; // Количество возможных направлений движения
    private static final int MIN_COORDINATE = 0;   // Минимальная координата
    private static final int UP = 0;    // Направление вверх
    private static final int DOWN = 1;  // Направление вниз
    private static final int LEFT = 2;  // Направление влево
    private static final int RIGHT = 3; // Направление вправо

    private final Animals animal;
    private static final Random random = new Random();

    public Walking(Animals animal) {
        this.animal = animal;
    }

    @Override
    public void run() {
        performWalking();
    }

    /**
     * Основной метод выполнения перемещения
     * Синхронизирован на уровне ячеек для предотвращения конфликтов при многопоточном доступе
     */
    private void performWalking() {
        if (!canMove()) return;

        // Вычисляем параметры движения
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

        // Проверяем, нужно ли выполнять перемещение
        if ((newX != animal.getX() || newY != animal.getY())
                && animal.getGameMap().isValidPosition(newX, newY)) {
            executeMovement(newX, newY);
        }
    }

    /**
     * Проверяет, может ли животное двигаться
     * @return true если движение возможно, false если нет
     */
    private boolean canMove() {
        return animal.isAlive()
                && animal.getGameMap() != null
                && animal.getSpeed() > 0;
    }

    /**
     * Выполняет фактическое перемещение животного
     * @param newX новая координата X
     * @param newY новая координата Y
     */
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


