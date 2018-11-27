package com.capstone;

import java.math.BigInteger;
import java.util.*;

/**
 * Created by Heena on 11/18/2018.
 */
public class CaseBasedReasoningVolatility {

    private final static double UTILITY_THRESHOLD = 74;
    private final static double MIN_SIMILARITY = 0.2;

    private final static int ATTR1_MIN = 1;
    private final static int ATTR1_MAX = 15;
    private final static int ATTR2_MIN = 30;
    private final static int ATTR2_MAX = 80;
    private final static int ATTR3_MIN = 5;
    private final static int ATTR3_MAX = 20;

    private Set<ArrayList<Integer>> knowledgeBase;
    private ArrayList<ArrayList<Integer>> qualifiedAdaptationFrame;
    private HashMap<ArrayList<Integer>, Double> qafSimilarityMap;
    private HashMap<ArrayList<Integer>, Double> qafUtilityMap;
    private HashMap<ArrayList<Integer>, Double> caseUtilityMap;

    public CaseBasedReasoningVolatility(){

        // populate knowledge base
        knowledgeBase = new HashSet<ArrayList<Integer>>(5);
        caseUtilityMap = new HashMap<ArrayList<Integer>, Double>();
        Random random = new Random();

        ArrayList<Integer> attrList = null;

        for(int i=0;knowledgeBase.size()<1000;i++) {
            attrList = new ArrayList<>();
            attrList.add(random.nextInt((ATTR1_MAX - ATTR1_MIN)+1)+ATTR1_MIN);
            attrList.add(random.nextInt((ATTR2_MAX - ATTR2_MIN)+1)+ATTR2_MIN);
            attrList.add(random.nextInt((ATTR3_MAX - ATTR3_MIN)+1)+ATTR3_MIN);
            if(!knowledgeBase.containsAll(attrList))
                knowledgeBase.add(attrList);
        }

        System.out.println(knowledgeBase.size());

        // calculate tentative utility for every case in the knowledge base - store in map
        knowledgeBase.forEach(kbCase -> {
                    int kbCaseValue = 0;
                    double kbCaseAverage = 0.0;
                    for(Integer attrVal: kbCase){
                        kbCaseValue += attrVal;
                    }
                    kbCaseAverage = (double)kbCaseValue;
                    caseUtilityMap.put(kbCase,kbCaseAverage/(ATTR3_MAX + ATTR2_MAX + ATTR1_MAX));
                }
        );

    }

    private static double getCaseUtility(Double similarity, Double utility){
        double negateSim = 1 - similarity;
        double mulUtility = negateSim * utility;
        //System.out.println(mulUtility);
        double caseUtility = 1 - mulUtility;
        return caseUtility;
    }

    private static double getEuclidianSimilarityWithCase(ArrayList<Integer> caseToCompare, List<Integer> adaptationRequest){
        int diffi = 0;
        double diffSquared = 0.0;
        double diff = 0.0;
        double similarity = 0.0;
        for(int i=0;i<caseToCompare.size();i++){
            diffi = caseToCompare.get(i) - adaptationRequest.get(i);
            diffSquared = Math.pow(diffi,2);
            diff += diffSquared;
        }
        similarity = 1/(1+Math.sqrt(diffSquared));
        //System.out.println("Similarity : " + similarity);
        return similarity;
    }

    private static void generateBestFit(List<Integer> adaptationRequest){

        ArrayList<Integer> firstFitCase = new ArrayList<>();
        ArrayList<Integer> bestFit = new ArrayList<>();

        double max_utility = 0.0;
        int imin = adaptationRequest.get(0) >= ATTR1_MIN && adaptationRequest.get(0) <= ATTR1_MAX ? adaptationRequest.get(0):ATTR1_MIN;
        int jmin = adaptationRequest.get(1) >= ATTR2_MIN && adaptationRequest.get(1) <= ATTR2_MAX ? adaptationRequest.get(1):ATTR2_MIN;
        int kmin = adaptationRequest.get(2) >= ATTR3_MIN && adaptationRequest.get(2) <= ATTR3_MAX ? adaptationRequest.get(2):ATTR3_MIN;

        for (int i = imin; i <= ATTR1_MAX; i++) {
            for (int j = jmin; j <= ATTR2_MAX; j++) {
                for (int k = kmin; k <= ATTR3_MAX; k++) {
                    firstFitCase.add(i);
                    firstFitCase.add(j);
                    firstFitCase.add(k);
                    double caseAverage = 0.0;
                    int noAttrs = firstFitCase.size();
                    int caseValue = firstFitCase.stream().mapToInt(Integer::intValue).sum();
                    double utility = (double)caseValue/noAttrs;

                    if (utility > UTILITY_THRESHOLD && utility > max_utility) {
                        max_utility = utility;
                        bestFit.clear();
                        for (i = 0; i < firstFitCase.size();i++) {
                            bestFit.add(firstFitCase.get(i));

                        }
                    }
                    firstFitCase.clear();
                }
            }
        }
    }

    public static void main(String[] args) {
        CaseBasedReasoningVolatility cbr = new CaseBasedReasoningVolatility();

        Random r= new Random();
        BigInteger sumTimes = BigInteger.ZERO;
        List<Long> times = new ArrayList<>();
        int caseRetrieved = 0;
        int caseGenerated = 0;
        int rcu = 0;

        int testRuns = 100;
       /* List<List<Integer>> adaptationRequests = new ArrayList<>();
        List<Integer> adaptationRequest = new ArrayList<>();
        // adaptation request with one attr value out of range
        int attr1Val = r.nextInt((ATTR1_MAX - ATTR1_MIN)+1)+ATTR1_MIN;
        int attr2Val = r.nextInt((ATTR2_MAX - ATTR2_MIN)+1)+ATTR2_MIN;
        int attr3Val = r.nextInt((60 - 30)+1)+30;

        //adaptation request with 2 attr values out of range
        int attr1Val2off = r.nextInt((ATTR1_MAX - ATTR1_MIN)+1)+ATTR1_MIN;
        int attr2Val2off = r.nextInt((100 - 80)+1)+80;
        int attr3Val2off = r.nextInt((70 - 20)+1)+20;

        //adaptation request with all attr values out of range
        int attr1Val3off = r.nextInt((50 - 16)+1)+16;
        int attr2Val3off = r.nextInt((100 - 80)+1)+80;
        int attr3Val3off = r.nextInt((70-20)+1)+20;

        adaptationRequest.add(attr1Val3off);
        adaptationRequest.add(attr2Val3off);
        adaptationRequest.add(attr3Val3off);
        adaptationRequests.add(adaptationRequest);*/

        List<List<Integer>> adaptationRequests = new ArrayList<>();
        for(int t=0;t<testRuns;t++) {
            List<Integer> adaptationRequest = new ArrayList<>();
            adaptationRequest.add(r.nextInt((ATTR1_MAX - ATTR1_MIN)+1)+ATTR1_MIN);
            adaptationRequest.add(r.nextInt((ATTR2_MAX - ATTR2_MIN)+1)+ATTR2_MIN);
            adaptationRequest.add(r.nextInt((70-20)+1)+20);
            adaptationRequests.add(adaptationRequest);
        }

        for(int t=0;t<testRuns;t++) {
            long startTime = System.nanoTime();
            generateBestFit(adaptationRequests.get(t));
            long endTime = System.nanoTime();
            long duration = (endTime - startTime);
            sumTimes = sumTimes.add(BigInteger.valueOf(duration));
        }

        System.out.println("Average Time required:" + sumTimes.divide(BigInteger.valueOf(testRuns)));

    }
}

/*
* Average amount of time required to generate a best fit case with 1 value out of range = 0.456235 ms
* Average amount of time required to generate a best fit case with 2 values out of range = 1.857171 ms
* Average amount of time required to generate a best fit case with 3 values out of range = 2.332560 ms
*
* Average amount of time required to generate a best fit case with 100 different cases - 1 attrs out of range - 1.493913 ms
* Average amount of time required to generate a best fit case with 100 different cases - 2 attrs out of range - 1.930368 ms
* Average amount of time required to generate a best fit case with 100 different cases - all attrs out of range - 2.456528 ms
* */