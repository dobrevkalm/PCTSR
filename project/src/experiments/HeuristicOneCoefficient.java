package experiments;

import helpers.Heuristic;
import helpers.HeuristicOne;
import helpers.HeuristicTwo;
import model.PathResult;

public class HeuristicOneCoefficient extends Experiment {
    private final int[] START_VERTICES = new int[]{ 0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90 };
    private final int[] AGENTS = new int[]{ 1, 2, 4, 8, 10, 15, 20, 25, 30 };

    public HeuristicOneCoefficient(String fileName) {
        super(fileName);
    }

    public void run() {
        printRow(String.format("%s","PUT YOUR HEADER ROW HERE"));

        /**
         * MAKE YOUR EXPERIMENT HERE
         */

        endExperiment();
    }

    private void calculateResults(boolean method, double[] multipliers, int agent) {
        int numVertices = START_VERTICES.length;
        int RUNS = 100;
        // results from all the runs
        double[] finalDistanceResults = new double[RUNS];
        double[] finalTimeResults = new double[RUNS];
        for (int i = 0; i < RUNS; i++) {
            // the distance for each route with the different starting vertex
            double[] routeDistanceResult = new double[numVertices];
            double[] routeTimeResults = new double[numVertices];

            // calculate results from each of the starting vertices
            for (int j = 0; j < numVertices; j++) {
                calculateResults(method, j, agent, multipliers, routeTimeResults, routeDistanceResult);
            }

            // sum the average time and distance from the different starting vertices
            double distanceRes = sumArrayDoubles(routeDistanceResult);
            finalDistanceResults[i] = distanceRes / numVertices;
            double timeRes = sumArrayDoubles(routeTimeResults);
            finalTimeResults[i] = timeRes / numVertices;
        }

        double finalDistance = sumArrayDoubles(finalDistanceResults);
        double finalTime = sumArrayDoubles(finalTimeResults);

        // write the results to the output file
        printRow(String.format("%s,%d,%.0f,%.0f,%.2f,%.2f", method, agent, multipliers[0], multipliers[1], (finalTime / RUNS), (finalDistance / RUNS)));
    }

    private void calculateResults(boolean method, int j, int agent, double[] multipliers, double[] routeTimeResults, double[] routeDistanceResult) {
        int startV = START_VERTICES[j];
        Heuristic h;
        // init heuristic method
        int MIN_PROFIT = 290;
        if (method) {
            h = new HeuristicOne(distanceMatrix, places, startV, agent, MIN_PROFIT, multipliers);
        } else {
            h = new HeuristicTwo(distanceMatrix, places, startV, agent, MIN_PROFIT);
        }
        // execution time
        double time = System.nanoTime();
        PathResult[] resultPath = h.getResultPaths();
        routeTimeResults[j] = System.nanoTime() - time;
        // total distance
        double routeDistance = 0d;
        for (PathResult res : resultPath) {
            routeDistance += res.getPathLength();
        }
        routeDistanceResult[j] = routeDistance;
    }
}
