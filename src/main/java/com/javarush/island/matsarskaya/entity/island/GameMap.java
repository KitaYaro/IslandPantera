package com.javarush.island.matsarskaya.entity.island;

import com.javarush.island.matsarskaya.config.ConfigData;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Setter
@Getter
public class GameMap {
    private MapCell[][] map;
    private static final int MIN_WIDTH = 4;
    private static final int MIN_HEIGHT = 4;
    private static final int MAX_WIDTH = 100;
    private static final int MAX_HEIGHT = 20;
    private int width;
    private int height;

    public GameMap(int width, int height) {
        if (width < MIN_WIDTH || height < MIN_HEIGHT) {
            throw new IllegalArgumentException(
                    "Map size cannot be less than " + MIN_WIDTH + MIN_HEIGHT);
        }
        if (width > MAX_WIDTH || height > MAX_HEIGHT) {
            throw new IllegalArgumentException(
                    "Map size cannot exceed " + MAX_WIDTH + MAX_HEIGHT);
        }
        this.width = width;
        this.height = height;
        map = new MapCell[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                map[i][j] = new MapCell();
            }
        }
    }

    //позволяет получить доступ к конкретной ячейке по координатам,
    // что необходимо для размещения и перемещения животных.
    public MapCell getCell(int x, int y) {
        if (!isValidPosition(x, y)) {
            throw new IllegalArgumentException("Coordinates out of bounds: x=" + x + ", y=" + y);
        }
        return map[y][x];
    }

    //проверка, находится ли позиция в пределах карты
    public boolean isValidPosition(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        }
        return true;
    }

    //Возвращает список доступных соседних клеток
    public List<MapCell> getNeighborsCells(int x, int y) {
        List<MapCell> neighbors = new ArrayList<>();
        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};

        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            if (isValidPosition(newX, newY)) {
                neighbors.add(getCell(newX, newY));
            }
        }
        return neighbors;
    }

    //Метод для размещения начальных животных и растений на карте
    public void initializeMap(ConfigData config) {
        Random random = new Random();
        // Добавляем траву в случайные ячейки
        for (int i = 0; i < 100; i++) { // 100 раз добавляем траву
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            MapCell cell = getCell(x, y);
            cell.addGrass();
        }
    }
}
