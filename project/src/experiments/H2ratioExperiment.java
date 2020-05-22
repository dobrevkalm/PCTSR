package experiments;

public class H2ratioExperiment extends HeuristicTwoExperiment {

    public H2ratioExperiment (String filename) {
        super(filename);
    }

    @Override
    public void run() {
        // header row
        printRow(String.format("%s,%s,%s,%s,%s,%s", "agents", "kmax", "percent", "ratio", "profit", "distance"));
        // run experiment
        runRatioExperiment();
        // close the writer and end the experiment
        endExperiment();
    }

    private void runRatioExperiment() {
        for (int agents : AGENTS) {
            int kmax = -1;
            int percent = -1;
            for (int m = 0; m <= 10; m++) {
                double mutationsRatio = -1;

                if (m > 0) {
                    // thank you Java for doing this to us -.-
                    mutationsRatio = m / 10d;
                }
                // indicate what is running
                System.out.printf("@@@ RUN -> %d <> %d <> %d <> %.2f%n", agents, kmax, percent, mutationsRatio);
                // run experiments with the different profits using the above coefficients
                runCoefficientsExperiments(agents, kmax, percent, mutationsRatio);
            }
        }
    }
}
