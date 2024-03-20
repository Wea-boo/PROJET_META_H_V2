package mkp;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;



public class MultipleKnapsackProblem {

    public static State dfsSearch(State initialState) {
        Stack<State> stack = new Stack<>();
        State bestState = null;
        int bestValue = 0;
        stack.push(initialState);
        int maxStackSize = 0;

        while (!stack.isEmpty()) {
            State currentState = stack.pop();
            int currentValue = currentState.calculateTotalValue();
            if (currentValue > bestValue) {
                bestValue = currentValue;
                bestState = currentState;
            }
            List<State> successors = currentState.getSuccessors();
            for (State successor : successors) {
                stack.push(successor);
            }
            if(stack.size() > maxStackSize){
                maxStackSize = stack.size();
            }
        }
        System.out.println("maxStackSize: " + maxStackSize);
        return bestState; // Return the best state  found
    }

    public static State bfsSearch(State initialState) {
        Queue<State> stack = new LinkedList<>();
        State bestState = null;
        int bestValue = 0;
        stack.add(initialState);
        int maxStackSize = 0;

        while (!stack.isEmpty()) {
            State currentState = stack.poll();
            int currentValue = currentState.calculateTotalValue();
            if (currentValue > bestValue) {
                bestValue = currentValue;
                bestState = currentState;
            }
            List<State> successors = currentState.getSuccessors();
            for (State successor : successors) {
                stack.add(successor);
            }
            if(stack.size() > maxStackSize){
                maxStackSize = stack.size();
            }
        }
        System.out.println("maxStackSize: " + maxStackSize);
        return bestState; // Return the best state  found
    }

    public static void main(String[] args) {
        // Create items
        Item item1 = new Item(6, 6);
        Item item2 = new Item(6, 3);
        Item item3 = new Item(9, 5);
        Item item4 = new Item(12, 8);
        Item item5 = new Item(15, 12);
        Item item6 = new Item(18, 21);
        Item item7 = new Item(17, 16);
        Item item8 = new Item(24, 19);
        Item item9 = new Item(19, 14);
        Item item10 = new Item(27, 18);
        Item item11 = new Item(10, 7);
        Item item12 = new Item(8, 4);
        Item item13 = new Item(14, 9);
        Item item14 = new Item(16, 11);
        Item item15 = new Item(20, 15);
        Item item16 = new Item(22, 17);
        Item item17 = new Item(9, 5);
        Item item18 = new Item(26, 20);
        // Item item19 = new Item(21, 13);
        // Item item20 = new Item(30, 22);

        // Item item1 = new Item(48, 10);
        // Item item2 = new Item(30, 30);
        // Item item3 = new Item(42, 25);
        // Item item4 = new Item(36, 50);
        // Item item5 = new Item(36, 35);
        // Item item6 = new Item(48, 30);
        // Item item7 = new Item(42, 15);
        // Item item8 = new Item(42, 40);
        // Item item9 = new Item(36, 30);
        // Item item10 = new Item(24, 35);
        // Item item11 = new Item(30, 45);
        // Item item12 = new Item(30, 10);
        // Item item13 = new Item(42, 20);
        // Item item14 = new Item(36, 30);
	    // Item item15 = new Item(36, 25);
        // Create knapsacks
        Knapsack knapsack1 = new Knapsack(500);
        Knapsack knapsack2 = new Knapsack(1000);
        Knapsack knapsack3 = new Knapsack(1200);

        // Create list of knapsacks and items
        List<Knapsack> knapsacks = new ArrayList<>();
        knapsacks.add(knapsack1);
        knapsacks.add(knapsack2);
        knapsacks.add(knapsack3);


        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(item3);
        items.add(item4);
        items.add(item5);
        items.add(item6);
        items.add(item7);
        items.add(item8);
        items.add(item9);
        items.add(item10);
        // items.add(item11);
        // items.add(item12);
        // items.add(item13);
        // items.add(item14);
        // items.add(item15);
        // items.add(item16);
        // items.add(item17);
        // items.add(item18);
        // items.add(item19);
        // items.add(item20);
    
        // Create instance of the problem

        State initialState = new State(knapsacks, items);
        // Solve the problem
        State bestStateResult = bfsSearch(initialState);

        // Print the best state found
        System.out.println(bestStateResult);

        // Print the total value of the best state found
        if(bestStateResult != null){
            System.out.println("Total value: " + bestStateResult.calculateTotalValue());
        }
    }
}