package mkp;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BSOTParameterTest {

    public static void main(String[] args) {
        // read knapacks and items from csv file
        String filePath = "p2_tests/15-150-test.csv";
        List<Knapsack> m =  MultipleKnapsackProblem.readKnapsacksFromCSV(filePath);
        List<Item> n = MultipleKnapsackProblem.readItemsFromCSV(filePath);
        // int numBees = 7;
        // int flip = 7;
        // int maxSteps = 5000;
        // int maxChance = 3;
        // int maxIterations = 10000;
        // BSO bso = new BSO();
        // Solution solution = bso.searchBSO(m, n, numBees, flip, maxChance, maxSteps, maxIterations);
        // System.out.println("Final solution: " + solution.calculateValue(n));

        int[] numBeesOptions = {5, 10, 15};
        int[] flipOptions = {4, 6, 8};
        int[] maxStepsOptions = {1000, 2000, 3000};
        int[] maxChanceOptions = {2, 3, 4};
        int[] maxIterationsOptions = {100, 250, 500, 750, 1000};

        try {
            FileWriter writer = new FileWriter("bso_param_test/BSO_Test_Results3.csv");
            writer.append("numBees,flip,maxSteps,maxChance,maxIterations,averageFitness,maxFitness\n");

            for (int numBees : numBeesOptions) {
                for (int flip : flipOptions) {
                    for (int maxSteps : maxStepsOptions) {
                        for (int maxChance : maxChanceOptions) {
                            for (int maxIterations : maxIterationsOptions) {
                                List<Integer> fitnessResults = new ArrayList<>();
                                for (int trial = 0; trial < 5; trial++) {
                                    BSO bso = new BSO();
                                    Solution solution = bso.searchBSO(m, n, numBees, flip, maxChance, maxSteps, maxIterations);
                                    fitnessResults.add(solution.getTotalValue());
                                }
                                int averageFitness = (int) fitnessResults.stream().mapToInt(val -> val).average().orElse(0.0);
                                int maxFitness = fitnessResults.stream().mapToInt(val -> val).max().orElse(0);
                                writer.append(String.format("%d,%d,%d,%d,%d,%d,%d%n", numBees, flip, maxSteps, maxChance, maxIterations, averageFitness, maxFitness));
                            }
                        }
                    }
                }
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.err.println("Error while writing to CSV file: " + e.getMessage());
        }
    }
}
