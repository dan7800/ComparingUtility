package com.capstone;
import java.math.BigInteger;
import java.util.*;

public class CaseBasedReasoning {


    private final static double UTILITY_THRESHOLD = 74;
    private final static double MIN_SIMILARITY = 0.2;

    private final static int ATTR1_MIN = 1;
    private final static int ATTR1_MAX = 15;
    private final static int ATTR2_MIN = 30;
    private final static int ATTR2_MAX = 80;
    private final static int ATTR3_MIN = 5;
    private final static int ATTR3_MAX = 20;

    private final double ATTR1_WEIGHT = 5;
    private final double ATTR2_WEIGHT = 1;
    private final double ATTR3_WEIGHT = 5;

    private Set<ArrayList<Integer>> knowledgeBase;
    private ArrayList<ArrayList<Integer>> qualifiedAdaptationFrame;
    private HashMap<ArrayList<Integer>, Double> qafSimilarityMap;
    private HashMap<ArrayList<Integer>, Double> qafUtilityMap;
    private HashMap<ArrayList<Integer>, Double> caseUtilityMap;

    public CaseBasedReasoning(){

        // populate knowledge base
        knowledgeBase = new HashSet<ArrayList<Integer>>(5);
        caseUtilityMap = new HashMap<ArrayList<Integer>, Double>();
        Random random = new Random();

        ArrayList<Integer> attrList = null;

        for(int i=0;knowledgeBase.size()<10000;i++) {
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

        /*knowledgeBase.forEach(kbCase -> {
            for(Integer val: kbCase) {
                System.out.print(val + " ");
            }
            System.out.print("Utility : " + caseUtilityMap.get(kbCase));
            System.out.println();
        });
        */

    }

    /*Calculates case usefulness/utility of case in QAF given similarity and utility
    * of case in knowledge base*/
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
        System.out.println("Similarity : " + similarity);
        return similarity;
    }

    private static double getCosineSimilarityWithCase(ArrayList<Integer> caseToCompare, ArrayList<Integer> adaptationRequest){
        double normCase = 0.0;
        double normAdapReq = 0.0;
        double dotProduct = 0.0;
        double similarity = 0.0;
        for(int j=0;j<adaptationRequest.size();j++){
            dotProduct += adaptationRequest.get(j) * caseToCompare.get(j);
            normAdapReq += Math.pow(adaptationRequest.get(j),2);
            normCase += Math.pow(caseToCompare.get(j),2);
        }
        similarity = dotProduct/(Math.sqrt(normAdapReq) * Math.sqrt(normCase));
        return similarity;
    }

    private ArrayList<ArrayList<Integer>> generateQaf(List<Integer> adapReq){


        qualifiedAdaptationFrame = new ArrayList<>();
        qafSimilarityMap = new HashMap<>();
        qafUtilityMap = new HashMap<>();

        knowledgeBase.forEach(kbCase -> {
            double similarity = getEuclidianSimilarityWithCase(kbCase,adapReq);
            if(similarity > MIN_SIMILARITY) {
                //System.out.println("Similarity : " + similarity);
                qualifiedAdaptationFrame.add(kbCase);
                qafSimilarityMap.put(kbCase,similarity);
                qafUtilityMap.put(kbCase,getCaseUtility(similarity,
                        caseUtilityMap.get(kbCase)));
            }
        });
        return qualifiedAdaptationFrame;
    }


    public static void main(String[] args) {
        //Initialize Knowledge Base
        CaseBasedReasoning cbr = new CaseBasedReasoning();

        Random r= new Random();
        BigInteger sumTimes = BigInteger.ZERO;
        List<Long> times = new ArrayList<>();
        int caseRetrieved = 0;
        int caseGenerated = 0;

        List<List<Integer>> adaptationRequests = new ArrayList<>();
//        adaptationRequests.add(Arrays.asList(12,14,38));
//        adaptationRequests.add(Arrays.asList(13,10,36));
//        adaptationRequests.add(Arrays.asList(14,11,46));
//        adaptationRequests.add(Arrays.asList(16,8,32));
//        adaptationRequests.add(Arrays.asList(19,9,46));
//        adaptationRequests.add(Arrays.asList(8,15,41));
//        adaptationRequests.add(Arrays.asList(6,14,58));
//        adaptationRequests.add(Arrays.asList(14,1,70));
//        adaptationRequests.add(Arrays.asList(16,5,37));
//        adaptationRequests.add(Arrays.asList(17,1,31));
//
//        adaptationRequests.add(Arrays.asList(13,15,58));
//        adaptationRequests.add(Arrays.asList(14,11,56));
//        adaptationRequests.add(Arrays.asList(15,12,66));
//        adaptationRequests.add(Arrays.asList(17,9,52));
//        adaptationRequests.add(Arrays.asList(20,10,66));
//        adaptationRequests.add(Arrays.asList(9,14,61));
//        adaptationRequests.add(Arrays.asList(7,15,78));
//        adaptationRequests.add(Arrays.asList(15,2,50));
//        adaptationRequests.add(Arrays.asList(17,6,57));
//        adaptationRequests.add(Arrays.asList(18,2,51));

        int testRuns = 100;
        int rcu = 0;

        for(int i=0;i<testRuns;i++){
            List<Integer> adaptationRequest = new ArrayList<>();
            adaptationRequest.add(r.nextInt((ATTR1_MAX - ATTR1_MIN)+1)+ATTR1_MIN);
            adaptationRequest.add(r.nextInt((ATTR2_MAX - ATTR2_MIN)+1)+ATTR2_MIN);
            adaptationRequest.add(r.nextInt((50 - 1)+1)+1);
            adaptationRequests.add(adaptationRequest);
        }

        long startTime = System.nanoTime();

        for(int t=0;t<testRuns;t++) {
            //Adaption Request made
            List<Integer> adaptationRequest = adaptationRequests.get(t);

            //System.out.print("Adaptation request : ");
            adaptationRequest.forEach(System.out::print);

            //Identify candidate cases from knowledge base for adaptation response
            ArrayList<ArrayList<Integer>> qaf = cbr.generateQaf(adaptationRequest);
            //System.out.println("Qualified Adaptation Frame : " + (qaf.size() == 0 ? "No cases" : ""));
//            for (int i = 0; i < qaf.size(); i++) {
//                for (Integer val : qaf.get(i))
//                    System.out.print(val + " ");
//                System.out.println();
//            }

            //if QAF is not empty - return case with max utility
            if (qaf.size() != 0) {
                Map.Entry<ArrayList<Integer>, Double> maxUtility = null;
                for (Map.Entry<ArrayList<Integer>, Double> entry : cbr.qafUtilityMap.entrySet()) {
                    if (maxUtility == null || entry.getValue().compareTo(maxUtility.getValue()) > 0) {
                        maxUtility = entry;
                    }
                }
                if (maxUtility != null){
                    rcu += maxUtility.getValue();
                   // System.out.println("Case with best utility: " + maxUtility.getValue());
                    caseRetrieved++;
                }
            } else {
                //else generate based on first fit/best fit
                caseGenerated++;
                System.out.println("");
                ArrayList<Integer> firstFitCase = new ArrayList<>();
                ArrayList<Integer> firstFitFound = new ArrayList<>();
                double max_utility = 0.0;

                for (int i = adaptationRequest.get(0); i <= ATTR1_MAX; i++) {
                    for (int j = adaptationRequest.get(1); j <= ATTR2_MAX; j++) {
                        for (int k = adaptationRequest.get(2); k <= ATTR3_MAX; k++) {
                            firstFitCase.add(i);
                            firstFitCase.add(j);
                            firstFitCase.add(k);
                            int caseValue = 0;
                            double caseAverage = 0.0;
                            int noAttrs = firstFitCase.size();
                            for (int m = 0; m < noAttrs; m++) {
                                caseValue += firstFitCase.get(m);
                            }
                            caseAverage = (double) caseValue;
                            double utility = caseAverage;
                            //firstFitCase.forEach(System.out::print);
                            //System.out.println("utility : -- " + utility);
                            if (utility > UTILITY_THRESHOLD && utility > max_utility) {
                                max_utility = utility;
                                //System.out.println("utility : -- " + utility);
                                //System.out.println("first fit found??");
                                firstFitFound.clear();
                                for (i = 0; i < firstFitCase.size();i++) {
                                    firstFitFound.add(firstFitCase.get(i));

                                }
                            }
                            firstFitCase.clear();
                        }
                    }
                }

                /*System.out.println("Best fit case when no QAF : ");
                for (int i = 0; i < firstFitFound.size(); i++) {
                    System.out.print(firstFitFound.get(i) + " ");
                }*/
            }
            long endTime = System.nanoTime();
            long duration = (endTime - startTime);
            //System.out.println(" Time required:" + duration);
            //times.add(duration);
            sumTimes = sumTimes.add(BigInteger.valueOf(duration));
        }

        System.out.println("Average Time required:" + sumTimes.divide(BigInteger.valueOf(testRuns)));
        System.out.println("No of times case retrieved from the KB : " + caseRetrieved);
        System.out.println("No of times a case is generated : " + caseGenerated);

    }
}

/* 1. High similarity value = 1.00
      Min utility threshold = 3.00
      Values for 3 attributes range from 1-5
*     First fit case generation
*       - with 10 cases in the KB = 1.065254 ms
*       - with 100 cases in the KB = 1.013420 ms
*       - with 1000 cases in the KB = 1.286698 ms
*       - with 10000 cases in the KB = 8.641223 ms
*
*   2. Avg similarity value = 0.7
*      Retrieving similar case from KB requires
*      - with 100 cases in the KB = 8.648981 ms
*      - with 1000 cases in the KB = 39.087555 ms
*      - with 10000 cases in the KB = 397.715961 ms =~ 0.39s
*
*      Avg similarity value = 0.5
*      Retrieving similar case from KB requires
*      - with 100 cases in the KB = 8.006866 ms
*      - with 1000 cases in the KB = 28.738609 ms
*      - with 10000 cases in the KB = 396.6688084 ms =~ 0.39s
*
*   3. Avg. similarity value = 0.7
*      Retrieving similar case from KB has a utility of
*      - with 100 cases in the KB = 0.999
*      - with 1000 cases in the KB = 0.999
*      - with 10000 cases in the KB = 0.999
*
*      Increasing average similarity value to = 0.9
*      Retrieving similar case from KB has a utility of
*      - with 100 cases in the KB = 0.997
*      - with 1000 cases in the KB = 0.999
*      - with 10000 cases in the KB = 0.999
*
*    4. High similarity value = 1.0
*       Best fit case generation
*       - with 10 cases in the KB = 5.654208 ms
*       - with 100 cases in the KB = 8.164838 ms
*       - with 1000 cases in the KB = 3.393230 ms
*       - with 10000 cases in the KB = 10.972725 ms
*
*  Switched to Euclidian distance for similarity calculation
*  Also changed the ranges from which attribute values are generated
*  Create an adaptation request that is outside of this range of values (random for all attributes)
*  Changed the knowledge base to a set to make sure there are no duplicate cases
*
*    1. Min similarity value = 0.2 - 100 tests
*      - with 10 cases in the KB:
*           Average Time required: 445.051949 ms
            No of times case retrieved from the KB : 42
            No of times a case is generated : 58
*      - with 100 cases in the KB
*           Average Time required: 241.312213 ms
            No of times case retrieved from the KB : 49
            No of times a case is generated : 51
*      - with 1000 cases in the KB
*           Average Time required: 279.932622 ms
            No of times case retrieved from the KB : 69
            No of times a case is generated : 31
*      - with 10000 cases in the KB
*           Average Time required: 622.077649 ms
            No of times case retrieved from the KB : 53
            No of times a case is generated : 47
*
*      Min similarity value = 0.4 - 100 tests
*      Retrieving similar case from KB requires
*      - with 10 cases in the KB
*           Average Time required:320.164195 ms
            No of times case retrieved from the KB : 34
            No of times a case is generated : 66
*      - with 100 cases in the KB
*           Average Time required:277.319073 ms
            No of times case retrieved from the KB : 33
            No of times a case is generated : 67
*      - with 1000 cases in the KB
*           Average Time required:395.582509 ms
            No of times case retrieved from the KB : 41
            No of times a case is generated : 59
       - with 10000 cases in the KB
            Average Time required:443.810544 ms
            No of times case retrieved from the KB : 45
            No of times a case is generated : 55
*
*
*   2. Min similarity value = 0.2 - 100 tests
*      Retrieved case has an average utility of -
*      - with 10 cases in the KB
*           Average Time required:372.543053 ms
            No of times case retrieved from the KB : 46
            No of times a case is generated : 54
            Avg utility value of retrieved cases : 0.30434782608695654
*      - with 100 cases in the KB
*           Average Time required:472.066121 ms
            No of times case retrieved from the KB : 38
            No of times a case is generated : 62
            Avg utility value of retrieved cases : 0.6578947368421053
*      - with 1000 cases in the KB
*           Average Time required:378.549680 ms
            No of times case retrieved from the KB : 47
            No of times a case is generated : 53
            Avg utility value of retrieved cases : 0.7659574468085106
*      - with 10000 cases in the KB
*           Average Time required:711.258075 ms
            No of times case retrieved from the KB : 44
            No of times a case is generated : 56
            Avg utility value of retrieved cases : 0.7045454545454546
*
*
*      Min similarity value = 0.4
*      Retrieved case has a utility of -
*      - with 10 cases in the KB
*           Average Time required: 253.492255 ms
            No of times case retrieved from the KB : 24
            No of times a case is generated : 76
            Avg utility value of retrieved cases : 0.625
*      - with 100 cases in the KB = 16.596202 ms
*           Average Time required: 460.318782 ms
            No of times case retrieved from the KB : 34
            No of times a case is generated : 66
            Avg utility value of retrieved cases : 0.9117647058823529
*      - with 1000 cases in the KB = 98.469220 ms
*           Average Time required: 311.249998 ms
            No of times case retrieved from the KB : 42
            No of times a case is generated : 58
            Avg utility value of retrieved cases : 0.9047619047619048
*      - with 10000 cases in the KB = 107.501161 ms
*           Average Time required: 559.857539 ms
            No of times case retrieved from the KB : 44
            No of times a case is generated : 56
            Avg utility value of retrieved cases : 0.9318181818181818

*   3. High similarity value  = 0.8    - generate a first fit
*      - with 10 cases in the KB
*           Average Time required:423.099162 ms
            No of times case retrieved from the KB : 12
            No of times a case is generated : 88
*      - with 100 cases in the KB
*           Average Time required:371.428541 ms
            No of times case retrieved from the KB : 26
            No of times a case is generated : 74
*      - with 1000 cases in the KB
*            Average Time required:376.820851 ms
             No of times case retrieved from the KB : 33
             No of times a case is generated : 67
*      - with 10000 cases in the KB
*           Average Time required:694.266901 ms
            No of times case retrieved from the KB : 24
            No of times a case is generated : 76


      High similairty value = 0.9
      - with 10 cases in the KB
*           Average Time required:354.938437 ms
            No of times case retrieved from the KB : 25
            No of times a case is generated : 75
*      - with 100 cases in the KB
*           Average Time required:277.993476 ms
            No of times case retrieved from the KB : 38
            No of times a case is generated : 62
*      - with 1000 cases in the KB
*            Average Time required:355.271329 ms
             No of times case retrieved from the KB : 32
             No of times a case is generated : 68
*      - with 10000 cases in the KB
*           Average Time required:728.201661 ms
            No of times case retrieved from the KB : 35
            No of times a case is generated : 65
*
*   4. High similarity value - best fit generation - starting from the adaptation request
*       - with 10 cases in the KB
*           Average Time required:139.601874 ms
            No of times case retrieved from the KB : 13
            No of times a case is generated : 87
*      - with 100 cases in the KB
*           Average Time required:49.425100 ms
            No of times case retrieved from the KB : 33
            No of times a case is generated : 67
*      - with 1000 cases in the KB
*            Average Time required:108.250675 ms
             No of times case retrieved from the KB : 33
             No of times a case is generated : 67
*      - with 10000 cases in the KB
*           Average Time required:423.061611 ms
            No of times case retrieved from the KB : 32
            No of times a case is generated : 68
*
*
*
*       */