package com.songlian.TransportionProblem;

public class Main {
    public static void main(String[] args) {
        int cost[][] = {
                {16,16,13,22,17,17},
                {14,14,13,19,15,15},
                {19,19,20,23,5000,5000},
                {5000,0,5000,0,5000,0}
        };
        int rows[] = {50, 60, 50, 50};
        int cols[] = {30, 20, 70, 30, 10, 50};

        Solution solution = new Solution(rows, cols, cost);
        solution.Run();
    }
}
