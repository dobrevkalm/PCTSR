package experiments;

import heuristics.*;
import model.PathResult;

import java.util.Locale;
import java.util.Random;

public class HeuristicComparison extends Experiment {
    private final int NUM_V = START_VERTICES.length;
    private double[] timeResults = new double[NUM_V];

    public HeuristicComparison(String fileName) {
        super(fileName);
    }

    public void run() {
        // warm up the compiler
        warmUp();

        System.out.println("STARTING THE EXPERIMENT");
        // print the header row for the csv
        printRow(String.format("%s,%s,%s,%s,%s", "heuristic", "agents", "profit", "time (ns)", "distance (km)"));

        // run with HeuristicOne and Two
        runExperiment("one");
        runExperiment("two");
        runExperiment("three");
        runExperiment("four");

        // close the print writer
        endExperiment();
    }

    // runs the experiment with the specified heuristic method
    private void runExperiment(String heuristic) {
        for (int agent : AGENTS) {
            // indicate what's running
            System.out.printf("%n - RUNNING heuristic %s with %d agents%n", heuristic, agent);

            for (int profit : PROFITS) {
                for (int i = 0; i < NUM_V; i++) {
                    int startV = START_VERTICES[i];
                    calculateResults(heuristic, startV, agent, profit, i);
                }

                double distance = calculateAverageDistance();
                double time = 0d;
                for (double timeRes : timeResults) {
                    time += timeRes;
                }

                // print the results on the output file
                printRow(String.format(Locale.US, "%s,%d,%d,%.2f,%.2f", heuristic, agent, profit, (time / NUM_V), (distance / 1000)));

                // reset the two result arrays
                resetResultArrays();
            }
        }
    }

    private void calculateResults(String heuristic, int startV, int agent, int profit, int resultIndex) {
        Heuristic h = null;
        // init heuristic method
        switch (heuristic) {
            case "one":
                h = new HeuristicOne(distanceMatrix, places, startV, agent, profit);
                break;
            case "two":
                h = new HeuristicTwo(distanceMatrix, places, startV, agent, profit);
                break;
            case "three":
                h = new HeuristicThree(distanceMatrix, places, startV, agent, profit);
                break;
            case "four":
                h = new HeuristicFour(distanceMatrix, places, startV, agent, profit);
                break;
        }

        fillTimeAndDistanceResults(h, resultIndex);
    }

    private void fillTimeAndDistanceResults(Heuristic h, int idx) {
        // get execution time
        double time = System.nanoTime();
        PathResult[] resultPath = h.getResultPaths();

        // save time results
        time = System.nanoTime() - time;

        // sum the total path length
        double totalDistance = 0d;
        for (PathResult res : resultPath) {
            totalDistance += res.getPathLength();
        }

        // fill the results
        timeResults[idx] = time;
        distanceResults[idx] = totalDistance;
    }

    // reset the arrays with all the results
    private void resetResultArrays() {
        // re-instantiate the distance array in the parent class
        resetDistanceArray();
        timeResults = new double[NUM_V];
    }

    // HELPERS TO WARM UP THE COMPILER. NOT PART OF THE ACTUAL EXPERIMENT

    private void warmUp() {
        System.out.printf("%nWARMING UP...");

        Random rnd = new Random();
        int tmp = 0;

        for (int i = 0; i < 10_000; i++) {
            int profit = rnd.nextInt(299);
            int startV = rnd.nextInt(90);
            int agents = rnd.nextInt(9);

            Heuristic h = new HeuristicOne(distanceMatrix, places, startV, (agents + 1), profit);
            tmp += sumWarmUpResults(h.getResultPaths());

            h = new HeuristicTwo(distanceMatrix, places, startV, (agents + 1), profit);
            tmp -= sumWarmUpResults(h.getResultPaths());

            h = new HeuristicThree(distanceMatrix, places, startV, (agents + 1), profit);
            tmp += sumWarmUpResults(h.getResultPaths());

            h = new HeuristicFour(distanceMatrix, places, startV, (agents + 1), profit);
            tmp -= sumWarmUpResults(h.getResultPaths());
        }
        System.out.printf(" DONE > %d <%n%n%n", tmp);
    }

    private int sumWarmUpResults(PathResult[] results) {
        int tmp = 0;
        for (PathResult res : results) {
            tmp += res.getPathLength();
        }
        return tmp;
    }
}
