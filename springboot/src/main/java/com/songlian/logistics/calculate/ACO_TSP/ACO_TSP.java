package com.songlian.logistics.calculate.ACO_TSP;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

//蚁群算法

@Data
public class ACO_TSP {

    public Ant[] ants; // 蚂蚁数组
    public int antNum = 15; // 蚂蚁数量
    public int cityNum; // 城市数量
    public int MAX_GEN = 500; // 运行代数
    public double[][] pheromone; // 信息素矩阵
    public double[][] distance; // 距离矩阵
    public double bestLength; // 最佳长度
    public int[] bestTour; // 最佳路径
    public int bestT; //最佳迭代数
    public List<double[]> locationList;

    // 三个参数
    private double alpha = 1; //信息素重要程度
    private double beta = 5; //启发式因子重要程度
    private double rho = 0.5; //信息素挥发速率

    //构造函数
    public ACO_TSP(List<double[]> locationList) {
        this.locationList = locationList;
        cityNum = locationList.size();
        ants = new Ant[antNum];
    }

    //外部调用窗口
    public void solve() {
        initVar();
        solver();
    }

    //
    public void solver() {
        // 迭代MAX_GEN次
        for (int g = 0; g < MAX_GEN; g++) {
            // antNum只蚂蚁
            for (int i = 0; i < antNum; i++) {
                // i这只蚂蚁走cityNum步，完整一个TSP
                for (int j = 1; j < cityNum; j++) {
                    ants[i].selectNextCity(pheromone);
                }
                // 把这只蚂蚁起始城市加入其禁忌表中
                // 禁忌表最终形式：起始城市,城市1,城市2...城市n,起始城市
                ants[i].getTabu().put(ants[i].getTabu().size(), ants[i].getFirstCity());
                // 查看这只蚂蚁行走路径距离是否比当前距离优秀
                if (ants[i].getTourLength() < bestLength) {
                    // 比当前优秀则拷贝优秀TSP路径
                    bestLength = ants[i].getTourLength();
                    for (int k = 0; k < cityNum + 1; k++) {
                        bestTour[k] = ants[i].getTabu().get(k);
                    }
                    bestT = g;
                }
                // 更新这只蚂蚁的信息数变化矩阵，对称矩阵
                for (int j = 0; j < cityNum; j++) {
                    ants[i].getDelta()[ants[i].getTabu().get(j)][ants[i]
                            .getTabu().get(j + 1)] = (double) (1.0 / ants[i]
                            .getTourLength());
                    ants[i].getDelta()[ants[i].getTabu().get(j + 1)][ants[i]
                            .getTabu().get(j)] = (1.0 / ants[i]
                            .getTourLength());
                }
            }
            // 更新信息素
            updatePheromone();
            // 重新初始化蚂蚁
            for (int i = 0; i < antNum; i++) {
                ants[i].initAnt(distance, alpha, beta);
            }
        }
        // 输出结果
        System.out.println("最佳迭代次数:" + bestT);
        System.out.println("最短路程为：" + bestLength);
        int[] bestPath = new int[cityNum + 1];
        System.arraycopy(bestTour, 0, bestPath, 0, bestTour.length);
        bestPath[cityNum] = bestPath[0];
        System.out.println("最佳路径为：" + Arrays.toString(bestPath));
    }

    //初始化
    public void initVar() {
        //初始化距离矩阵
        distance = new double[cityNum][cityNum];
        for (int i = 0; i < cityNum; i++) {
            for (int j = i; j < cityNum; j++) {
                if (i == j) {
                    distance[i][j] = 0.0;
                } else {
                    distance[i][j] = getDistance(locationList.get(i), locationList.get(j));
                    distance[j][i] = distance[i][j];
                }
            }
        }
        //初始化信息素矩阵
        pheromone = new double[cityNum][cityNum];
        for (int i = 0; i < cityNum; i++) {
            for (int j = 0; j < cityNum; j++) {
                pheromone[i][j] = 0.1; // 初始化为0.1
            }
        }
        bestLength = Integer.MAX_VALUE;
        bestTour = new int[cityNum + 1];
        // 放置蚂蚁
        for (int i = 0; i < antNum; i++) {
            ants[i] = new Ant(cityNum);
            ants[i].initAnt(distance, alpha, beta);
        }
    }

    // 更新信息素
    private void updatePheromone() {
        // 信息素挥发
        for (int i = 0; i < cityNum; i++) {
            for (int j = 0; j < cityNum; j++) {
                pheromone[i][j] = pheromone[i][j] * (1 - rho);
            }
        }
        // 信息素更新
        for (int i = 0; i < cityNum; i++) {
            for (int j = 0; j < cityNum; j++) {
                for (int k = 0; k < antNum; k++) {
                    pheromone[i][j] += ants[k].getDelta()[i][j];
                }
            }
        }
    }

    //计算两点之间的距离（如果使用伪欧氏距离，可以减少计算量）
    public double getDistance(double[] place1, double[] place2) {
        //伪欧氏距离在根号内除以了一个10
        return Math.sqrt((Math.pow(place1[0]-place2[0],2)+Math.pow(place1[1]-place2[1],2))/10.0);
//        return Math.sqrt((Math.pow(place1[0] - place2[0], 2) + Math.pow(place1[1] - place2[1], 2)));
    }

}