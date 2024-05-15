package mkp;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MetaComparison {
        private static final int RUNS_PER_FILE = 5;

    public static void main(String[] args) {
        String[] files = {"5-50-test.csv", "10-100-test.csv", "15-150-test.csv", "20-200-test.csv", "25-250-test.csv", "30-300-test.csv", "35-350-test.csv", "40-400-test.csv", "45-450-test.csv", "50-500-test.csv"};
        String[] metrics = {"ga_bso_comparison/average_total_value.csv", "ga_bso_comparison/average_time_ms.csv", "ga_bso_comparison/average_time_s.csv"};

        try {
            FileWriter valueWriter = new FileWriter(metrics[0]);
            FileWriter timeMsWriter = new FileWriter(metrics[1]);
            FileWriter timeSWriter = new FileWriter(metrics[2]);

            // Write headers
            valueWriter.append("Filename,BSO,GA\n");
            timeMsWriter.append("Filename,BSO,GA\n");
            timeSWriter.append("Filename,BSO,GA\n");

            for (String file : files) {
                double bsoTotalValueSum = 0, gaTotalValueSum = 0;
                long bsoTimeMsSum = 0, gaTimeMsSum = 0;
                double bsoTimeSSum = 0, gaTimeSSum = 0;

                for (int i = 0; i < RUNS_PER_FILE; i++) {
                    String filePath = "p2_tests/" + file;
                    List<Knapsack> knapsacks = MultipleKnapsackProblem.readKnapsacksFromCSV(filePath);
                    List<Item> items = MultipleKnapsackProblem.readItemsFromCSV(filePath);

                    long startTime = System.currentTimeMillis();
                    Solution bsoSolution = new BSO().searchBSO(knapsacks, items, 15, 8, 2, 3000, 1000);
                    long bsoDuration = System.currentTimeMillis() - startTime;
                    bsoTotalValueSum += bsoSolution.calculateValue(items);
                    bsoTimeMsSum += bsoDuration;
                    bsoTimeSSum += bsoDuration / 1000.0;

                    startTime = System.currentTimeMillis();
                    //new Algo_Genetique();
                    Individu gaIndividu = Algo_Genetique.Recherche_AlgoGenetique(knapsacks.size(), items.size(), 1000, 500, 500, 0.8f, 0.3f, items, knapsacks);
                    long gaDuration = System.currentTimeMillis() - startTime;
                    gaTotalValueSum += gaIndividu.Fitness;
                    gaTimeMsSum += gaDuration;
                    gaTimeSSum += gaDuration / 1000.0;
                }
                System.out.println("File " + file + " done");
                // Compute averages
                valueWriter.append(String.format("%s,%d,%d%n", file, (int) bsoTotalValueSum / RUNS_PER_FILE, (int) gaTotalValueSum / RUNS_PER_FILE));
                timeMsWriter.append(String.format("%s,%d,%d%n", file, bsoTimeMsSum / RUNS_PER_FILE, gaTimeMsSum / RUNS_PER_FILE));
                timeSWriter.append(String.format(Locale.US, "%s,%f,%f%n", file, bsoTimeSSum / RUNS_PER_FILE, gaTimeSSum / RUNS_PER_FILE));
            }

            valueWriter.close();
            timeMsWriter.close();
            timeSWriter.close();
        } catch (IOException e) {
            System.err.println("Error writing to CSV files: " + e.getMessage());
        }
    }
}
