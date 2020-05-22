package experiments;

import heuristics.Heuristic;
import heuristics.HeuristicTwo;
import model.PathResult;

import java.util.Locale;

public abstract class HeuristicTwoExperiment extends Experiment {

    HeuristicTwoExperiment(String fileName) {
        super(fileName);
    }

    @Override
    public abstract void run();

    void runCoefficientsExperiments(int agents, int kmax, int percent, double mutationsRatio) {
        for (int i = 0; i < PROFITS.length; i += 5) {
            int profit = PROFITS[i];

            calculateResults(agents, profit, kmax, percent, mutationsRatio);

            double distanceResult = calculateAverageDistance();

            // print row with test result
            printRow(String.format(Locale.US,"%d,%d,%d,%.2f,%d,%.2f", agents, kmax, percent, mutationsRatio, profit, distanceResult));

            // re-instantiate the distance array in the parent class
            resetDistanceArray();
        }
    }

    void calculateResults(int agents, int profit, int kmax, int percent, double mutationsRatio) {
        // calculate results from every starting vertex and take the average
        for (int i = 0; i < START_VERTICES.length; i++) {
            int startV = START_VERTICES[i];
            Heuristic h = new HeuristicTwo(distanceMatrix, places, startV, agents, profit, kmax, percent, mutationsRatio);
            calculateDistanceResults(h, i);
        }
    }

    @Override
    void calculateDistanceResults(Heuristic h, int index) {
        final int trialRuns = 10;
        double[] routeDistanceResults = new double[trialRuns];

        for (int i = 0; i < trialRuns; i++) {
            PathResult[] resultPath = h.getResultPaths();
            // sum the total path length
            double routeDistance = 0d;
            for (PathResult res : resultPath) {
                routeDistance += res.getPathLength();
            }
            routeDistanceResults[i] = routeDistance;
        }

        double averageDistance = 0d;
        for (double distance : routeDistanceResults) {
            averageDistance += distance;
        }

        distanceResults[index] = averageDistance / trialRuns;
    }
}
