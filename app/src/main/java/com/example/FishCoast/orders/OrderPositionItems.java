package com.example.FishCoast.orders;

public class OrderPositionItems {

    private String name;
    private Double cost;
    private int unit;
    private Double quantity;

    public OrderPositionItems(String name, Double cost, int unit, Double quantity) {
        this.name = name;
        this.cost = cost;
        this.unit = unit;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
}
