package com.javarush.island.matsarskaya.entity.organism;

public class AnimalStats {
    private double weight;
    private int maxCountPerCell;
    private int speed;
    private double foodRequired;

    public AnimalStats(double foodRequired, int speed, int maxCountPerCell, double weight) {
        this.foodRequired = foodRequired;
        this.speed = speed;
        this.maxCountPerCell = maxCountPerCell;
        this.weight = weight;
    }
    public AnimalStats(){}


    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getMaxCountPerCell() {
        return maxCountPerCell;
    }

    public void setMaxCountPerCell(int maxCountPerCell) {
        this.maxCountPerCell = maxCountPerCell;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public double getFoodRequired() {
        return foodRequired;
    }

    public void setFoodRequired(double foodRequired) {
        this.foodRequired = foodRequired;
    }






    @Override
    public String toString() {
        return "AnimalStat{" +
                "Weight=" + weight +
                ", MaxCountPerCell=" + maxCountPerCell +
                ", Speed=" + speed +
                ", FoodRequired=" + foodRequired +
                '}';
    }
}
