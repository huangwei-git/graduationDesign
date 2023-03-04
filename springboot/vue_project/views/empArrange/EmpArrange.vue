<template>
  <div id="root">

    <el-button type="warning" @click="genRandomPeople">生成随机人数</el-button>
    <el-button type="warning" @click="genRandomSalary" style="margin-left: 5px">生成随机费用</el-button>

    <el-popover
        placement="bottom"
        width="320px"
        trigger="click"
        style="margin-left: 5px">
      <div>
        <span>随机人数均值</span>
        <el-input-number v-model="peopleAverage" :min="50" :max="500" :step="10"></el-input-number>
      </div>
      <div>
        <span>随机人数标准差</span>
        <el-input-number v-model="peopleRange" :min="0" :max="500" :step="5"></el-input-number>
      </div>
      <el-divider></el-divider>
      <div>
        <span>随机费用均值</span>
        <el-input-number v-model="salaryAverage" :min="20" :max="1000" :step="5"></el-input-number>
      </div>
      <div>
        <span>随机费用标准差</span>
        <el-input-number v-model="salaryrange"  :min="0" :max="500" :step="5"></el-input-number>
      </div>
      <el-button type="warning" slot="reference">随机数配置</el-button>
    </el-popover>

    <el-button type="info" @click="clearFormData" style="margin-left: 5px">清空</el-button>
    <el-button type="primary" @click="submitForm" style="margin-left: 5px">提交</el-button>

    <div style="display: flex;justify-content: center;margin-top: 20px">
      <el-card class="box-card" style="text-align: center;width: 65%">
        <div slot="header" class="clearfix">
          <span>约束条件</span>
          <el-tag type="success" style="margin-left: 5px" effect="dark">人数</el-tag>
          <el-tag type="danger" style="margin-left: 5px" effect="dark">费用</el-tag>

            <el-popover
                style="float: right; padding: 0px 0"
                placement="top"
                width="200"
                trigger="click">
              <el-input-number :min="100" :max="1000" :step="100" v-model="rangeMaxValue"></el-input-number>
              <el-button slot="reference" type="text">范围最大值</el-button>
            </el-popover>

        </div>
        <el-form label-position="right" :inline="true" :model="formData" label-width="150px" class="demo-form-inline sl-no" style="width: 100%;text-align: center">
          <el-form-item label="工作时长" style="display: block">
            <el-slider
                v-model="formData.workTime"
                :step="2"
                :min="2"
                :max="12"
                style="width: 300px"
                show-stops>
            </el-slider>
          </el-form-item>

          <el-form-item label="安排员工数限制" style="display: block">
            <div style="display: flex">
              <el-slider
                  style="width: 400px;display: inline-block"
                  v-model="formData.rangeValue"
                  range
                  :min="0"
                  :max="rangeMaxValue">
              </el-slider>
              <div style="display: inline-block;margin-left: 10px">
                下限 <el-switch
                    v-model="formData.min"
                    inactive-color="#cccccc"
                    active-color="#13ce66">
                </el-switch>
              </div>
              <div style="display: inline-block;margin-left: 10px">
                上限 <el-switch
                    v-model="formData.max"
                    inactive-color="#cccccc"
                    active-color="#13ce66">
                </el-switch>
              </div>
            </div>
          </el-form-item>

          <el-form-item v-for="(ele,index) in timeStep" :key="index" :label="ele.label">
            <el-input-number v-model="formData.people[index]" placeholder="人数" :step="1" :min="0" :max="1000" step-strictly style="width: 150px"></el-input-number>
            <el-input-number v-model="formData.salary[index]" placeholder="费用" :step="1" :min="0" :max="1000" step-strictly style="width: 150px;margin-left: 5px" ></el-input-number>
          </el-form-item>
        </el-form>
      </el-card>

      <div>
        <RandomDataCharts/>
      </div>
    </div>

    <div align="center" v-show="solution[0].objValue != ''">
      <el-table
          :data="solution"
          border
          :cell-style="{ textAlign: 'center' }"
          :header-cell-style="{ textAlign: 'center',background:'#f5f7fa',color:'#950842' }"
          style="width: 90%;margin-top: 20px">
        <el-table-column label="开始工作的时间">
          <el-table-column v-for="(field,index) in fieldMap" :label="field.label" prop="values">
            <template slot-scope="scope">
              {{scope.row.values[index]}}人
            </template>
          </el-table-column>

        </el-table-column>
        <el-table-column label="最小所需费用" prop="objValue"></el-table-column>
      </el-table>

      <ArrangeCharts/>
    </div>


    

  </div>
</template>

<script>
import RandomDataCharts from "./RandomDataCharts";
import ArrangeCharts from "./ArrangeCharts";

export default {
  name: "EmpArrange",
  data(){
    return {
      formData:{people:Array(12).fill(''),salary:Array(12).fill(''),workTime:8,rangeValue:[10,100],min:false,max:false},
      timeStep:[
          {label:'00:00-02:00'},
          {label:'02:00-04:00'},
          {label:'04:00-06:00'},
          {label:'06:00-08:00'},
          {label:'08:00-10:00'},
          {label:'10:00-12:00'},
          {label:'12:00-14:00'},
          {label:'14:00-16:00'},
          {label:'16:00-18:00'},
          {label:'18:00-20:00'},
          {label:'20:00-22:00'},
          {label:'22:00-00:00'},
      ],
      solution:[{values:[],objValue:''}],
      fieldMap:[
        {label:'00:00'},
        {label:'02:00'},
        {label:'04:00'},
        {label:'06:00'},
        {label:'08:00'},
        {label:'10:00'},
        {label:'12:00'},
        {label:'14:00'},
        {label:'16:00'},
        {label:'18:00'},
        {label:'20:00'},
        {label:'22:00'},
    ],
      rangeMaxValue:200,
      peopleAverage:150,
      peopleRange:70,
      salaryAverage:60,
      salaryrange:30,
    }
  },
  methods:{
    genRandomPeople() {
      this.formData.people = this.GenNormalDistributionData(this.peopleAverage,this.peopleRange,true);
    },
    genRandomSalary(){
      this.formData.salary = this.GenNormalDistributionData(this.salaryAverage,this.salaryrange,false);

    },
    clearFormData(){
      this.formData.people = Array(12).fill('');
      this.formData.salary = Array(12).fill('');
      this.formData.workTime = 8;
      this.formData.rangeValue = [0, 100];
      this.formData.min = false;
      this.formData.max = false;
      this.rangeMaxValue = 200;
      this.solution = [{values:[],objValue:''}];
    },
    submitForm(){
      this.$axios.post(this.$httpUrl + "/user/empArrange",this.formData)
      .then(res => res.data)
      .then(res => {
        if(res.code == 200){
          this.solution[0].values = res.data.values;
          this.solution[0].objValue = res.data.objValue;
        }else{
          alert(res.msg);
        }
      })
    },
    // Box-Muller法:获得正态分布随机数
    //  average为均值，range为标准差
    getNumberInNormalDistribution(average, range){
      return average + (this.randomNormalDistribution() * range);
    },
    randomNormalDistribution(){
      var u=0.0, v=0.0, w=0.0, c=0.0;
      do{
        //获得两个（-1,1）的独立随机变量
        u=Math.random()*2-1.0;
        v=Math.random()*2-1.0;
        w=u*u+v*v;
      }while(w==0.0||w>=1.0)
      // Box-Muller转换
      c = Math.sqrt((-2 * Math.log(w)) / w);
      //返回2个标准正态分布的随机数，封装进一个数组返回
      //当然，因为这个函数运行较快，也可以扔掉一个
      //return [u*c,v*c];
      return u * c;
    },
    GenNormalDistributionData(average,range,enableSort) {
      let arrLeft = Array(6);
      let arrRight = Array(6);
      const round = Math.round;
      for (let i = 0; i < 6; i++) {
        arrLeft[i] = -1;
        while(arrLeft[i] <= 0){
          arrLeft[i] = Math.round(this.getNumberInNormalDistribution(average, range));
        }
      }
      for (let i = 0; i < 6; i++) {
        arrRight[i] = -1;
        while(arrRight[i] <= 0){
          arrRight[i] = Math.round(this.getNumberInNormalDistribution(average, range));
        }
      }
      if(enableSort){
        arrLeft = arrLeft.sort((a,b) => a - b);
        arrRight = arrRight.sort((a,b) => b - a);
      }
      let arr = Array().concat(arrLeft,arrRight);
      console.log(arr);
      return arr;
    }
  },
  watch:{
    "formData.people":{
      handler(n,o){this.$bus.$emit("genRandomPeople",this.formData.people);}
    },
    "formData.salary":{
      handler(n,o){this.$bus.$emit("genRandomSalary",this.formData.salary);}
    },
    'solution':{
      deep:true,
      handler(n,o){this.$bus.$emit("genRandomArrange",this.solution[0].values);}
    }
  },
  components:{
    RandomDataCharts,
    ArrangeCharts
  }
}
</script>

<style scoped>
.el-popover div span{
  display: inline-block;
  height: 38px;
  line-height: 38px;
  width: 110px;
  margin: 5px 0;
}
</style>