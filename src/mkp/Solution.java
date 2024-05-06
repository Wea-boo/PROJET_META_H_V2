package mkp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Solution implements Comparable<Solution> {
    public int[] knapsackAssignments;
    private int totalValue = -1;
    private final Random random;

    public Solution(int numItems) {
        random = new Random();
        knapsackAssignments = new int[numItems];
        // Initialize all items as not assigned to any knapsack
        for (int i = 0; i < numItems; i++) {
            knapsackAssignments[i] = -1;
        }
    }
    public int getTotalValue() {
        return totalValue;
    }
    public int calculateValue(List<Item> items) {
        if(totalValue != -1) {
            //System.out.println("already calculated");
            return totalValue;
        }
        totalValue = 0;
        for (int i = 0; i < knapsackAssignments.length; i++) {
            if (knapsackAssignments[i] != -1) {
                totalValue += items.get(i).value;
            }
        }
        return totalValue;
    }
    public void initializeRandomSolution(List<Knapsack> knapsacks, List<Item> items) {
        for (int i = 0; i < knapsackAssignments.length; i++) {
            int randomValue = random.nextInt(100);
            if (randomValue != 0) {
                // 1/10 chance of not assigning the item to any knapsack
                knapsackAssignments[i] = random.nextInt(knapsacks.size());
            }
        }
        
        repairSolution(knapsacks, items);
        calculateValue(items);
    }

    public void cacheInvalidate(){
        totalValue = -1;
    }

    public void repairSolution(List<Knapsack> knapsacks, List<Item> items) {
        // Gather the overweight knapsacks and the items assigned to them
        List<Integer> overweightKnapsackIndices = new ArrayList<>();
        Map<Integer, List<Integer>> itemIndicesByKnapsack = new HashMap<>();
    
        for (int i = 0; i < knapsackAssignments.length; i++) {
            int knapsackIndex = knapsackAssignments[i];
            if (knapsackIndex != -1) {
                Item item = items.get(i);
                Knapsack knapsack = knapsacks.get(knapsackIndex);
                int currentWeight = itemIndicesByKnapsack.getOrDefault(knapsackIndex, new ArrayList<>()).stream()
                        .mapToInt(j -> items.get(j).weight)
                        .sum();
    
                if (currentWeight + item.weight > knapsack.capacity) {
                    overweightKnapsackIndices.add(knapsackIndex);
                }
    
                itemIndicesByKnapsack.computeIfAbsent(knapsackIndex, k -> new ArrayList<>()).add(i);
            }
        }
    
        // For each overweight knapsack, randomly remove or reassign items until it's no longer overweight
        for (int knapsackIndex : overweightKnapsackIndices) {
            List<Integer> itemIndices = itemIndicesByKnapsack.get(knapsackIndex);
            Knapsack knapsack = knapsacks.get(knapsackIndex);
            int currentWeight = itemIndices.stream()
                    .mapToInt(j -> items.get(j).weight)
                    .sum();
    
            while (currentWeight > knapsack.capacity) {
                int randomIndex = random.nextInt(itemIndices.size());
                int itemIndex = itemIndices.remove(randomIndex);
                currentWeight -= items.get(itemIndex).weight;
    
                // Try to reassign the removed item to another knapsack
                boolean assigned = false;
                for (int j = 0; j < knapsacks.size(); j++) {
                    if (j != knapsackIndex && itemIndicesByKnapsack.get(j).stream()
                            .mapToInt(k -> items.get(k).weight)
                            .sum() + items.get(itemIndex).weight <= knapsacks.get(j).capacity) {
                        itemIndicesByKnapsack.get(j).add(itemIndex);
                        knapsackAssignments[itemIndex] = j;
                        assigned = true;
                        break;
                    }
                }
    
                // If the item cannot be assigned to any knapsack, leave it unassigned
                if (!assigned) {
                    knapsackAssignments[itemIndex] = -1;
                }
            }
        }
    }

    public boolean isFeasible(List<Knapsack> knapsacks, List<Item> items) {
        int[] knapsackWeights = new int[knapsacks.size()];
        for (int i = 0; i < knapsackAssignments.length; i++) {
            int knapsackIndex = knapsackAssignments[i];
            if (knapsackIndex != -1) {
                Item item = items.get(i);
                knapsackWeights[knapsackIndex] += item.weight;
                if (knapsackWeights[knapsackIndex] > knapsacks.get(knapsackIndex).capacity) {
                    return false;
                }
            }
        }
        return true;
    }

    public void assignItemToKnapsack(int itemIndex, int knapsackIndex) {
        knapsackAssignments[itemIndex] = knapsackIndex;
    }

    public void removeItemFromKnapsack(int itemIndex) {
        knapsackAssignments[itemIndex] = -1;
    }

    public boolean isItemAssigned(int itemIndex) {
        return knapsackAssignments[itemIndex] != -1;
    }

    public int getKnapsackAssignment(int itemIndex) {
        return knapsackAssignments[itemIndex];
    }

    public int distance(Solution other) {
        int distance = 0;
        for (int i = 0; i < knapsackAssignments.length; i++) {
            if (knapsackAssignments[i] != other.knapsackAssignments[i]) {
                distance++;
            }
        }
        return distance;
    }

    public int similarity(Solution other) {
        return knapsackAssignments.length - distance(other);
    }

    public Solution copy() {
        Solution clone = new Solution(knapsackAssignments.length);
        clone.knapsackAssignments = Arrays.copyOf(knapsackAssignments, knapsackAssignments.length);
        clone.totalValue = totalValue;
        return clone;
    }
    
    public void flip(int index, List<Item> items, List<Knapsack> knapsacks) {
        boolean found = false;
        if (knapsackAssignments[index] == -1) {
            int start = 0;

            while(start < knapsacks.size() && !found){
                knapsackAssignments[index] = start;
                if(isFeasible(knapsacks, items)){
                    found = true;
                }
                start++;
            }

        } else {
            int stop = knapsackAssignments[index];
            int start = (knapsackAssignments[index] + 1) % knapsacks.size();
            while(start != stop && !found){
                //System.out.println("one");
                knapsackAssignments[index] = start;
                start = (start+1) % knapsacks.size();
                if(isFeasible(knapsacks, items)){
                    found = true;
                }
            }
            if(!found){
                knapsackAssignments[index] = -1;
            }
        }
    }

    public void localSearch(int maxSteps, List<Item> items, List<Knapsack> knapsacks) {
        Solution bestSolution = copy();
        int steps = 0;
        while (steps < maxSteps) {
            Solution neighbor = generateNeighbor(items, knapsacks);
            
            if (neighbor.isFeasible(knapsacks, items) && neighbor.calculateValue(items) > bestSolution.calculateValue(items)) {
                bestSolution = neighbor.copy();
                bestSolution.cacheInvalidate();
            }
            steps++;
        }
        knapsackAssignments = bestSolution.knapsackAssignments;
        cacheInvalidate();
        calculateValue(items);
    }

    public Solution generateNeighbor(List<Item> items, List<Knapsack> knapsacks) {
        Solution newSolution = copy();  // Assume this creates a deep copy
        newSolution.cacheInvalidate();
        int itemIndex = random.nextInt(items.size());
        int newKnapsackIndex = random.nextInt(knapsacks.size());
        // Try moving item to a new knapsack
        newSolution.assignItemToKnapsack(itemIndex, newKnapsackIndex);
        return newSolution;
    }

    @Override
    public int compareTo(Solution other) {
        return Integer.compare(this.totalValue, other.totalValue);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Solution other = (Solution) obj;
        return Arrays.equals(knapsackAssignments, other.knapsackAssignments);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(knapsackAssignments);
    }

    public String toString(){
        return Arrays.toString(knapsackAssignments);
    }
    //maybe a main for testing sake
    public static void main(String[] args) {
        // Test the Solution class
        List<Knapsack> knapsacks = MultipleKnapsackProblem.readKnapsacksFromCSV("p2_tests/20-200-test.csv");
        List<Item> items = MultipleKnapsackProblem.readItemsFromCSV("p2_tests/20-200-test.csv");
        // System.out.println(knapsacks.size());
        // System.out.println(items.size());
        Solution solution = new Solution(items.size());
        // solution.initializeRandomSolution(knapsacks, items);
        solution.knapsackAssignments = new int[]{13, 11, 17, -1, 15, 14, 6, 7, 0, 3, 7, 3, 19, 4, 8, -1, 11, -1, 17, -1, 6, 5, 9, 2, 6, 12, 3, 10, 1, -1, 6, 9, 6, 14, 9, -1, 16, 8, 4, -1, 3, 14, 9, -1, 19, 8, 6, 14, 7, 9, -1, 4, 7, 6, 4, 0, 2, -1, 5, 4, 17, 6, 19, 12, 4, 13, -1, 12, 5, 13, -1, 19, -1, 4, 3, 6, 5, 1, 0, -1, 0, 15, 18, -1, 6, 19, 2, 2, -1, 8, 0, -1, -1, 16, 7, 16, -1, 4, 11, 13, 17, 14, 0, 0, 10, 19, 1, 0, 13, -1, 7, 12, 16, 18, -1, 7, -1, 12, 11, 19, 19, 5, -1, 1, 13, 0, 13, 15, 2, 1, 18, 12, -1, 0, 1, -1, 3, 15, -1, 10, 10, -1, 10, 8, 7, 15, 11, 7, 13, 18, 18, 3, 11, 4, 3, -1, 2, 6, -1, 9, 17, 10, -1, 13, 17, -1, 9, 13, 15, -1, 5, 11, -1, -1, -1, 6, -1, 16, 1, 12, 1, 16, 8, -1, -1, 5, 7, -1, 1, 8, 17, 10, -1, -1, 2, 4, 1, 12, 8, -1};
        System.out.println("Initial solution value: " + solution.calculateValue(items));
        // testing flip
        // System.out.println("Before flip: " +  Arrays.toString(solution.knapsackAssignments));
        // solution.flip(0, items, knapsacks);
        // System.out.println("After flip: " +  Arrays.toString(solution.knapsackAssignments));
        // System.out.println("is feasible: " + solution.isFeasible(knapsacks, items));
        // testing local search
        // solution.localSearch(1000000, items, knapsacks);
        // System.out.println("After local search: " +  Arrays.toString(solution.knapsackAssignments));
        // System.out.println("is feasible: " + solution.isFeasible(knapsacks, items));
        // System.out.println("Solution value: " + solution.calculateValue(items));


    }
}
