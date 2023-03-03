package com.songlian.logistics.calculate.GA_TSP;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class Genome {
    // 距离矩阵
    public static double[][] dist;
    // 基因序列
    private int[] genomeArray;
    // 适应度函数值
    private double fitness;
    // 序列对应的路径长度
    private double pathLen;

    // 构造函数
    public Genome(int[] genomeArray) {
        this.genomeArray = genomeArray.clone();
        updateFitnessAndPathLen();
    }

    // 获取适应值和路径长度
    public void updateFitnessAndPathLen(){
        pathLen = Genome.getPathLen(genomeArray);
        fitness = 10000d/pathLen;
    }

    // 求路程长度
    public static double getPathLen(int[] path) {
        double pathLen = 0.0;
        for (int i = 1; i < path.length; i++) {
            // 起点到终点途径路程累加
            pathLen += dist[path[i - 1]][path[i]];
        }
        // 然后再加上返回起点的路程
        pathLen += dist[path[0]][path[path.length - 1]];
        return pathLen;
    }

}
