package experiments;

import helpers.Heuristic;
import helpers.HeuristicOne;
import model.PathResult;

import java.util.Locale;

public class HeuristicOneExperiment extends Experiment {
    //start vertices are selected in a way that they are places in different parts of Denmark and with different profits
    //private final int[] START_VERTICES = new int[]{1, 2, 14, 17, 20, 30, 39, 55, 80, 83, 86};
    private final int[] AGENTS = new int[]{1, 2, 3, 4, 5, 6};
    private int[] PROFITS;
    private int[] START_VERTICES;
    private final int MAX_PROFIT = 299;
    private final int MAX_START_VERTEX = 90;
    private final double[] DISTANCE_MULTIPLIER = new double[]{1.0, 2.0, 4.0, 8.0, 16.0, 32.0};
    private double[] PROFIT_CONSTANT = new double[]{-1.0, 0.0, 1.0, 2.0, 4.0, 8.0, 16.0};

    public HeuristicOneExperiment(String fileName) {
        super(fileName);
        initProfitArray();
        initStartVertices();
    }

    public void run() {
        printRow(String.format("%s,%s,%s,%s,%s", "coefficient", "agentNo", "coefficientVal", "desiredProfit", "distanceRes"));
        experimentCoefficients(DISTANCE_MULTIPLIER, true);
        experimentCoefficients(PROFIT_CONSTANT, false);
        endExperiment();
    }

    private void initProfitArray() {
        PROFITS = new int[MAX_PROFIT / 5];
        int idx = 0;
        for (int i = 5; i <= MAX_PROFIT; i = i + 5) {
            PROFITS[idx] = i;
            idx++;
        }
    }

    private void initStartVertices() {
        START_VERTICES = new int[MAX_START_VERTEX + 1];
        for (int i = 0; i <= MAX_START_VERTEX; i++) {
            START_VERTICES[i] = i;
        }
    }

    private void experimentCoefficients(double[] coeff, boolean isDistanceCoeff) {
        for (int i = 0; i < PROFITS.length; i++) {
            int desiredProfit = PROFITS[i];
            for (int j = 0; j < coeff.length; j++) {
                for (int k = 0; k < AGENTS.length; k++) {
                    int agentsNumber = AGENTS[k];
                    calculateResults(coeff[j], isDistanceCoeff, agentsNumber, desiredProfit);
                }
            }
        }
    }

    private void calculateResults(double coeff, boolean isDistanceCeff, int agent, int desiredProfit) {
        int numVertices = START_VERTICES.length;
        // the distance for each route with the different starting vertex
        double[] routeDistanceResult = new double[numVertices];

        // calculate results from each of the starting vertices
        for (int j = 0; j < numVertices; j++) {
            calculateResults(j, desiredProfit, agent, coeff, isDistanceCeff, routeDistanceResult);
        }

        //avg distance (from the different starting vertices) for given desired profit
        double distanceRes = sumArrayDoubles(routeDistanceResult) / numVertices;


        // write the results to the output file
        if (isDistanceCeff) {
            printRow(String.format(Locale.US, "%s,%d,%.0f,%d,%.2f", "dist", agent, coeff, desiredProfit, distanceRes));
        } else {
            printRow(String.format(Locale.US, "%s,%d,%.0f,%d,%.2f", "prof", agent, coeff, desiredProfit, distanceRes));
        }

    }

    private void calculateResults(int j, int desiredProfit, int agent, double coeff, boolean isDistanceCoeff, double[] routeDistanceResult) {
        int startV = START_VERTICES[j];
        Heuristic h;
        // init heuristic method
        h = new HeuristicOne(distanceMatrix, places, startV, agent, desiredProfit, coeff, isDistanceCoeff);
        PathResult[] resultPath = h.getResultPaths();
        // total distance
        double routeDistance = 0d;
        for (PathResult res : resultPath) {
            routeDistance += res.getPathLength();
        }
        routeDistanceResult[j] = routeDistance;
    }
}
