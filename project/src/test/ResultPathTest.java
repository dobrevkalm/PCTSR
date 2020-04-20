package test;

import heuristics.Heuristic;
import heuristics.HeuristicOne;
import heuristics.HeuristicTwo;
import model.PathResult;
import model.Place;
import reader.DataReader;

import java.util.List;

public class ResultPathTest {

    public void getResultPath(boolean one, int startVertex, int agentsNumber, int minProfit) {
        final int ALL_PROFITS = 380;
        DataReader reader = new DataReader();
        double[][] distanceMatrix = reader.getDistanceMatrix();
        Place[] places = reader.getAllCompanies();
        if (minProfit > (ALL_PROFITS - places[startVertex].getFirmProfit())) {
            System.out.println("Not enough profit to collect");
        } else {
            Heuristic h;
            if (one) {
                h = new HeuristicOne(distanceMatrix, places, startVertex, agentsNumber, minProfit);
            } else {
                h = new HeuristicTwo(distanceMatrix, places, startVertex, agentsNumber, minProfit);
            }
            printResults(h.getClass().getName(), h.getResultPaths());
        }
    }

    private void printResults(String method, PathResult[] results) {
        printResultHeader(method);
        int agent = 0;
        for (PathResult pr : results) {
            System.out.println("\n Result for agent #" + agent++);
            printResultPath(pr.getResultPath());
        }
        printResultFooter(getTotalLength(results), getTotalProfitCollected(results));
    }

    private void printResultFooter(double length, double profit) {
        System.out.printf("%n%s %.2f", "Total length:", length);
        System.out.printf("%n%s %.2f%n", "Total profit:", profit);
        System.out.println("\n########################");
        System.out.println("########################\n\n");
    }

    private void printResultHeader(String method) {
        String name = method.substring(method.indexOf('.') + 1);
        System.out.println("\n########################");
        System.out.println("> " + name + " Results <");
        System.out.println("########################");
    }

    private double getTotalLength(PathResult[] results) {
        double length = 0.0;
        for(PathResult res: results) {
            length += res.getPathLength();
        }
        return length;
    }

    private double getTotalProfitCollected(PathResult[] results) {
        double profit = 0.0;
        for(PathResult res: results) {
            profit += res.getActualProfit();
        }
        return profit;
    }

    private void printResultPath(List<Place> places) {
        System.out.println(places.toString().replaceAll("\\[", "Start").replaceAll("\\]", "\nEnd"));
    }
}
