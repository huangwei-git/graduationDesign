package com.songlian.logistics.calculate.DEV;


import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.cplex.IloCplex;

//    数据参数定义
class Data{
    //        目标系数
    double[] objectiveCoefficient={7, 8, 2, 9, 6};
    //        约束系数
    double[][] constraintCoefficient={{5, 7, 9, 2, 1}, {18, 4, -9, 10, 12}, {4, 7, 3, 8, 5}, {5, 13, 16, 3, -7}};
    //        约束值
    double[] constraintValue={250, 285, 211, 315};
    //        变量数量
    int variableNumber=5;
    //        约束数量
    int constrainNumber=4;
}

public class RunTest {
    Data data;
    // cplex内部对象
    IloCplex model;
    // 定义变量
    public  IloNumVar[] x;

    public RunTest(Data data){
        this.data = data;
    }

    // 求解函数
    public void solve() throws IloException {
        if(model.solve() == false){
            System.out.println("Can't find solution");
            return ;
        }else{
            System.out.println("目标值：" + model.getObjValue());
            for (int i = 0; i < data.variableNumber; i++) {
                System.out.println("变量值x["+(i+1)+"]:"+model.getValue(x[i]));
            }
        }
    }

    // 根据数学模型建立求解模型
    public void BuildModel() throws IloException {
        model = new IloCplex();
        model.setOut(null);

        // 变量
        x = new IloNumVar[data.variableNumber];

        // 定义cplex变量x的数据类型及取值范围
        for (int i = 0; i < data.variableNumber; i++) {
            x[i] = model.numVar(0,1e15, IloNumVarType.Int,"x["+i+"]");
        }

        // 设置目标函数
        IloNumExpr obj = model.numExpr();
        for (int i = 0; i < data.variableNumber; i++) {
            obj = model.sum(obj,model.prod(data.objectiveCoefficient[i],x[i]));
        }
        // 目标函数设置为求最大值
        model.addMaximize(obj);

        //添加约束
        for (int k = 0; k < data.constrainNumber; k++) {
            IloNumExpr expr = model.numExpr();
            for (int i = 0; i < data.variableNumber; i++) {
                expr = model.sum(expr,model.prod(data.constraintCoefficient[k][i],x[i]));
            }
            model.addLe(expr, data.constraintValue[k]);
        }
    }

    public static void main(String[] args) throws IloException {
        Data data = new Data();
        RunTest lp = new RunTest(data);
        lp.BuildModel();
        lp.solve();
    }
}
