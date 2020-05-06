package experiments;

public class HeuristicTwoSingleCoefficient extends HeuristicTwoExperiment {
    private String coefficient;

    public HeuristicTwoSingleCoefficient(String fileName, String coefficient) {
        super(fileName);
        this.coefficient = coefficient.toLowerCase();
    }

    @Override
    void runExperiment() {
        for (int agents = 1; agents <= 5; agents += 2) {
            switch (coefficient) {
                case "kmax":
                    runKmax(agents);
                    break;
                case "percent":
                    runPercent(agents);
                    break;
                case "ratio":
                    runRatio(agents);
                    break;
            }
        }
    }

    private void runKmax(int agents) {
        for (int kmax = 1; kmax < 30; kmax++) {
            int percent = 80;
            double ratio = 0.7;

            runCoefficientsExperiments(agents, kmax, percent, ratio);
        }
    }

    private void runPercent(int agents) {
        for (int percent = 1; percent <= 100; percent++) {
            int kmax = 10;
            double ratio = 0.7;

            runCoefficientsExperiments(agents, kmax, percent, ratio);
        }
    }

    private void runRatio(int agents) {
        for (int ratio = 1; ratio <= 100; ratio++) {
            int kmax = 10;
            int percent = 80;
            double mutationsRatio = ratio / 100d;

            runCoefficientsExperiments(agents, kmax, percent, mutationsRatio);
        }
    }
}
