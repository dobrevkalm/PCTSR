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

    public HeuristicOne(double[][] distanceMatrix, Place[] places, int startVertex, int agentsNumber, int minProfit) {
        super(distanceMatrix, places, startVertex, agentsNumber, minProfit);
        this.neighborList = initializeNeighborList();
    }

    private List<ArrayList<Neighbor>> initializeNeighborList() {
        List<ArrayList<Neighbor>> neighborList = new ArrayList<>(places.length);
        for (Place place : places) {
            neighborList.add(place.getId(), new ArrayList<>());
            //neighborList.set(place.getId(), new ArrayList<>());
            for (Place placeNeighbor : places) {
                if (place.getId() != placeNeighbor.getId()) { //don't add itself
                    distance = distanceMatrix[place.getId()][placeNeighbor.getId()];

                    if (place.getFirmProfit() != 0 || place.getId() == this.startVertex) {
                        neighborList.get(place.getId()).add(new Neighbor(placeNeighbor.getId(), distance, placeNeighbor.getFirmProfit()));
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