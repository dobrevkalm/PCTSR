package experiments;

import helpers.Heuristic;
import helpers.HeuristicTwo;
import model.PathResult;

public class HeuristicTwoExperiment extends Experiment {

    public HeuristicTwoExperiment(String fileName) {
        super(fileName);
    }

    @Override
    public void run() {
        // header row
        printRow(String.format("%s,%s,%s,%s,%s, %s", "agents", "kmax", "percent", "ratio", "distance", "time"));
        // warm java
        warmUp();
        // run experiment
        runExperiment();
        // close the writer and end the experiment
        endExperiment();
    }

    private void runExperiment() {
        for (int agents : AGENTS) {
            for (int kmax = 1; kmax <= 15; kmax++) {
                for (int percent = 10; percent <= 100; percent += 10) {
                    for (int m = 1; m <= 10; m++) {
                        // thank you java for making us do this due to your wonderful work with doubles -.-
                        double mutationsRatio = m / 10d;
                        // only make 10 runs for general results. Might do 100 and when running the final experiment
                        int runs = RUNS/10;
                        // store all the results
                        double[] finalDistanceResults = new double[runs];
                        double[] finalTimeResults = new double[runs];

                        makeRuns(finalDistanceResults, finalTimeResults, agents, kmax, percent, mutationsRatio);

                        double distanceResult = sumArrayDoubles(finalDistanceResults) / (runs);
                        double timeResult = sumArrayDoubles(finalTimeResults) / (runs);

                        // print row with test result
                        printRow(String.format("%d,%d,%d,%.2f,%.2f,%.2f", agents, kmax, percent, mutationsRatio, distanceResult, timeResult));
                    }
                }
            }
        }
    }

    private void makeRuns(double[] finalDistanceResults, double[] finalTimeResults, int agents, int kmax, int percent, double mutationsRatio) {
        System.out.printf("@@@ RUN WITH %d - %d - %.2f @@@%n", kmax, percent, mutationsRatio);
        for (int i = 0; i < RUNS/10; i++) {
            int numTrials = START_VERTICES.length;
            double[] runTimeResults = new double[numTrials];
            double[] runDistanceResults = new double[numTrials];

            // calculate results from every starting vertex
            for (int j = 0; j < numTrials; j++) {
                int startV = START_VERTICES[j];
                Heuristic h = new HeuristicTwo(distanceMatrix, places, startV, agents, MIN_PROFIT, kmax, percent, mutationsRatio);
                calculateTimeAndDistanceResults(h, j, runTimeResults, runDistanceResults);
            }

            // average results from all vertices
            double timeResult = sumArrayDoubles(runTimeResults) / numTrials;
            double distanceResult = sumArrayDoubles(runDistanceResults) / numTrials;

            // fill the final result arrays
            finalDistanceResults[i] = distanceResult;
            finalTimeResults[i] = timeResult;
        }
    }
}
