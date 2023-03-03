package com.songlian.logistics.calculate.IP_EmpArrange;

public class LpData{
    // 目标系数
    double[] objectiveCoefficient = {80, 80, 60, 60, 50, 60, 80, 80, 60, 60, 50, 60};
    // 约束系数
    double[][] constraintCoefficient={
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1},
            {1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1},
            {1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1},
    };
    // 约束值
    double[] constraintValue={60, 80, 150, 200, 300, 100, 60, 80, 150, 200, 300, 100};
    // 变量数量
    int variableNumber = 12;
    // 约束数量
    int constrainNumber = 12;

    public double[] getObjectiveCoefficient() {
        return objectiveCoefficient;
    }

    public void setObjectiveCoefficient(double[] objectiveCoefficient) {
        this.objectiveCoefficient = objectiveCoefficient;
    }

    public double[][] getConstraintCoefficient() {
        return constraintCoefficient;
    }

    public void setConstraintCoefficient(double[][] constraintCoefficient) {
        this.constraintCoefficient = constraintCoefficient;
    }

    public double[] getConstraintValue() {
        return constraintValue;
    }

    public void setConstraintValue(double[] constraintValue) {
        this.constraintValue = constraintValue;
    }

    public int getVariableNumber() {
        return variableNumber;
    }

    public void setVariableNumber(int variableNumber) {
        this.variableNumber = variableNumber;
    }

    public int getConstrainNumber() {
        return constrainNumber;
    }

    public void setConstrainNumber(int constrainNumber) {
        this.constrainNumber = constrainNumber;
    }
}