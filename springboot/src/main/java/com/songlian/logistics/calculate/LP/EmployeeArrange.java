package com.songlian.logistics.calculate.LP;


import com.songlian.logistics.calculate.Main;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.cplex.IloCplex;

import java.util.HashMap;
import java.util.Map;

//    数据参数定义


public class EmployeeArrange {
    LpData data;
    // cplex内部对象
    IloCplex model;
    // 定义变量
    public IloNumVar[] x;
    public IloNumVar[][] y;

    public EmployeeArrange(LpData data){
        this.data = data;
    }

    public Map Run() throws IloException {
        this.BuildModel4();
        return this.solve();
    }

    // 求解函数
    public Map solve() throws IloException {
        Map map = null;
        if(model.solve() == false){
            System.out.println("Can't find solution");
        }else{
            map = new HashMap();
            int values[] = new int[12];
            System.out.println("目标值：" + model.getObjValue());
            int objValue = (int) model.getObjValue();
            for (int i = 0; i < 12; i++) {
                //System.out.println("变量值" + "x[" + (i + 1) + "]:" + model.getValue(y[0][i]));
                values[i] = (int) model.getValue(y[0][i]);
            }
            map.put("values",values);
            map.put("objValue",objValue);
        }
        return map;
    }

    // 修正了目标函数
    public void BuildModel4() throws IloException {
        model = new IloCplex();
        model.setOut(null);


        int rowLen = data.variableNumber / 12;
        int colLen = 12;

        // 变量
        y = new IloNumVar[rowLen][colLen];

        // 定义cplex变量x的数据类型及取值范围
        for (int i = 0; i < rowLen; i++) {
            for (int j = 0; j < colLen; j++) {
                y[i][j] = model.numVar(0, 1e15, IloNumVarType.Int, "x["+i+"]["+j+"]");
            }
        }


        /**
         * 设置目标函数：在k时段工作的人数 * 该时段用人成本
         */
        IloNumExpr obj = model.numExpr();
        for (int k = 0; k < colLen; k++) {
            IloNumExpr tmpObj = model.numExpr();
            for (int i = 0; i < rowLen; i++) {
                for (int j = 0; j < colLen; j++) {
                    // 该时段的人数
                    tmpObj = model.sum(tmpObj,model.prod(data.constraintCoefficient[k + i * colLen][j],y[i][j]));
                }
            }
            // 用人成本
            obj = model.sum(obj,model.prod(data.objectiveCoefficient[k], tmpObj));
        }
        // 目标函数设置为求最小值
        model.addMinimize(obj);

        /**
         * 添加约束
         *      满足各时段的人数要求
         */
        for(int k = 0;k < colLen;k++){
            IloNumExpr expr = model.numExpr();
            for (int i = 0; i < rowLen; i++) {
                for (int j = 0; j < colLen; j++) {
                    expr = model.sum(expr,model.prod(data.constraintCoefficient[k + i * colLen][j],y[i][j]));
                }
            }
            model.addGe(expr,data.constraintValue[k]);
        }
    }

    public void BuildModel5() throws IloException {
        model = new IloCplex();
        model.setOut(null);


        int rowLen = data.variableNumber / 12;
        int colLen = 12;

        // 变量
        y = new IloNumVar[rowLen][colLen];

        // 定义cplex变量x的数据类型及取值范围
        for (int i = 0; i < rowLen; i++) {
            for (int j = 0; j < colLen; j++) {
                y[i][j] = model.numVar(data.constraintValue[12], data.constraintValue[13], IloNumVarType.Int, "x["+i+"]["+j+"]");
            }
        }


        /**
         * 设置目标函数：在k时段工作的人数 * 该时段用人成本
         */
        IloNumExpr obj = model.numExpr();
        for (int k = 0; k < colLen; k++) {
            IloNumExpr tmpObj = model.numExpr();
            for (int i = 0; i < rowLen; i++) {
                for (int j = 0; j < colLen; j++) {
                    // 该时段的人数
                    tmpObj = model.sum(tmpObj,model.prod(data.constraintCoefficient[k + i * colLen][j],y[i][j]));
                }
            }
            // 用人成本
            obj = model.sum(obj,model.prod(data.objectiveCoefficient[k], tmpObj));
        }
        // 目标函数设置为求最小值
        model.addMinimize(obj);

        /**
         * 添加约束
         *      满足各时段的人数要求
         */
        for(int k = 0;k < colLen;k++){
            IloNumExpr expr = model.numExpr();
            IloNumExpr exprNumLimit = model.numExpr();
            for (int i = 0; i < rowLen; i++) {
                for (int j = 0; j < colLen; j++) {
                    expr = model.sum(expr, model.prod(data.constraintCoefficient[k + i * colLen][j], y[i][j]));
                    exprNumLimit = model.sum(exprNumLimit,y[i][j]);
                }
            }
            model.addGe(expr,data.constraintValue[k]);
        }
    }


    public static void main(String[] args) throws IloException {
        LpData data = new LpData();
        data.setVariableNumber(12);
        data.setConstrainNumber(12);
        data.setConstraintCoefficient(new Main().getDoubleArray(10,1));
        double[] constraintArr = {
                112, 147, 167, 142, 188, 271, 302, 261, 305, 239, 189, 138,
                0,1e15,
        };
        double[] objectiveArr = {
                54 , 122, 63, 18, 12, 50, 64, 66, 74, 26, 29, 17
        };
        data.setConstraintValue(constraintArr);
        data.setObjectiveCoefficient(objectiveArr);

        EmployeeArrange employeeArrange = new EmployeeArrange(data);
        employeeArrange.BuildModel5();
        int arr[] = (int[]) employeeArrange.solve().get("values");
        for(int i : arr){
            System.out.println(i);
        }
    }

    public static double[][] getConstraintCoefficient(int init,int cnt) {
        double arr[][] = new double[cnt * 12][12];
        for(int t = 0;t < cnt;t++){
            int workTime = init + (t * 2);
            int step = workTime >> 1;
            int start = 13 - step;
            for (int i = 0 + (12 * t); i < 12 * (t + 1); i++) {
                for (int j = 0; j < step; j++) {
                    arr[i][(start + j) % 12] = 1;
                }
                start = (start + 1) % 12;
            }
        }
        return arr;
    }
}
