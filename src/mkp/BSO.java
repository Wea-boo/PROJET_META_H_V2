package mkp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class BSO {

    public Solution searchBSO(List<Knapsack> knapsacks, List<Item> items, int numBees, int flip, int maxChances, int maxSteps, int maxIter){
        Solution sref = new Solution(items.size());
        sref.initializeRandomSolution(knapsacks,items);
        //System.out.println("Initial solution: " + sref.calculateValue(items));
        Set<Solution> tabuList = new HashSet<>();
        Set<Solution> danceList = new HashSet<>();
        int iter = 0;
        AtomicInteger nbChances = new AtomicInteger(maxChances);

        while(iter < maxIter){
            tabuList.add(sref);
            List<Solution> bees = determineSearchPoints(sref, numBees, flip, items, knapsacks);
            
            for (Solution bee: bees){
                bee.localSearch(maxSteps, items, knapsacks);
                danceList.add(bee);
            }

            sref = selectionReferenceSolution(sref, danceList, tabuList, knapsacks, items, maxChances, nbChances);
            iter++;
            danceList.clear(); // clear danceList for next iteration, I assume that danceList must be consistent and only keep the result of the bee's local search of one iteration  
        }

        return sref;
    }

    public static List<Solution> determineSearchPoints(Solution sref, int numBees, int flip, List<Item> items, List<Knapsack> knapsacks){
        List<Solution> bees = new ArrayList<>();
        int h = 0;
        while(bees.size() < numBees && h < flip && h < items.size()){ // Third condition for small instances where flip surpasses the number of items
            Solution s = sref.copy();
            int p = 0;
            do{
                // System.out.println("flip: " + flip + " p: " + p + " h: " + h + " flip*p+h: " + flip*p+h + " items.size(): " + items.size());
                s.flip(flip*p+h, items, knapsacks);
                p++;
            } while(flip* p + h > items.size());
            s.calculateValue(items);
            bees.add(s);
            h++;
        }
        return bees;
    }

    public static Solution selectionReferenceSolution(Solution sref, Set<Solution> danceList, Set<Solution> tabuList, List<Knapsack> knapsacks, List<Item> items, int maxChance, AtomicInteger nbChances){
        Set<Solution> validDanceList = new HashSet<>(danceList);
        validDanceList.removeAll(tabuList); //and with that we have absolutely nothing to fear about sRefs being in tabuList

        if(validDanceList.isEmpty()) {
            Solution newSref = new Solution(items.size());
            newSref.initializeRandomSolution(knapsacks, items);
            return newSref;
        }

        Solution sBest = Collections.max(validDanceList);
        int deltaF = sBest.getTotalValue() - sref.getTotalValue();
        
        if(deltaF > 0){
            Set<Solution> bestSolutions = validDanceList.stream()
                .filter(s -> s.getTotalValue() == sBest.getTotalValue())
                .collect(Collectors.toSet());

            nbChances.set(maxChance); // Reset chances
            if (bestSolutions.size() > 1) {
                return selectBasedOnDiversity(bestSolutions, tabuList).get(0);
            } else {
                return sBest;
            }
        } else {
            nbChances.decrementAndGet();
            if(nbChances.get() > 0){
                Set<Solution> bestSolutions = validDanceList.stream()
                    .filter(s -> s.getTotalValue() == sBest.getTotalValue())
                    .collect(Collectors.toSet());

                // No chance reset
                if (bestSolutions.size() > 1) {
                    return selectBasedOnDiversity(bestSolutions, tabuList).get(0);
                } else {
                    return sBest;
                }
            } else {
                List<Solution> divSolutions = selectBasedOnDiversity(validDanceList, tabuList);
                nbChances.set(maxChance); // Reset chances
                return Collections.max(divSolutions);
            }
        }
    }
    private static List<Solution> selectBasedOnDiversity(Set<Solution> solutions, Set<Solution> tabuList) {
        // Directly compute the maximum diversity to avoid storing all scores
        int maxDiversity = solutions.stream()
            .mapToInt(s -> calculateDiversity(s, tabuList))
            .max()
            .orElse(Integer.MAX_VALUE);  // This behavior should never occur in practice, but this method is also a viable way to convert an OptionalInt to an int
    
        // Filter solutions that match the maximum diversity, calculated in real-time
        return solutions.stream()
            .filter(s -> calculateDiversity(s, tabuList) == maxDiversity)
            .collect(Collectors.toList());
    }

    private static int calculateDiversity(Solution solution, Set<Solution> tabuList) { // degree of diversity of s == min{d(s, s') | s' in tabuList}
        // This assumes diversity is calculated as the minimum distance to any solution in the tabu list
        return tabuList.stream()
            .mapToInt(solution::distance) // Assuming distance method calculates some kind of difference
            .min()
            .orElse(Integer.MAX_VALUE); // Same thing as before, this should never happen in practice, but it's a way to convert an OptionalInt to an int
    }
    public static void main(String[] args) {
        List<Knapsack> knapsacks = MultipleKnapsackProblem.readKnapsacksFromCSV("p2_tests/20-200-test.csv");
        List<Item> items = MultipleKnapsackProblem.readItemsFromCSV("p2_tests/20-200-test.csv");
        // System.out.println(knapsacks.size());
        // System.out.println(items.size());
        // Solution solution = new Solution(items.size());
        // solution.initializeRandomSolution(knapsacks, items);
        // int numBees = 7;
        // int flip = 5;
        // System.out.println(solution);
        // System.out.println("-----------------------------------");
        // List<Solution> testBees = determineSearchPoints(solution, numBees, flip, items, knapsacks);
        // for(Solution bee: testBees){
        //     System.out.println(bee);
        // }
        // testing BSO
        int numBees = 7;
        int flip = 7;
        int maxSteps = 5000;
        int maxChance = 3;
        int maxIterations = 10000;
        BSO bso = new BSO();
        Solution solution = bso.searchBSO(knapsacks, items, numBees, flip, maxChance, maxSteps, maxIterations);
        System.out.println("Final solution: " + solution.calculateValue(items));
    }
}
