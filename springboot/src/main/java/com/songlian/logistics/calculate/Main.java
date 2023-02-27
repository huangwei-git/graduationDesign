package com.songlian.logistics.calculate;

public class Main {
    public static void main(String[] args) {
        Main Mainclas = new Main();
        double[][] arr = Mainclas.getDoubleArray2(8,12);
        for (int k = 0; k < 6; k++) {
            for(int i = 0;i < 12;i++){
                for (int j = 0; j < 12; j++) {
                    System.out.print(arr[i+k*12][j] + "     ");
                }
                System.out.println();
            }
            System.out.println("\n===============================");
        }
    }

    public double[][] getDoubleArray(int init,int cnt) {
        double arr[][] = new double[cnt * 12][12];
        for(int t = 0;t < cnt;t++){
            int workTime = init + (t * 2);
            int step = workTime >> 1;
            int start = 13 - step;
            for (int i = 0 + (12 * t); i < 12 * (t + 1); i++) {
                for (int j = 0; j < step; j++) {
                    arr[i][(start + j) % 12] = 1;
                }
                start = (start + 1) % 12;
            }
        }
        return arr;
    }

    public double[][] getDoubleArray2(int min,int max) {
        double arr[][] = new double[6 * 12][12];
        for(int t = 0;t < 6;t++){
            int workTime = 2 + (t * 2);
            int step = workTime >> 1;
            int start = 13 - step;
            if(workTime < min || workTime > max) continue;
            for (int i = 0 + (12 * t); i < 12 * (t + 1); i++) {
                for (int j = 0; j < step; j++) {
                    arr[i][(start + j) % 12] = 1;
                }
                start = (start + 1) % 12;
            }
        }
        int k = 2;
        return arr;
    }
}

/*
map.get("people") = [112, 147, 167, 142, 188, 271, 302, 261, 305, 237, 179, 148]
map.get("salary") = [119, 149, 179, 133, 219, 250, 264, 266, 274, 226, 129, 117]

 */