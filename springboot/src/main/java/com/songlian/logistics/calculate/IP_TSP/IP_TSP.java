package com.songlian.logistics.calculate.IP_TSP;

import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.List;

public class IP_TSP {
    // 城市坐标<[x,y]>
    List<double[]> locationList;
    // 距离矩阵
    double[][] distance;
    // 城市数量
    int cityNum;
    // 开始地点索引
    int startIndex;

    public IP_TSP(List<double[]> locationList) {
        this.locationList = locationList;
    }

    public void solve() {
        initVar();
        solver();
    }

    private void solver() {
        try {
            IloCplex cplex = new IloCplex();
            //决策变量
            IloIntVar[][] intVars = new IloIntVar[cityNum][cityNum];
            for (int i = 0; i < cityNum; i++) {
                for (int j = 0; j < cityNum; j++) {
                    if (i != j) {
                        intVars[i][j] = cplex.intVar(0, 1);
                    }
                }
            }
            //目标函数
            IloLinearNumExpr target = cplex.linearNumExpr();
            for (int i = 0; i < cityNum; i++) {
                for (int j = 0; j < cityNum; j++) {
                    if (i != j) {
                        target.addTerm(distance[i][j], intVars[i][j]);
                    }
                }
            }
            //求目标函数的最小值
            cplex.addMinimize(target);
            //约束
            //约束1：每行每列之和等于1
            for (int i = 0; i < cityNum; i++) {
                IloLinearNumExpr expr_row = cplex.linearNumExpr();
                IloLinearNumExpr expr_col = cplex.linearNumExpr();
                for (int j = 0; j < cityNum; j++) {
                    if (i != j) {
                        expr_row.addTerm(1, intVars[i][j]);
                        expr_col.addTerm(1, intVars[j][i]);
                    }
                }
                cplex.addEq(expr_row, 1);
                cplex.addEq(expr_col, 1);
            }
            //约束2：消除子回路
            IloNumVar[] u = cplex.numVarArray(cityNum, 0, Double.MAX_VALUE);
            for (int i = 1; i < cityNum; i++) {
                for (int j = 1; j < cityNum; j++) {
                    if (j != i) {
                        IloLinearNumExpr expr = cplex.linearNumExpr();
                        expr.addTerm(1.0, u[i]);
                        expr.addTerm(-1.0, u[j]);
                        expr.addTerm(cityNum - 1, intVars[i][j]);
                        cplex.addLe(expr, cityNum - 2);
                    }
                }
            }
            //取消cplex输出
            cplex.setOut(null);
            //求解
            if (cplex.solve()) {
                List<Integer> bestPath = new ArrayList<>();
                bestPath.add(startIndex);
                int index = startIndex;
                while (true) {
                    for (int i = 0; i < intVars[index].length; i++) {
                        if (index != i && cplex.getValue(intVars[index][i]) > 1e-06) {
                            index = i;
                            bestPath.add(i);
                            break;
                        }
                    }
                    if (index == startIndex) {
                        break;
                    }
                }
                System.out.println("最短路径为：" + bestPath);
                System.out.println("最短路径长度为：" + cplex.getObjValue());
            } else {
                System.err.println("此题无解");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 初始化变量
    public void initVar() {
        // 开始地点索引
        startIndex = 0;
        // 城市数量为点的数量
        cityNum = locationList.size();
        // 距离矩阵
        distance = new double[cityNum][cityNum];
        // 初始化距离矩阵
        for (int i = 0; i < distance.length; i++) {
            for (int j = i; j < distance[i].length; j++) {
                if (i == j) {
                    // 对角线为无穷大
                    distance[i][j] = Double.MAX_VALUE;
                } else {
                    // 计算i到j的距离
                    distance[i][j] = getDistance(locationList.get(i), locationList.get(j));
                    distance[j][i] = distance[i][j];
                }
            }
        }
    }

    // 计算两点之间的距离（使用伪欧氏距离，可以减少计算量）
    public double getDistance(double[] place1, double[] place2) {
        // 伪欧氏距离在根号内除以了一个10
//        return Math.sqrt((Math.pow(place1[0] - place2[0], 2) + Math.pow(place1[1] - place2[1], 2)) / 10.0);
        return Math.sqrt((Math.pow(place1[0] - place2[0], 2) + Math.pow(place1[1] - place2[1], 2)));
    }

}
