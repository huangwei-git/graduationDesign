package com.songlian.logistics.calculate.Transport;

import com.songlian.logistics.calculate.TSP.TspSolution;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Path {

    public static int[] getPath(List<HashMap> list,int dist[][]){
        int vn = list.size() + 1;
        int[][] graph = new int[vn][vn];

        List<Integer> indexList = new LinkedList<>();

        for(int i = 1;i < list.size() + 1;i++){
            HashMap map = list.get(i - 1);
            int toll = (int) map.get("toll");
            int idx = (int) map.get("seq");
            graph[0][i] = graph[i][0] = toll/10;
            indexList.add(idx);
        }

        for(int i = 1;i < vn;i++){
            int idx = indexList.get(i - 1);
            for(int j = 1;j < vn;j++){
                    graph[i][j] = graph[j][i] = dist[idx][indexList.get(j - 1)];
            }
        }
        System.out.println("============================");
        System.out.println(list.get(0).get("supplierName"));
        for(int i = 0;i < vn;i++){
            for(int j = 0;j < vn;j++){
                System.out.print(graph[i][j] + " ");
            }
            System.out.println();
        }

        int path[] = new int[list.size()];
        TspSolution tspSolution = new TspSolution(graph);
        int res[] = tspSolution.printPath();
        for (int i = 1; i < res.length; i++) {
            int idx = res[i];
            path[i - 1] = idx - 1;
            System.out.print(list.get(idx - 1).get("demanderName") + "   ");
        }
        System.out.println("\n============================");

        return path;
    }
}
