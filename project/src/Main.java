import experiments.*;
import GUI.GUI;
import test.*;

public class Main {
    public static void main(String[] args) {
        runGUI();
    }

    // runs the interface
    private static void runGUI() {
        GUI gui = new GUI();
        gui.run();
        /*Experiment e = new HeuristicTwoExperiment("test.csv");
        e.run();*/
    }

    /**
     * Run this method to get a console output of the result path calculated
     * @param one true for HeuristicOne, false for HeuristicTwo
     * @param startVertex the company we want to start at (0 to 90)
     * @param agentsNumber the number of agents collecting profit (1 to 10)
     * @param minProfit the desired collected profit
     */
    private static void runResultPathTest(boolean one, int startVertex, int agentsNumber, int minProfit) {
        ResultPathTest rpt = new ResultPathTest();
        rpt.getResultPath(one, startVertex, agentsNumber, minProfit);
    }

    // general tests for the two solutions and the data reader
    private static void runGeneralTests() {
        HeuristicTest ht = new HeuristicTest();
        ht.runTests();
        ReaderTest rt = new ReaderTest();
        rt.runTests();
    }
}
