package com.songlian.TransportionProblem;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

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

    public Solution(){}
    public Solution(int[] a, int[] b, int[][] cost) {
        A = a;
        this.numA = a.length;
        B = b;
        this.numB = b.length;
        Cost = cost;
        tab = new int[numA][numB];
    }

    public int[] testGetGap(int arr[][], int rows[], int cols[]){
        int max = Integer.MIN_VALUE;
        int x,y,xx,yy;
        x = y = xx = yy = -1;

        // 获取行的差额
        for (int i = 0; i < numA; i++) {
            // 如果该行被划掉，则不计算
            if (rows[i] == 1) continue;
            int min1 = Integer.MAX_VALUE;
            int min2 = Integer.MAX_VALUE;
            xx = i;
            for (int j = 0; j < numB; j++) {
                // 如果第j列没被划掉
                if (cols[j] == 0) {
                    if (arr[i][j] < min2){
                        if(arr[i][j] <= min1){
                            min2 = min1;
                            min1 = arr[i][j];
                            yy = j;
                        }else min2 = arr[i][j];
                    }
                }
            }
            if(min2 != Integer.MAX_VALUE){
                int gap = min2 - min1;
                if(gap > max){
                    max = gap;
                    x = xx;
                    y = yy;
                }
            }
        }

        // 获取列的差额
        for (int j = 0; j < numB; j++) {
            // 如果该列被划掉，则不计算
            if (cols[j] == 1) continue;
            int min1 = Integer.MAX_VALUE;
            int min2 = Integer.MAX_VALUE;
            yy = j;
            for (int i = 0; i < numA; i++) {
                // 如果第j行没被划掉
                if (rows[i] == 0) {
                    if (arr[i][j] < min2){
                        if(arr[i][j] <= min1){
                            min2 = min1;
                            min1 = arr[i][j];
                            xx = i;
                        }else min2 = arr[i][j];
                    }
                }
            }
            if(min2 != Integer.MAX_VALUE){
                int gap = min2 - min1;
                if(gap > max){
                    max = gap;
                    x = xx;
                    y = yy;
                }
            }
        }

        System.out.println(arr[x][y]);
        return new int[]{xx,yy};
    }

    public Point getGap(int rows[], int cols[]){
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

    public void init(){
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
        for(int i = 0;i < numA;i++){
            for (int j = 0;j < numB;j++){
                System.out.print(tab[i][j]+"\t");
            }
            System.out.println();
        }
    }

    public void findCycle(int x,int y,int arr[][]){
        LinkedList list = dfs(x, y, x, y, arr, new int[arr.length][arr[0].length], 1);
        System.out.println(list);
    }

    // 计算位势
    public void getGeopotential() {
        /* 准备工作 */
        // flag为0时，使用rowStack
        // flag为1时，使用colStack
        int flag = 0;
        Stack<Integer> rowStack = new Stack<Integer>();
        Stack<Integer> colStack = new Stack<Integer>();
        Stack<Integer> stack = null;
        int rowPotential[] = new int[numA];
        int colPotential[] = new int[numB];
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
                        colPotential[i] = Cost[op][i] - rowPotential[op];
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
                        rowPotential[i] = Cost[i][op] - colPotential[op];
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
        for (int i = 0; i < numA; i++) System.out.print(rowPotential[i] + " ");
        System.out.println();
        for(int i = 0;i < numB;i++) System.out.print(colPotential[i] + " ");
        System.out.println();
    }

    /**
     *  start_x/y 为起始点
     *  x,y 为迭代点
     *  deep记录深度
     *
     *  寻找闭合回路时，不同于常规的领接矩阵，这里相邻的两个数组才代表有边：
     *      如：
     *          0 0 5 2
     *          3 0 1 1
     *          0 6 0 3
     *      对于(1,0)的3来说，右边坐标为(1,2)的点有边 ， 而(1,3)无边
     *      补充： 此处用了很多判断条件处理上述逻辑，若此算法后续有bug，可以尝试构建符合上述逻辑的图去寻找闭合回路
     *
     *
     */
    public LinkedList dfs(int start_x, int start_y, int x, int y, int arr[][], int visited[][], int deep){
        visited[x][y] = 1;
        // 遍历行
        for(int i = 0;i < arr[0].length;i++){
            // 遇到起始点，判断当前深度
            if(x == start_x && i == start_y){
                // deep > 3时形成回路
                // deep = 1时为起始点，
                // 1 < deep < 3则为两个点的回路，不符合要求
                if(deep > 3){
                    System.out.println(x+"==="+y);
                    LinkedList res = new LinkedList();
                    res.addFirst(new Point(x,y));
                    return res;
                }
                else if (deep != 1) break;
            }
            if (arr[x][i] != 0){
                if(visited[x][i] == 0){
                    LinkedList res = dfs(start_x, start_y, x, i, arr, visited, deep + 1);
                    if(res != null){
                        System.out.println(x+"==="+y);
                        res.addFirst(new Point(x,y));
                        return res;
                    }
                }else break;
            }
        }

        // 遍历列
        for(int i = 0;i < arr.length;i++){
            if(i == start_x && y == start_y){
                if(deep > 3){
                    System.out.println(x+"==="+y);
                    LinkedList res = new LinkedList();
                    res.addFirst(new Point(x,y));
                    return res;
                }else if (deep != 1) break;
            }
            if(arr[i][y] != 0){
                if(visited[i][y] == 0){
                    LinkedList res = dfs(start_x, start_y, i, y, arr, visited, deep + 1);
                    if(res != null){
                        System.out.println(x+"==="+y);
                        res.addFirst(new Point(x,y));
                        return res;
                    }
                }else break;
            }
        }

        return null;
    }

    /**
     * 一、 将数组转换为图
     *      1. 先将有数据的坐标打包成point对象（即构建图时的顶点），存入Linkedlist中，同时用map映射point与索引
     *          1.1 point在LinkedList中的顺序作为后面构建图时的顶点号
     *          1.2 通过LinkedList可以快速找到某个顶点号对应的point
     *          1.3 通过map可以快速找到某个point对应的顶点号
     *      2. 将数组构建成十字链表，方便查找相邻元素
     *          2.1 十字链表采用双链表形式
     *          2.2 十字链表对象中包括一个链表结点类型的二维数组，用于映射坐标对应十字链表中的结点，用于快速通过point找到其在十字链表中的位置
     *      3. 用领接矩阵存储图，顶点个数为LinkedList的size
     *      4. 遍历LinkedList，然后去十字链表中查找其上下左右结点，即其图中的邻接点
     *          4.1 取出上下左右结点中不为null的point对象，去map中将point映射为顶点号
     *
     *  二、 找v1出发的闭合回路：
     *      1. 假设存在一条回路v1-v2-v3-v4，那么：
     *          切断v1-v2的边，如果存在一条v1->v2的路径，那么就存在回路
     *      2. 因此，依次切断v1的一条邻边v1-x，若能找到v1到x的路径，那么就能构成回路
     *          2.1 采用dijkstra算法找单源路：
     *              2.1.1 解决了找闭合回路困难问题
     *              2.1.2 该路径为最短路径，解决了闭合回路路径过长问题
     *          2.2 该方法不用判断路径长度
     * @param arr
     */
    public void genGraph(int arr[][]) {
        int rowLen = arr.length;
        int colLen = arr[0].length;
        LinkedList<Point> list = new LinkedList<>();
        Map<Point, Integer> listMap = new HashMap<>();
        int cnt = 0;
        //
        for (int i = 0; i < rowLen; i++){
            for(int j = 0;j < colLen;j++){
                if(arr[i][j] != 0){
                    Point point = new Point(i,j);
                    list.addLast(point);
                    listMap.put(point,cnt++);
                }
            }
        }
        int[][] graph = new int[list.size()][list.size()];
        OrthList orthList = new OrthList(arr);
        for(int i = 0;i < list.size();i++){
            Point point = list.get(i);
            ListNode node = orthList.map[point.x][point.y];
            if (node.up != null && node.up.point != null) {
                int index = listMap.get(node.up.point);
                graph[index][i] = graph[i][index] = 1;
            }
            if (node.down != null && node.down.point != null) {
                int index = listMap.get(node.down.point);
                graph[index][i] = graph[i][index] = 1;
            }
            if (node.right != null && node.right.point != null) {
                int index = listMap.get(node.right.point);
                graph[index][i] = graph[i][index] = 1;
            }
            if (node.left != null && node.left.point != null) {
                int index = listMap.get(node.left.point);
                graph[index][i] = graph[i][index] = 1;
            }
        }
        for(int i = 0;i < list.size();i++){
            for(int j = 0;j < list.size();j++){
                System.out.print(graph[i][j]+" ");
            }
            System.out.println();
        }
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
