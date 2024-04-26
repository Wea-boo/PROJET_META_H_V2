package mkp;

import java.util.List;
import java.util.Random;

public class Solution {
    private int[] knapsackAssignments;
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
            knapsackAssignments[i] = random.nextInt(knapsacks.size());
        }
        System.out.println("Initial solution value: " + calculateValue(items));
        System.out.println("Initial solution is feasible: " + isFeasible(knapsacks, items));
        repairSolution(knapsacks, items);
        System.out.println("Repaired solution value: " + calculateValue(items));
        System.out.println("Repaired solution is feasible: " + isFeasible(knapsacks, items));
    }
    public void repairSolution(List<Knapsack> knapsacks, List<Item> items) {
        int[] knapsackWeights = new int[knapsacks.size()];
        for (int i = 0; i < knapsackAssignments.length; i++) {
            int knapsackIndex = knapsackAssignments[i];
            Item item = items.get(i);
            if (knapsackWeights[knapsackIndex] + item.weight <= knapsacks.get(knapsackIndex).capacity) {
                knapsackWeights[knapsackIndex] += item.weight;
            } else {
                // Try to reassign the item to a different knapsack
                boolean assigned = false;
                for (int j = 0; j < knapsacks.size(); j++) {
                    if (knapsackWeights[j] + item.weight <= knapsacks.get(j).capacity) {
                        knapsackWeights[j] += item.weight;
                        knapsackAssignments[i] = j;
                        assigned = true;
                        break;
                    }
                }
                // If the item cannot be assigned to any knapsack, leave it unassigned
                if (!assigned) {
                    knapsackAssignments[i] = -1;
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

    //maybe a main for testing sake
    public static void main(String[] args) {
        // Test the Solution class

    }
}
