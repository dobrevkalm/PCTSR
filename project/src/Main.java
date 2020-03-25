import helpers.*;
import model.PathResult;
import model.Place;
import reader.DataReader;

import java.util.List;

public class Main {
    private static DataReader reader = new DataReader();

    public static void main(String[] args) {
        double[][] distanceMatrix = reader.getDistanceMatrix();
        Place[] places = reader.getAllCompanies();
        int startVertex = 5;
        int agentsNumber = 2;
        // in billions MAX is 380.70
        int minProfit = 200;

        Heuristic hOne = new HeuristicOne(distanceMatrix, places, startVertex, agentsNumber, minProfit);
        HeuristicTwo hTwo = new HeuristicTwo(distanceMatrix, places, startVertex, agentsNumber, minProfit);

        // Results for solution 1
        printResults(hOne.getClass().getName(), hOne.getResultPaths());
        // Results for solution 2
        printResults(hTwo.getClass().getName(), hTwo.getResultPaths());
    }

    private static void printResults(String method, PathResult[] results) {
        printResultHeader(method);
        int agent = 0;
        for (PathResult pr : results) {
            System.out.println("\n Result for agent #" + agent++);
            printResultPath(pr.getResultPath());
        }
        System.out.println("\nTotal length: " + getTotalLength(results));
        System.out.println(getTotalProfitCollected(results));
    }

    private static void printResultHeader(String method) {
        String name = method.substring(method.indexOf('.') + 1);
        System.out.println("\n ######################## ");
        System.out.println(" > " + name + " Results <");
        System.out.println(" ######################## ");
    }

    private static double getTotalLength(PathResult[] results) {
        double length = 0.0;
        for(PathResult res: results) {
            length += res.getPathLength();
        }
        return length;
    }

    private static double getTotalProfitCollected(PathResult[] results) {
        double profit = 0.0;
        for(PathResult res: results) {
            profit += res.getActualProfit();
        }
        return profit;
    }

    private static void printResultPath(List<Place> places) {
        System.out.println(places.toString().replaceAll("\\[", "Start").replaceAll("\\]", "\nEnd"));
    }
}
