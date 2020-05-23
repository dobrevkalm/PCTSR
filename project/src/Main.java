import GUI.GUI;
import experiments.*;
import test.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            runGUI();
        } else {
            switch (args[0]) {
                case "-t":
                    runGeneralTests();
                    break;
                case "-rp":
                    runResultPathTest();
                    break;
                case "-e": {
                    if (args.length == 1) {
                        runAllExperiments();
                    } else {
                        runExperiment(Integer.parseInt(args[1]));
                    }
                    break;
                }
                default:
                    wrongArgument();
                    System.exit(0);
            }
        }
    }

    // runs the interface
    private static void runGUI() {
        GUI gui = new GUI();
        gui.run();
    }

    private static void runAllExperiments() {
        Experiment[] experiments = new Experiment[] {
                new HeuristicOneExperiment("heuristic_one.csv"),
                new H2kmaxExperiment("heuristic_two_kmax_random.csv", true),
                new H2kmaxExperiment("heuristic_two_kmax_fixedRatio.csv", false),
                new H2percentExperiment("heuristic_two_percent.csv"),
                new H2ratioExperiment("heuristic_two_ratio.csv"),
                new HeuristicComparison("heuristic_comparison.csv")
        };

        for (Experiment e : experiments) {
            e.run();
        }
    }

    private static void runExperiment(int experiment) {
        String timeStamp = new SimpleDateFormat("dd-MM-yyyy HH.mm.ss").format(new Date());
        String fileName;
        Experiment e = null;

        switch (experiment) {
            case 1: {
                fileName = "h_one_" + timeStamp + ".csv";
                e = new HeuristicOneExperiment(fileName);
                break;
            }
            case 2: {
                fileName = "h_two_kmax_rand_" + timeStamp + ".csv";
                e = new H2kmaxExperiment(fileName, true);
                break;
            }
            case 3: {
                fileName = "h_two_kmax_fix_" + timeStamp + ".csv";
                e = new H2kmaxExperiment(fileName, false);
                break;
            }
            case 4: {
                fileName = "h_two_percent_" + timeStamp + ".csv";
                e = new H2percentExperiment(fileName);
                break;
            }
            case 5: {
                fileName = "h_two_ratio_" + timeStamp + ".csv";
                e = new H2ratioExperiment(fileName);
                break;
            }
            case 6: {
                fileName = "h_comparison_" + timeStamp + ".csv";
                e = new HeuristicComparison(fileName);
                break;
            }
            default:
                System.out.println("Wrong experiment selected!\nAvailable: [1, 2, 3, 4, 5, 6]");
                System.exit(0);
        }

        e.run();
    }

    private static void runResultPathTest() {
        Scanner sc = new Scanner(System.in);

        // select heuristic method
        System.out.println(" - Choose heuristic method.\n - Enter one, two, three or four:");
        String heuristic = sc.nextLine();

        // select the starting vertex
        System.out.println(" - Choose starting vertex.\n - Enter a number between 0 and 90:");
        int startVertex = Integer.parseInt(sc.nextLine());

        // select the number of agents
        System.out.println(" - Choose the number of sales representatives.\n - Enter a number between 1 and 10:");
        int agentsNumber = Integer.parseInt(sc.nextLine());

        // select desired profit
        System.out.println(" - Choose desired minimum collected profit.\n - Enter value between 5 and 299:");
        int minProfit = Integer.parseInt(sc.nextLine());

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

    private static void wrongArgument() {
        System.out.println("## Wrong argument entered ##");
        System.out.println("## Available arguments: [-t, -rp, -e, -e {number} ##");
        System.out.println("## Visit https://github.com/dobrevkalm/PCTSR for more info ##");
    }
}
