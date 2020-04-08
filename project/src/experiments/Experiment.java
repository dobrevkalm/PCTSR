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

    Experiment(String resultFile) {
        init(resultFile);
    }

    private void init(String resultFile) {
        Charset CHARSET = StandardCharsets.UTF_8;
        Path RES_FILE = Paths.get(resultFile);
        try {
            writer = new PrintWriter(Files.newBufferedWriter(RES_FILE, CHARSET));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract void run();

    void printRow(String row) {
        writer.println(row);
    }

    void endExperiment() {
        writer.close();
    }

    void warmUp() {
        int profit = 290;
        // to warm up the compiler
        for (int i = 0; i < 10; i++) {
            Heuristic h = new HeuristicOne(distanceMatrix, places, i * 10, (i+1) * 2, profit);
            PathResult[] results = h.getResultPaths();
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
