package com.capstone;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Heena on 11/27/2018.
 */
public class CrossEntropy {

    private final static int ATTR1_MIN = 1;
    private final static int ATTR1_MAX = 5;
    private final static int ATTR2_MIN = 1;
    private final static int ATTR2_MAX = 5;
    private final static int ATTR3_MIN = 1;
    private final static int ATTR3_MAX = 5;

    private final static int MAX_ITERATIONS = 10;
    private final static int NO_OF_SAMPLES = 10;
    private final static double RATIO_OF_BEST_RESULTS = 0.6;

    private final static double EXPONENTIAL_SMOOTHENING_FACTOR_ALPHA = 0.5;

    private static double calculateUtility(ArrayList<Integer> solution){
        return solution.stream().mapToInt(Integer::intValue).average()
    }

    private static double evaluateSolution(ArrayList<Integer> solution){
        return CrossEntropy.calculateUtility(solution);
    }

    private static boolean isBetterThatBestSoFar(ArrayList<Integer> solution, ArrayList<Integer> bestSolution){
        return calculateUtility(solution) > calculateUtility(bestSolution);
    }

    private static int chooseRandomValueInRange(int min, int max){
        Random r = new Random();
        return r.nextInt((max - min)+1)+min;
    }

    private static ArrayList<Integer> getBestSolution(){
        while()
    }

    public static void main(String [] args){

    }

}
