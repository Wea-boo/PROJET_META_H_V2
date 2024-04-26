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
    public void generateCSVRandomTestFile(String filePath, int numKnapsacks, int numItems, int maxKnapsackCapacity, int maxItemValue, int maxItemWeight) {
        try (FileWriter csvWriter = new FileWriter(filePath)) {
            csvWriter.append(numItems + "," + numKnapsacks + "\n"); // 1st line: number of items, number of knapsacks

            List<Item> items = generateItems(numItems, maxItemValue, maxItemWeight);
            List<Knapsack> knapsacks = generateKnapsacks(numKnapsacks, maxKnapsackCapacity);

            int totalWeight = items.stream().mapToInt(item -> item.weight).sum();
            int totalCapacity = knapsacks.stream().mapToInt(knapsack -> knapsack.capacity).sum();
            knapsacks = generateKnapsacks(numKnapsacks, Math.max(totalWeight, totalCapacity));

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
        int numKnapsacks = 5;
        int maxKnapsackCapacity = 100;
        int numItems = 20;
        int maxItemValue = 50;
        int maxItemWeight = 30;
        long seed = 42; // Change this value to get different instances

        InstanceGenerator generator = new InstanceGenerator(seed);
        List<Knapsack> knapsacks = generator.generateKnapsacks(numKnapsacks, maxKnapsackCapacity);
        List<Item> items = generator.generateItems(numItems, maxItemValue, maxItemWeight);

        System.out.println("Knapsacks:");
        for (Knapsack knapsack : knapsacks) {
            System.out.println(knapsack);
        }

        System.out.println("\nItems:");
        for (Item item : items) {
            System.out.println(item);
        }
    }
}
