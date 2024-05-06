package mkp;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InstanceGenerator {
    private final Random random;

    public InstanceGenerator(long seed) {
        this.random = new Random(seed);
    }

    public List<Knapsack> generateKnapsacks(int numKnapsacks, int maxCapacity) {
        List<Knapsack> knapsacks = new ArrayList<>();
        for (int i = 0; i < numKnapsacks; i++) {
            int capacity = random.nextInt(maxCapacity + 1);
            knapsacks.add(new Knapsack(capacity));
        }
        return knapsacks;
    }

    public List<Item> generateItems(int numItems, int maxValue, int maxWeight) {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < numItems; i++) {
            int value = random.nextInt(maxValue + 1);
            int weight = random.nextInt(maxWeight + 1);
            items.add(new Item(value, weight));
        }
        return items;
    }
    public void generateCSVRandomTestFile(String filePath, int numKnapsacks, int numItems) {
        try (FileWriter csvWriter = new FileWriter(filePath)) {
            csvWriter.append(numItems + "," + numKnapsacks + "\n"); // 1st line: number of items, number of knapsacks
    
            List<Item> items = new ArrayList<>();
            List<Knapsack> knapsacks = new ArrayList<>();
    
            // Generate items with random weights and values between 0 and 200
            int totalWeight = 0;
            for (int i = 0; i < numItems; i++) {
                int weight = random.nextInt(201); // Random weight between 0 and 200
                int value = random.nextInt(201); // Random value between 0 and 200
                totalWeight += weight;
                items.add(new Item(value, weight));
            }
    
            // Generate knapsacks with capacities between 20% and 50% of the total weight
            // int minCapacity = (int) (0.2 * totalWeight);
            // int maxCapacity = (int) (0.5 * totalWeight);
            // for (int i = 0; i < numKnapsacks; i++) {
            //     int capacity = minCapacity + random.nextInt(maxCapacity - minCapacity + 1);
            //     knapsacks.add(new Knapsack(capacity));
            // }
            int totalCapacity = (int) (totalWeight * 0.5); // E.g., 50% of total item weight
            for (int j = 0; j < numKnapsacks; j++) {
                // Distribute capacity unevenly among knapsacks
                int capacity = (totalCapacity / numKnapsacks) + random.nextInt(totalCapacity / numKnapsacks);
                knapsacks.add(new Knapsack(capacity));
            }
            for (Knapsack knapsack : knapsacks) {
                csvWriter.append(String.valueOf(knapsack.capacity));
                if (knapsack != knapsacks.get(knapsacks.size() - 1)) {
                    csvWriter.append(",");
                } else {
                    csvWriter.append("\n");
                }
            }
    
            for (Item item : items) {
                csvWriter.append(item.weight + "," + item.value + "\n");
            }
    
            csvWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int numKnapsacks = 20;
        int numItems = 200;
        long seed = 42; // Change this value to get different instances

        InstanceGenerator generator = new InstanceGenerator(seed);
        String instanceFolderPath = "p2_tests";
        generator.generateCSVRandomTestFile(instanceFolderPath + "/20-200-test.csv", numKnapsacks, numItems);
    }
}
