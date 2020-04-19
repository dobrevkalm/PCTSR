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

public abstract class Experiment {
    private static PrintWriter writer;
    private DataReader reader = new DataReader();
    Place[] places = reader.getAllCompanies();
    double[][] distanceMatrix = reader.getDistanceMatrix();
    final int[] START_VERTICES = new int[]{ 0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90 };
    final int[] AGENTS = new int[]{ 1, 2, 4, 8, 10 };
    final int RUNS = 100;
    final int MIN_PROFIT = 290;

    // the result file is the name of the output file, e.g. results.csv
    Experiment(String resultFile) {
        init(resultFile);
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

    // all experiments must have a run method
    public abstract void run();

    // print a row to the output file. The string must be formatted to fit the file type.
    // For csv, use fx String.format("%s,%s,%d,%.2f", "someString", "anotherString", 234, 44.145)
    void printRow(String row) {
        writer.println(row);
    }

    // close the writer at the end of the experiment
    void endExperiment() {
        writer.close();
    }

    // sum all the doubles in an array. Used a lot during the experiments.
    double sumArrayDoubles(double[] array) {
        double res = 0d;
        for (double d : array) {
            res += d;
        }
        return res;
    }

    // used to calculate the time taken for calculating result path and its distance with given heuristic method object
    void calculateTimeAndDistanceResults(Heuristic h, int index, double[] timeResults, double[] distanceResult) {
        // get execution time
        double time = System.nanoTime();
        PathResult[] resultPath = h.getResultPaths();
        timeResults[index] = System.nanoTime() - time;
        // sum the total path length
        double routeDistance = 0d;
        for (PathResult res : resultPath) {
            routeDistance += res.getPathLength();
        }
        distanceResult[index] = routeDistance;
    }

    // just used to warm up the compiler before running the actual experiments
    void warmUp() {
        int profit = 290;
        // to warm up the compiler
        for (int i = 0; i < 10; i++) {
            Heuristic h = new HeuristicOne(distanceMatrix, places, i * 10, (i+1) * 2, profit);
            PathResult[] results = h.getResultPaths();
            // tmp double used to collect and print the results in order to make sure they get computed
            double tmp = 0d;
            for (PathResult res : results) {
                tmp += res.getPathLength();
            }
            h = new HeuristicTwo(distanceMatrix, places, i * 10, (i+1) * 2, profit);
            results = h.getResultPaths();
            for (PathResult res : results) {
                tmp += res.getPathLength();
            }
            System.out.println(tmp);
        }
    }
}
