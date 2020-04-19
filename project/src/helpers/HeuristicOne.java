package helpers;

import model.Neighbor;
import model.PathResult;
import model.Place;

import java.util.ArrayList;
import java.util.List;

public class HeuristicOne extends Heuristic {
    private int minHeuristicId = 0;
    private int availablePlaces = 0;
    private double distance = 0.0;
    private double minHeuristic = Double.MAX_VALUE;
    private List<ArrayList<Neighbor>> neighborList;
    private double coefficient = Double.MIN_VALUE;
    private boolean isDistanceCoeff;

    public HeuristicOne(double[][] distanceMatrix, Place[] places, int startVertex, int agentsNumber, int minProfit) {
        super(distanceMatrix, places, startVertex, agentsNumber, minProfit);
        this.neighborList = initializeNeighborList();
    }

    // this constructor will be used for experiments
    // the double[] multipliers is used in the Neighbor class to set the ratio between distance and profit
    public HeuristicOne(double[][] distanceMatrix, Place[] places, int startVertex, int agentsNumber, int minProfit, double coefficient, boolean isDistanceCoeff) {
        super(distanceMatrix, places, startVertex, agentsNumber, minProfit);
        this.coefficient = coefficient;
        this.neighborList = initializeNeighborList();
        this.isDistanceCoeff = isDistanceCoeff;
    }

    private List<ArrayList<Neighbor>> initializeNeighborList() {
        List<ArrayList<Neighbor>> neighborList = new ArrayList<>(places.length);
        for (Place place : places) {
            neighborList.add(place.getId(), new ArrayList<>());
            for (Place placeNeighbor : places) {
                if (place.getId() != placeNeighbor.getId()) { //don't add itself
                    double distance = distanceMatrix[place.getId()][placeNeighbor.getId()];

                    if (place.getFirmProfit() != 0 || place.getId() == this.startVertex) {
                        if (this.coefficient == Double.MIN_VALUE) {
                            neighborList.get(place.getId()).add(new Neighbor(placeNeighbor.getId(), distance, placeNeighbor.getFirmProfit()));
                        } else {
                            // if we have the multipliers we set the coefficient
                            Neighbor neighbor = new Neighbor(placeNeighbor.getId(), distance, placeNeighbor.getFirmProfit());
                            // multipliers 0 is the distance multiplier and 1 is the profit multiplier
                            neighbor.setHeuristicCoefficient(coefficient, isDistanceCoeff);
                            neighborList.get(place.getId()).add(neighbor);
                        }
                    }
                }
            }
        }
        return neighborList;
    }

    public PathResult[] getResultPaths() {
        initializeAgentsStartingVertex();

        //set the starting vertex as visited
        visited[startVertex] = true;

        // while currently collected profit is smaller than required minimum profit to collect
        while (sumProfit < minProfit) {
            for (int i = 0; i < agentsNumber; i++) {
                for (Neighbor neighbor : neighborList.get(pathResult[i].getActualPlaceID())) {
                    // find a not visited neighbor with the smallest coefficient ((distance*2)/profit)
                    findNonVisitedVertex(neighbor);
                }

                if (availablePlaces == 0) break;
                availablePlaces = 0;
                updatePathResult(i);

                //set added vertex as visited
                visited[minHeuristicId] = true;
                minHeuristic = Double.MAX_VALUE;

                // update the sum of profits and check if it's enough
                if (updateSumProfit()) {
                    break;
                }
            }
        }

        //connect generated path with the starting vertex to create a cycle and try to shorten the route with opt2
        connectAndShortenGeneratedPath();

        return pathResult;
    }

    private void connectAndShortenGeneratedPath() {
        for (int i = 0; i < agentsNumber; i++) {
            pathResult[i].getResultPath().add(places[startVertex]);
            pathResult[i].increasePathLength(distanceMatrix[startVertex][pathResult[i].getActualPlaceID()]);
            boolean useOpt2 = true;
            while (useOpt2) {
                useOpt2 = pathResult[i].opt2();
            }
        }
    }

    private boolean updateSumProfit() {
        sumProfit += places[minHeuristicId].getFirmProfit();
        return sumProfit >= minProfit;
    }

    private void updatePathResult(int agentIndex) {
        pathResult[agentIndex].setActualPlaceID(minHeuristicId);
        pathResult[agentIndex].increasePathLength(distance);
        pathResult[agentIndex].getResultPath().add(places[minHeuristicId]);
        pathResult[agentIndex].increaseActualProfit(places[minHeuristicId].getFirmProfit());
    }

    private void findNonVisitedVertex(Neighbor neighbor) {
        if (neighbor.getProfit() != 0 && !visited[neighbor.getId()] && neighbor.getHeuristic() < minHeuristic) {
            minHeuristic = neighbor.getHeuristic();
            minHeuristicId = neighbor.getId();
            distance = neighbor.getDistance();
            availablePlaces++;
        }
    }

    private void initializeAgentsStartingVertex() {
        // add the starting vertex for all the agents
        for (int i = 0; i < agentsNumber; i++) {
            pathResult[i] = new PathResult(distanceMatrix);
            pathResult[i].getResultPath().add(places[startVertex]);
            pathResult[i].setActualPlaceID(startVertex);
        }
    }
}