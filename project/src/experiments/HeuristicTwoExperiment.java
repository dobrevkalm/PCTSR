package experiments;

import heuristics.Heuristic;
import heuristics.HeuristicTwo;

import java.util.Locale;

public class HeuristicTwoExperiment extends Experiment {

    public HeuristicTwoExperiment(String fileName) {
        super(fileName);
    }

    @Override
    public void run() {
        // header row
        printRow(String.format("%s,%s,%s,%s,%s,%s", "agents", "kmax", "percent", "ratio", "profit", "distance"));
        // warm java
        warmUp();
        // run experiment
        runExperiment();
        // close the writer and end the experiment
        endExperiment();
    }

    void runExperiment() {
        for (int agents : AGENTS) {
            for (int kmax = 1; kmax <= 15; kmax += 2) {
                for (int percent = 10; percent <= 100; percent += 10) {
                    for (int m = 1; m <= 10; m++) {
                        // thank you java for making us do this due to your wonderful work with doubles -.-
                        double mutationsRatio = m / 10d;
                        // run experiments with the different profits using the above coefficients
                        runCoefficientsExperiments(agents, kmax, percent, mutationsRatio);
                    }
                }
            }
        }
    }

    void runCoefficientsExperiments(int agents, int kmax, int percent, double mutationsRatio) {
        for (int i = 0; i < PROFITS.length; i += 3) {
            int profit = PROFITS[i];
            // indicate what is running
            System.out.printf("@@@ RUN -> %d <> %d <> %d <> %.2f <> %d%n", agents, kmax, percent, mutationsRatio, profit);

            calculateResults(agents, profit, kmax, percent, mutationsRatio);

            double distanceResult = calculateAverageDistance();

            // print row with test result
            printRow(String.format(Locale.US,"%d,%d,%d,%.2f,%d,%.2f", agents, kmax, percent, mutationsRatio, profit, distanceResult));

            // re-instantiate the distance array in the parent class
            resetDistanceArray();
        }
    }

    private void calculateResults(int agents, int profit, int kmax, int percent, double mutationsRatio) {
        // calculate results from every starting vertex and take the average
        for (int i = 0; i < START_VERTICES.length; i++) {
            int startV = START_VERTICES[i];
            Heuristic h = new HeuristicTwo(distanceMatrix, places, startV, agents, profit, kmax, percent, mutationsRatio);
            calculateDistanceResults(h, i);
        }
    }
}
