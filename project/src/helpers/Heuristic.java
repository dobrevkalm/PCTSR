package helpers;

import model.PathResult;
import model.Place;

public class Heuristic {
    protected Place[] places;
    protected int startVertex;
    protected int agentsNumber;
    protected int minProfit;
    protected PathResult[] pathResult;
    protected double[][] distanceMatrix;
    protected boolean[] visited;
    protected double sumProfit = 0.0;

    public Heuristic(double[][] distanceMatrix, Place[] places, int startVertex, int agentsNumber, int minProfit) {
        this.distanceMatrix = distanceMatrix;
        this.places = places;
        this.startVertex = startVertex;
        this.agentsNumber = agentsNumber;
        this.minProfit = minProfit;
        this.visited = new boolean[places.length];
        this.pathResult = new PathResult[this.agentsNumber];
    }
}
