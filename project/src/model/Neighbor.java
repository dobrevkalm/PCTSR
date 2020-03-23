package model;

public class Neighbor {
    private int id;
    private double distance;
    private double profit;
    private double heuristic;

    public Neighbor(int id, double distance, double profit) {
        this.id = id;
        this.distance = distance;
        this.profit = profit;
        if (profit != 0) this.heuristic = (distance*2)/profit;
    }

    // GETTERS

    public int getId() {
        return id;
    }

    public double getDistance() {
        return distance;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public double getHeuristic() {
        return heuristic;
    }

}