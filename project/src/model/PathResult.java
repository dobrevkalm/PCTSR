package model;

import java.util.ArrayList;
import java.util.List;

public class PathResult {
    private List<Place> resultPath;
    private double pathLength;
    private double actualProfit;
    private int actualPlaceID;
    private double[][] neighborsMatrix;

    //second solution
    private double previousMinLength;
    private List<Place> previousMinPlaces;
    private double previousMaxProfit;

    public PathResult(double[][] neighborsMatrix) {
        this.neighborsMatrix = neighborsMatrix;
        this.resultPath = new ArrayList<>();
        this.pathLength = 0;
        this.actualProfit = 0;
        this.actualPlaceID = 0;
        this.previousMinPlaces = new ArrayList<>();

    }

    public boolean opt2() {
        double minus, plus;
        int n = this.resultPath.size();
        List<Place> newPlaces;
        for(int i = 0; i < n-2; i++) {
            for(int j = i+2; j < n-1; j++) {
                minus = neighborsMatrix[resultPath.get(i).getId()][resultPath.get(i+1).getId()] + neighborsMatrix[resultPath.get(j).getId()][resultPath.get(j+1).getId()];
                plus = neighborsMatrix[resultPath.get(i).getId()][resultPath.get(j).getId()] + neighborsMatrix[resultPath.get(i+1).getId()][resultPath.get(j+1).getId()];
                if (plus < minus) {
                    newPlaces = new ArrayList<>();
                    for (int k = 0; k <= i; k++) {
                        newPlaces.add(resultPath.get(k));
                    }
                    for( int k = j; k >= i+1; k--) {
                        newPlaces.add(resultPath.get(k));
                    }
                    for (int k = j+1; k <= n-1; k++) {
                        newPlaces.add(resultPath.get(k));
                    }
                    this.resultPath = newPlaces;
                    this.pathLength += plus - minus;
                    return true;
                }
            }
        }
        return false;
    }

    public void realData() {
        double length = 0;
        double profit = 0;
        for (int i = 0; i < resultPath.size(); i++) {
            if (i < resultPath.size()-1) {
                length += neighborsMatrix[resultPath.get(i).getId()][resultPath.get(i+1).getId()];
            }
            profit += resultPath.get(i).getFirmProfit();
        }
    }

    public List<Place> getResultPath() {
        return resultPath;
    }

    public void setResultPath(List<Place> resultPath) {
        this.resultPath = resultPath;
    }

    public int getActualPlaceID() {
        return actualPlaceID;
    }

    public void setActualPlaceID(int actualPlaceID) {
        this.actualPlaceID = actualPlaceID;
    }

    public void increaseActualProfit(double amountToIncrease) {
        this.actualProfit += amountToIncrease;
    }

    public void increasePathLength(double distanceAdded) {
        this.pathLength += distanceAdded;
    }

    public double getPathLength() {
        return pathLength;
    }

    public void setPathLength(double pathLength) {
        this.pathLength = pathLength;
    }

    public double getActualProfit() {
        return actualProfit;
    }

    public void setActualProfit(double actualProfit) {
        this.actualProfit = actualProfit;
    }

    public List<Place> getPreviousMinPlaces() {
        return previousMinPlaces;
    }

    public void setPreviousMinPlaces(List<Place> previousMinPlaces) {
        this.previousMinPlaces = previousMinPlaces;
    }

    public double getPreviousMaxProfit() {
        return previousMaxProfit;
    }

    public void setPreviousMaxProfit(double previousMaxProfit) {
        this.previousMaxProfit = previousMaxProfit;
    }

    public double getPreviousMinLength() {
        return previousMinLength;
    }

    public void setPreviousMinLength(double previousMinLength) {
        this.previousMinLength = previousMinLength;
    }
}