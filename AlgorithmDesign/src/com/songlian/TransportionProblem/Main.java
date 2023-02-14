package com.songlian.TransportionProblem;

public class Main {
    public static void main(String[] args) {
        int cost[][] = {{3, 11, 3, 10}, {1, 9, 2, 8}, {7, 4, 10, 5}};
        int rows[] = {7, 4, 9};
        int cols[] = {3, 6, 5, 6};

        int test[][] = {
                {0, 0, 3, 10},
                {1, 0, 2, 0},
                {0, 4, 0, 5}
        };
        Solution solution = new Solution(rows, cols, cost);
        solution.init();
        System.out.println("==========================");
        solution.genGraph(solution.getTab());
    }
}
