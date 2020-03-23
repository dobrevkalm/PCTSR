package helpers;

import model.Neighbor;
import model.PathResult;
import model.Place;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HeuristicTwo {
    private List<ArrayList<Neighbor>> neighborList;
    private Place[] places;
    private double[][] distanceMatrix;
    private PathResult[] pR;
    private boolean[] visited;

    private int startVertex, agentsNumber, minProfit;
    private double  previousMinLength = 100000000.0,  distance, sumProfit = 0.0, sumLength = 0.0;

    public HeuristicTwo(double[][] distanceMatrix, Place[] places, int startVertex, int agentsNumber, int minProfit) {
        this.places = places;
        this.startVertex = startVertex;
        this.agentsNumber = agentsNumber;
        this.minProfit = minProfit;
        this.visited = new boolean[places.length];
        this.pR = new PathResult[this.agentsNumber];
        this.distanceMatrix = distanceMatrix;

        this.neighborList = initializeNeighborList();
    }

    private List<ArrayList<Neighbor>> initializeNeighborList() {
        List<ArrayList<Neighbor>> neighborList = new ArrayList<>(places.length);
        for (Place place : places) {
            neighborList.set(place.getId(), new ArrayList<>());
            for (Place placeNeighbor : places) {
                if (place.getId() != placeNeighbor.getId()) {
                    distance = distanceMatrix[place.getId()][placeNeighbor.getId()];

                    if (place.getFirmProfit() != 0 || place.getId() == this.startVertex) {
                        neighborList.get(place.getId()).add(new Neighbor(placeNeighbor.getId(), distance, placeNeighbor.getFirmProfit()));
                    }
                }
            }
        }
        return neighborList;
    }

    public PathResult[] ResultPath() {
        for (int i = 0; i < agentsNumber; i++) {
            pR[i] = new PathResult(distanceMatrix);
            pR[i].getResultPath().add(places[startVertex]);
            pR[i].getResultPath().add(places[startVertex]);
        }

        //set the starting vertex as visited
        visited[startVertex] = true;

        while (sumProfit < minProfit) {
            for (int i = 0; i < agentsNumber; i++) {
                sumProfit += Insert(i);
                if (sumProfit >= minProfit) break;
            }
        }

        //try to shorten the routes with opt2
        for (int i = 0; i < agentsNumber; i++) {
            boolean useOpt2 = true;
            while (useOpt2){
                useOpt2 = pR[i].opt2();
            }
        }

        previousMinLength = 10000000000.0;
        for (int i = 0; i < agentsNumber; i++) {
            previousMinLength += pR[i].getPathLength();
        }

        Random random = new Random();
        int percent;

        int kmax = 10;

        for (int k = 0; k < kmax; k++) {
            percent = random.nextInt(80);
            for (int a = 0; a < agentsNumber; a++) {
                int n = pR[a].getResultPath().size();
                //number of vertices to remove per 1 agent
                int verticesToRemove = n * percent / 100;

                for (int i = 0; i < verticesToRemove; i++) {
                    if (i < 3) {
                        RemoveBestPlaces(a);
                    } else {
                        Remove(a);
                    }
                }
            }

            // calculate total collected profit after changes
            sumProfit = 0.0;
            for (int a = 0; a < agentsNumber; a++) {
                sumProfit += pR[a].getActualProfit();
            }

            // repeat insert operation until condition of the minimum required profit is satisfied
            while (sumProfit < minProfit) {
                for (int i = 0; i < agentsNumber; i++) {
                    sumProfit += Insert(i);
                    if (sumProfit >= minProfit) break;
                }
            }

            // calculate the total distance
            sumLength = 0.0;
            for (int i = 0; i < agentsNumber; i++) {
                sumLength += pR[i].getPathLength();
            }


            if (sumLength < previousMinLength) {
                previousMinLength = sumLength;
                // save the previous mutations of the routes
                for (int i = 0; i < agentsNumber; i++) {
                    List<Place> currrentPath = pR[i].getResultPath();
                    // save current result path, profit and length in the previousMinPlaces, previousMaxProfit, previousMinLength
                    // to be able to refer to them after further mutations
                    pR[i].setPreviousMinPlaces(new ArrayList<>(currrentPath));
                    double currentProfit = pR[i].getActualProfit();
                    double currentPathLength = pR[i].getPathLength();
                    pR[i].setPreviousMaxProfit(currentProfit);
                    pR[i].setPreviousMinLength(currentPathLength);
                    // adjust visited[] array to mirror actual current state
                    refreshVisitedVerticesArray();
                }
            } else {
                for (int i = 0; i < agentsNumber; i++) {
                    List<Place> previousPlaces = pR[i].getPreviousMinPlaces();
                    // revert resultPath, actualProfit and pathLength to the previous (better) values
                    pR[i].setResultPath(new ArrayList<>(previousPlaces));
                    double previousProfit = pR[i].getPreviousMaxProfit();
                    double previousLength = pR[i].getPreviousMinLength();
                    pR[i].setActualProfit(previousProfit);
                    pR[i].setPathLength(previousLength);
                    // adjust visited[] array to mirror actual current state
                    refreshVisitedVerticesArray();
                }
            }
        }

        for (int i = 0; i < agentsNumber; i++) {
            List<Place> previousPlaces = pR[i].getPreviousMinPlaces();
            pR[i].setResultPath(new ArrayList<>(previousPlaces));
            //refreshVisitedVerticesArray();
            double previousProfit = pR[i].getPreviousMaxProfit();
            double previousLength = pR[i].getPreviousMinLength();
            pR[i].setActualProfit(previousProfit);
            pR[i].setPathLength(previousLength);
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

    // k is the agent number
    public void Remove(int k) {
        int n = pR[k].getResultPath().size();
        double bestH = -1.0;
        int besti = -1;
        double minusLength = 0.0;

        // iterate through vertices
        for (int i = 1; i < n - 1; i++) {
            int previousVertexId = pR[k].getResultPath().get(i - 1).getId();
            int iVertexId = pR[k].getResultPath().get(i).getId();
            int nextVertexId = pR[k].getResultPath().get(i + 1).getId();
            double minus = distanceMatrix[previousVertexId][iVertexId] + distanceMatrix[iVertexId][nextVertexId];
            double plus = distanceMatrix[previousVertexId][nextVertexId];

            double minusProfit = pR[k].getResultPath().get(i).getFirmProfit();

            if ((minus - plus) / minusProfit > bestH) {
                bestH = (minus - plus) / minusProfit;
                besti = i;
                minusLength = minus - plus;
            }
        }

        double decreasedProfit = pR[k].getResultPath().get(besti).getFirmProfit();
        pR[k].increasePathLength(-minusLength);
        pR[k].increaseActualProfit(-decreasedProfit);

        //unmark the removed from the path vertex
        visited[pR[k].getResultPath().get(besti).getId()] = false;
        //remove the vertex from the result path (cycle)
        pR[k].getResultPath().remove(besti);
    }

    public void RemoveBestPlaces(int k) {
        int n = pR[k].getResultPath().size();
        double bestH = -1.0;
        // besti <- index of the vertext in the ResultPath to remove
        int besti = -1;
        double minusLength = 0.0;

        for (int i = 1; i < n - 1; i++) {
            int previousVertexId = pR[k].getResultPath().get(i - 1).getId();
            int iVertexId = pR[k].getResultPath().get(i).getId();
            int nextVertexId = pR[k].getResultPath().get(i + 1).getId();
            double minus = distanceMatrix[previousVertexId][iVertexId] + distanceMatrix[iVertexId][nextVertexId];
            double plus = distanceMatrix[previousVertexId][nextVertexId];

            double minusProfit = pR[k].getResultPath().get(i).getFirmProfit();

            if (minusProfit > bestH) {
                bestH = minusProfit;
                besti = i;
                minusLength = minus - plus;
            }
        }

        double decreasedProfit = pR[k].getResultPath().get(besti).getFirmProfit();
        pR[k].increasePathLength(-minusLength);
        pR[k].increaseActualProfit(-decreasedProfit);

        //unmark the removed from the path vertex
        visited[pR[k].getResultPath().get(besti).getId()] = false;
        //remove the vertex from the result path (cycle)
        pR[k].getResultPath().remove(besti);
    }

    public double Insert(int k) {
        int n = pR[k].getResultPath().size();
        double bestH = -1.0;
        // index of the vertex to be inserted to the resultPath
        int besti = -1;
        // place to be inserted to the resultPath
        int bestj = -1;
        double plusLength = 0.0;

        // iterate through all the vertices
        for (int i = 0; i < visited.length; i++) {
            // consider only not visited vertices
            if (!visited[i]) {
                // find the best place to insert a new vertex
                for (int j = 1; j < n; j++) {
                    double tmpLength = pR[k].getPathLength();
                    double tmpProfit = pR[k].getActualProfit();

                    List<Place> resPath = pR[k].getResultPath();
                    double minus = distanceMatrix[resPath.get(j - 1).getId()][resPath.get(j).getId()];
                    double plus = distanceMatrix[resPath.get(j - 1).getId()][i] + distanceMatrix[i][resPath.get(j).getId()];
                    double plusProfit = places[i].getFirmProfit();

                    if (plusProfit / (plus - minus) > bestH) {
                        bestH = plusProfit / (plus - minus);
                        besti = i;
                        bestj = j;
                        plusLength = plus - minus;
                    }
                }
            }
        }

        Place placeToInsert = places[besti];
        double profitIncrease = placeToInsert.getFirmProfit();

        pR[k].increasePathLength(plusLength);
        pR[k].increaseActualProfit(profitIncrease);

        pR[k].getResultPath().add(bestj, placeToInsert);
        visited[besti] = true;

        return profitIncrease;
    }

    public void refreshVisitedVerticesArray() {
        this.visited = new boolean[this.places.length];
        for (int i = 0; i < agentsNumber; i++) {
            List<Place> resPath = pR[i].getResultPath();

            for (int j = 0; j < resPath.size(); j++) {
                visited[resPath.get(j).getId()] = true;
            }
        }
    }
}
