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

        final int P = 380;
        final int V = places.length;
        // we have 91 companies so we should not need more than 10 agents...
        final int A = 10;

        while (vertex < V) {
            final int MAX_P = (int) (P - places[vertex].getFirmProfit());
            while (agents < A) {
                while(profit < MAX_P) {
                    Heuristic[] H = new Heuristic[] {
                        new HeuristicOne(matrix, places, vertex, agents, profit),
                        new HeuristicTwo(matrix, places, vertex, agents, profit),
                        new HeuristicThree(matrix, places, vertex, agents, profit)
                    };
                    System.out.printf("%n| V = %d | A = %d | P = %d |%n", vertex, agents, profit);
                    for (Heuristic h : H) {
                        test(h);
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

    private void test(Heuristic h) {
        h.getResultPaths();
        System.out.println("--> " + h.getMethodName() + " DONE <---");
    }
}
