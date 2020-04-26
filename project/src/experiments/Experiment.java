package experiments;

import heuristics.Heuristic;
import heuristics.HeuristicOne;
import heuristics.HeuristicTwo;
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
import java.util.Random;

public abstract class Experiment {
    private static PrintWriter writer;
    private DataReader reader = new DataReader();
    Place[] places = reader.getAllCompanies();
    double[][] distanceMatrix = reader.getDistanceMatrix();
    // number of agents to experiment with
    final int[] AGENTS = new int[]{ 1, 2, 3, 4, 6, 8, 10 };
    // the number of vertices
    private final int NUM_VERTICES = 91;
    // the different amount of profit we will be looking for when making an experimental run
    int[] PROFITS;
    // the different starting vertices we will make an experimental run from
    int[] START_VERTICES;
    // to store our distance results for each experimental run; we will have as many results as the number of starting vertices
    double[] distanceResults = new double[NUM_VERTICES];

    // the result file is the name of the output file, e.g. results.csv
    Experiment(String resultFile) {
        init(resultFile);
        initProfitArray();
        initStartVertices();
    }

    // instantiate the print writer
    private void init(String resultFile) {
        Charset CHARSET = StandardCharsets.UTF_8;
        Path RES_FILE = Paths.get(resultFile);
        try {
            writer = new PrintWriter(Files.newBufferedWriter(RES_FILE, CHARSET));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // the desired profit we will be looking for will start at 5 and go up to 295, increasing by 5 each time (eg. 5, 10, 15, ..., 295)
    private void initProfitArray() {
        int MAX_PROFIT = 299;
        PROFITS = new int[MAX_PROFIT / 5];
        int idx = 0;
        for (int i = 5; i <= MAX_PROFIT; i += 5) {
            PROFITS[idx] = i;
            idx++;
        }
    }

    // we will make an experimental run from each of our 90 starting vertices
    private void initStartVertices() {
        START_VERTICES = new int[NUM_VERTICES];
        for (int i = 0; i < NUM_VERTICES; i++) {
            START_VERTICES[i] = i;
        }
    }

    // re-instantiate a clean distance array after each time we calculate results for an experimental run
    void resetDistanceArray(){
        this.distanceResults = new double[NUM_VERTICES];
    }

    // used to run the experiment
    public abstract void run();

    // print a row to the output file. The string must be formatted to fit the file type.
    // For csv, use fx String.format(Locale.US, "%s,%s,%d,%.2f", "someString", "anotherString", 234, 44.145)
    void printRow(String row) {
        writer.println(row);
    }

    // close the writer at the end of the experiment
    void endExperiment() {
        writer.close();
    }

    // sum um all the distances in the distance array and return the average
    double calculateAverageDistance() {
        double res = 0d;
        for (double d : distanceResults) {
            res += d;
        }
        return res / distanceResults.length;
    }

    // calculate distance results for an experimental run with given heuristic method
    void calculateDistanceResults(Heuristic h, int index) {
        PathResult[] resultPath = h.getResultPaths();
        // sum the total path length
        double routeDistance = 0d;
        for (PathResult res : resultPath) {
            routeDistance += res.getPathLength();
        }
        distanceResults[index] = routeDistance;
    }

    // just used to warm up the compiler before running the actual experiments
    void warmUp() {
        Random rnd = new Random(12345678);
        // tmp double used to collect and print the results in order to make sure they get computed
        int tmp1 = 0, tmp2 = 0;

        for (int i = 0; i < 100; i++) {
            int profit = rnd.nextInt(290);
            int startV = rnd.nextInt(90);
            Heuristic h = new HeuristicOne(distanceMatrix, places, startV, (i+1) * 2, profit);
            PathResult[] results = h.getResultPaths();
            for (PathResult res : results) {
                tmp1 += res.getPathLength();
            }
            h = new HeuristicTwo(distanceMatrix, places, startV, (i+1) * 2, profit);
            results = h.getResultPaths();
            for (PathResult res : results) {
                tmp2 += res.getPathLength();
            }
        }
        System.out.printf("Warmed up > %d <> %d <%n%n", tmp1, tmp2);
    }
}