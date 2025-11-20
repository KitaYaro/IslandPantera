package com.javarush.island.matsarskaya.entity;

import com.javarush.island.matsarskaya.config.AnimalConfigService;
import com.javarush.island.matsarskaya.map.Cell;
import com.javarush.island.matsarskaya.map.GameMap;
import com.javarush.island.matsarskaya.organism.Eating;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
    private static boolean showDetailedStats = true; // Можно изменить на false для уменьшения вывода
    private static final Random random = new Random();


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

        // Проверяем, что животное может двигаться (speed > 0)
        if (speed <= 0) return;
        int direction = random.nextInt(4);
        int newX = this.x;
        int newY = this.y;
        // Убедимся, что speed > 0 перед вызовом nextInt
        int moveDistance = (speed > 0) ? random.nextInt(speed) + 1 : 0;

        switch (direction) {
            case 0 -> newX = Math.max(0, this.x - moveDistance); // Вверх
            case 1 -> newX = Math.min(gameMap.getHeight() - 1, this.x + moveDistance); // Вниз
            case 2 -> newY = Math.max(0, this.y - moveDistance); // Влево
            case 3 -> newY = Math.min(gameMap.getWidth() - 1, this.y + moveDistance); // Вправо
        }

        if (newX != this.x || newY != this.y) {
            if (gameMap.isValidPosition(newX, newY)) {
                Cell currentCell = gameMap.getCell(this.x, this.y);
                Cell targetCell = gameMap.getCell(newX, newY);

                // Синхронизация при перемещении между ячейками
                if (currentCell != null && targetCell != null) {
                    synchronized (currentCell) {
                        synchronized (targetCell) {
                            // Проверяем, не превышено ли максимальное количество животных в целевой ячейке
                            if (targetCell.getAnimalList().size() < this.maxCountPerCell) {
                                currentCell.removeAnimal(this);
                                targetCell.addAnimal(this);
                                this.x = newX;
                                this.y = newY;
                            }
                        }
                    }
                }
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
    public void reproduction() {
        if (!isAlive() || gameMap == null) return;

        // Добавим проверку минимального веса для размножения
        if (this.weight < 1.0) { // Минимальный вес для размножения 1.0 кг
            return;
        }

        Cell currentCell = gameMap.getCell(this.x, this.y);
        if (currentCell == null) return;

        // Синхронизация размножения
        synchronized (currentCell) {
            // Добавляем вероятность размножения, например 30%
            if (new Random().nextInt(100) > 30) {
                return; // 70% шанс, что размножение не произойдет
            }

            // Собираем всех живых особей того же вида в этой ячейке
            List<Animal> potentialPartners = currentCell.getAnimalList().stream()
                    .filter(animal -> animal != this && //животное не может размножаться с самим собой
                            animal.getClass() == this.getClass() && // размножение происходит только с особями того же вида
                            animal.isAlive() && // партнер должен быть жив
                            animal instanceof Animals) // партнер должен быть животным
                    .collect(Collectors.toList());

            // Проверяем, достаточно ли животных для размножения (минимум 1 партнер)
            if (potentialPartners.isEmpty()) {
                return;
            }

            // Проверяем, не превышено ли максимальное количество животных в ячейке
            long currentCount = currentCell.getAnimalList().stream()
                    .filter(animal -> animal.getClass() == this.getClass())
                    .count();

            if (currentCount >= this.maxCountPerCell) {
//                System.out.println(this.getClass().getSimpleName() + " не может размножаться - достигнут лимит в ячейке");
                return;
            }

            // Выбираем первого партнера
            Animals partner = (Animals) potentialPartners.get(0);

            // Создаем потомка
            try {
                Animals offspring = this.getClass().getDeclaredConstructor().newInstance();

                // Устанавливаем начальный вес потомка (сумма 10% от каждого родителя)
                double offspringWeight = this.weight * 0.1 + partner.getWeight() * 0.1;
                offspring.setWeight(offspringWeight);
                offspring.setSpeed(this.speed);
                offspring.setFoodRequired(this.foodRequired);
                offspring.setMaxCountPerCell(this.maxCountPerCell);
                offspring.setWeightLossPerStep(this.weightLossPerStep);
                offspring.setGameMap(this.gameMap);
                offspring.setCoordinates(this.x, this.y);
                offspring.setAlive(true);

                // Родители теряют вес (по 10% каждый)
                this.weight *= 0.9; // теряет 10%
                partner.setWeight(partner.getWeight() * 0.9); // теряет 10%

                // Добавляем потомка в ячейку
                currentCell.addAnimal(offspring);

//                System.out.println(this.getClass().getSimpleName() + " размножился! Появился потомок с весом: " +
//                        String.format("%.2f", offspring.getWeight()));
            } catch (InstantiationException e) {
                System.err.println("Ошибка при создании потомка (InstantiationException): " + e.getMessage());
                return; // Прерываем размножение при ошибке
            } catch (IllegalAccessException e) {
                System.err.println("Ошибка при создании потомка (IllegalAccessException): " + e.getMessage());
                return; // Прерываем размножение при ошибке
            } catch (Exception e) {
                System.err.println("Ошибка при создании потомка: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    public void prepareForEatingCheck() {
        this.weightBeforeEating = this.weight;
    }

    //Метод для потери веса
    public void loseWeightOverTime() {

        double weightLossPercent = 0.01 + (0.04 * weight / (weight + 10)); // Адаптивный процент
        double actualWeightLoss = this.weight * weightLossPercent;

        // Минимальная потеря веса для очень маленьких животных
        actualWeightLoss = Math.max(0.001, actualWeightLoss);

        this.weight -= actualWeightLoss;

        // Выводим сообщение только для животных с весом > 1 или при смерти
//        if (this.weight < 1.0 || actualWeightLoss > 0.1) {
//            System.out.println(this.getClass().getSimpleName() + " потерял " + String.format("%.4f", actualWeightLoss) + " вес. Текущий вес: " + String.format("%.2f", this.weight));
//        }
        // Проверяем смерть от голода
        if (this.weight <= 0) {
            this.alive = false;
//            System.out.println(this.getClass().getSimpleName() + " умер от голода");
            // Удаляем мертвое животное с карты
            Cell currentCell = gameMap.getCell(this.x, this.y);
            if (currentCell != null) {
                currentCell.removeAnimal(this);
            }
        }
        // Сбрасываем флаг для следующего такта
        this.hasEaten = false;
    }

    //метод для отображения статистики в класс Animals
    public void printStatus() {
//        if (showDetailedStats) {
//            System.out.println(this.getClass().getSimpleName() +
//                    " at (" + (this.x + 1) + "," + (this.y + 1) + ")" + // Сдвигаем на 1 для пользователя
//                    " Weight: " + String.format("%.2f", this.weight) +
//                    " Alive: " + this.alive +
//                    " HasEaten: " + this.hasEaten);
//        }
    }
}
