package mkp;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class FinalTest {
    private static final int RUNS_PER_FILE = 5;
    private static final long MAX_EXECUTION_TIME_SECONDS = 7200; // 2 hours
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    // atomic integer for A*'s score
    private static AtomicInteger astarScore = new AtomicInteger(0);

    public static void main(String[] args) {
        String[] files = {"3-20-test.csv"};
        String outputFile = "all_comparison/final_test_results.csv";

        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.append("Filename,BSO_time,GA_time,A*_time,BSO_score,GA_score,A*_score\n");

            for (String file : files) {
                String filePath = "p2_tests/" + file;
                List<Knapsack> knapsacks = MultipleKnapsackProblem.readKnapsacksFromCSV(filePath);
                List<Item> items = MultipleKnapsackProblem.readItemsFromCSV(filePath);
                State initialState = new State(knapsacks, items);

                double bsoTimeSumS = 0, gaTimeSumS = 0;
                int bsoScore = 0, gaScore = 0;
                double astarResult;

                for (int i = 0; i < RUNS_PER_FILE; i++) {
                    long startTime = System.currentTimeMillis();
                    Solution bsoSolution = new BSO().searchBSO(knapsacks, items, 15, 8, 2, 3000, 1000);
                    bsoScore += bsoSolution.calculateValue(items);
                    long bsoDuration = System.currentTimeMillis() - startTime;
                    bsoTimeSumS += bsoDuration / 1000.0;

                    startTime = System.currentTimeMillis();
                    Individu gaIndividu = Algo_Genetique.Recherche_AlgoGenetique(knapsacks.size(), items.size(), 1000, 500, 500, 0.8f, 0.3f, items, knapsacks);
                    long gaDuration = System.currentTimeMillis() - startTime;
                    gaScore += gaIndividu.Fitness;
                    gaTimeSumS += gaDuration / 1000.0;
                }

                System.out.println("Moment of truth...");
                astarResult = runAlgorithmWithTimeout(executor, () -> MultipleKnapsackProblem.aStarSearchTesting(initialState, items.size()), MAX_EXECUTION_TIME_SECONDS * 1000L);

                double bsoAverageTimeS = bsoTimeSumS / RUNS_PER_FILE;
                double gaAverageTimeS = gaTimeSumS / RUNS_PER_FILE;
                bsoScore /= RUNS_PER_FILE;
                gaScore /= RUNS_PER_FILE;

                writer.append(String.format(Locale.US, "%s,%f,%f,%f,%d,%d,%d%n", file, bsoAverageTimeS, gaAverageTimeS, astarResult,bsoScore,gaScore,astarScore.get()));
            }
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }

    private static double runAlgorithmWithTimeout(ExecutorService executor, Callable<MultipleKnapsackProblem.SearchResult> task, long timeout) {
        Future<MultipleKnapsackProblem.SearchResult> future = executor.submit(task);

        try {
            MultipleKnapsackProblem.SearchResult result = future.get(timeout, TimeUnit.MILLISECONDS);
            astarScore.set(result.bestValue);
            return result.executionTime / 1000.0; // Convert to seconds
        } catch (TimeoutException e) {
            future.cancel(true);
            return -2; // Mark as timeout
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            future.cancel(true);
            return -2; // Handle thread interruption similarly to timeout
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof OutOfMemoryError) {
                return -1; // Mark as out of memory
            }
            cause.printStackTrace();
            return -1; // General error case
        }
    }
}