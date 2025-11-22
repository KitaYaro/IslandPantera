package com.javarush.island.matsarskaya.organism;

import com.javarush.island.matsarskaya.config.AnimalConfigService;
import com.javarush.island.matsarskaya.entity.Animal;
import com.javarush.island.matsarskaya.entity.Animals;
import com.javarush.island.matsarskaya.map.Cell;
import java.util.*;
import java.util.stream.Collectors;

public class Eating implements Runnable {
    private final Animals animal;
    private final AnimalConfigService animalConfigService;
    private static final Random random = new Random();

    public Eating(Animals animal, AnimalConfigService animalConfigService) {
        this.animal = animal;
        this.animalConfigService = animalConfigService;
    }

    @Override
    public void run() {
        performEating();
    }

    /**
     * Основной метод выполнения питания
     * Синхронизирован на уровне ячейки для предотвращения конфликтов при многопоточном доступе
     */
    private void performEating() {
        if (!isEatingPossible()) return;

        Cell currentCell = animal.getGameMap().getCell(animal.getX(), animal.getY());
        if (currentCell == null) return;

        // Синхронизация на уровне ячейки для выбора жертвы и поедания
        synchronized (currentCell) {
            Map<String, Integer> eatingProbabilities = getEatingProbabilities();
            List<Animal> potentialPrey = getPotentialPrey(currentCell);
            List<Grass> availableGrass = currentCell.getGrassList();

            // Проверяем, может ли животное есть растения
            Integer grassProbability = eatingProbabilities.get("grass");
            boolean canEatGrass = grassProbability != null && grassProbability > 0;

            // Выбираем подходящую пищу в зависимости от типа животного
            if (canEatGrass && !availableGrass.isEmpty()) {
                tryEatGrass(availableGrass, grassProbability, currentCell);
            } else if (!potentialPrey.isEmpty()) {
                tryEatPrey(potentialPrey, eatingProbabilities, currentCell);
            }
        }
    }

    /**
     * Проверяет, возможно ли питание для данного животного
     * @return true если питание возможно, false если нет
     */
    private boolean isEatingPossible() {
        return animal.isAlive()
                && animal.getGameMap() != null;
    }

    /**
     * Получает вероятности поедания различных организмов из конфигурации
     * @return Map с вероятностями поедания
     */
    private Map<String, Integer> getEatingProbabilities() {
        String animalType = animal.getClass().getSimpleName().toLowerCase();
        return Optional.ofNullable(animalConfigService)
                .map(service -> service.getEatingProbabilities(animalType))
                .orElse(Collections.emptyMap());
    }

    /**
     * Получает список потенциальных жертв в текущей ячейке
     * @param currentCell текущая ячейка
     * @return список животных, которые могут быть съедены
     */
    private List<Animal> getPotentialPrey(Cell currentCell) {
        return currentCell.getAnimalList().stream()
                .filter(Objects::nonNull)
                .filter(prey -> prey != animal
                        && prey.isAlive()
                        && !prey.getClass().equals(animal.getClass()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Пытается поесть траву
     * @param availableGrass список доступной травы
     * @param grassProbability вероятность поедания травы
     * @param currentCell текущая ячейка
     */
    private void tryEatGrass(List<Grass> availableGrass, Integer grassProbability, Cell currentCell) {
        if (random.nextInt(100) < grassProbability) {
            // Поедаем одно растение
            availableGrass.stream()
                    .findFirst()
                    .ifPresent(grass -> {
                        double grassWeight = grass.getNutritionalValue();
                        animal.setWeight(animal.getWeight() + grassWeight);
                        animal.setHasEaten(true);
                        currentCell.removeGrass(grass);
                    });
        }
    }

    /**
     * Пытается поесть жертву
     * @param potentialPrey список потенциальных жертв
     * @param eatingProbabilities вероятности поедания разных типов жертв
     * @param currentCell текущая ячейка
     */
    private void tryEatPrey(List<Animal> potentialPrey, Map<String, Integer> eatingProbabilities, Cell currentCell) {
        Animal prey = potentialPrey.get(random.nextInt(potentialPrey.size()));
        String preyType = prey.getClass().getSimpleName().toLowerCase();
        Integer probability = eatingProbabilities.get(preyType);

        if (probability != null && random.nextInt(100) < probability) {
            // Реальное поедание
            double preyWeight = prey instanceof Animals ? ((Animals) prey).getWeight() : 1.0;
            animal.setWeight(animal.getWeight() + preyWeight);
            animal.setHasEaten(true);

            // Удаляем жертву из ячейки и помечаем как мертвую
            currentCell.removeAnimal(prey);
            if (prey instanceof Animals) {
                ((Animals) prey).setAlive(false);
            }
        }
    }
}
