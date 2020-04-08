package experiments;

import helpers.Heuristic;
import helpers.HeuristicOne;
import helpers.HeuristicTwo;
import model.PathResult;

public class HeuristicComparison extends Experiment {
    private final int[] START_VERTICES = new int[]{ 0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90 };
    private final int[] AGENTS = new int[]{ 1, 2, 4, 8, 10, 15, 20, 25, 30 };

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
        int RUNS = 100;
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
        int MIN_PROFIT = 290;
        if (method) {
            h = new HeuristicOne(distanceMatrix, places, startV, agent, MIN_PROFIT);
        } else {
            h = new HeuristicTwo(distanceMatrix, places, startV, agent, MIN_PROFIT);
        }
        // get execution time
        double time = System.nanoTime();
        PathResult[] resultPath = h.getResultPaths();
        routeTimeResults[j] = System.nanoTime() - time;
        // sum the total path length
        double routeDistance = 0d;
        for (PathResult res : resultPath) {
            routeDistance += res.getPathLength();
        }
        routeDistanceResult[j] = routeDistance;
    }
}
