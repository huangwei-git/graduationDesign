package com.songlian.logistics.calculate.Transport;

import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

@Component
public class TransportionSolution {
    // 工厂供应量
    private int supplier[];
    private int numOfSupplier;
    // 客户需求量
    private int demander[];
    private int numOfDemander;
    // 运价表
    private int costTab[][];
    // 方案表
    private int planTab[][];
    // 基变量
    private int target[][];
    // 行位势
    private int rowGeopotential[];
    // 列位势
    private int colGeopotential[];
    // 模式
    private boolean useVogel = false;
    // 是否产销不平衡
    private boolean unBalance = false;
    // 是否打印过程
    private boolean openProcessPrint = false;
    // 方案总费用
    private int total = 0;
    // 迭代次数
    private int cnt=1;

    // 记录每次的方案
    private List<int[][]> recordPlan = new LinkedList<>();
    private List<Integer> recordTotal = new LinkedList<>();
    private List<int[][]> recordCheckField = new LinkedList<>();
    private List<List<Point>> recordLoop = new LinkedList<>();
    private List<Integer> recordAdjust = new LinkedList<>();

    public TransportionSolution(){}
    public TransportionSolution(int[] a, int[] b, int[][] cost) {
        supplier = a;
        this.numOfSupplier = a.length;
        demander = b;
        this.numOfDemander = b.length;
        costTab = cost;
    }
    public TransportionSolution(int[] a, int[] b, int[][] cost, boolean useVogel) {
        supplier = a;
        this.numOfSupplier = a.length;
        demander = b;
        this.numOfDemander = b.length;
        costTab = cost;
        this.useVogel = useVogel;
    }

    public void solve(){
        // 0. 产销平衡处理
        supplyAndDemandBalance();
        // 1. 获得初始方案
        if(useVogel) initialPlanByVogel();
        else initialPlanByMinElement();
        // 2. 位势法 + 闭合回路调整
        Point point = null;
        while((point = getBaseVariable()) != null){
            cnt++;
            // 2.1 获得入基变量
            // 2.2 通过入基变量求闭合回路
            LinkedList<Point> path = getClosedLoop(point.x, point.y);
            adjustClosedLoop(path,point);
            if(this.openProcessPrint) System.out.println("-----------------------");
        }

        this.total = calcTotal();
        /*==========输出最终方案/*==========*/
        if(this.openProcessPrint){
            System.out.println("==========最终方案为==========");
            printArray(planTab);
            System.out.println("迭代次数："+cnt);
            System.out.println("需要的价格为：" + this.total);
        }
    }

    // 0. 产销平衡化
    private void supplyAndDemandBalance(){
        int supply = 0,demand = 0;
        for(int i = 0;i < numOfSupplier;i++) supply += supplier[i];
        for(int i = 0;i < numOfDemander;i++) demand += demander[i];
        int diff = supply - demand;
        if(diff > 0){
            unBalance = true;
            int tmpDemaner[] = new int[numOfDemander + 1];
            for(int i = 0;i < numOfDemander;i++) tmpDemaner[i] = demander[i];
            tmpDemaner[numOfDemander] = diff;
            numOfDemander++;
            demander = tmpDemaner;

            int tmpCostTab[][] = new int[numOfSupplier][numOfDemander];
            for (int i = 0; i < numOfSupplier; i++) {
                int j;
                for (j = 0; j < numOfDemander - 1; j++) {
                    tmpCostTab[i][j] = costTab[i][j];
                }
                tmpCostTab[i][j] = 0;
            }
            costTab = tmpCostTab;
        }

        planTab = new int[numOfSupplier][numOfDemander];
        target = new int[numOfSupplier][numOfDemander];
        rowGeopotential = new int[numOfSupplier];
        colGeopotential = new int[numOfDemander];
    }

    // 1. 伏格尔法-贪心：计算所有行和列中，最小值和次小值的差值，从中取出最大的一个，作为入基变量
    private Point getGap(int invalidRows[], int invalidColumns[]){
        int max = Integer.MIN_VALUE;
        Point p = new Point(-1,-1);
        Point tmpP = new Point(-1,-1);

        // 获取行的差额
        for (int i = 0; i < numOfSupplier; i++) {
            // 如果该行被划掉，则不计算
            if (invalidRows[i] == 1) continue;
            int min1 = Integer.MAX_VALUE;
            int min2 = Integer.MAX_VALUE;
            tmpP.x = i;
            for (int j = 0; j < numOfDemander; j++) {
                // 如果第j列没被划掉
                if (invalidColumns[j] == 0) {
                    if (costTab[i][j] < min2){
                        if(costTab[i][j] <= min1){
                            min2 = min1;
                            min1 = costTab[i][j];
                            tmpP.y = j;
                        }else min2 = costTab[i][j];
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
        for (int j = 0; j < numOfDemander; j++) {
            // 如果该列被划掉，则不计算
            if (invalidColumns[j] == 1) continue;
            int min1 = Integer.MAX_VALUE;
            int min2 = Integer.MAX_VALUE;
            tmpP.y = j;
            for (int i = 0; i < numOfSupplier; i++) {
                // 如果第j行没被划掉
                if (invalidRows[i] == 0) {
                    if (costTab[i][j] < min2){
                        if(costTab[i][j] <= min1){
                            min2 = min1;
                            min1 = costTab[i][j];
                            tmpP.x = i;
                        }else min2 = costTab[i][j];
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

    // 2. 获得初始解决方案
    //      2.1 方法一： Vogel伏格尔法
    private void initialPlanByVogel(){
        int temp_A[] = supplier.clone();
        int temp_B[] = demander.clone();
        int invalidRows[] = new int[numOfSupplier];
        int invalidColumns[] = new int[numOfDemander];
        // 记录没被划掉的行
        int cnt_row = numOfSupplier;
        // 记录没被划掉的列
        int cnt_col = numOfDemander;
        Point point = null;
        if(unBalance) for(int i = 0;i < numOfSupplier;i++) costTab[i][numOfDemander - 1] = Integer.MAX_VALUE - 1;
        while(cnt_row != 0 && cnt_col != 0){
            if(cnt_row == 1 && cnt_col == 1){
                for(int i = 0; i < numOfSupplier; i++) if(invalidRows[i] == 0) point.x = i;
                for(int j = 0; j < numOfDemander; j++) if(invalidColumns[j] == 0) point.y = j;
            }else point = getGap(invalidRows,invalidColumns);

            int cnt = 0;
            int min = temp_A[point.x] < temp_B[point.y]?temp_A[point.x]:temp_B[point.y];
            planTab[point.x][point.y] = min;
            target[point.x][point.y] = 1;
            if((temp_A[point.x] -= min) == 0) {invalidRows[point.x] = 1;cnt_row--;cnt++;};
            if((temp_B[point.y] -= min) == 0) {invalidColumns[point.y] = 1;cnt_col--;cnt++;};
            // 退化
            if(cnt == 2){
                System.out.println("出现退化！");
                Point tmpPoint = new Point(-1, -1);
                int tmp = Integer.MAX_VALUE;
                for(int i = 0;i < numOfDemander;i++){
                    if(invalidColumns[i] == 1 || i == point.y) continue;
                    if(costTab[point.x][i] <= tmp){
                        tmp = costTab[point.x][i];
                        tmpPoint.x = point.x;
                        tmpPoint.y = i;
                    }
                }
                for(int i = 0;i < numOfSupplier;i++){
                    if(invalidRows[i] == 1 || i == point.x) continue;
                    if(costTab[i][point.y] <= tmp){
                        tmp = costTab[i][point.y];
                        tmpPoint.x = i;
                        tmpPoint.y = point.y;
                    }
                }
                if(tmpPoint.x != -1){
                    planTab[tmpPoint.x][tmpPoint.y] = 0;
                    target[tmpPoint.x][tmpPoint.y] = 1;
                }
            }
        }
        if(unBalance) for(int i = 0;i < numOfSupplier;i++) costTab[i][numOfDemander - 1] = 0;

        if(this.openProcessPrint){
            System.out.println("==========初始解决方案==========");
            printArray(planTab);
        }
        recordPlan.add(this.clonePlanTable());
        recordTotal.add(this.calcTotal());
    }

    //      2.2 方法二： 最小元素法
    private void initialPlanByMinElement(){
        int temp_A[] = supplier.clone();
        int temp_B[] = demander.clone();
        int invalidRows[] = new int[numOfSupplier];
        int invalidColumns[] = new int[numOfDemander];
        // 记录没被划掉的行
        int cnt_row = numOfSupplier;
        // 记录没被划掉的列
        int cnt_col = numOfDemander;
        Point point = new Point(-1, -1);

        if(unBalance) for(int i = 0;i < numOfSupplier;i++) costTab[i][numOfDemander - 1] = Integer.MAX_VALUE - 1;
        while(cnt_row != 0 && cnt_col != 0){
            if(cnt_row == 1 && cnt_col == 1){
                for(int i = 0; i < numOfSupplier; i++) if(invalidRows[i] == 0) point.x = i;
                for(int j = 0; j < numOfDemander; j++) if(invalidColumns[j] == 0) point.y = j;
            }else {
                int min = Integer.MAX_VALUE;
                for (int i = 0; i < numOfSupplier; i++) {
                    if(invalidRows[i] == 1) continue;
                    for (int j = 0; j < numOfDemander; j++) {
                        if (invalidColumns[j] == 1) continue;
                        if(costTab[i][j] < min){
                            min = costTab[i][j];
                            point.x = i;
                            point.y = j;
                        }
                    }
                }
            };

            int cnt = 0;
            // 找到单元格对应的行、列中叫小者
            int min = temp_A[point.x] < temp_B[point.y] ? temp_A[point.x] : temp_B[point.y];
            // 确定供需关系
            planTab[point.x][point.y] = min;
            // 标记为基变量
            target[point.x][point.y] = 1;
            if((temp_A[point.x] -= min) == 0) {invalidRows[point.x] = 1;cnt_row--;cnt++;};
            if((temp_B[point.y] -= min) == 0) {invalidColumns[point.y] = 1;cnt_col--;cnt++;};

            // 退化
            if(cnt == 2){
                // 找补"0"的单元格
                Point tmpPoint = new Point(-1, -1);
                int tmp = Integer.MAX_VALUE;
                for(int i = 0;i < numOfDemander;i++){
                    if(invalidColumns[i] == 1 || i == point.y) continue;
                    if(costTab[point.x][i] <= tmp){
                        tmp = costTab[point.x][i];
                        tmpPoint.x = point.x;
                        tmpPoint.y = i;
                    }
                }
                for(int i = 0;i < numOfSupplier;i++){
                    if(invalidRows[i] == 1 || i == point.x) continue;
                    if(costTab[i][point.y] <= tmp){
                        tmp = costTab[i][point.y];
                        tmpPoint.x = i;
                        tmpPoint.y = point.y;
                    }
                }
                if(tmpPoint.x != -1){
                    planTab[tmpPoint.x][tmpPoint.y] = 0;
                    target[tmpPoint.x][tmpPoint.y] = 1;
                }
            }
        }
        if(unBalance) for(int i = 0;i < numOfSupplier;i++) costTab[i][numOfDemander - 1] = 0;

        if(this.openProcessPrint){
            System.out.println("==========初始解决方案==========");
            printArray(planTab);
        }
        
        this.recordPlan.add(this.clonePlanTable());
        this.recordTotal.add(this.calcTotal());
    }

    // 3. 计算位势
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
        int visitedRow[] = new int[numOfSupplier];
        int visitedCol[] = new int[numOfDemander];
        rowStack.push(0);
        visitedRow[0] = 1;
        stack = rowStack;

        /* 迭代 */
        while (!stack.isEmpty()){
            while(!stack.isEmpty()){
                int op = stack.pop();
                // flag=0时，遍历op行的每一列，计算列位势
                if(flag == 0){
                    for(int i = 0; i < numOfDemander; i++){
                        // 如果已经计算过位势 或 对应位置的元素为非基变量 ，则跳过
                        if(visitedCol[i] == 1 || target[op][i] == 0) continue;
                        // 列位势 = 运费 - 行位势
                        colGeopotential[i] = costTab[op][i] - rowGeopotential[op];
                        visitedCol[i] = 1;
                        colStack.push(i);
                    }
                }
                // flag=1时，遍历op行的每一行，计算行位势
                else{
                    for(int i = 0; i < numOfSupplier; i++){
                        // 如果已经计算过位势 或 对应位置的元素为非基变量 ，则跳过
                        if(visitedRow[i] == 1 || target[i][op] == 0) continue;
                        // 行位势 = 运费 - 列位势
                        rowGeopotential[i] = costTab[i][op] - colGeopotential[op];
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
        if(this.openProcessPrint){
            System.out.print("列位势：");
            for (int i = 0; i < numOfSupplier; i++) System.out.print(rowGeopotential[i] + " ");
            System.out.print("行位势：");
            for(int i = 0; i < numOfDemander; i++) System.out.print(colGeopotential[i] + " ");
            System.out.println();
        }
    }

    // 4. 计算检验数：获得入基变量的point对象
    private Point getBaseVariable(){
        getGeopotential();

        int check[][] = new int[numOfSupplier][numOfDemander];

        Point point = new Point(-1, -1);
        int minThet = 0;
        for (int i = 0; i < numOfSupplier; i++) {
            for (int j = 0; j < numOfDemander; j++) {
                int thet = costTab[i][j] - (rowGeopotential[i] + colGeopotential[j]);
                check[i][j] = thet;
                if(thet < minThet && costTab[i][j] != 0){
                    minThet = thet;
                    point.x = i;
                    point.y = j;
                }
            }
        }

        /*==========输出检验数==========*/
        if (this.openProcessPrint){
            System.out.println("检验数:");
            printArray(check);
        }
        this.recordCheckField.add(check);

        if(minThet == 0) return null;
        else return point;
    }

    /* 求矩形闭合回路 */
    //private Point getClosedLoopByRctangle(int arr[][],int x,int y){
    //    Point point = new Point(-1,-1);
    //    for (int i = 0; i < arr.length; i++) {
    //        for (int j = 0; j < arr[0].length; j++){
    //            if(i == x || j == y || arr[i][j] == 0) continue;
    //            if(arr[i][y] != 0 && arr[x][j] != 0){
    //                point.x = i;
    //                point.y = j;
    //                return point;
    //            }
    //        }
    //    }
    //    return point;
    //}

    // 5. 递归求闭合回路1
    private LinkedList getClosedLoop(int S, int T) {
        LinkedList path = null;
        int visisted[][] = new int[target.length][target[0].length];
        // 在当前点的左右方向出发找闭合回路
        path = findClosedLoop(0, S, T, S, T, visisted, true);
        // 如果左右方向出发找不到，则上下方向出发寻找闭合回路
        if(path == null)
            path = findClosedLoop(1, S, T, S, T, visisted,true);

        /*==========输出闭合回路路径/*==========*/
        if (this.openProcessPrint){
            System.out.println("所得闭合回路为：");
            printLoop(path);
        }
        this.recordLoop.add(path);

        return path;
    }
    /**
     * 递归寻找点（x,y）出发，能到达（S，T）的路径
     *  其中，isFirst变量用于排除自回路
     * @param target
     * @param direction：方向，为0则当前在左右方向找，为1则在上下方向找
     * @param x：当前所在点x轴坐标
     * @param y：当前所在点y轴坐标
     * @param S:目标点x轴坐标
     * @param T:目标所在点x轴坐标
     * @param visited：标记点是否访问过
     * @param isFirst：标记是不是目标点第一次调用该递归函数
     * @return
     */
    private LinkedList<Point> findClosedLoop(int direction, int x, int y,int S,int T, int visited[][],boolean isFirst) {
        if(x == S  && y == T && !isFirst) return new LinkedList<>();
        if (direction == 0) {
            // 左右寻找
            for (int j = 0; j < target[0].length; j++) {
                // 如果该点未访问过 or 该点为目标点且非第一次调用，则递归寻找
                if((target[x][j] != 0 && visited[x][j] == 0) || (x == S && j == T && !isFirst)){
                    visited[x][j] = 1;
                    LinkedList ret = findClosedLoop(1, x, j, S, T, visited, false);
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
            for (int i = 0; i < target.length; i++) {
                if(target[i][y] != 0 && visited[i][y] == 0 || (i == S && y == T  && !isFirst)){
                    visited[i][y] = 1;
                    LinkedList ret = findClosedLoop(0, i, y, S, T, visited,false);
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

    // 6. 调整闭合回路
    private void adjustClosedLoop(LinkedList<Point> path,Point point){
        int delta = Integer.MAX_VALUE;
        // 1. 找闭合回路的变化值
        int index = path.indexOf(point);
        if(index % 2 == 0) index = 1;
        else index = 0;
        Point tmpPoint = new Point(-1,-1);
        for(int i = index;i < path.size();i+=2){
            Point p = path.get(i);
            int val = planTab[p.x][p.y];
            if(val < delta){
                tmpPoint = p;
                delta = val;
            }
        }
        target[tmpPoint.x][tmpPoint.y] = 0;
        target[point.x][point.y] = 1;
        // 计算原总价
        int oldTotal = calcTotal();
        // 2. 修改回路上的分配方案
        int direction = 1;
        if(index == 0) direction = -1;
        for(Point p : path){
            planTab[p.x][p.y] += direction * delta;
            direction *= -1;
        }
        // 计算修改后的总价
        int newTotal = calcTotal();
        // 输出调整后的方案：
        if(this.openProcessPrint){
            System.out.println("调整量：" + delta+"\n调整后的方案");
            printArray(planTab);
            System.out.println("调整前运费：" + oldTotal);
            System.out.println("调整后运费：" + newTotal);
            System.out.println("节省运费：" + (oldTotal - newTotal));
        }
        if(delta > 0){
            this.recordPlan.add(this.clonePlanTable());
            this.recordTotal.add(newTotal);
        }
    }

    // 7. 计算总运费
    private int calcTotal() {
        int sum = 0;
        for (int i = 0; i < numOfSupplier; i++) {
            for (int j = 0; j < numOfDemander; j++) {
                sum += (costTab[i][j] * planTab[i][j]);
            }
        }
        return sum;
    }

    // 打印回路
    private void printLoop(LinkedList<Point> path){
        int loop[][] = new int[numOfSupplier][numOfDemander];
        for(int i = 0; i < numOfSupplier; i++){
            for(int j = 0; j < numOfDemander; j++){
                if (path.contains(new Point(i, j))) {
                    System.out.print("* ");
                } else {
                    System.out.print("- ");
                }
            }
            System.out.println();
        }
    }

    // 打印二维数组
    private void printArray(int arr[][]){
        for(int i = 0; i < numOfSupplier; i++){
            for(int j = 0; j < numOfDemander; j++){
                if(target[i][j] == 1)
                    System.out.print("" + arr[i][j] + "_\t");
                else
                    System.out.print("" + arr[i][j] + "\t");
            }
            System.out.println();
        }
    }


    /* getter and setter */
    public int[] getSupplier() {
        return supplier;
    }
    public void setSupplier(int[] supplier) {
        this.supplier = supplier;numOfSupplier = supplier.length;
    }
    public int[] getDemander() {
        return demander;
    }
    public void setDemander(int[] demander) {
        this.demander = demander;numOfDemander = demander.length;
    }
    public int[][] getCostTab() {
        return costTab;
    }

    public int getTotal() {
        return total;
    }

    public int[][] getPlanTab() {
        return planTab;
    }

    public void setCostTab(int[][] costTab) {
        this.costTab = costTab;
    }

    public int[][] clonePlanTable() {
        int clone[][] = new int[this.numOfSupplier][this.numOfDemander];
        for (int i = 0; i < this.numOfSupplier; i++) {
            for (int j = 0; j < this.numOfDemander; j++) {
                clone[i][j] = this.planTab[i][j];
            }
        }
        return clone;
    }

    public boolean isOpenProcessPrint() {
        return openProcessPrint;
    }

    public void setOpenProcessPrint(boolean openProcessPrint) {
        this.openProcessPrint = openProcessPrint;
    }

    public List<int[][]> getRecordPlan() {
        return recordPlan;
    }

    public void setRecordPlan(List<int[][]> recordPlan) {
        this.recordPlan = recordPlan;
    }

    public List<Integer> getRecordTotal() {
        return recordTotal;
    }

    public void setRecordTotal(List<Integer> recordTotal) {
        this.recordTotal = recordTotal;
    }

    public List<int[][]> getRecordCheckField() {
        return recordCheckField;
    }

    public void setRecordCheckField(List<int[][]> recordCheckField) {
        this.recordCheckField = recordCheckField;
    }

    public List<List<Point>> getRecordLoop() {
        return recordLoop;
    }

    public void setRecordLoop(List<List<Point>> recordLoop) {
        this.recordLoop = recordLoop;
    }

    public List<Integer> getRecordAdjust() {
        return recordAdjust;
    }

    public void setRecordAdjust(List<Integer> recordAdjust) {
        this.recordAdjust = recordAdjust;
    }

}
