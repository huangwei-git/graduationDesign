package com.songlian.logistics.calculate.Transport;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        int cost[][] = {
                {3,11,3,10},
                {1,9,2,8},
                {7,4,10,5}
        };
        int supplier[] = {2,2,2};
        int demander[] = {2,2,2,0};

        TransportionSolution solution = new TransportionSolution(supplier, demander, cost,true);
        solution.setOpenProcessPrint(true);
        solution.solve();

        //int arr[]={1,2,3,3,3,3,4,5,6,6,6,7};
        //int idx = arr.length - 1;
        //List<Integer> list = new LinkedList<>();
        //int min = arr[0];
        //for (int i = 1; i <= idx; i++) {
        //    if(arr[i] > min){
        //        min = arr[i];
        //    }else list.add(i);
        //}
        //System.out.println("list = " + list);
    }
}
/**
 * [33, 0, 0, 0, 0]
 * [0, 0, 0, 63, 0]
 * [0, 0, 44, 11, 7]
 * [0, 17, 3, 0, 0]
 * [9, 19, 0, 0, 0]
 *
 */