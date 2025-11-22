package com.javarush.island.matsarskaya.organism;

import com.javarush.island.matsarskaya.entity.Animal;
import com.javarush.island.matsarskaya.entity.Animals;
import com.javarush.island.matsarskaya.map.Cell;
import com.javarush.island.matsarskaya.map.GameMap;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Reproduction implements Runnable {
    // Константы для улучшения читаемости
    private static final double DEFAULT_MIN_WEIGHT_FOR_REPRODUCTION = 1.0;
    private static final double LIGHT_ANIMALS_MAX_WEIGHT_THRESHOLD = 5.0;
    private static final double LIGHT_ANIMALS_MIN_WEIGHT_FOR_REPRODUCTION = 0.2;
    private static final int HIGH_DENSITY_REPRODUCTION_CHANCE = 5; // 5% шанс размножения при высокой плотности
    private static final int DEFAULT_REPRODUCTION_CHANCE = 100; // 100% шанс размножения по умолчанию
    private static final int REDUCED_REPRODUCTION_CHANCE = 40; // 40% шанс размножения если не ел
    private static final double PARENT_WEIGHT_LOSS_PERCENT = 0.1; // 10% потеря веса у родителей
    private static final double OFFSPRING_WEIGHT_PERCENT = 0.1; // 10% от веса каждого родителя для потомка
    private static final int POPULATION_MULTIPLIER = 5; // Множитель для ограничения популяции

    private final Animals animal;
    private static final Random random = new Random();

    public Reproduction(Animals animal) {
        this.animal = animal;
    }

    @Override
    public void run() {
        performReproduction();
    }

    /**
     * Основной метод выполнения размножения
     * Синхронизирован на уровне ячейки для предотвращения конфликтов при многопоточном доступе
     */
    private void performReproduction() {
        if (!canReproduce()) return;

        Cell currentCell = animal.getGameMap().getCell(animal.getX(), animal.getY());
        if (currentCell == null) return;

        // Синхронизация размножения
        synchronized (currentCell) {
            if (isPopulationTooDense()) {
                // Снижаем вероятность размножения при высокой плотности
                if (random.nextInt(100) > HIGH_DENSITY_REPRODUCTION_CHANCE) {
                    return;
                }
            }

            // Стандартная логика размножения
            int reproductionChance = calculateReproductionChance();
            if (random.nextInt(100) > reproductionChance) {
                return;
            }

            List<Animal> potentialPartners = findPotentialPartners(currentCell);
            Animal partnerAnimal = potentialPartners.stream()
                    .findFirst()
                    .orElse(null);

            if (partnerAnimal == null) {
                return;
            }
            if (isCellOvercrowded(currentCell)) {
                return;
            }

            Animals partner = (Animals) partnerAnimal;
            createOffspring(partner, currentCell);
        }
    }

    /**
     * Проверяет, может ли животное размножаться
     * @return true если размножение возможно, false если нет
     */
    private boolean canReproduce() {
        return animal.isAlive()
                && animal.getGameMap() != null
                && animal.getWeight() >= getMinWeightForReproduction();
    }

    /**
     * Получает минимальный вес для размножения в зависимости от типа животного
     * @return минимальный вес для размножения
     */
    private double getMinWeightForReproduction() {
        if (animal.getMaxWeight() < LIGHT_ANIMALS_MAX_WEIGHT_THRESHOLD) {
            return LIGHT_ANIMALS_MIN_WEIGHT_FOR_REPRODUCTION;
        }
        return DEFAULT_MIN_WEIGHT_FOR_REPRODUCTION;
    }

    /**
     * Проверяет, перенаселена ли популяция
     * @return true если популяция перенаселена, false если нет
     */
    private boolean isPopulationTooDense() {
        long totalCount = countAnimalsOfType(animal.getClass());
        int maxTotalPopulation = animal.getMaxCountPerCell() * POPULATION_MULTIPLIER;
        return totalCount > maxTotalPopulation;
    }

    /**
     * Вычисляет шанс размножения
     * @return шанс размножения в процентах
     */
    private int calculateReproductionChance() {
        return animal.isHasEaten() ? DEFAULT_REPRODUCTION_CHANCE : REDUCED_REPRODUCTION_CHANCE;
    }

    /**
     * Находит потенциальных партнеров для размножения
     * @param currentCell текущая ячейка
     * @return список потенциальных партнеров
     */
    private List<Animal> findPotentialPartners(Cell currentCell) {
        return currentCell.getAnimalList().stream()
                .filter(a -> a != animal // животное не может размножаться с самим собой
                        && a.getClass() == animal.getClass() // размножение происходит только с особями того же вида
                        && a.isAlive()).collect(Collectors.toList()); // партнер должен быть жив
    }

    /**
     * Проверяет, переполнена ли ячейка животными
     * @param currentCell текущая ячейка
     * @return true если ячейка переполнена, false если нет
     */
    private boolean isCellOvercrowded(Cell currentCell) {
        long currentCount = currentCell.getAnimalList().stream()
                .filter(a -> a.getClass() == animal.getClass())
                .count();
        return currentCount >= animal.getMaxCountPerCell();
    }

    /**
     * Создает потомка от двух родителей
     * @param partner второй родитель
     * @param currentCell текущая ячейка
     */
    private void createOffspring(Animals partner, Cell currentCell) {
        try {
            Animals offspring = animal.getClass().getDeclaredConstructor().newInstance();

            // Устанавливаем начальный вес потомка (сумма 10% от каждого родителя)
            double offspringWeight = animal.getWeight() * OFFSPRING_WEIGHT_PERCENT
                    + partner.getWeight() * OFFSPRING_WEIGHT_PERCENT;

            // Инициализируем параметры потомка через initializeFromConfig
            offspring.initializeFromConfig(
                    offspringWeight,
                    animal.getSpeed(),
                    animal.getFoodRequired(),
                    animal.getMaxCountPerCell(),
                    animal.getWeightLossPerStep()
            );

            offspring.setGameMap(animal.getGameMap());
            offspring.setCoordinates(animal.getX(), animal.getY());
            offspring.setAlive(true);

            // Родители теряют вес (по 10% каждый)
            animal.setWeight(animal.getWeight() * (1 - PARENT_WEIGHT_LOSS_PERCENT));
            partner.setWeight(partner.getWeight() * (1 - PARENT_WEIGHT_LOSS_PERCENT));

            // Добавляем потомка в ячейку
            currentCell.addAnimal(offspring);
        } catch (InstantiationException | IllegalAccessException e) {
            System.err.println("Ошибка при создании потомка: " + e.getMessage());
            return; // Прерываем размножение при ошибке
        } catch (Exception e) {
            System.err.println("Неожиданная ошибка при создании потомка: " + e.getMessage());
        }
    }

    /**
     * Вспомогательный метод для подсчета животных определенного типа
     * @param animalType тип животного
     * @return количество животных этого типа
     */
    private long countAnimalsOfType(Class<? extends Animal> animalType) {
        long count = 0;
        GameMap gameMap = animal.getGameMap();
        if (gameMap == null) return count;

        for (int x = 0; x < gameMap.getHeight(); x++) {
            for (int y = 0; y < gameMap.getWidth(); y++) {
                Cell cell = gameMap.getCell(x, y);
                if (cell != null) {
                    count += cell.getAnimalList().stream()
                            .filter(a -> a.getClass() == animalType && a.isAlive())
                            .count();
                }
            }
        }
        return count;
    }
}

