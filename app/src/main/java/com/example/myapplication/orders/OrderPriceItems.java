package com.example.myapplication.orders;


class OrderPriceItems {

    private String name;
    private Double cost;
    private int unit;

    public OrderPriceItems(String name, Double cost, int unit) {
        this.name = name;
        this.cost = cost;
        this.unit = unit;
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

}
