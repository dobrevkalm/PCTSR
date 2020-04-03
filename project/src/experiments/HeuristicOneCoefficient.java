package experiments;

import helpers.Heuristic;
import helpers.HeuristicOne;
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
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final Path RES_FILE = Paths.get("HeuristicOneResults.csv");

    private DataReader reader = new DataReader();
    private Place[] places = reader.getAllCompanies();
    private double[][] distanceMatrix = reader.getDistanceMatrix();
    private final int[] START_VERTICES = new int[]{0, 5, 10, 15, 25, 35, 45, 55, 70, 85};
    private final int[] AGENTS = new int[]{1, 2, 4, 8};
    private final int MIN_PROFIT = 290;

    public void run() {
        try {
            writer = new PrintWriter(Files.newBufferedWriter(RES_FILE, CHARSET));
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer.println(String.format("%s,%s,%s,%s,%s", "agents", "distanceMultiplier", "profitMultiplier", "time", "distance"));
        for (int dm = 1; dm <= 50; dm++) {
            final double distanceMultiplier = dm / 10d;
            for (int pm = 1; pm <= 50; pm++) {
                final double profitMultiplier = pm / 10d;
                calculateResults(new double[]{distanceMultiplier, profitMultiplier});
            }
        }
        writer.close();
    }

    private void calculateResults(double[] multipliers) {
        int numVertices = START_VERTICES.length;
        for (int agent : AGENTS) {
            // the distance for each route with the different starting vertex
            double[] routeResult = new double[numVertices];
            double[] routeTime = new double[numVertices];
            for (int j = 0; j < numVertices; j++) {
                int startV = START_VERTICES[j];
                // init heuristic method
                Heuristic h = new HeuristicOne(distanceMatrix, places, startV, agent, MIN_PROFIT, multipliers);
                double time = System.nanoTime();
                PathResult[] resultPath = h.getResultPaths();
                routeTime[j] = System.nanoTime() - time;
                double distance = 0d;
                for (PathResult res : resultPath) {
                    distance += res.getPathLength();
                }
                routeResult[j] = distance;
            }
            double res = 0d;
            for (double length : routeResult) {
                res += length;
            }
            double timeRes = 0d;
            for (double t : routeTime) {
                timeRes += t;
            }
            writeResultsToFile(agent, multipliers[0], multipliers[1], (timeRes / numVertices), (res / numVertices));
        }
    }

    private void writeResultsToFile(int agents, double distanceMultiplier, double profitMultiplier, double time, double distance) {
        writer.println(String.format("%d,%.2f,%.2f,%.2f,%.2f", agents, distanceMultiplier, profitMultiplier, time, distance));
    }

}
