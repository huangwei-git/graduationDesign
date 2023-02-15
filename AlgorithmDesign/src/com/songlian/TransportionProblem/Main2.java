package com.songlian.TransportionProblem;

public class Main2 {
    public static void main(String[] args) {
        int cost[][] = {
                {3,2,4,5},
                {2,3,5,3},
                {1,4,2,4}
        };
        int supplier[] = {30,40,50};
        int demander[] = {16,30,24,50};

        Solution solution = new Solution(supplier, demander, cost);
        solution.Run();
    }
}
