package helpers;

import model.PathResult;
import model.Place;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HeuristicTwo extends Heuristic {
    // the test field variables are used for the experiments
    private int testKmax = -1; // will determine the size of mutations to be made
    private double testPercent = -1; // will determine the number of vertices to remove/change based on the vertices covered by the agent
    private double testMutationsRatio = -1; // the ratio used to choose whether to remove the most profitable vertex in the route

    public HeuristicTwo(double[][] distanceMatrix, Place[] places, int startVertex, int agentsNumber, int minProfit) {
        super(distanceMatrix, places, startVertex, agentsNumber, minProfit);
    }

    // constructor used for experiments
    public HeuristicTwo(double[][] distanceMatrix, Place[] places, int startVertex, int agentsNumber, int minProfit, int kmax, double percent, double mutationsRatio) {
        super(distanceMatrix, places, startVertex, agentsNumber, minProfit);
        this.testKmax = kmax;
        this.testPercent = percent;
        this.testMutationsRatio = mutationsRatio;
    }

    public PathResult[] getResultPaths() {
        initializeAgentsStartingVertex();

        //set the starting vertex as visited
        visited[startVertex] = true;
        gatherProfitPerAgent();
        //try to shorten the routes with opt2
        shortenRoutes();

        // perform selected number of mutations in order to optimize the route
        performMutations();

        updateAgentsPathResult(false);

        //try to shorten the routes with opt2
        shortenRoutes();

        return pathResult;
    }

    // k is the agent number
    private void remove(int k, boolean best) {
        int n = pathResult[k].getResultPath().size();
        double bestH = -1.0;
        int besti = -1;
        double minusLength = 0.0;

        // iterate through vertices
        for (int i = 1; i < n - 1; i++) {
            int previousVertexId = pathResult[k].getResultPath().get(i - 1).getId();
            int iVertexId = pathResult[k].getResultPath().get(i).getId();
            int nextVertexId = pathResult[k].getResultPath().get(i + 1).getId();
            double minus = distanceMatrix[previousVertexId][iVertexId] + distanceMatrix[iVertexId][nextVertexId];
            double plus = distanceMatrix[previousVertexId][nextVertexId];

            double minusProfit = pathResult[k].getResultPath().get(i).getFirmProfit();

            if (!best && (minus - plus) / minusProfit > bestH) {
                bestH = (minus - plus) / minusProfit;
                besti = i;
                minusLength = minus - plus;
            } else if (best && minusProfit > bestH) {
                bestH = minusProfit;
                besti = i;
                minusLength = minus - plus;
            }
        }

        if(besti != -1) {
            double decreasedProfit = pathResult[k].getResultPath().get(besti).getFirmProfit();
            pathResult[k].increasePathLength(-minusLength);
            pathResult[k].increaseActualProfit(-decreasedProfit);
            //unmark the removed from the path vertex
            visited[pathResult[k].getResultPath().get(besti).getId()] = false;
            //remove the vertex from the result path (cycle)
            pathResult[k].getResultPath().remove(besti);
        }
    }

    private double insert(int k) {
        int n = pathResult[k].getResultPath().size();
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

                    List<Place> resPath = pathResult[k].getResultPath();
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

        double profitIncrease = 0.0;
        if (besti == -1) {
            return profitIncrease;
        }

        Place placeToInsert = places[besti];
        profitIncrease = placeToInsert.getFirmProfit();
        pathResult[k].increasePathLength(plusLength);
        pathResult[k].increaseActualProfit(profitIncrease);
        pathResult[k].getResultPath().add(bestj, placeToInsert);
        visited[besti] = true;

        return profitIncrease;
    }

    private void refreshVisitedVerticesArray() {
        this.visited = new boolean[this.places.length];
        for (int i = 0; i < agentsNumber; i++) {
            List<Place> resPath = pathResult[i].getResultPath();

            for (Place place : resPath) {
                visited[place.getId()] = true;
            }
        }
    }

    private void performMutations() {
        double previousMinLength = 10000000000.0;
        for (int i = 0; i < agentsNumber; i++) {
            previousMinLength += pathResult[i].getPathLength();
        }

        Random random = new Random();
        // the number of mutations
        int kmax = 10;
        double percent = random.nextInt(80);

        // for experiments
        if (this.testKmax != -1 && this.testPercent != -1) {
            kmax = this.testKmax;
            percent = this.testPercent;
        }

        for (int k = 0; k < kmax; k++) {
            previousMinLength = generateMutations(percent, previousMinLength);
        }
    }

    private double generateMutations(double percent, double previousMinLength) {
        for (int agent = 0; agent < agentsNumber; agent++) {
            generateAgentRouteMutation(agent, percent);
        }

        // calculate total collected profit after changes
        sumProfit = 0.0;
        for (int a = 0; a < agentsNumber; a++) {
            sumProfit += pathResult[a].getActualProfit();
        }

        // repeat insert operation until condition of the minimum required profit is satisfied
        gatherProfitPerAgent();

        // calculate the total distance
        double sumLength = 0.0;
        for (int i = 0; i < agentsNumber; i++) {
            sumLength += pathResult[i].getPathLength();
        }

        double previousMin = previousMinLength;
        if (sumLength < previousMin) {
            previousMin = sumLength;
            // save the previous mutations of the routes
            savePreviousRoute();
        } else {
            updateAgentsPathResult(true);
        }
        return previousMin;
    }

    private void generateAgentRouteMutation(int agent, double percent) {
        int n = pathResult[agent].getResultPath().size();
        // how many vertices should be removed based on the vertices covered
        // if one agents covers 10 vertices and testKmax = 0.3, we will make 3 mutations (30% of 10)
        int verticesToRemove = (int) (n * percent);
        int mutationsRatio = 3;

        // used for experiments
        if (this.testMutationsRatio != -1) {
            // what part of the remove mutations should be done with removing the best vertex
            mutationsRatio = (int) (this.testMutationsRatio * verticesToRemove);
        }

        for (int i = 0; i < verticesToRemove; i++) {
            if (i < mutationsRatio) {
                remove(agent, true);
            } else {
                remove(agent, false);
            }
        }
    }

    private void savePreviousRoute() {
        for (int i = 0; i < agentsNumber; i++) {
            List<Place> currrentPath = pathResult[i].getResultPath();
            // save current result path, profit and length in the previousMinPlaces, previousMaxProfit, previousMinLength
            // to be able to refer to them after further mutations
            pathResult[i].setPreviousMinPlaces(new ArrayList<>(currrentPath));
            double currentProfit = pathResult[i].getActualProfit();
            double currentPathLength = pathResult[i].getPathLength();
            pathResult[i].setPreviousMaxProfit(currentProfit);
            pathResult[i].setPreviousMinLength(currentPathLength);
            // adjust visited[] array to mirror actual current state
            refreshVisitedVerticesArray();
        }
    }

    private void updateAgentsPathResult(boolean refresh) {
        for (int i = 0; i < agentsNumber; i++) {
            List<Place> previousPlaces = pathResult[i].getPreviousMinPlaces();
            // revert resultPath, actualProfit and pathLength to the previous (better) values
            pathResult[i].setResultPath(new ArrayList<>(previousPlaces));
            double previousProfit = pathResult[i].getPreviousMaxProfit();
            double previousLength = pathResult[i].getPreviousMinLength();
            pathResult[i].setActualProfit(previousProfit);
            pathResult[i].setPathLength(previousLength);
            // adjust visited[] array to mirror actual current state
             if (refresh) {
                 refreshVisitedVerticesArray();
             }
        }
    }

    private void shortenRoutes() {
        for (int i = 0; i < agentsNumber; i++) {
            boolean useOpt2 = true;
            while (useOpt2){
                useOpt2 = pathResult[i].opt2();
            }
        }
    }

    private void gatherProfitPerAgent() {
        while (sumProfit < minProfit) {
            for (int i = 0; i < agentsNumber; i++) {
                sumProfit += insert(i);
                if (sumProfit >= minProfit) break;
            }
        }
    }

    private void initializeAgentsStartingVertex() {
        for (int i = 0; i < agentsNumber; i++) {
            pathResult[i] = new PathResult(distanceMatrix);
            pathResult[i].getResultPath().add(places[startVertex]);
            pathResult[i].getResultPath().add(places[startVertex]);
        }
    }
}
