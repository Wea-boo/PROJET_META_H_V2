package mkp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

public class MultipleKnapsackProblem {
    private static final Random rand = new Random();

    public static class SearchResult {
        State bestState;
        long nodesExplored;
        int maxOpenSize;
        int bestValue;
        long executionTime; // Added field for execution time
    
        // Modified constructor to include executionTime
        public SearchResult(State bestState, long nodesExplored, int maxOpenSize, int bestValue, long executionTime) {
            this.bestState = bestState;
            this.nodesExplored = nodesExplored;
            this.maxOpenSize = maxOpenSize;
            this.bestValue = bestValue;
            this.executionTime = executionTime; // Assign execution time
        }
    
        // Modified toString() method or create a new method to include execution time in the output
        public String toString() {
            return "Execution Time: " + executionTime + "ms, Best value: " + bestValue + ", Nodes explored: " + nodesExplored + ", Max nodes in memory: " + maxOpenSize;
        }
    }

    public static State dfsSearch(State initialState) {
        Stack<State> stack = new Stack<>();
        State bestState = null;
        int bestValue = 0;
        stack.push(initialState);
        int maxOpenSize = 0; //the maximum nodes that were put in stack during the execution
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
            if(stack.size() > maxOpenSize){
                maxOpenSize = stack.size();
            }
        }
        System.out.println("maxOpenSize: " + maxOpenSize);
        System.out.println("nodesExplored: " + nodesExplored);
        return bestState; // Return the best state  found
    }
    // DFS modified in order to record data for testing purposes:
    public static SearchResult dfsSearchTesting(State initialState, int maxDepth) {
        long startTime = System.currentTimeMillis();
        Stack<State> stack = new Stack<>();
        State bestState = null;
        int bestValue = 0;
        stack.push(initialState);
        int maxOpenSize = 0; //the maximum nodes that were put in stack during the execution
        long nodesExplored = 0;
        while (!stack.isEmpty()) {
            State currentState = stack.pop();
            if (currentState.nextItemIndex > maxDepth) { // Skip states that exceed the maximum depth, this is how we use max depth
                continue;
            }
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
            if(stack.size() > maxOpenSize){
                maxOpenSize = stack.size();
            }
        }
        long executionTime = System.currentTimeMillis() - startTime;
        
        return new SearchResult(bestState, nodesExplored, maxOpenSize, bestValue, executionTime);
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
    public static SearchResult bfsSearchTesting(State initialState, int maxDepth) {
        long startTime = System.currentTimeMillis();
        Queue<State> queue = new LinkedList<>();
        State bestState = null;
        int bestValue = 0;
        queue.add(initialState);
        int maxQueueSize = 0;
        long nodesExplored = 0;
        while (!queue.isEmpty()) {
            State currentState = queue.poll();
            if (currentState.nextItemIndex > maxDepth) { // Skip states that exceed the maximum depth, this is how we use max depth
                continue;
            }
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
        long executionTime = System.currentTimeMillis() - startTime;
        return new SearchResult(bestState, nodesExplored, maxQueueSize, bestValue, executionTime);
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
            if(open.size() > maxOpenSize){
                maxOpenSize = open.size();
            } 
        }
        System.out.println("maxOpenSize: " + maxOpenSize);
        System.out.println("nodesExplored: " + nodesExplored);
        return bestState;
    }
    // A* modified in order to record data for testing purposes:
    public static SearchResult aStarSearchTesting(State initialState, int maxDepth) {
        long startTime = System.currentTimeMillis();
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
            if (currentState.nextItemIndex >= maxDepth) {
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
        long executionTime = System.currentTimeMillis() - startTime;
        return new SearchResult(bestState, nodesExplored, maxOpenSize, bestValue, executionTime);
    }
    

    public static List<Item> generateItems(int N) {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            int weight = 1 + rand.nextInt(100); // Adjust range as needed.
            int value = 1 + rand.nextInt(100); // Adjust range as needed.
            items.add(new Item(weight, value));
        }
        return items;
    }

    public static List<Knapsack> generateKnapsacks(int K, int totalItemWeight) {
        List<Knapsack> knapsacks = new ArrayList<>();
        for (int i = 0; i < K; i++) {
            int capacity = (int) (totalItemWeight * (0.2 + 0.3 * rand.nextDouble())); // Example: 20%-50% of total weight
            knapsacks.add(new Knapsack(capacity));
        }
        return knapsacks;
    }

    public static void writeResultsToCSV(String filePath, List<? extends Number> dfsData, List<? extends Number> bfsData, List<? extends Number> aStarData) throws IOException {
        try (FileWriter csvWriter = new FileWriter(filePath)) {
            csvWriter.append("DFS,BFS,A*\n");
            for (int i = 0; i < dfsData.size(); i++) {
                csvWriter.append(dfsData.get(i) + "," + bfsData.get(i) + "," + aStarData.get(i) + "\n");
            }
            csvWriter.flush();
        }
    }

    public static void generateCSVRandomTestFile(String filePath, int K, int N) {
        try (FileWriter csvWriter = new FileWriter(filePath)) {
            csvWriter.append(N + "," + K + "\n"); //1st line: number of items, number of knapsacks
            List<Item> items = generateItems(N);
            List<Knapsack> knapsacks = generateKnapsacks(K, items.stream().mapToInt(item -> item.weight).sum());

            for (Knapsack knapsack : knapsacks) {
                csvWriter.append(String.valueOf(knapsack.capacity)); // Convert int to String
                if(knapsack != knapsacks.get(knapsacks.size() - 1)){
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

    public static State CSVToState(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            // Read the first line to get the number of items and knapsacks
            line = br.readLine();
            String[] sizes = line.split(",");
            int numItems = Integer.parseInt(sizes[0]);
            int numKnapsacks = Integer.parseInt(sizes[1]);

            // Read the second line to get the capacities of knapsacks
            line = br.readLine();
            String[] capacities = line.split(",");
            List<Knapsack> knapsacks = new ArrayList<>();
            for (int i = 0; i < numKnapsacks; i++) {
                int capacity = Integer.parseInt(capacities[i]);
                knapsacks.add(new Knapsack(capacity));
            }

            // Read subsequent lines to create items
            List<Item> items = new ArrayList<>();
            for (int i = 0; i < numItems; i++) {
                line = br.readLine();
                String[] itemData = line.split(",");
                int weight = Integer.parseInt(itemData[0]);
                int value = Integer.parseInt(itemData[1]);
                items.add(new Item(weight, value));
            }

            // Create and return the state object
            return new State(knapsacks, items);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // Return null if there's an error
    }
    
}