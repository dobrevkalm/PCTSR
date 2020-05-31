package test;

import heuristics.*;
import model.*;
import reader.*;

public class HeuristicTest {
    private DataReader reader = new DataReader();

    public void runTests() {
        double[][] matrix = reader.getDistanceMatrix();
        Place[] places = reader.getAllCompanies();
        int vertex = 0;
        int agents = 1;
        int profit = 100;

        double P = 0d;
        for (Place place : places) {
            P += place.getFirmProfit();
        }

        final int V = places.length;
        final int A = 10;

        // we want to run each heuristic solution from each starting vertex, with different number of agents and different profit to be collected
        while (vertex < V) {
            final int MAX_P = (int) (P - places[vertex].getFirmProfit());
            while (agents < A) {
                while (profit < MAX_P) {
                    Heuristic[] H = new Heuristic[]{
                            new HeuristicOne(matrix, places, vertex, agents, profit),
                            new HeuristicTwo(matrix, places, vertex, agents, profit),
                            new HeuristicThree(matrix, places, vertex, agents, profit),
                            new HeuristicFour(matrix, places, vertex, agents, profit)
                    };

                    for (Heuristic h : H) {
                        h.getResultPaths();
                    }
                    profit += 5;
                }
                profit = 100;
                agents++;
            }
            agents = 1;
            vertex++;
        }
    }
}
