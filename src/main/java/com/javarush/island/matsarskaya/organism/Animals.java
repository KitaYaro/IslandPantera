package com.javarush.island.matsarskaya.organism;

import com.javarush.island.matsarskaya.map.Cell;
import com.javarush.island.matsarskaya.map.GameMap;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class Animals implements Animal{
    private int x;
    private int y;
    protected boolean alive = true;
    protected GameMap gameMap;

    public void setCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }
    @Override
    public void walking() {
        if(isAlive()) {
            int direction = (int) (Math.random() * 4);
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
                // Получаем текущую и новую ячейки
                Cell currentCell = gameMap.getCell(this.x, this.y);
                Cell newCell = gameMap.getCell(newX, newY);

                // Перемещаем животное из текущей ячейки в новую
                if (currentCell != null && newCell != null) {
                    currentCell.removeAnimal(this);
                    newCell.addAnimal(this);

                    // Обновляем координаты животного
                    this.x = newX;
                    this.y = newY;
                }
            }
        }
    }

    @Override
    public void eating() {
    }

    @Override
    public void reproduction() {
    }

    @Override
    public boolean isAlive() {
        return alive;
    }
}
