import GUI.GUI;
import experiments.*;
import test.*;

public class Main {
    public static void main(String[] args) {
        //runGUI();
        runExperiment();
    }

    // runs the interface
    private static void runGUI() {
        GUI gui = new GUI();
        gui.run();
    }

    // to run an experiment
    private static void runExperiment() {
        // comment/uncomment the experiments you'd like to run
        Experiment[] experiments = new Experiment[] {
                //new HeuristicOneExperiment("h1.csv"),
                //new HeuristicOneExperiment("h1.csv"),
                //new HeuristicComparison("hc.csv"),
                new HeuristicTwoSingleCoefficient("h2_kmax.csv", "kmax"),
                new HeuristicTwoSingleCoefficient("h2_percent.csv", "percent"),
                new HeuristicTwoSingleCoefficient("h2_ratio.csv", "ratio")
        };

        for (Experiment e : experiments) {
            e.run();
        }
    }

    /**
     * Run this method to get a console output of the result path calculated
     * @param heuristic the method you want to test - [one, two, three]
     * @param startVertex the company we want to start at (0 to 90)
     * @param agentsNumber the number of agents collecting profit (1 to 10)
     * @param minProfit the desired collected profit
     */
    private static void runResultPathTest(String heuristic, int startVertex, int agentsNumber, int minProfit) {
        ResultPathTest rpt = new ResultPathTest();
        rpt.getResultPath(heuristic, startVertex, agentsNumber, minProfit);
    }

    // general tests for the two solutions and the data reader
    private static void runGeneralTests() {
        HeuristicTest ht = new HeuristicTest();
        ht.runTests();
        ReaderTest rt = new ReaderTest();
        rt.runTests();
    }
}
