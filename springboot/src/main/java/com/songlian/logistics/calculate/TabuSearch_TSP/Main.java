package com.songlian.logistics.calculate.TabuSearch_TSP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        double locs[][] = {{1549,3408}, {1561,2641}, {3904,3453}, {2216,1177}, {1563,4906}, {3554,827}, {2578,4370}, {3358,2054}, {143,4789}, {610,774}, {1557,4064}, {771,1823}, {4753,4192}, {2037,1897}, {4692,1887}, {839,415}, {4314,2696}, {428,3626}, {2725,543}, {2349,263}, {770,2550}, {1627,1361}, {2139,3908}, {1977,2775}, {4345,11}};
        List<double[]> list = new ArrayList<>();
        Collections.addAll(list, locs);
        TS_TSP tsp = new TS_TSP(list);
        tsp.solve();
    }
}
