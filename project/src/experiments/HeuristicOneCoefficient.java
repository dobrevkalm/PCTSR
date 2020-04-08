package experiments;

import helpers.Heuristic;
import helpers.HeuristicOne;
import helpers.HeuristicTwo;
import model.PathResult;
import model.Place;
import reader.DataReader;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HeuristicOneCoefficient {
    private static PrintWriter writer;
    private DataReader reader = new DataReader();
    private Place[] places = reader.getAllCompanies();
    private double[][] distanceMatrix = reader.getDistanceMatrix();
    private final int[] START_VERTICES = new int[]{ 0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90 };
    private final int[] AGENTS = new int[]{ 1, 2, 4, 8, 10, 15, 20, 25, 30 };
    private final int MIN_PROFIT = 290;
    private final int RUNS = 100;

    public void run(String experiment, String resultFile) {
        Charset CHARSET = StandardCharsets.UTF_8;
        Path RES_FILE = Paths.get(resultFile);
        try {
            writer = new PrintWriter(Files.newBufferedWriter(RES_FILE, CHARSET));
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer.println(String.format("%s,%s,%s,%s,%s,%s", "method", "agents", "distanceMultiplier", "profitMultiplier", "time", "distance"));

        switch (experiment) {
            case "one":
                E1("one");
                E1("two");
                break;
        }

        writer.close();
    }

    private void E1(String method) {
        warmUp();
        for (int agent : AGENTS) {
            calculateResults(method, new double[]{1d, 1d}, agent);
        }
    }

    private void calculateResults(String method, double[] multipliers, int agent) {
        int numVertices = START_VERTICES.length;
        double[] finalDistanceResults = new double[RUNS];
        double[] finalTimeResults = new double[RUNS];
        for (int i = 0; i < RUNS; i++) {
            // the distance for each route with the different starting vertex
            double[] routeDistanceResult = new double[numVertices];
            double[] routeTimeResults = new double[numVertices];

            for (int j = 0; j < numVertices; j++) {
                calculateResults(method, j, agent, multipliers, routeTimeResults, routeDistanceResult);
            }

            double distanceRes = sumArrayDoubles(routeDistanceResult);
            finalDistanceResults[i] = distanceRes / numVertices;

            double timeRes = sumArrayDoubles(routeTimeResults);
            finalTimeResults[i] = timeRes / numVertices;
        }

        double finalDistance = sumArrayDoubles(finalDistanceResults);
        double finalTime = sumArrayDoubles(finalTimeResults);

        writeResultsToFile(method, agent, multipliers[0], multipliers[1], (finalTime / RUNS), (finalDistance / RUNS));
    }

    private void calculateResults(String method, int j, int agent, double[] multipliers, double[] routeTimeResults, double[] routeDistanceResult) {
        int startV = START_VERTICES[j];
        Heuristic h;
        // init heuristic method
        if (method.toLowerCase().contains("one")) {
            h = new HeuristicOne(distanceMatrix, places, startV, agent, MIN_PROFIT, multipliers);
        } else {
            h = new HeuristicTwo(distanceMatrix, places, startV, agent, MIN_PROFIT);
        }
        double time = System.nanoTime();
        PathResult[] resultPath = h.getResultPaths();
        routeTimeResults[j] = System.nanoTime() - time;
        double routeDistance = 0d;
        for (PathResult res : resultPath) {
            routeDistance += res.getPathLength();
        }
        routeDistanceResult[j] = routeDistance;
    }

    private double sumArrayDoubles(double[] array) {
        double res = 0d;
        for (double d : array) {
            res += d;
        }
        return res;
    }

    private void writeResultsToFile(String method, int agents, double distanceMultiplier, double profitMultiplier, double time, double distance) {
        writer.println(String.format("%s,%d,%.0f,%.0f,%.0f,%.0f", method, agents, distanceMultiplier, profitMultiplier, time, distance));
    }

    private void warmUp() {
        // to warm up the compiler
        for (int i = 0; i < 10; i++) {
            Heuristic h = new HeuristicOne(distanceMatrix, places, i * 10, (i+1) * 2, MIN_PROFIT);
            PathResult[] results = h.getResultPaths();
            double tmp = 0d;
            for (PathResult res : results) {
                tmp += res.getPathLength();
            }
            h = new HeuristicTwo(distanceMatrix, places, i * 10, (i+1) * 2, MIN_PROFIT);
            results = h.getResultPaths();
            for (PathResult res : results) {
                tmp += res.getPathLength();
            }
            System.out.println(tmp);
        }
    }
}
