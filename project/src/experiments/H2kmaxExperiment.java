package experiments;

public class H2kmaxExperiment  extends HeuristicTwoExperiment {

    public H2kmaxExperiment (String filename) {
        super(filename);
    }

    @Override
    public void run() {
        // header row
        printRow(String.format("%s,%s,%s,%s,%s,%s", "agents", "kmax", "percent", "ratio", "profit", "distance"));
        // warm java
        warmUp();
        // run experiment
        runKmaxExperiment();
        // close the writer and end the experiment
        endExperiment();
    }

    private void runKmaxExperiment() {
        for (int agents : AGENTS) {
            for (int kmax = 1; kmax <= 40; kmax += 2) {
                int percent = -1;
                // thank you java for making us do this -.-
                double mutationsRatio = 0.3;
                // indicate what is running
                System.out.printf("@@@ RUN -> %d <> %d <> %d <> %.2f%n", agents, kmax, percent, mutationsRatio);
                // run experiments with the different profits using the above coefficients
                runCoefficientsExperiments(agents, kmax, percent, mutationsRatio);
            }
        }
    }
}
