package mkp;

import java.util.Comparator;

public class AStarComparator implements Comparator<State> {
    @Override
    public int compare(State state1, State state2) {
        // Calculate the f scores for each state
        int score1 = state1.calculateCost() + state1.calculateHeuristic();
        int score2 = state2.calculateCost() + state2.calculateHeuristic();

        // Compare scores in descending order (maximize f score)
        return Integer.compare(score2, score1);
    }
}