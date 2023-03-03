package com.songlian.logistics.calculate.DP_TSP;

import java.util.LinkedList;
import java.util.List;

/**
 * dp[i, V]：
 *      1. i为出发顶点
 *      2. V为顶点集合
 *      3. dp[i, V]表示从i出发，经过了V中所有顶点一次后，所用的最短路径
 *      4. dp[i, V] =
 *          4.1 graph[i, s]: 如果 V = ∅
 *          4.2 min {graph[i, k] + dp[k, V-{k}]}， 其中 k ∈ V 且 V ≠ ∅
 *      5. 对于n个顶点的图，V集合一共有 2 ^ (n - 1)种可能 （去掉了起始点）
 *          5.1 把1,2,...,n的顶点编号看做二进制位数，若i在集合中，则权重为2 ^ (i-1)，如V={1,2,4},则索引为idx=1+2+8，对应dp[?, 8]
 *          5.2 由上可知，对于dp[i, j]，判断是否包括点x，可以通过判断j的二进制编码中第x-1位是否为1判断，即 j & (j >> (x - 1))=1，则包括x；=0则不包括x
 *          5.3 dp[i, j]中，取出顶点x：j = j ^ (1 << (x - 1))
 */

// 默认从0出发
public class TspSolution {

    public static void main(String[] args) {
        /**
        10_	0	0	0	0
         0	28_	0	25_	0
         0	0	49_	0	2_
         12_	7_	9_	0	0
         43_	0	0	0	0a
         */
        int g[][] = {
                {0,3,10000,8,9},
                {3,0,3,10,5},
                {100000,3,0,4,3},
                {8,10,4,0,20},
                {9,5,3,20,0}
        };

        new TspSolution(g).printPath();
    }
    private int graph[][];

    private int rowLen;

    private int colLen;

    private int dp[][];

    private List<Integer> path;

    public TspSolution(int map[][]){
        this.graph = map;
        this.rowLen = map.length;
        this.colLen = 1 << (this.rowLen - 1);
        dp = new int[this.rowLen][this.colLen];
        path = new LinkedList<>();
    }


    public void TSP(){
        //  初始化dp表：当V为空集时，dp[i][0]等于点i到起始点0的距离
        for(int i = 0;i < this.rowLen;i++){
            dp[i][0] = graph[i][0];
        }
        // 动态规划求解：经过的点数逐渐变多，因此从V=1开始遍历顶点，
        for(int V = 1;V < this.colLen;V++){
            for(int x = 0;x < this.rowLen;x++){
                this.dp[x][V] = Integer.MAX_VALUE;
                // 如果V中包含了顶点x，即已经走过了x，则跳过
                if((V >> (x - 1) & 1) == 1) continue;
                // 如果还未经过顶点x，则计算V中加入x后的最短路径
                for(int i = 1;i < this.rowLen;i++){
                    //      设最短路径为：v1-v2-...-vk-x
                    //          则有 dp[x,V]= graph(v,x) + dp[vk, V-{vk}]
                    if((V >> (i - 1) & 1) == 0) continue;
                    if(this.dp[x][V] > this.graph[x][i] + this.dp[i][V ^ (1 << (i - 1))]){
                        this.dp[x][V] = this.graph[x][i] + this.dp[i][V ^ (1 << (i - 1))];
                    }
                }
            }
        }
    }

    // 判断是否访问了所有顶点
    public boolean isVisited(int visited[]){
        for (int i = 1; i < this.rowLen; i++) {
            if(visited[i] == 0){
                return false;
            }
        }
        return true;
    }


    // 获得得到的最优路径
    public void getPath(){
        int visited[] = new int[this.rowLen];
        // 上一个节点的编号,从起始点0开始
        int preV = 0;
        // 将除起始点的所有顶点加入集合V
        int V = this.colLen - 1;
        int tmp = 0;
        int min = Integer.MAX_VALUE;
        int cnt = this.rowLen - 1;

        // 初始化path
        path.add(0);

        // 当还有节点未访问
        //while(!isVisited(visited)){
        while(cnt > 0){
            // 找到最短路径： 0 -> i -> V-{i}
            for (int i = 1; i < this.rowLen; i++) {
                if(visited[i] == 0 && this.dp[i][V & (1 << (i - 1))] != 0){
                    if(min > this.graph[i][preV] + this.dp[i][V ^ (1 << (i - 1))]){
                        min = graph[i][preV] + dp[i][V ^ (1 << (i - 1))];
                        tmp = i;
                    }
                }
            }
            preV = tmp;
            path.add(tmp);
            visited[tmp] = 1;
            cnt--;
            V = V ^ (1 << (tmp - 1));
            min = Integer.MAX_VALUE;
        }
    }

    public int[] printPath(){
        this.TSP();
        this.getPath();
        int res[] = new int[path.size()];
        res[0] = this.dp[0][this.colLen - 1];

        for (int i = 0; i < path.size(); i++) {
            int val = path.get(i);
            if(i != 0) res[i] = path.get(i);
        }

        // 输出结果
        //System.out.println("最小值为：" + this.dp[0][this.colLen - 1]);
        //path.forEach(v -> {
        //    System.out.print(v + " --> ");
        //});
        //System.out.println(0);

        return res;
    }
}
