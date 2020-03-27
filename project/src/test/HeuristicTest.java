package test;

import helpers.*;
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

        final int P = 380;
        final int V = places.length;
        // we have 91 companies so we should not need more than 10 agents...
        final int A = 10;

        while (vertex < V) {
            final int MAX_P = (int) (P - places[vertex].getFirmProfit());
            while (agents < A) {
                while(profit < MAX_P) {
                    Heuristic h1 = new HeuristicOne(matrix, places, vertex, agents, profit);
                    Heuristic h2 = new HeuristicOne(matrix, places, vertex, agents, profit);
                    System.out.printf("%n| V = %d | A = %d | P = %d |%n", vertex, agents, profit);
                    h1.getResultPaths();
                    System.out.println("------------------------");
                    System.out.println("--- ### ONE DONE ### ---");
                    h2.getResultPaths();
                    System.out.println("--- ### TWO DONE ### ---");
                    System.out.println("------------------------");
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
