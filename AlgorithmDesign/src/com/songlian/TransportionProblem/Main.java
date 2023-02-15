package com.songlian.TransportionProblem;

public class Main {
    public static void main(String[] args) {
        int cost[][] = {{8,7,3,2}, {4,7,5,1}, {2,4,9,6}};
        int rows[] = {1,9,4};
        int cols[] = {3,2,4,5};
        //int tab[][] = {{0, 0, 3, 10}, {1, 0, 2, 0}, {0, 4, 0, 5}};
        //int test[][] = {
        //        {3, 15, 0, 0},
        //        {13, 0, 5, 0},
        //        {0, 0, 2, 17}
        //};
        Solution solution = new Solution(rows, cols, cost);
        solution.Run();
    }
}
