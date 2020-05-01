package heuristics;

import model.PathResult;
import model.Place;

public abstract class Heuristic {
    Place[] places;
    int startVertex;
    int agentsNumber;
    int minProfit;
    PathResult[] pathResult;
    double[][] distanceMatrix;
    boolean[] visited;
    double sumProfit = 0.0;

    Heuristic(double[][] distanceMatrix, Place[] places, int startVertex, int agentsNumber, int minProfit) {
        this.distanceMatrix = distanceMatrix;
        this.places = places;
        this.startVertex = startVertex;
        this.agentsNumber = agentsNumber;
        this.minProfit = minProfit;
        this.visited = new boolean[places.length];
        this.pathResult = new PathResult[this.agentsNumber];
    }

    public abstract PathResult[] getResultPaths();

    public double getSumProfit() {
        return sumProfit;
    }

    public String getMethodName() {
        String name = this.getClass().getName();
        return name.substring(name.indexOf('.') + 1);
    }
}
