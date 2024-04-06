package mkp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class State {
    int[] knapsackWeights; //current weights of all knapsacks, size = K (number of knapsacks)
    List<Knapsack> knapsacks; // size = K (number of knapsacks), k.capacity
    List<Item> items; // size = N (number of items), n.weight, n.value
    int[] itemInKnapsack; // size = N (number of items), -1 if not packed, k if packed in knapsack k
    // [0, -1, 1, 2, 0] means item 0 is packed in knapsack 0, item 1 is not packed, item 2 is packed in knapsack 1, item 3 is packed in knapsack 2, item 4 is packed in knapsack 0
    int nextItemIndex; // index of the next item to consider, can also represent the depths of the state/node in the search tree

    public State(List<Knapsack> knapsacks, List<Item> items) {
        this.knapsacks = knapsacks;
        this.items = items;
        this.knapsackWeights = new int[knapsacks.size()];
        this.itemInKnapsack = new int[items.size()];
        Arrays.fill(this.itemInKnapsack, -1);
        this.nextItemIndex = 0; //start with the first item
    }

    public List<State> getSuccessors() { // method that generates the successors of a node/state
        List<State> successors = new ArrayList<>();
        if (nextItemIndex >= items.size()) {
            return successors; // No more items to consider
        }
        // Try packing the next item into each knapsack
        for (int knapsackIndex = 0; knapsackIndex < knapsacks.size(); knapsackIndex++) {
            State clone = this.clone();
            if (clone.packItem(nextItemIndex, knapsackIndex)) {
                clone.nextItemIndex++; // Move to the next item
                successors.add(clone);
            }
        }
        // Add a successor state for skipping the item
        State skippedItemState = this.clone();
        skippedItemState.nextItemIndex++;
        successors.add(skippedItemState);
        // 0 <= number of successors <= K+1
        return successors;
    }

    public boolean packItem(int itemIndex, int knapsackIndex) {
        Item item = items.get(itemIndex);
        Knapsack knapsack = knapsacks.get(knapsackIndex);
        if (itemInKnapsack[itemIndex] == -1 && knapsackWeights[knapsackIndex] + item.weight <= knapsack.capacity) {
            knapsackWeights[knapsackIndex] += item.weight;
            itemInKnapsack[itemIndex] = knapsackIndex;
            return true;
        }
        return false;
    }
        
    public int calculateTotalValue() {
        int totalValue = 0;
        for (int i = 0; i < itemInKnapsack.length; i++) {
            if (itemInKnapsack[i] != -1) { // If the item is packed
                totalValue += items.get(i).value; // Add its value
            }
        }
        return totalValue;
    }

    public int calculateCost(){
        return calculateTotalValue();
    }

    public int calculateHeuristic() { //h(n) = sum of the values of the remaining items
        List<Item> remainingItems = new ArrayList<>(items.subList(nextItemIndex, items.size())); 
        // [0,...,nextItemIndex-1] are already decided, [nextItemIndex,...,N-1] are the remaining items, even if itemInKnapsack[i] == -1, if i < nextItemIndex, it is considered as decided
        return remainingItems.stream().mapToInt(item -> item.value).sum();
    }

    @Override
    public State clone() { //method to generate a copy of the current object/state
        State cloned = new State(this.knapsacks, this.items);
        cloned.knapsackWeights = this.knapsackWeights.clone();
        cloned.itemInKnapsack = this.itemInKnapsack.clone();
        cloned.nextItemIndex = this.nextItemIndex;
        return cloned;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return nextItemIndex == state.nextItemIndex &&
            Arrays.equals(itemInKnapsack, state.itemInKnapsack);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(nextItemIndex);
        result = 31 * result + Arrays.hashCode(itemInKnapsack);
        return result;
    }

    @Override
    public String toString() {
        return "KnapsackWeights=" + Arrays.toString(knapsackWeights) +
            ", itemInKnapsack=" + Arrays.toString(itemInKnapsack);
    }

}

