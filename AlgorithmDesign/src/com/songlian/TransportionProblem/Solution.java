package com.songlian.TransportionProblem;

import com.sun.istack.internal.localization.NullLocalizable;

import java.util.*;

class Solution{
    // 工厂供应量
    private int A[];
    private int numA;
    // 客户需求量
    private int B[];
    private int numB;
    // 运价
    private int Cost[][];
    // 最终方案
    private int tab[][];
    private int rowGeopotential[];
    private int colGeopotential[];

    public Solution(){}
    public Solution(int[] a, int[] b, int[][] cost) {
        A = a;
        this.numA = a.length;
        B = b;
        this.numB = b.length;
        Cost = cost;
        tab = new int[numA][numB];
        rowGeopotential = new int[numA];
        colGeopotential = new int[numB];
    }

    public void Run(){
        // 1. 获得初始方案
        getInitialScheme();
        // 2. 位势法 + 闭合回路调整
        Point point = null;
        while((point = getBaseVariable()) != null){
            // 2.1 获得入基变量
            // 2.2 通过入基变量求闭合回路
            LinkedList<Point> path = getclosedLoop(tab, point.x, point.y);
            int delta = Integer.MAX_VALUE;
            // 2.3 找闭合回路的变化值
            for(int i = 1;i < path.size();i+=2){
                Point p = path.get(i);
                int val = tab[p.x][p.y];
                if(val < delta) delta = val;
            }
            // 2.4 修改回路上的分配方案
            int direction = 1;
            for(Point p : path){
                tab[p.x][p.y] += direction * delta;
                direction *= -1;
            }
        }
        /*==========输出最终方案/*==========*/
        System.out.println("最终方案为：");
        for(int i = 0;i < numA;i++){
            for(int j = 0;j < numB;j++){
                System.out.print(tab[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("需要的价格为：" + getCost(Cost,tab));
    }

    // 贪心法：计算所有行和列中，最小值和次小值的差值，从中取出最大的一个，作为入基变量
    private Point getGap(int rows[], int cols[]){
        int max = Integer.MIN_VALUE;
        Point p = new Point(-1,-1);
        Point tmpP = new Point(-1,-1);

        // 获取行的差额
        for (int i = 0; i < numA; i++) {
            // 如果该行被划掉，则不计算
            if (rows[i] == 1) continue;
            int min1 = Integer.MAX_VALUE;
            int min2 = Integer.MAX_VALUE;
            tmpP.x = i;
            for (int j = 0; j < numB; j++) {
                // 如果第j列没被划掉
                if (cols[j] == 0) {
                    if (Cost[i][j] < min2){
                        if(Cost[i][j] <= min1){
                            min2 = min1;
                            min1 = Cost[i][j];
                            tmpP.y = j;
                        }else min2 = Cost[i][j];
                    }
                }
            }
            if(min2 != Integer.MAX_VALUE){
                int gap = min2 - min1;
                if(gap > max){
                    max = gap;
                    p.setPoint(tmpP);
                }
            }
        }

        // 获取列的差额
        for (int j = 0; j < numB; j++) {
            // 如果该列被划掉，则不计算
            if (cols[j] == 1) continue;
            int min1 = Integer.MAX_VALUE;
            int min2 = Integer.MAX_VALUE;
            tmpP.y = j;
            for (int i = 0; i < numA; i++) {
                // 如果第j行没被划掉
                if (rows[i] == 0) {
                    if (Cost[i][j] < min2){
                        if(Cost[i][j] <= min1){
                            min2 = min1;
                            min1 = Cost[i][j];
                            tmpP.x = i;
                        }else min2 = Cost[i][j];
                    }
                }
            }
            if(min2 != Integer.MAX_VALUE){
                int gap = min2 - min1;
                if(gap > max){
                    max = gap;
                    p.setPoint(tmpP);
                }
            }
        }
        return p;
    }


    // 获得初始解决方案
    public void getInitialScheme(){
        int cpA[] = A.clone();
        int cpB[] = B.clone();
        int rows[] = new int[numA];
        int cols[] = new int[numB];
        // 记录没被划掉的行
        int cnt_row = numA;
        // 记录没被划掉的列
        int cnt_col = numB;
        Point point = null;
        while(cnt_row != 0 && cnt_col != 0){
            if(cnt_row == 1 && cnt_col == 1){
                for(int i = 0;i < numA;i++) if(rows[i] == 0) point.x = i;
                for(int j = 0;j < numB;j++) if(cols[j] == 0) point.y = j;
            }else point = getGap(rows,cols);

            int min = cpA[point.x] < cpB[point.y]?cpA[point.x]:cpB[point.y];
            tab[point.x][point.y] = min;
            if((cpA[point.x] -= min) == 0) {rows[point.x] = 1;cnt_row--;};
            if((cpB[point.y] -= min) == 0) {cols[point.y] = 1;cnt_col--;};
        }

        System.out.println("==========初始解决方案==========");
        for(int i = 0;i < numA;i++){
            for (int j = 0;j < numB;j++){
                System.out.print(tab[i][j]+"\t");
            }
            System.out.println();
        }
    }


    // 计算位势
    private void getGeopotential() {
        /**
         * 计算位势
         */
        // flag为0时，使用rowStack
        // flag为1时，使用colStack
        int flag = 0;
        Stack<Integer> rowStack = new Stack<Integer>();
        Stack<Integer> colStack = new Stack<Integer>();
        Stack<Integer> stack = null;
        int visitedRow[] = new int[numA];
        int visitedCol[] = new int[numB];
        rowStack.push(0);
        visitedRow[0] = 1;
        stack = rowStack;

        /* 迭代 */
        while (!stack.isEmpty()){
            while(!stack.isEmpty()){
                int op = stack.pop();
                // flag=0时，遍历op行的每一列，计算列位势
                if(flag == 0){
                    for(int i = 0;i < numB;i++){
                        // 如果已经计算过位势 或 对应位置的元素为非基变量 ，则跳过
                        if(visitedCol[i] == 1 || tab[op][i] == 0) continue;
                        // 列位势 = 运费 - 行位势
                        colGeopotential[i] = Cost[op][i] - rowGeopotential[op];
                        visitedCol[i] = 1;
                        colStack.push(i);
                    }
                }
                // flag=1时，遍历op行的每一行，计算行位势
                else{
                    for(int i = 0;i < numA;i++){
                        // 如果已经计算过位势 或 对应位置的元素为非基变量 ，则跳过
                        if(visitedRow[i] == 1 || tab[i][op] == 0) continue;
                        // 行位势 = 运费 - 列位势
                        rowGeopotential[i] = Cost[i][op] - colGeopotential[op];
                        visitedRow[i] = 1;
                        rowStack.push(i);
                    }
                }
            }
            if(flag == 0){
                flag = 1;
                stack = colStack;
            }else{
                flag = 0;
                stack = rowStack;
            }
        }
        //System.out.println("==========位势==========");
        //for (int i = 0; i < numA; i++) System.out.print(rowGeopotential[i] + " ");
        //System.out.println();
        //for(int i = 0;i < numB;i++) System.out.print(colGeopotential[i] + " ");
        //System.out.println();
    }

    // 计算检验数:thet = 费用 - (行位势 + 列位势)
    // 获得入基变量的point对象，若无需入基，返回null
    public Point getBaseVariable(){
        getGeopotential();

        int arr[][] = new int[numA][numB];

        Point point = new Point(-1, -1);
        int minThet = 0;
        for (int i = 0; i < numA; i++) {
            for (int j = 0; j < numB; j++) {
                int thet = Cost[i][j] - (rowGeopotential[i] + colGeopotential[j]);
                arr[i][j] = thet;
                if(thet < minThet){
                    minThet = thet;
                    point.x = i;
                    point.y = j;
                }
            }
        }
        //System.out.println("==========检验数==========");
        //for(int i = 0;i < numA;i++){
        //    for(int j = 0;j < numB;j++){
        //        System.out.print(arr[i][j] + " ");
        //    }
        //    System.out.println();
        //}
        if(minThet == 0) return null;
        else return point;
    }

    // 求矩形闭合回路
    public Point getClosedLoopByRctangle(int arr[][],int x,int y){
        Point point = new Point(-1,-1);
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++){
                if(i == x || j == y || arr[i][j] == 0) continue;
                if(arr[i][y] != 0 && arr[x][j] != 0){
                    point.x = i;
                    point.y = j;
                    return point;
                }
            }
        }
        return point;
    }

    ///**
    // * 一、 将数组转换为图
    // *      1. 先将有数据的坐标打包成point对象（即构建图时的顶点），存入Linkedlist中，同时用map映射point与索引
    // *          1.1 point在LinkedList中的顺序作为后面构建图时的顶点号
    // *          1.2 通过LinkedList可以快速找到某个顶点号对应的point
    // *          1.3 通过map可以快速找到某个point对应的顶点号
    // *      2. 将数组构建成十字链表，方便查找相邻元素
    // *          2.1 十字链表采用双链表形式
    // *          2.2 十字链表对象中包括一个链表结点类型的二维数组，用于映射坐标对应十字链表中的结点，用于快速通过point找到其在十字链表中的位置
    // *      3. 用领接矩阵存储图，顶点个数为LinkedList的size
    // *      4. 遍历LinkedList，然后去十字链表中查找其上下左右结点，即其图中的邻接点
    // *          4.1 取出上下左右结点中不为null的point对象，去map中将point映射为顶点号
    // *
    // *  二、 找v1出发的闭合回路：
    // *      1. 假设存在一条回路v1-v2-v3-v4，那么：
    // *          切断v1-v2的边，如果存在一条v1->v2的路径，那么就存在回路
    // *      2. 因此，依次切断v1的一条邻边v1-x，若能找到v1到x的路径，那么就能构成回路
    // *          2.1 采用dijkstra算法找单源路：
    // *              2.1.1 解决了找闭合回路困难问题
    // *              2.1.2 该路径为最短路径，解决了闭合回路路径过长问题
    // *          2.2 该方法不用判断路径长度
    // * @param arr
    // */
    //public void genGraph(int arr[][]) {
    //    int rowLen = arr.length;
    //    int colLen = arr[0].length;
    //    LinkedList<Point> list = new LinkedList<>();
    //    Map<Point, Integer> listMap = new HashMap<>();
    //    int cnt = 0;
    //    //
    //    for (int i = 0; i < rowLen; i++){
    //        for(int j = 0;j < colLen;j++){
    //            if(arr[i][j] != 0){
    //                Point point = new Point(i,j);
    //                list.addLast(point);
    //                listMap.put(point,cnt++);
    //            }
    //        }
    //    }
    //    int[][] graph = new int[list.size()][list.size()];
    //    OrthList orthList = new OrthList(arr);
    //    for(int i = 0;i < list.size();i++){
    //        Point point = list.get(i);
    //        ListNode node = orthList.map[point.x][point.y];
    //        if (node.up != null && node.up.point != null) {
    //            int index = listMap.get(node.up.point);
    //            graph[index][i] = graph[i][index] = 1;
    //        }
    //        if (node.down != null && node.down.point != null) {
    //            int index = listMap.get(node.down.point);
    //            graph[index][i] = graph[i][index] = 1;
    //        }
    //        if (node.right != null && node.right.point != null) {
    //            int index = listMap.get(node.right.point);
    //            graph[index][i] = graph[i][index] = 1;
    //        }
    //        if (node.left != null && node.left.point != null) {
    //            int index = listMap.get(node.left.point);
    //            graph[index][i] = graph[i][index] = 1;
    //        }
    //    }
    //
    //    //System.out.println("==========邻接矩阵==========");
    //    //for(int i = 0;i < list.size();i++){
    //    //    for(int j = 0;j < list.size();j++){
    //    //        System.out.print(graph[i][j]+" ");
    //    //    }
    //    //    System.out.println();
    //    //}
    //
    //    // 计算回路
    //}
    //
    //public void dijkstra(int[][] graph,int n,int s){
    //    int visited[] = new int[n];
    //    int dist[] = new int[n];
    //    int parent[] = new int[n];
    //    visited[s] = 1;
    //    Arrays.fill(dist,Integer.MAX_VALUE);
    //    Arrays.fill(parent,-1);
    //    for (int i = 0; i < n; i++) {
    //        if(graph[s][i] == 1){
    //            dist[i] = 1;
    //            parent[i] = s;
    //        }
    //    }
    //    for (int i = 0; i < n; i++) {
    //        int v = -1;
    //        int min = Integer.MAX_VALUE;
    //        for (int j = 0; j < n; j++) {
    //            if(visited[j] == 0 && dist[i] < min){
    //                min = dist[i];
    //                v = j;
    //            }
    //        }
    //        if(v == -1) break;
    //        visited[v] = 1;
    //        for(int j = 0;j < n;j++){
    //            if(visited[j] == 0 && graph[v][j] != 0){
    //                if(dist[j] > dist[v] + graph[v][j]){
    //                    dist[j] = dist[v] + graph[v][j];
    //                    parent[j] = v;
    //                }
    //            }
    //        }
    //    }
    //}

    // 计算运费
    public int getCost(int cost[][], int arr[][]) {
        int cnt = 0;
        int lenX = cost.length;
        int lenY = cost[0].length;
        for (int i = 0; i < lenX; i++) {
            for (int j = 0; j < lenY; j++) {
                cnt += (cost[i][j] * arr[i][j]);
            }
        }
        return cnt;
    }

    // 递归求闭合回路1
    public LinkedList getclosedLoop(int arr[][], int S, int T) {
        LinkedList path = null;
        int visisted[][] = new int[arr.length][arr[0].length];
        // 在当前点的左右方向出发找闭合回路
        path = findClosedLoop(arr, 0, S, T, S, T, visisted, true);
        // 如果左右方向出发找不到，则上下方向出发寻找闭合回路
        if(path == null)
            path = findClosedLoop(arr, 1, S, T, S, T, visisted,true);

        /*==========输出闭合回路路径/*==========*/
        path.forEach(item -> {
            System.out.println(item);
        });
        return path;
    }
    /**
     * 递归寻找点（x,y）出发，能到达（S，T）的路径
     *  其中，isFirst变量用于排除自回路
     * @param arr
     * @param direction：方向，为0则当前在左右方向找，为1则在上下方向找
     * @param x：当前所在点x轴坐标
     * @param y：当前所在点y轴坐标
     * @param S:目标点x轴坐标
     * @param T:目标所在点x轴坐标
     * @param visited：标记点是否访问过
     * @param isFirst：标记是不是目标点第一次调用该递归函数
     * @return
     */
    public LinkedList<Point> findClosedLoop(int arr[][], int direction, int x, int y,int S,int T, int visited[][],boolean isFirst) {
        if(x == S  && y == T && !isFirst) return new LinkedList<>();
        if (direction == 0) {
            // 左右寻找
            for (int j = 0; j < arr[0].length; j++) {
                // 如果该点未访问过 or 该点为目标点且非第一次调用，则递归寻找
                if((arr[x][j] != 0 && visited[x][j] == 0) || (x == S && j == T && !isFirst)){
                    visited[x][j] = 1;
                    LinkedList ret = findClosedLoop(arr, 1, x, j, S, T, visited, false);
                    // 若找到目标点，则该点加入路径并返回；否则撤销该点作为路径
                    if (ret != null) {
                        ret.add(new Point(x, y));
                        return ret;
                    }
                    else visited[x][j] = 0;
                }
            }
        } else {
            // 上下寻找
            for (int i = 0; i < arr.length; i++) {
                if(arr[i][y] != 0 && visited[i][y] == 0 || (i == S && y == T  && !isFirst)){
                    visited[i][y] = 1;
                    LinkedList ret = findClosedLoop(arr, 0, i, y, S, T, visited,false);
                    if (ret != null) {
                        ret.add(new Point(x, y));
                        return ret;
                    }
                    visited[i][y] = 0;
                }
            }
        }

        return null;
    }

    public int[] getA() {return A;}
    public void setA(int[] a) {A = a;}
    public int[] getB() {return B;}
    public void setB(int[] b) {B = b;}
    public int[][] getCost() {return Cost;}
    public void setCost(int[][] cost) {Cost = cost;}
    public int getNumA() {return numA;}
    public void setNumA(int numA) {this.numA = numA;}
    public int getNumB() {return numB;}
    public void setNumB(int numB) {this.numB = numB;}
    public int[][] getTab() {return tab;}
    public void setTab(int[][] tab) {this.tab = tab;}
}
