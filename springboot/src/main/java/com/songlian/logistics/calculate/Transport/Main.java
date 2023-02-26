package com.songlian.logistics.calculate.Transport;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        //int cost[][] = {
        //        {3,2,4,5},
        //        {2,3,5,3},
        //        {1,4,2,4}
        //};
        //int supplier[] = {30,40,50};
        //int demander[] = {16,30,24,50};
        //
        //TransportionSolution solution = new TransportionSolution(supplier, demander, cost,true);
        //solution.Run();

        int arr[]={1,2,3,3,3,3,4,5,6,6,6,7};
        int idx = arr.length - 1;
        List<Integer> list = new LinkedList<>();
        int min = arr[0];
        for (int i = 1; i <= idx; i++) {
            if(arr[i] > min){
                min = arr[i];
            }else list.add(i);
        }
        System.out.println("list = " + list);
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