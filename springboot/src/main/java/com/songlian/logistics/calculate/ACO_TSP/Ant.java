package com.songlian.logistics.calculate.ACO_TSP;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Data
public class Ant implements Cloneable {
    // 禁忌表 <index,cityIndex>
    private HashMap<Integer, Integer> tabu;
    // 允许搜索的城市
    private List<Integer> allowedCities;
    // 信息素变化矩阵
    private double[][] delta;
    // 距离矩阵
    private double[][] distance;
    private double alpha;
    private double beta;
    // 路径长度
    private int tourLength;
    // 城市数量
    private int cityNum;
    // 起始城市
    private int firstCity;
    // 当前城市
    private int currentCity;

    // 构造函数
    public Ant(int cityNum) {
        this.cityNum = cityNum;
        this.tourLength = 0;
    }

    // 初始化
    public void initAnt(double[][] distance, double a, double b) {
        alpha = a;
        beta = b;
        // 初始允许搜索的城市集合
        allowedCities = new ArrayList<>();
        // 初始禁忌表
        tabu = new HashMap<>();
        // 初始距离矩阵
        this.distance = distance;
        // 初始信息数变化矩阵为0
        delta = new double[cityNum][cityNum];
        for (int i = 0; i < cityNum; i++) {
            Integer integer = i;
            allowedCities.add(integer);
            for (int j = 0; j < cityNum; j++) {
                delta[i][j] = 0.f;
            }
        }
        // 设置起始城市
        Random random = new Random(System.currentTimeMillis());
        firstCity = random.nextInt(cityNum); // 随机选取第一个城市
        // 允许搜索的城市集合中移除起始城市
        for (Integer i : allowedCities) {
            if (i == firstCity) {
                allowedCities.remove(i);
                break;
            }
        }
        // 将起始城市添加至禁忌表
        tabu.put(tabu.size(), firstCity);
        // 当前城市为起始城市
        currentCity = firstCity;
    }

    //选择下一个城市
    public void selectNextCity(double[][] pheromone) {
        double[] p = new double[cityNum];
        double sum = 0d;
        // 计算分母部分
        for (Integer i : allowedCities) {
            sum += Math.pow(pheromone[currentCity][i], alpha)
                    * Math.pow(1.0 / distance[currentCity][i], beta);
        }
        // 计算概率矩阵
        for (int i = 0; i < cityNum; i++) {
            boolean flag = false;
            for (Integer j : allowedCities) {
                if (i == j) {
                    p[i] = (double) (Math.pow(pheromone[currentCity][i], alpha) * Math
                            .pow(1.0 / distance[currentCity][i], beta)) / sum;
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                p[i] = 0.f;
            }
        }
        // 轮盘赌选择下一个城市
        Random random = new Random();
        double sleectP = random.nextDouble();
        int selectCity = 0;
        double sum1 = 0d;
        for (int i = 0; i < cityNum; i++) {
            sum1 += p[i];
            if (sum1 >= sleectP) {
                selectCity = i;
                break;
            }
        }
        // 从允许选择的城市中去除select city
        for (Integer i : allowedCities) {
            if (i == selectCity) {
                allowedCities.remove(i);
                break;
            }
        }
        // 在禁忌表中添加select city
        tabu.put(tabu.size(), selectCity);
        // 将当前城市改为选择的城市
        currentCity = selectCity;
    }

    //计算路径长度
    public double calculateTourLength() {
        double len = 0.0;
        //禁忌表tabu最终形式：起始城市,城市1,城市2...城市n,起始城市
        for (int i = 0; i < cityNum; i++) {
            len += distance[this.tabu.get(i)][this.tabu.get(i + 1)];
        }
        return len;
    }

    //获取路径长度
    public double getTourLength() {
        return calculateTourLength();
    }

}
