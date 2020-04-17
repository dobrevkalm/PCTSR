package experiments;

import helpers.Heuristic;
import helpers.HeuristicOne;
import helpers.HeuristicTwo;
import model.PathResult;

public class HeuristicComparison extends Experiment {

    public HeuristicComparison(String fileName) {
        super(fileName);
    }

    public void run() {
        // print the header row for the csv
        printRow(String.format("%s,%s,%s,%s", "method", "agents", "time", "distance"));
        // warm up the compiler
        warmUp();
        // run with HeuristicOne and Two
        runExperiment(true);
        runExperiment(false);
        // close the print writer
        endExperiment();
    }

    // true for HeuristicOne, false for HeuristicTwo
    private void runExperiment(boolean method) {
        for (int agent : AGENTS) {
            calculateResults(method, agent);
        }
    }

    private void calculateResults(boolean method, int agent) {
        int numVertices = START_VERTICES.length;
        // store the total distance and total time from each run
        double[] finalDistanceResults = new double[RUNS];
        double[] finalTimeResults = new double[RUNS];

        for (int i = 0; i < RUNS; i++) {
            // the distance and time for each route with the different starting vertex
            double[] routeDistanceResult = new double[numVertices];
            double[] routeTimeResults = new double[numVertices];

            for (int j = 0; j < numVertices; j++) {
                calculateResults(method, j, agent, routeTimeResults, routeDistanceResult);
            }

            double distanceRes = sumArrayDoubles(routeDistanceResult);
            finalDistanceResults[i] = distanceRes / numVertices;

            double timeRes = sumArrayDoubles(routeTimeResults);
            finalTimeResults[i] = timeRes / numVertices;
        }

        double finalDistance = sumArrayDoubles(finalDistanceResults);
        double finalTime = sumArrayDoubles(finalTimeResults);

        // print the results on the output file
        printRow(String.format("%s,%d,%.2f,%.2f", (method ? "one" : "two"), agent, (finalTime / RUNS), (finalDistance / RUNS)));
    }

    private void calculateResults(boolean method, int j, int agent, double[] routeTimeResults, double[] routeDistanceResult) {
        int startV = START_VERTICES[j];
        Heuristic h;
        // init heuristic method
        if (method) {
            h = new HeuristicOne(distanceMatrix, places, startV, agent, MIN_PROFIT);
        } else {
            h = new HeuristicTwo(distanceMatrix, places, startV, agent, MIN_PROFIT);
        }
        calculateTimeAndDistanceResults(h, j, routeTimeResults, routeDistanceResult);
    }
}
