package com.capstone;

import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
import java.lang.Integer;

/**
 * Created by Heena on 11/29/2018.
 */
public class RandomCollection {
    private final NavigableMap<Double, Integer> map = new TreeMap<Double, Integer>();
    private final Random random;
    private double total = 0;

    public RandomCollection() {
        this(new Random());
    }

    public RandomCollection(Random random) {
        this.random = random;
    }

    public RandomCollection add(double weight, Integer result) {
        if (weight <= 0) return this;
        total += weight;
        map.put(total, result);
        return this;
    }

    public Integer next() {
        double value = random.nextDouble() * total;
        return map.higherEntry(value) == null? new Integer(0) : map.higherEntry(value).getValue() ;
    }

    public void displayCollection(){
        for(Map.Entry<Double, Integer> e: map.entrySet()){
            System.out.println(Math.ceil(e.getKey()) + "-" + e.getValue());
        }
    }
}