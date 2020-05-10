package heuristics;

import model.Place;

import java.util.List;
import java.util.Random;

public class HeuristicFour extends HeuristicTwo {

    public HeuristicFour(double[][] distanceMatrix, Place[] places, int startVertex, int agentsNumber, int minProfit) {
        super(distanceMatrix, places, startVertex, agentsNumber, minProfit);
    }

    @Override
    void performMutations() {
        double previousMinLength = 10000000000.0;
        for (int i = 0; i < agentsNumber; i++) {
            previousMinLength += pathResult[i].getPathLength();
        }

        Random random = new Random();
        // the number of mutations
        int kmax = 10;

        for (int k = 0; k < kmax; k++) {
            int percent = random.nextInt(80);
            previousMinLength = generateMutations(percent, previousMinLength);
        }
    }

    @Override
    double generateMutations(int percent, double previousMinLength) {
        for (int agent = 0; agent < agentsNumber; agent++) {
            generateAgentRouteMutation(agent, percent);
        }

        // calculate total collected profit after changes
        sumProfit = 0.0;
        for (int a = 0; a < agentsNumber; a++) {
            sumProfit += pathResult[a].getActualProfit();
        }

        // repeat insert operation until condition of the minimum required profit is satisfied
        // gather profit method adds vertices
        gatherProfit();

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

    private void gatherProfit() {
        while (sumProfit < minProfit) {
            for (int i = 0; i < agentsNumber; i++) {
                sumProfit += insert();
                if (sumProfit >= minProfit) break;
            }
        }
    }

    private double insert() {
        double bestH = -1.0;
        // index of the vertex to be inserted to the resultPath
        int besti = -1;
        // place to be inserted to the resultPath
        int bestj = -1;
        double plusLength = 0.0;
        int agent = -1;

        // find the agent and vertex with the best ratio: profit / increase of travelled distance
        for (int agentNo = 0; agentNo < pathResult.length; agentNo++) {
            int n = pathResult[agentNo].getResultPath().size();

            // iterate through all the vertices
            for (int i = 0; i < visited.length; i++) {
                // consider only not visited vertices
                if (!visited[i]) {
                    // find the best place to insert a new vertex
                    for (int j = 1; j < n; j++) {

                        List<Place> resPath = pathResult[agentNo].getResultPath();
                        double minus = distanceMatrix[resPath.get(j - 1).getId()][resPath.get(j).getId()];
                        double plus = distanceMatrix[resPath.get(j - 1).getId()][i] + distanceMatrix[i][resPath.get(j).getId()];
                        double plusProfit = places[i].getFirmProfit();

                        if (plusProfit / (plus - minus) > bestH) {
                            bestH = plusProfit / (plus - minus);
                            besti = i;
                            bestj = j;
                            plusLength = plus - minus;
                            agent = agentNo;
                        }
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
        pathResult[agent].increasePathLength(plusLength);
        pathResult[agent].increaseActualProfit(profitIncrease);
        pathResult[agent].getResultPath().add(bestj, placeToInsert);
        visited[besti] = true;

        return profitIncrease;
    }
}
