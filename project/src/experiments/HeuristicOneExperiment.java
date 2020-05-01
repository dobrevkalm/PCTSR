package experiments;

import heuristics.Heuristic;
import heuristics.HeuristicOne;

import java.util.Locale;

public class HeuristicOneExperiment extends Experiment {
    private final double[] DISTANCE_MULTIPLIER = new double[]{1.0, 2.0, 4.0, 8.0, 16.0, 32.0};
    private double[] PROFIT_CONSTANT = new double[]{-1.0, 0.0, 1.0, 2.0, 4.0, 8.0, 16.0};

    public HeuristicOneExperiment(String fileName) {
        super(fileName);
    }

    public void run() {
        printRow(String.format("%s,%s,%s,%s,%s", "coefficient", "agentNo", "coefficientVal", "desiredProfit", "distanceRes"));

        // warm up the compiler
        warmUp();

        runExperiment(DISTANCE_MULTIPLIER, true);
        runExperiment(PROFIT_CONSTANT, false);

        endExperiment();
    }

    private void runExperiment(double[] coefficients, boolean isDistanceCoeff) {
        for (int desiredProfit : PROFITS) {
            for (double coeff : coefficients) {
                for (int agentsNumber : AGENTS) {
                    // indicate what's running
                    System.out.printf("@@@ RUN -> %d <> %.2f <> %d%n", desiredProfit, coeff, agentsNumber);
                    calculateResults(coeff, isDistanceCoeff, agentsNumber, desiredProfit);
                }
            }
        }
    }

    private void calculateResults(double coeff, boolean isDistanceCeff, int agent, int desiredProfit) {
        // calculate results from each of the starting vertices
        for (int j = 0; j < START_VERTICES.length; j++) {
            calculate(j, desiredProfit, agent, coeff, isDistanceCeff);
        }

        //avg distance (from the different starting vertices) for given desired profit
        double distanceRes = calculateAverageDistance();

        // write the results to the output file
        if (isDistanceCeff) {
            printRow(String.format(Locale.US, "%s,%d,%.0f,%d,%.2f", "dist", agent, coeff, desiredProfit, distanceRes));
        } else {
            printRow(String.format(Locale.US, "%s,%d,%.0f,%d,%.2f", "prof", agent, coeff, desiredProfit, distanceRes));
        }

        // re-instantiate the distance array in the parent class
        resetDistanceArray();
    }

    private void calculate(int j, int desiredProfit, int agent, double coeff, boolean isDistanceCoeff) {
        int startV = START_VERTICES[j];
        Heuristic h = new HeuristicOne(distanceMatrix, places, startV, agent, desiredProfit, coeff, isDistanceCoeff);
        calculateDistanceResults(h, j);
    }
}
