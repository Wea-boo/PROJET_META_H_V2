    package mkp;

    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.List;
    import java.util.Objects;

    public class State {
        int[] knapsackWeights;
        List<Knapsack> knapsacks;
        List<Item> items;
        int[] itemInKnapsack;
        int nextItemIndex;

        public State(List<Knapsack> knapsacks, List<Item> items) {
            this.knapsacks = knapsacks;
            this.items = items;
            this.knapsackWeights = new int[knapsacks.size()];
            this.itemInKnapsack = new int[items.size()];
            Arrays.fill(this.itemInKnapsack, -1);
            this.nextItemIndex = 0;
        }

        public List<State> getSuccessors() {
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
            return successors;
        }

        public boolean packItem(int itemIndex, int knapsackIndex) {
            Item item = items.get(itemIndex);
            Knapsack knapsack = knapsacks.get(knapsackIndex);
            if (itemInKnapsack[itemIndex] == -1 && knapsackWeights[knapsackIndex] + item.weight <= knapsack.capacity) {
                //itemPacked[itemIndex] = true;
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

        @Override
        public State clone() {
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
