package experiments;

import heuristics.Heuristic;
import heuristics.HeuristicOne;

import java.util.Locale;

public class HeuristicOneExperiment extends Experiment {
    private final double[] DISTANCE_MULTIPLIER = new double[]{-1.0, 0.03125, 0.0625, 0.125, 0.25, 0.5, 1.0, 2.0, 4.0, 8.0, 16.0, 32.0, 64.0};

    public HeuristicOneExperiment(String fileName) {
        super(fileName);
    }

    public void run() {
        printRow(String.format("%s,%s,%s,%s,%s", "coefficient", "agentNo", "coefficientVal", "desiredProfit", "distanceRes"));

        runExperiment(DISTANCE_MULTIPLIER);

        endExperiment();
    }

    private void runExperiment(double[] coefficients) {
        for (int desiredProfit : PROFITS) {
            for (double coeff : coefficients) {
                for (int agentsNumber : AGENTS) {
                    // indicate what's running
                    System.out.printf("@@@ RUN -> %d <> %.2f <> %d%n", desiredProfit, coeff, agentsNumber);
                    calculateResults(coeff, agentsNumber, desiredProfit);
                }
            }
        }
    }

    private void calculateResults(double coeff, int agent, int desiredProfit) {
        // calculate results from each of the starting vertices
        for (int j = 0; j < START_VERTICES.length; j++) {
            calculate(j, desiredProfit, agent, coeff);
        }

        //avg distance (from the different starting vertices) for given desired profit
        double distanceRes = calculateAverageDistance();

        // write the results to the output file
        printRow(String.format(Locale.US, "%s,%d,%.5f,%d,%.2f", "dist", agent, coeff, desiredProfit, distanceRes));

        // re-instantiate the distance array in the parent class
        resetDistanceArray();
    }

    private void calculate(int j, int desiredProfit, int agent, double coeff) {
        int startV = START_VERTICES[j];
        Heuristic h = new HeuristicOne(distanceMatrix, places, startV, agent, desiredProfit, coeff);
        calculateDistanceResults(h, j);
    }
}
