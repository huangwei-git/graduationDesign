package com.songlian.logistics.calculate;

import com.songlian.logistics.calculate.ACO_TSP.ACO_TSP;
import com.songlian.logistics.calculate.GA_TSP.GA_TSP;
import com.songlian.logistics.calculate.IP_TSP.IP_TSP;
import com.songlian.logistics.calculate.TabuSearch_TSP.TS_TSP;
import com.songlian.logistics.pojo.OrderForm;
import com.songlian.logistics.service.impl.OrderFormServiceImpl;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        File file = new File("./src/main/resources/att48.tsp");
        BufferedReader br = null;
        FileReader fr = null;

        int N = 48;

        double point[][] = new double[N][2];
        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);

            String data = br.readLine();
            while (data != null){
                String strs[] = data.split(" ");
                int index = Integer.valueOf(strs[0]) - 1;
                point[index][0] = Double.valueOf(strs[1]);
                point[index][1] = Double.valueOf(strs[2]);
                data = br.readLine();
            }

            List<double[]> list = new ArrayList<>();
            Collections.addAll(list,point);
            double[][] dist = new double[N][N];
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    dist[i][j] = getDistance(point[i],point[j]);
                }
            }
            System.out.println(new TS_TSP(list,dist).solve());
            System.out.println(new GA_TSP(list,dist).solve());
            System.out.println(new ACO_TSP(list,dist).solve());
            System.out.println(new IP_TSP(list,dist).solve());

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            if(fr!=null){
                fr.close();
            }
            if(br!=null){
                br.close();
            }
        }

    }

    public static double getDistance(double[] place1, double[] place2) {
        // 伪欧氏距离在根号内除以了一个10
        return Math.sqrt((Math.pow(place1[0] - place2[0], 2) + Math.pow(place1[1] - place2[1], 2)));
//        return Math.sqrt((Math.pow(place1[0] - place2[0], 2) + Math.pow(place1[1] - place2[1], 2)));
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