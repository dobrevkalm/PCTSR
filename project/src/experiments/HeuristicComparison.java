package experiments;

import heuristics.*;
import model.PathResult;

public class HeuristicComparison extends Experiment {
    private final int NUM_V = START_VERTICES.length;
    private double[] timeResults = new double[NUM_V];

    public HeuristicComparison(String fileName) {
        super(fileName);
    }

    public void run() {
        // print the header row for the csv
        printRow(String.format("%s,%s,%s,%s,%s", "method", "agents", "profit", "time", "distance"));

        // warm up the compiler
        warmUp();

        // run with HeuristicOne and Two
        runExperiment("one");
        runExperiment("two");
        runExperiment("three");

        // close the print writer
        endExperiment();
    }

    // true for HeuristicOne, false for HeuristicTwo
    private void runExperiment(String method) {
        for (int agent : AGENTS) {
            for (int profit : PROFITS) {
                for (int startV : START_VERTICES) {
                    // indicate what's running
                    System.out.printf("@@@ RUN -> %d <> %d <> %d%n", agent, profit, startV);

                    int resultIndex = 0;
                    calculateResults(method, startV, agent, profit, resultIndex);

                    double distance = calculateAverageDistance();
                    double time = 0d;
                    for (double timeRes : timeResults) {
                        time += timeRes;
                    }

                    // print the results on the output file
                    printRow(String.format("%s,%d,%d,%.2f,%.2f", method, agent, profit, (time / NUM_V), distance));

                    // reset the two result arrays
                    resetResultArrays();
                }
            }
        }
    }

    private void calculateResults(String method, int startV, int agent, int profit, int resultIndex) {
        Heuristic h = null;
        // init heuristic method
        switch (method) {
            case "one":
                h = new HeuristicOne(distanceMatrix, places, startV, agent, profit);
                break;
            case "two":
                h = new HeuristicTwo(distanceMatrix, places, startV, agent, profit);
                break;
            case "three":
                h = new HeuristicThree(distanceMatrix, places, startV, agent, profit);
                break;
        }

        getTimeAndDistanceResults(h, resultIndex);
    }

    private void getTimeAndDistanceResults(Heuristic h, int idx) {
        // get execution time
        double time = System.nanoTime();
        PathResult[] resultPath = h.getResultPaths();
        // sum the total path length
        double totalDistance = 0d;
        for (PathResult res : resultPath) {
            totalDistance += res.getPathLength();
        }
        // fill the results in the arrays
        timeResults[idx] = System.nanoTime() - time;
        distanceResults[idx] = totalDistance;
    }

    private void resetResultArrays() {
        // re-instantiate the distance array in the parent class
        resetDistanceArray();
        timeResults = new double[NUM_V];
    }
}
