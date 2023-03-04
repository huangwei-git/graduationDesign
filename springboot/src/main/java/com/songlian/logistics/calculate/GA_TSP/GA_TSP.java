package com.songlian.logistics.calculate.GA_TSP;

import com.songlian.logistics.calculate.TspData;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/*
**  Create by: 王孙康宏
    Date:2021-08-04
    Time:9:07
*/
public class GA_TSP implements Serializable {
    // 城市坐标集合 <[x,y]>
    List<double[]> locationList;
    // 距离矩阵
    double[][] dist;
    // 城市数量
    int cityNum;
    // 放置所有的种群基因信息
    private List<Genome> population = new ArrayList<>();
    // 新一代的种群基因信息
    private List<Genome> newPopulation = new ArrayList<>();
    // 种群数量信息
    private int popSize = 100;
    // 种群总的适应度数值
    private double totalFitness = 0;
    // 最好的适应度的对应的染色体(基因序列、路径序列)
    private Genome bestGenome;
    // 基因突变概率
    private double mutationRate = 0.4;
    // 基因交叉概率
    private double crossoverRate = 0.98;
    // 遗传的代数(第几代)
    private int t = 0;
    // 变异对换次数
    private double variationExchangeCount = 1;
    // 遗传最大的迭代次数
    private int MAX_GEN = 2000;
    // 复制最优解的次数(选择种群的最优个体，然后复制几次，将最优个体复制多个，存到新的集合中)
    int cloneNumOfBestIndividual = 3;
    // 最佳迭代数
    private int bestT = 0;
    // 随机函数对象
    public Random random;
    // 各个个体的累积概率
    double[] probabilitys;

    public GA_TSP(List<double[]> locationList, double dist[][]) {
        this.locationList = locationList;
        this.dist = dist;
    }

    public TspData solve() throws IOException, ClassNotFoundException {
        initVar();
        return solver();
    }

    public TspData solver() throws IOException, ClassNotFoundException {
        while (t < MAX_GEN) {
            popSize = population.size();
            // 基因进化
            evolution();
            // 更新种群
            population = copyGenomeList(newPopulation);
            t++;
        }

        // 输出结果
        //System.out.println("最佳迭代次数:" + bestT);
        //System.out.println("最短路程为：" + bestGenome.getPathLen());
        int[] bestPath = new int[cityNum + 1];
        System.arraycopy(bestGenome.getGenomeArray(), 0, bestPath, 0, bestGenome.getGenomeArray().length);
        bestPath[cityNum] = bestPath[0];
        //System.out.println("最佳路径为：" + Arrays.toString(bestPath));

        // 包装返回结果
        TspData tspData = new TspData(bestGenome.getPathLen(), Arrays.toString(bestPath));
        return tspData;
    }

    // 进化函数
    // 进化函数，正常交叉变异
    public void evolution() {
        //更新累积概率和总适应度
        updateProbabilityAndTotalFitness();
        // 挑选某代种群中适应度最高的个体
        HashSet<Integer> actionIndex = selectBestGenomeAndJoinNext();
        // 赌轮选择策略对剩下的scale-1个个体进行随机的交叉，变异
        while (newPopulation.size() < population.size()) {
            double r = random.nextDouble();
            for (int j = 0; j < probabilitys.length; j++) {
                if (r <= probabilitys[j]) {
                    newPopulation.add(population.get(j));
                    break;
                }
            }
        }

        // 交叉
        if (random.nextDouble() <= crossoverRate) {
            int r = random.nextInt(cityNum);
            int k = random.nextInt(cityNum);
            while (r == k){
                r = random.nextInt(cityNum);
            }
            cross(k, r);
        }

        for (int i = 0; i < population.size(); i++) {
            // 变异
            if (random.nextDouble() <= mutationRate) {
                variation(i);
            }
        }

    }

    // 单点映射交叉
    private void cross(int k1, int k2) {
        // 获取要交叉的两个基因
        int[] genomeArray1 = newPopulation.get(k1).getGenomeArray();
        int[] genomeArray2 = newPopulation.get(k2).getGenomeArray();
        // 找到交叉位置
        int crossIndex = random.nextInt(cityNum);
        // 获取交叉片段
        for (int i = 0; i <= crossIndex; i++) {
            int temp = genomeArray1[i];
            genomeArray1[i] = genomeArray2[i];
            genomeArray2[i] = temp;
        }
        // 找到重复基因
        HashSet<Integer> set = new HashSet<>();
        // <index,value>
        HashMap<Integer, Integer> repeatMap = new HashMap<>();
        for (int i = 0; i < genomeArray1.length; i++) {
            if (!set.add(genomeArray1[i])) {
                repeatMap.put(i, genomeArray1[i]);
            }
        }
        set.clear();
        for (int i = 0; i < genomeArray2.length; i++) {
            if (!set.add(genomeArray2[i])) {
                for (int key : repeatMap.keySet()) {
                    genomeArray1[key] = genomeArray2[i];
                    genomeArray2[i] = repeatMap.get(key);
                    repeatMap.remove(key);
                    break;
                }
            }
        }
        // 交叉完毕，将基因放回个体，再将个体放回种群，并更新他们的适应值和路径长度
        newPopulation.get(k1).setGenomeArray(genomeArray1);
        newPopulation.get(k1).updateFitnessAndPathLen();
        newPopulation.get(k2).setGenomeArray(genomeArray2);
        newPopulation.get(k2).updateFitnessAndPathLen();
    }

    public void cross2(int k1, int k2) {
        // 获取要交叉的两个基因
        int[] genomeArray1 = newPopulation.get(k1).getGenomeArray();
        int[] genomeArray2 = newPopulation.get(k2).getGenomeArray();
        //使用LinkedHashSet存储元素，方便替换元素时，对元素进行删减
        LinkedHashSet<Integer> hashSetI = new LinkedHashSet<>();
        LinkedHashSet<Integer> hashSetJ = new LinkedHashSet<>();
        for (int k = 0; k < this.cityNum; k++) {
            hashSetI.add(genomeArray1[k]);
            hashSetJ.add(genomeArray2[k]);
        }
        //开始交换的位置，一直交换到尾部，即单点交叉(begin至少要从1开始，否则没有意义)
        int begin = random.nextInt(this.cityNum - 1) + 1;
        for (int k = begin; k < this.cityNum; k++) {
            //交换两个基因的对应位置
            int temp = genomeArray1[k];
            genomeArray1[k] = genomeArray2[k];
            genomeArray2[k] = temp;
            //删除对应的元素，该元素已经在k位置了
            hashSetI.remove(genomeArray1[k]);
            hashSetJ.remove(genomeArray2[k]);
        }
        //重新填充非交叉区域的元素
        begin = 0;
        for (Integer element : hashSetI) {
            genomeArray1[begin++] = element;
        }
        begin = 0;
        for (Integer element : hashSetJ) {
            genomeArray2[begin++] = element;
        }
        // 交叉完毕，将基因放回个体，再将个体放回种群，并更新他们的适应值和路径长度
        newPopulation.get(k1).setGenomeArray(genomeArray1);
        newPopulation.get(k1).updateFitnessAndPathLen();
        newPopulation.get(k2).setGenomeArray(genomeArray2);
        newPopulation.get(k2).updateFitnessAndPathLen();
    }

    // 变异(多次对换)
    private void variation(int k) {
        Genome genome = newPopulation.get(k);
        int[] genomeArray = genome.getGenomeArray();
        for (int i = 0; i < variationExchangeCount; i++) {
            int r1 = random.nextInt(cityNum);
            int r2 = random.nextInt(cityNum);
            while (r1 == r2) {
                r1 = random.nextInt(cityNum);
                r2 = random.nextInt(cityNum);
            }
            //交换
            int temp = genomeArray[r1];
            genomeArray[r1] = genomeArray[r2];
            genomeArray[r2] = temp;
        }
        //将变异后的基因序列放回个体
        genome.setGenomeArray(genomeArray);
        // 更新基因的适应值和路程长度
        genome.updateFitnessAndPathLen();
        //将变异后的个体放回种群
        newPopulation.set(k, genome);
    }

    //初始化变量
    public void initVar() {
        this.cityNum = locationList.size();
        this.population = new ArrayList<>();
        random = new Random(System.currentTimeMillis());
        //初始化距离矩阵
        //dist = new double[cityNum][cityNum];
        //for (int i = 0; i < cityNum; i++) {
        //    for (int j = i; j < cityNum; j++) {
        //        if (i != j) {
        //            dist[i][j] = getDistance(locationList.get(i), locationList.get(j));
        //            dist[j][i] = dist[i][j];
        //        }
        //    }
        //}

        Genome.dist = dist;
        //初始化种群信息
        List<Integer> path = new ArrayList<>();
        for (int i = 0; i < cityNum; i++) {
            path.add(i);
        }
        for (int i = 0; i < popSize; i++) {
            Collections.shuffle(path);
            int[] initPath = new int[cityNum];
            for (int j = 0; j < path.size(); j++) {
                initPath[j] = path.get(j);
            }
            Genome genome = new Genome(initPath.clone());
            population.add(genome);
        }
        bestGenome = copyGenome(population.get(0));
        for (int i = 1; i < popSize; i++) {
            Genome genome = population.get(i);
            if (bestGenome.getFitness() < genome.getFitness()) {
                bestGenome = copyGenome(genome);
            }
        }
        //System.out.println("初始解为：" + bestGenome.getPathLen());
    }

    // 挑选种群中适应度最大的个体基因直接加入下一代种群
    public HashSet<Integer> selectBestGenomeAndJoinNext() {
        newPopulation = new ArrayList<>();
        HashSet<Integer> actionIndex = new HashSet<>();
        Genome tempBest = population.get(0);
        int maxIndex = 0;
        for (int i = 1; i < population.size(); i++) {
            if (population.get(i).getFitness() > tempBest.getFitness()) {
                tempBest = population.get(i);
                maxIndex = i;
            }
        }
        actionIndex.add(maxIndex);
        if (tempBest.getFitness() > bestGenome.getFitness()) {
            bestGenome = copyGenome(tempBest);
            bestT = t;
//            System.out.println("当前代数：" + t + " : " + bestGenome.getPathLen());
        }
        for (int i = 0; i < cloneNumOfBestIndividual; i++) {
            newPopulation.add(copyGenome(tempBest));
        }
        return actionIndex;
    }

    // 更新各个个体的累积概率和种群总适应度
    public void updateProbabilityAndTotalFitness() {
        probabilitys = new double[population.size()];
        totalFitness = 0;
        for (Genome genome : population) {
            totalFitness += genome.getFitness();
        }
        double rate = 0.0;
        for (int i = 0; i < population.size(); i++) {
            rate += (population.get(i).getFitness() / totalFitness);
            probabilitys[i] = rate;
        }
    }

    // 计算两点之间的距离（使用伪欧氏距离，可以减少计算量）
    private double getDistance(double[] place1, double[] place2) {
        // 伪欧氏距离在根号内除以了一个10
        return Math.sqrt((Math.pow(place1[0] - place2[0], 2) + Math.pow(place1[1] - place2[1], 2)) / 10.0);
//        return Math.sqrt((Math.pow(place1[0] - place2[0], 2) + Math.pow(place1[1] - place2[1], 2)));
    }

    // 拷贝单个基因
    public Genome copyGenome(Genome genome) {
        Genome copy = new Genome();
        copy.setGenomeArray(genome.getGenomeArray().clone());
        copy.setFitness(genome.getFitness());
        copy.setPathLen(genome.getPathLen());
        return copy;
    }

    // 拷贝基因集合
    public List<Genome> copyGenomeList(List<Genome> genomeList) {
        List<Genome> copyList = new ArrayList<>();
        for (Genome genome : genomeList) {
            copyList.add(copyGenome(genome));
        }
        return copyList;
    }

}
