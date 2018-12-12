package com.capstone;

import com.sun.tools.javac.main.Option;

import java.math.BigInteger;
import java.util.*;

/**
 * Created by Heena on 11/27/2018.
 */
public class CrossEntropy {

    private final static Map<Integer, List<Integer>> DECISION_PARAM_VALUES;
    static{
        Map<Integer, List<Integer>> map = new HashMap<>();
        map.put(0,Arrays.asList(1,2,3,4,5,6,7,8,9,10));
        map.put(1,Arrays.asList(1,2,3,4,5,6,7,8,9,10));
        map.put(2,Arrays.asList(1,2,3,4,5,6,7,8,9,10));
        map.put(3,Arrays.asList(1,2,3,4,5,6,7,8,9,10));
        map.put(4,Arrays.asList(1,2,3,4,5,6,7,8,9,10));
        map.put(5,Arrays.asList(1,2,3,4,5,6,7,8,9,10));
        map.put(6,Arrays.asList(1,2,3,4,5,6,7,8,9,10));
        map.put(7,Arrays.asList(1,2,3,4,5,6,7,8,9,10));
        map.put(8,Arrays.asList(1,2,3,4,5,6,7,8,9,10));
        map.put(9,Arrays.asList(1,2,3,4,5,6,7,8,9,10));
        DECISION_PARAM_VALUES = Collections.unmodifiableMap(map);
    }

    private final static int MAX_ITERATIONS = 1000;
    private final static int NO_OF_SAMPLES = 10;
    private final static int NO_OF_DECISION_PARAMS = 10;
    private final static double RATIO_OF_BEST_RESULTS = 0.7;
    private final static int SIZE_OF_RANGE = 10;

    private final static double EXPONENTIAL_SMOOTHENING_FACTOR_ALPHA = 0.9;

    private static double calculateUtility(List<Integer> solution){
        OptionalDouble average = solution.stream().mapToInt(Integer::intValue).average();
        if(average.isPresent())
            return average.getAsDouble();
        return 0.0;
    }

    private static double evaluateSolution(List<Integer> solution){
        return calculateUtility(solution);
    }

    private static boolean isBetterThanBestSoFar(List<Integer> solution, List<Integer> bestSolution){
        return calculateUtility(solution) > calculateUtility(bestSolution);
    }

    private static int getRandomValueInRange(int min, int max){
        Random r = new Random();
        return r.nextInt((max - min)+1)+min;
    }

    private static List<RandomCollection> createNewDistribution(List<List<Integer>> fiprime, List<List<Integer>> fi){
        /*for every decision parameter create new sets from which random value can be selected
        * set for every decision parameter contains only those values that appeared more than once last time
        */

        List<List<Double>> newFi = new ArrayList<>();
        List<RandomCollection> newSets = new ArrayList<>();
        double alpha = EXPONENTIAL_SMOOTHENING_FACTOR_ALPHA;

        for(int i=0;i<fiprime.size();i++){
            List<Double> currentDecParamDistribution = new ArrayList<>();
            for(int j=0;j<DECISION_PARAM_VALUES.get(i).size();j++){
                currentDecParamDistribution.add(alpha *fiprime.get(i).get(j) + (1- alpha) * fi.get(i).get(j));
            }
            newFi.add(currentDecParamDistribution);
        }

        for(int list=0;list<newFi.size();list++){
            RandomCollection collection = new RandomCollection();
            for(int i=0;i<newFi.get(list).size();i++){
                // add (weight of i, i)
                collection.add(newFi.get(list).get(i), i);
            }
           newSets.add(collection);
        }
        return newSets;
    }

    private static List<List<Integer>> createIntermediateDistribution(PriorityQueue<List<Integer>> bestSolutions){

        int noOfSolutions = (int)Math.ceil(RATIO_OF_BEST_RESULTS*bestSolutions.size());
        List<List<Integer>> topSolutions = new ArrayList<>();
        List<List<Integer>> fiPrime = new ArrayList<>();

        int i=0;
        while(i < noOfSolutions){
            topSolutions.add(bestSolutions.poll());
            i++;
        }
        /*
            for decision param in decision params
        *       for solution in top polled solutions
        *           create a map for next decision parameter value frequencies of size range for current decision parameter
        *           update map - map.put(decision param value index, value +1)
        *       iterate over map
        *         if(map.get(decisionParamIndex) ! = 0
        *           fiprime[decision param value index][decisionParamvalue] = map.get(decision param value index)/10;
        *         else
        *           fiprime[decision param value index][decisionParamvalue] = 0
        *       fiPrime.add(fiPrime[decisionParam value index])
         *      clear map
          *
        * */
        for(int paramIdx=0; paramIdx<NO_OF_DECISION_PARAMS; paramIdx++) {
            Map<Integer, Integer> decParamCount = new HashMap<>();
            for (List<Integer> solution : topSolutions) {
                decParamCount.put(solution.get(paramIdx), decParamCount.getOrDefault(solution.get(paramIdx), 0) + 1);
            }
            List<Integer> paramIdxList = new ArrayList<>();
            for (i = 0; i < DECISION_PARAM_VALUES.get(paramIdx).size(); i++) {
                if (decParamCount.containsKey(i)) {
                    paramIdxList.add(decParamCount.get(i));
                } else {
                    paramIdxList.add(0);
                }
            }
            fiPrime.add(paramIdxList);
        }
        return fiPrime;

    }

    private static List<Integer> getBestSolution(){
        int i=1;
        double utility = 0.0;
        double max_utility = Double.MIN_VALUE;
        List<Integer> solution = null;
        List<Integer> bestSolution = null;

        List<List<Integer>> fiPrime = new ArrayList<>(NO_OF_DECISION_PARAMS);
        List<RandomCollection> fi = new ArrayList<>(NO_OF_DECISION_PARAMS);

        PriorityQueue<List<Integer>> bestSolutions =
                new PriorityQueue<List<Integer>>((l1, l2) -> Double.compare(calculateUtility(l2), calculateUtility(l1)));

        bestSolution = new ArrayList<>(NO_OF_DECISION_PARAMS);
        while(max_utility < 9.5){

            solution = new ArrayList<>(NO_OF_DECISION_PARAMS);

            for(int j=0;j<NO_OF_DECISION_PARAMS;j++) {
                if(i==1){
                    List<Integer> values = DECISION_PARAM_VALUES.get(j);
                    solution.add(getRandomValueInRange(values.get(0), values.get(values.size()-1)));
                }else{
                    //add to solution based on probability distribution function fi
                    RandomCollection set = fi.get(j);
                    Object o = set.next();
                    int idx = o.equals(null)? 0:(Integer)o;
                    solution.add(DECISION_PARAM_VALUES.get(j).get(idx));
                }
            }

            utility = evaluateSolution(solution);
            if (max_utility == Double.MIN_VALUE || isBetterThanBestSoFar(solution, bestSolution)) {
                System.out.println(max_utility);
                max_utility = utility;
                bestSolution = solution;
                bestSolutions.offer(bestSolution);
            }
            // Select top rho * N solutions to create fi prime
            fiPrime = createIntermediateDistribution(bestSolutions);
            // re-calculate distribution fi
            List<List<Integer>> fi1 = new ArrayList<>();
            if(i == 1){
                for(int j=0;j<NO_OF_DECISION_PARAMS;j++){
                    List<Integer> currentList = new ArrayList<>();
                    for(int paramIdx=0; paramIdx < DECISION_PARAM_VALUES.get(j).size();paramIdx++){
                        currentList.add(1);
                    }
                    fi1.add(currentList);
                }
                fi = createNewDistribution(fiPrime, fi1);
                fi.forEach(RandomCollection::displayCollection);
            }else{
                List<List<Integer>> fiToPass = new ArrayList<>();
                for(int j=0;j<NO_OF_DECISION_PARAMS;j++){
                    List<Integer> currentDistribution = new ArrayList<>();
                    for(int paramIdx=0; paramIdx < DECISION_PARAM_VALUES.get(j).size();paramIdx++){
                        currentDistribution.add((Integer)fi.get(j).next());
                    }
                    fiToPass.add(currentDistribution);
                }
                fi = createNewDistribution(fiPrime, fiToPass);
            }
            i++;
        }
        System.out.println("Iterations : " + i);
        return bestSolution;
    }

    public static void main(String [] args){
        int testRuns = 100;
        List<List<Integer>> bestSolns = new ArrayList<>();
        List<Double> testUtilitiesOfBestSoln = new ArrayList<>();
        double bestUtility = 0.0;
        BigInteger sumTimes = BigInteger.ZERO;
        long startTime = System.nanoTime();
        for(int i=0;i<testRuns;i++){
            List<Integer> bestSolution = getBestSolution();
            bestSolns.add(bestSolution);
            double utility = calculateUtility(bestSolution);
            testUtilitiesOfBestSoln.add(utility);
            if(utility > bestUtility){
                bestUtility = utility;
            }
            long endTime = System.nanoTime();
            long duration = (endTime - startTime);
            sumTimes = sumTimes.add(BigInteger.valueOf(duration));
        }

        System.out.println("Best solutions : ");
        bestSolns.forEach(bestSolution -> {
            bestSolution.forEach(System.out::print);
            System.out.println("");
        });

        System.out.println("Utilities : ");
        testUtilitiesOfBestSoln.forEach(System.out::println);

        OptionalDouble average = testUtilitiesOfBestSoln.stream().mapToDouble(Double::doubleValue).average();
        System.out.println("Average utility of best solution for alpha = " +EXPONENTIAL_SMOOTHENING_FACTOR_ALPHA+
                " over " + MAX_ITERATIONS +" iterations : " + average.getAsDouble() + " while best utility seen was : " + bestUtility +
                ". Time required to arrive at best solution : " + sumTimes.divide(BigInteger.valueOf(testRuns)));

    }

}

/*
* Varying the value of alpha to study how the reliance on previous distributions affect the utility of the solution returned
* The test runs, no of iterations/conditions for convergence and total percentage of best solutions chosen is the same here
*
* 1. Average utility of best solution for alpha = 0.8 over 100 iterations : 7.715
*       while best utility seen was : 8.7.
*       Time required to arrive at best solution : 611.733043 ms
*
* 2. Average utility of best solution for alpha = 0.5 over 100 iterations : 7.733
*       while best utility seen was : 8.8.
*       Time required to arrive at best solution : 650.768685 ms
*
* 3. Average utility of best solution for alpha = 0.3 over 100 iterations : 7.637
*       while best utility seen was : 8.7.
*       Time required to arrive at best solution : 647.324352 ms

* Varying the number of iterations/convergence conditions for different values of alpha while keeping everything else same
*
* 1. Average utility of best solution for alpha = 0.3
*       over 300 iterations : 8.02
*       while best utility seen was : 8.8.
*       Time required to arrive at best solution : 1.036461054 s
*
*       over 400 iterations : 8.066
*       while best utility seen was : 9.5.
*       Time required to arrive at best solution : 1.144546289 s
*
* 2. Average utility of best solution for alpha = 0.5
*       over 300 iterations : 8.059
*       while best utility seen was : 9.0.
*       Time required to arrive at best solution : 0.971272149 s
*
*       over 400 iterations : 8.119
*       while best utility seen was : 9.0.
*       Time required to arrive at best solution : 1.146389391 s
*
*
  3. Average utility of best solution for alpha = 0.8
*
*      over 300 iterations : 8.052999999999999
*      while best utility seen was : 9.1.
*      Time required to arrive at best solution : 1.039827179 s
*
*       over 400 iterations : 8.058
*       while best utility seen was : 9.1.
*       Time required to arrive at best solution : 1.213707309 s
*
*
* */