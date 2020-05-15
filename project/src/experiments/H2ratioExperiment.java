package experiments;

public class H2ratioExperiment extends HeuristicTwoExperiment {

    public H2ratioExperiment (String filename) {
        super(filename);
    }

    @Override
    public void run() {
        // header row
        printRow(String.format("%s,%s,%s,%s,%s,%s", "agents", "kmax", "percent", "ratio", "profit", "distance"));
        // warm java
        warmUp();
        // run experiment
        runRatioExperiment();
        // close the writer and end the experiment
        endExperiment();
    }

    private void runRatioExperiment() {
        for (int agents : AGENTS) {
            int kmax = 20;
            int percent = -1;
            for (int m = 0; m <= 10; m++) {
                // thank you java for making us do this -.-
                double mutationsRatio = -1;

                if (m > 0) {
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
