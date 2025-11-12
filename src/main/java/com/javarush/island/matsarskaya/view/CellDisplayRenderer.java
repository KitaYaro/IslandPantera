package com.javarush.island.matsarskaya.view;

import com.javarush.island.matsarskaya.entity.Animal;
import com.javarush.island.matsarskaya.entity.island.MapCell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CellDisplayRenderer {
    private static final int DISPLAY_SIZE = 2;

    //Основной метод для создания визуального представления ячейки
    public IconAnimal[][] renderCellDisplay(MapCell cell) {
        // Создаем пустую сетку 2x2 для отображения
        IconAnimal[][] cellDisplay = new IconAnimal[DISPLAY_SIZE][DISPLAY_SIZE];
        // Заполняем всю сетку пустыми иконками по умолчанию
        for (int i = 0; i < DISPLAY_SIZE; i++) {
            for (int j = 0; j < DISPLAY_SIZE; j++) {
                cellDisplay[i][j] = IconAnimal.EMPTY;
            }
        }
        // Заполняем сетку реальными иконками на основе содержимого ячейки
        fillDisplayGrid(cellDisplay, cell);
        // Возвращаем готовую сетку для отображения
        return cellDisplay;
    }

    //Метод для заполнения сетки иконками
    private void fillDisplayGrid(IconAnimal[][] cellDisplay, MapCell cell) {

        // Подсчитываем количество животных по типам
        Map<Class<? extends Animal>, Integer> animalCounts = analyzeCellContents(cell);
        // Сортируем типы животных по количеству (по убыванию)
        List<Map.Entry<Class<? extends Animal>, Integer>> sortedAnimals =
                new ArrayList<>(animalCounts.entrySet());
        sortedAnimals.sort(Map.Entry.<Class<? extends Animal>, Integer>comparingByValue().reversed());
        // Определяем, какие иконки показывать
        List<IconAnimal> iconsToDisplay = new ArrayList<>();
        // Добавляем иконки животных (максимум 3, чтобы оставить место для растений)
        int animalIconsAdded = 0;
        for (Map.Entry<Class<? extends Animal>, Integer> entry : sortedAnimals) {
            if (animalIconsAdded < 3) {
                IconAnimal icon = getIconForAnimal(entry.getKey());
                if (icon != IconAnimal.EMPTY) {
                    iconsToDisplay.add(icon);
                    animalIconsAdded++;
                }
            } else {
                break;
            }
        }
        // Добавляем иконку растений, если они есть
        if (cell.getGrassCount() > 0) {
            iconsToDisplay.add(IconAnimal.PLANT);
        }
        // Заполняем сетку 2x2 иконками
        int iconIndex = 0;
        for (int i = 0; i < DISPLAY_SIZE; i++) {
            for (int j = 0; j < DISPLAY_SIZE; j++) {
                if (iconIndex < iconsToDisplay.size()) {
                    cellDisplay[i][j] = iconsToDisplay.get(iconIndex);
                    iconIndex++;
                } else {
                    cellDisplay[i][j] = IconAnimal.EMPTY;
                }
            }
        }
    }

    private IconAnimal getIconForAnimal(Class<? extends Animal> animalClass) {

        // Сопоставляем классы животных с иконками
        String className = animalClass.getSimpleName().toLowerCase();

        return switch (className) {
            case "wolf" -> IconAnimal.WOLF;
            case "boa" -> IconAnimal.BOA;
            case "fox" -> IconAnimal.FOX;
            case "bear" -> IconAnimal.BEAR;
            case "eagle" -> IconAnimal.EAGLE;
            case "horse" -> IconAnimal.HORSE;
            case "deer" -> IconAnimal.DEER;
            case "rabbit" -> IconAnimal.RABBIT;
            case "mouse" -> IconAnimal.MOUSE;
            case "goat" -> IconAnimal.GOAT;
            case "sheep" -> IconAnimal.SHEEP;
            case "boar" -> IconAnimal.BOAR;
            case "buffalo" -> IconAnimal.BUFFALO;
            case "duck" -> IconAnimal.DUCK;
            case "caterpillar" -> IconAnimal.CATERPILLAR;
            case "plant" -> IconAnimal.PLANT;
            default -> IconAnimal.EMPTY;
        };
    }

    //логика подсчета животных по типам
    private Map<Class<? extends Animal>, Integer> analyzeCellContents(MapCell cell) {
        Map<Class<? extends Animal>, Integer> animalCounts = new HashMap<>();
        for (Animal animal : cell.getAnimals()) {
            Class<? extends Animal> animalClass = animal.getClass();
            animalCounts.put(animalClass, animalCounts.getOrDefault(animalClass, 0) + 1);
        }
        return animalCounts;
    }
}

