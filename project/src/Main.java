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
        int minProfit = 150;

        Heuristic hOne = new HeuristicOne(distanceMatrix, places, startVertex, agentsNumber, minProfit);
        HeuristicTwo hTwo = new HeuristicTwo(distanceMatrix, places, startVertex, agentsNumber, minProfit);

        PathResult[] hOneResults = hOne.getResultPaths();

        PathResult[] hTwoResults = hTwo.getResultPaths();
        for (PathResult pr : hOneResults) {
            System.out.println("\nResult for agent -> ");
            printResultPath(pr.getResultPath());
        }

        System.out.println("\n ######################## ");
        System.out.println(" > HeuristicTwo Results ");
        System.out.println(" ######################## ");

        for (PathResult pr : hTwoResults) {
            System.out.println("\nResult for agent -> ");
            printResultPath(pr.getResultPath());
        }
    }

    private static void printResultPath(List<Place> places) {
        System.out.println(places.toString().replaceAll("\\[", "Start").replaceAll("\\]", "\nEnd"));
    }
}
