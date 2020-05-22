package experiments;

public class H2percentExperiment extends HeuristicTwoExperiment {

    public H2percentExperiment (String filename) {
        super(filename);
    }

    @Override
    public void run() {
        // header row
        printRow(String.format("%s,%s,%s,%s,%s,%s", "agents", "kmax", "percent", "ratio", "profit", "distance"));
        // warm java
        warmUp();
        // run experiment
        runPercentExperiment();
        // close the writer and end the experiment
        endExperiment();
    }

    private void runPercentExperiment() {
        for (int agents : AGENTS) {
            int kmax = -1;
            for (int percent = 0; percent <= 100; percent += 5) {
                double mutationsRatio = -1;
                // indicate what is running
                System.out.printf("@@@ RUN -> %d <> %d <> %d <> %.2f%n", agents, kmax, percent > 0 ? -1 : percent, mutationsRatio);
                // run experiments with the different profits using the above coefficients
                runCoefficientsExperiments(agents, kmax, percent, mutationsRatio);
            }
        }
    }
}
