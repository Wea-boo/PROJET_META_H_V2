package mkp;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GATParameterTest {
        public static void main(String[] args) {
        // Read knapsacks and items from CSV file
        String filePath = "p2_tests/15-150-test.csv";
        List<Knapsack> knapsacks = MultipleKnapsackProblem.readKnapsacksFromCSV(filePath);
        List<Item> items = MultipleKnapsackProblem.readItemsFromCSV(filePath);

        int numKnapsacks = knapsacks.size();
        int individualSize = items.size();

        int[] populationSizeOptions = {100, 500, 1000};
        float[] mutationRateOptions = {(float) 0.1, (float) 0.2, (float) 0.3};
        float[] crossoverRateOptions = {(float) 0.6, (float) 0.7, (float) 0.8};
        int[] generationOptions = {100, 250, 500, 750, 1000};

        try {
            FileWriter writer = new FileWriter("ga_param_test/GA_Test_Results.csv");
            writer.append("populationSize,mutationRate,crossoverRate,numGenerations,selectedPopulationSize,averageFitness,maxFitness\n");

            for (int populationSize : populationSizeOptions) {
                int selectedPopulationSize = populationSize / 2;
                for (float mutationRate : mutationRateOptions) {
                    for (float crossoverRate : crossoverRateOptions) {
                        for (int numGenerations : generationOptions) {
                            List<Integer> fitnessResults = new ArrayList<>();
                            for (int trial = 0; trial < 5; trial++) {
                                Individu bestIndividual = Algo_Genetique.Recherche_AlgoGenetique(numKnapsacks, individualSize, populationSize, numGenerations, selectedPopulationSize, crossoverRate, mutationRate, items, knapsacks);
                                fitnessResults.add((int) bestIndividual.GetFitness());
                            }
                            int averageFitness = (int) fitnessResults.stream().mapToInt(val -> val).average().orElse(0.0);
                            int maxFitness = fitnessResults.stream().mapToInt(val -> val).max().orElse(0);
                            writer.append(String.format(Locale.US, "%d,%.2f,%.2f,%d,%d,%d,%d%n", populationSize, mutationRate, crossoverRate, numGenerations, selectedPopulationSize, averageFitness, maxFitness));
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
