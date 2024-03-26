package mkp;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.Stack;



public class MultipleKnapsackProblem {
    private static final Random rand = new Random();
    private static final int nbTests = 10;
    public static class SearchResult {
        State bestState;
        long nodesExplored;
        int maxStackSize;
        int bestValue;

        public SearchResult(State bestState, long nodesExplored, int maxStackSize, int bestValue) {
            this.bestState = bestState;
            this.nodesExplored = nodesExplored;
            this.maxStackSize = maxStackSize;
            this.bestValue = bestValue;
        }

        public String toString() {
            return "Best value: " + bestValue + ", Nodes explored: " + nodesExplored + ", Max stack size: " + maxStackSize;
        }
    }

    public static State dfsSearch(State initialState) {
        Stack<State> stack = new Stack<>();
        State bestState = null;
        int bestValue = 0;
        stack.push(initialState);
        int maxStackSize = 0; //the maximum nodes that were put in stack during the execution
        long nodesExplored = 0;
        while (!stack.isEmpty()) { //DFS is exhaustive search, so we keep searching until the stack is empty
            State currentState = stack.pop();
            nodesExplored++;
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
        System.out.println("nodesExplored: " + nodesExplored);
        return bestState; // Return the best state  found
    }
    // DFS modified in order to record data for testing purposes:
    public static SearchResult dfsSearchTesting(State initialState) {
        Stack<State> stack = new Stack<>();
        State bestState = null;
        int bestValue = 0;
        stack.push(initialState);
        int maxStackSize = 0; //the maximum nodes that were put in stack during the execution
        long nodesExplored = 0;
        while (!stack.isEmpty()) {
            State currentState = stack.pop();
            nodesExplored++;
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
        return new SearchResult(bestState, nodesExplored, maxStackSize, bestValue);
    }
    
    public static State bfsSearch(State initialState) {
        Queue<State> queue = new LinkedList<>();
        State bestState = null;
        int bestValue = 0;
        queue.add(initialState);
        int maxQueueSize = 0;
        long nodesExplored = 0;
        while (!queue.isEmpty()) {
            State currentState = queue.poll();
            nodesExplored++;
            int currentValue = currentState.calculateTotalValue();
            if (currentValue > bestValue) {
                bestValue = currentValue;
                bestState = currentState;
            }
            List<State> successors = currentState.getSuccessors();
            for (State successor : successors) {
                queue.add(successor);
            }
            if(queue.size() > maxQueueSize){
                maxQueueSize = queue.size();
            }
        }
        System.out.println("maxQueueSize: " + maxQueueSize);
        System.out.println("nodesExplored: " + nodesExplored);
        return bestState; // Return the best state  found
    }
    // BFS modified in order to record data for testing purposes:
    public static SearchResult bfsSearchTesting(State initialState) {
        Queue<State> queue = new LinkedList<>();
        State bestState = null;
        int bestValue = 0;
        queue.add(initialState);
        int maxQueueSize = 0;
        long nodesExplored = 0;
        while (!queue.isEmpty()) {
            State currentState = queue.poll();
            nodesExplored++;
            int currentValue = currentState.calculateTotalValue();
            if (currentValue > bestValue) {
                bestValue = currentValue;
                bestState = currentState;
            }
            List<State> successors = currentState.getSuccessors();
            for (State successor : successors) {
                queue.add(successor);
            }
            if (queue.size() > maxQueueSize) {
                maxQueueSize = queue.size();
            }
        }
        return new SearchResult(bestState, nodesExplored, maxQueueSize, bestValue);
    }

    public static State aStarSearch(State initialState){
        PriorityQueue<State> open = new PriorityQueue<>(new AStarComparator());
        State bestState = null;
        int bestValue = 0;
        open.add(initialState);
        int maxOpenSize = 0;
        long nodesExplored = 0;
        boolean goalReached = false;
        while(!open.isEmpty() && !goalReached){
            State currentState = open.poll();
            nodesExplored++;
            // System.out.println("Current state: " + currentState);
            // System.out.println("item index: " + currentState.nextItemIndex);
            // System.out.println("Current value: " + currentState.calculateTotalValue());
            // System.out.println("f(n) = " + currentState.calculateCost() + " + " + currentState.calculateHeuristic());
            // System.out.println("------------------------------------------------------------------------------------");
            int currentValue = currentState.calculateTotalValue();
            if (currentValue > bestValue) {
                bestValue = currentValue;
                bestState = currentState;
            }
            if (currentState.nextItemIndex >= currentState.items.size()) {
                goalReached = true;
            }
            List<State> successors = currentState.getSuccessors();
            for (State successor : successors) {
                // System.out.println("Successor: " + successor);
                // System.out.println("item index: " + successor.nextItemIndex);
                // System.out.println("Successor value: " + successor.calculateTotalValue());
                // System.out.println("f(n) = " + successor.calculateCost() + " + " + successor.calculateHeuristic());
                open.add(successor);
            }
            if(open.size() > maxOpenSize){
                maxOpenSize = open.size();
            } 
        }
        System.out.println("maxOpenSize: " + maxOpenSize);
        System.out.println("nodesExplored: " + nodesExplored);
        return bestState;
    }
    // A* modified in order to record data for testing purposes:
    public static SearchResult aStarSearchTesting(State initialState) {
        PriorityQueue<State> open = new PriorityQueue<>(new AStarComparator());
        State bestState = null;
        int bestValue = 0;
        open.add(initialState);
        int maxOpenSize = 0;
        long nodesExplored = 0;
        boolean goalReached = false;
        while (!open.isEmpty() && !goalReached) {
            State currentState = open.poll();
            nodesExplored++;
            int currentValue = currentState.calculateTotalValue();
            if (currentValue > bestValue) {
                bestValue = currentValue;
                bestState = currentState;
            }
            if (currentState.nextItemIndex >= currentState.items.size()) {
                goalReached = true;
            }
            List<State> successors = currentState.getSuccessors();
            for (State successor : successors) {
                open.add(successor);
            }
            if (open.size() > maxOpenSize) {
                maxOpenSize = open.size();
            }
        }
        return new SearchResult(bestState, nodesExplored, maxOpenSize, bestValue);
    }
    

    private static List<Item> generateItems(int N) {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            int weight = 1 + rand.nextInt(100); // Adjust range as needed.
            int value = 1 + rand.nextInt(100); // Adjust range as needed.
            items.add(new Item(weight, value));
        }
        return items;
    }

    private static List<Knapsack> generateKnapsacks(int K, int totalItemWeight) {
        List<Knapsack> knapsacks = new ArrayList<>();
        for (int i = 0; i < K; i++) {
            int capacity = (int) (totalItemWeight * (0.2 + 0.3 * rand.nextDouble())); // Example: 20%-50% of total weight
            knapsacks.add(new Knapsack(capacity));
        }
        return knapsacks;
    }

    private static void writeResultsToCSV(String filePath, List<? extends Number> dfsData, List<? extends Number> bfsData, List<? extends Number> aStarData) throws IOException {
        try (FileWriter csvWriter = new FileWriter(filePath)) {
            csvWriter.append("DFS,BFS,A*\n");
            for (int i = 0; i < dfsData.size(); i++) {
                csvWriter.append(dfsData.get(i) + "," + bfsData.get(i) + "," + aStarData.get(i) + "\n");
            }
            csvWriter.flush();
        }
    }

    
    public static void main(String[] args) {
        // Example configuration
        int N = 17; // Number of items
        int K = 2; // Number of knapsacks

        // Test the algorithms
        List<Long> execTimesDFS = new ArrayList<>();
        List<Long> execTimesBFS = new ArrayList<>();
        List<Long> execTimesAStar = new ArrayList<>();

        List<Long> nodesExploredDFS = new ArrayList<>();
        List<Long> nodesExploredBFS = new ArrayList<>();
        List<Long> nodesExploredAStar = new ArrayList<>();

        List<Integer> maxStackSizeDFS = new ArrayList<>();
        List<Integer> maxQueueSizeBFS = new ArrayList<>();
        List<Integer> maxOpenSizeAStar = new ArrayList<>();

        List<Integer> bestValuesDFS = new ArrayList<>();
        List<Integer> bestValuesBFS = new ArrayList<>();
        List<Integer> bestValuesAStar = new ArrayList<>();

        for (int i = 0; i < nbTests; i++) {
            List<Item> items = generateItems(N);
            int totalItemWeight = items.stream().mapToInt(item -> item.weight).sum();
            List<Knapsack> knapsacks = generateKnapsacks(K, totalItemWeight);
            State initialState = new State(knapsacks, items);
            System.out.println(knapsacks);
            System.out.println(items);

            long startTime = System.currentTimeMillis();
            SearchResult resultDFS = dfsSearchTesting(initialState);
            long endTime = System.currentTimeMillis();
            execTimesDFS.add(endTime - startTime);
            nodesExploredDFS.add(resultDFS.nodesExplored);
            maxStackSizeDFS.add(resultDFS.maxStackSize);
            bestValuesDFS.add(resultDFS.bestValue);

            startTime = System.currentTimeMillis();
            SearchResult resultBFS = bfsSearchTesting(initialState);
            endTime = System.currentTimeMillis();
            execTimesBFS.add(endTime - startTime);
            nodesExploredBFS.add(resultBFS.nodesExplored);
            maxQueueSizeBFS.add(resultBFS.maxStackSize);
            bestValuesBFS.add(resultBFS.bestValue);

            startTime = System.currentTimeMillis();
            SearchResult resultAStar = aStarSearchTesting(initialState);
            endTime = System.currentTimeMillis();
            execTimesAStar.add(endTime - startTime);
            nodesExploredAStar.add(resultAStar.nodesExplored);
            maxOpenSizeAStar.add(resultAStar.maxStackSize);
            bestValuesAStar.add(resultAStar.bestValue);
            items = null;
            knapsacks = null;
            initialState = null;
        }

        try {
            writeResultsToCSV("./test_results/" + K + "-" + N + "-nodes_explored.csv", nodesExploredDFS, nodesExploredBFS, nodesExploredAStar);
            writeResultsToCSV("./test_results/" + K + "-" + N + "-exec_times.csv", execTimesDFS, execTimesBFS, execTimesAStar);
            writeResultsToCSV("./test_results/" + K + "-" + N + "-max_nodes_mem.csv", maxStackSizeDFS, maxQueueSizeBFS, maxOpenSizeAStar);
            writeResultsToCSV("./test_results/" + K + "-" + N + "-best_values.csv", bestValuesDFS, bestValuesBFS, bestValuesAStar);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}