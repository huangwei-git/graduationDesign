package com.songlian.logistics.calculate.TabuSearch_TSP;

import java.util.*;

/*  禁忌搜索算法
**  Create by: WSKH
    Date:2021-07-26
    Time:20:27
*/

public class TS_TSP {
    // 最大的迭代次数(提高这个值可以稳定地提高解质量，但是会增加求解时间)
    public final int MAX_GEN = 100000;
    // 每次搜索领域的个数(这个值不要太大，太大的话搜索效率会降低)
    public final int N = 100;
    // 禁忌长度
    public final int len = 20;
    // 城市数量，手动设置
    public int cityNum;
    // 禁忌表
    public int[][] tabooList;
    // 初始路径编码
    public int[] Ghh;
    // 最好的路径编码
    public int[] bestGh;
    // 当前最好路径编码
    public int[] LocalGh;
    // 存放临时编码
    public int[] tempGh;
    // 距离矩阵
    public double[][] dist;
    // 最佳的迭代次数*
    public int bestT;
    // 最优解
    public double bestEvaluation;
    // 每次领域搜索的最优解（领域最优解）
    public double LocalEvaluation;
    // 临时解
    public double tempEvaluation;
    // 每个城市的坐标
    public List<double[]> pointList;
    // 当前迭代
    public int t;
    // 随机函数对象
    public Random random;
    // 当前禁忌表长度
    public int l = 0;

    // 构造函数
    public TS_TSP(List<double[]> pointList) {
        this.pointList = pointList;
    }

    // 外部调用接口
    public void solve() throws Exception {
        initVar();
        tabuSearch();
    }

    // 禁忌搜索主函数
    public void tabuSearch() {
        // 开始迭代，停止条件为达到指定迭代次数
        while (t <= MAX_GEN) {
            // 当前领域搜索次数
            int n = 0;
            LocalEvaluation = Double.MAX_VALUE;
            while (n <= N) {
                // 得到当前编码Ghh的邻居编码tempGh
                tempGh = generateNewGh(Ghh.clone(), tempGh.clone());
                // 判断其是否在禁忌表中
                if (!judge(tempGh)) {
                    // 如果不在
                    tempEvaluation = evaluate(tempGh);
                    if (tempEvaluation < LocalEvaluation) {
                        // 如果临时解优于本次领域搜索的最优解
                        // 那么就将临时解替换本次领域搜索的最优解
                        LocalGh = tempGh.clone();
                        LocalEvaluation = tempEvaluation;
                    }
                    n++;
                }
            }
            if (LocalEvaluation < bestEvaluation) {
                // 如果本次搜索的最优解优于全局最优解
                // 那么领域最优解替换全局最优解
                bestT = t;
                bestGh = LocalGh.clone();
                bestEvaluation = evaluate(bestGh);
            }
            Ghh = LocalGh.clone();
            // 加入禁忌表
            enterTabooList(LocalGh.clone());
            t++;
        }
        // 输出结果
        System.out.println("最佳迭代次数:"+bestT);
        System.out.println("最短路程为："+bestEvaluation);
        int[] bestPath = new int[cityNum+1];
        System.arraycopy(bestGh, 0, bestPath, 0, bestGh.length);
        bestPath[cityNum] = bestPath[0];
        System.out.println("最佳路径为："+ Arrays.toString(bestPath));
    }

    // 加入禁忌队列
    public void enterTabooList(int[] tempGh) {
        if (l < len) {
            // 如果当前禁忌表还有空位，则直接加入即可
            tabooList[l] = tempGh.clone();
            l++;
        } else {
            // 如果禁忌表已经满了，则移除第一个进表的路径，添加新的路径到禁忌表末尾
            // 后面的禁忌编码全部向前移动一位，覆盖掉当前第一个禁忌编码
            for (int i = 0; i < tabooList.length - 1; i++) {
                tabooList[i] = tabooList[i + 1].clone();
            }
            // 将tempGh加入到禁忌队列的最后
            tabooList[tabooList.length - 1] = tempGh.clone();
        }
    }

    // 判断路径编码是否存在于禁忌表中
    public boolean judge(int[] tempGh) {
        int count = 0;
        for (int[] ints : tabooList) {
            for (int j = 0; j < ints.length; j++) {
                if (tempGh[j] != ints[j]) {
                    count++;
                    break;
                }
            }
        }
        return count != tabooList.length;
    }

    // 领域交换，生成新解(随机指定数组中的两个数，不包括首尾，然后让这两个数进行位置互换，达到生成一个新路线的作用)
    public int[] generateNewGh(int[] Gh, int[] tempGh) {
        int temp;
        //将Gh复制到tempGh
        tempGh = Gh.clone();

        int r1 = 0;
        int r2 = 0;
        //这段代码(r1==0||r2==0)是为了保证起点不受改变，如果有固定的起点的话，可以使用这几行代码
//        while (r1==r2||(r1==0||r2==0)){
//            r1 = random.nextInt(cityNum);
//            r2 = random.nextInt(cityNum);
//        }
        while (r1 == r2) {
            r1 = random.nextInt(cityNum);
            r2 = random.nextInt(cityNum);
        }
        // 交换
        temp = tempGh[r1];
        tempGh[r1] = tempGh[r2];
        tempGh[r2] = temp;
        return tempGh.clone();
    }

    // 随机生成一个初始解
    public int[] getInitGh() throws Exception {
        HashSet<Integer> indexList = new HashSet<>();
        for (int i = 0; i < Ghh.length; i++) {
            while (true) {
                int r = random.nextInt(cityNum);
                if (!indexList.contains(r)) {
                    // 只有当地点不重合时才添加进列表，保证初始解中没有重复的地点
                    indexList.add(r);
                    Ghh[i] = r;
                    break;
                }
            }
        }
        System.out.println("初始解：" + Arrays.toString(Ghh));
        return Ghh.clone();
    }

    // 计算两点之间的距离（使用伪欧氏距离，可以减少计算量）
    public double getDistance(double[] place1, double[] place2) {
        //伪欧氏距离在根号内除以了一个10
        return Math.sqrt((Math.pow(place1[0] - place2[0], 2) + Math.pow(place1[1] - place2[1], 2)) / 10.0);
        // 欧式距离
//        return Math.sqrt((Math.pow(place1[0]-place2[0],2)+Math.pow(place1[1]-place2[1],2)));
    }

    // 初始化变量
    public void initVar() throws Exception {
        cityNum = pointList.size();//城市数量为点的数量
        tabooList = new int[len][cityNum];//禁忌表
        Ghh = new int[cityNum];//初始路径编码
        bestGh = new int[cityNum];//最好的路径编码
        LocalGh = new int[cityNum];//当前最好路径编码
        tempGh = new int[cityNum];//存放临时编码
        dist = new double[cityNum][cityNum];//距离矩阵
        // 初始化距离矩阵
        for (int i = 0; i < dist.length; i++) {
            for (int j = i; j < dist[i].length; j++) {
                if (i == j) {
                    // 对角线为0
                    dist[i][j] = 0.0;
                } else {
                    // 计算i到j的距离和j到i的距离
                    dist[i][j] = getDistance(pointList.get(i), pointList.get(j));
                    dist[j][i] = dist[i][j];
                }
            }
        }
        // 初始化参数
        bestT = 0;
        t = 0;
        random = new Random();
        Ghh = getInitGh();
        // 复制当前路径编码给最优路径编码
        tempGh = Ghh.clone();
        bestGh = tempGh.clone();
        bestEvaluation = evaluate(bestGh);
        tempEvaluation = evaluate(tempGh);
        LocalEvaluation = Double.MAX_VALUE;
    }

    // 评价函数(根据序列获取路径长度)
    public double evaluate(int[] path) {
        double pathLen = 0.0;
        for (int i = 1; i < path.length; i++) {
            //起点到终点途径路程累加
            pathLen += dist[path[i - 1]][path[i]];
        }
        //然后再加上返回起点的路程
        pathLen += dist[path[0]][path[path.length - 1]];
        return pathLen;
    }

}
