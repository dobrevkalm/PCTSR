package helpers;

import model.Neighbor;
import model.PathResult;
import model.Place;

import java.util.ArrayList;
import java.util.List;

public class HeuristicOne {
    private List<ArrayList<Neighbor>> neighborList;
    private Place[] places;
    private PathResult[] pR;
    private double[][] distanceMatrix;
    //number of places in the matrix
    private int n;

    private int startVertex, agentsNumber, minProfit, minHeuristicId = 0, availablePlaces = 0;
    private double distance = 0.0, minHeuristic = Double.MAX_VALUE, sumProfit = 0.0;

    public HeuristicOne(double[][] distanceMatrix, Place[] places, int startVertex, int agentsNumber, int minProfit) {
        this.places = places;
        this.startVertex = startVertex;
        this.agentsNumber = agentsNumber;
        this.minProfit = minProfit;
        this.distanceMatrix = distanceMatrix;
        this.n = places.length;
        pR = new PathResult[this.agentsNumber];

        neighborList = initializeNeighborList();
    }

    private List<ArrayList<Neighbor>> initializeNeighborList() {
        List<ArrayList<Neighbor>> neighborList = new ArrayList<>(places.length);
        for (Place place : places) {
            neighborList.set(place.getId(), new ArrayList<>());
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
        boolean[] visited = new boolean[places.length];

        // add the starting vertex for all the agents
        for (int i = 0; i < agentsNumber; i++) {
            pR[i] = new PathResult(distanceMatrix);
            pR[i].getResultPath().add(places[startVertex]);
            pR[i].setActualPlaceID(startVertex);
        }

        //set the starting vertex as visited
        visited[startVertex] = true;

        // while currently collected profit is smaller than required minimum profit to collect
        while (sumProfit < minProfit) {
            for (int i = 0; i < agentsNumber; i++) {
                for (Neighbor neighbor : neighborList.get(pR[i].getActualPlaceID())) {
                    // find a not visited neighbor with the smallest coefficient ((distance*2)/profit)
                    if (neighbor.getProfit() != 0 && !visited[neighbor.getId()] && neighbor.getHeuristic() < minHeuristic) {
                        minHeuristic = neighbor.getHeuristic();
                        minHeuristicId = neighbor.getId();
                        distance = neighbor.getDistance();
                        availablePlaces++;
                    }
                }
                if (availablePlaces == 0) break;
                availablePlaces = 0;

                pR[i].setActualPlaceID(minHeuristicId);
                pR[i].increasePathLength(distance);
                pR[i].getResultPath().add(places[minHeuristicId]);
                pR[i].increaseActualProfit(places[minHeuristicId].getFirmProfit());
                //set added vertex as visited
                visited[minHeuristicId] = true;
                minHeuristic = Double.MAX_VALUE;

                sumProfit += places[minHeuristicId].getFirmProfit();
                if (sumProfit >= minProfit) {
                    break;
                }
            }
        }

        //connect generated path with the starting vertex to create a cycle
        for (int i = 0; i < agentsNumber; i++) {
            pR[i].getResultPath().add(places[startVertex]);
            pR[i].increasePathLength(distanceMatrix[startVertex][pR[i].getActualPlaceID()]);
        }

        //try to shorten the routes with opt2
        for (int i = 0; i < agentsNumber; i++) {
            boolean useOpt2 = true;
            while (useOpt2){
                useOpt2 = pR[i].opt2();
            }
        }

        return pR;
    }

}