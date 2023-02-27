<template>
  <div id="root">

    <el-button type="warning" @click="genRandomPeople">生成随机人数</el-button>
    <el-button type="warning" @click="genRandomSalary">生成随机费用</el-button>
    <el-button type="info" @click="clearFormData">清空</el-button>
    <el-button type="primary" @click="submitForm">提交</el-button>

    <div style="display: flex;justify-content: center;margin-top: 20px">
      <el-card class="box-card" style="text-align: center;width: 65%">
        <div slot="header" class="clearfix">
          <span>约束条件</span>
          <el-tag type="success" style="margin-left: 5px" effect="dark">人数</el-tag>
          <el-tag type="danger" style="margin-left: 5px" effect="dark">费用</el-tag>

            <el-popover
                style="float: right; padding: 0px 0"
                placement="bottom"
                width="200"
                trigger="hover">
              <el-input-number v-model="rangeMaxValue"></el-input-number>
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
    </div>

  </div>
</template>

<script>

let item = {need:'',salary:''};

export default {
  name: "EmpArrange",
  data(){
    return {
      //form:{params:Array(12).fill(item)},
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
      randomRange:{
        people:[[40,100],[40,100],[60,120],[60,120],[120,180],[180,240],[200,260],[200,260],[180,260],[120,180],[60,120],[40,100]],
        salary:[[100,150],[100,150],[100,150],[100,150],[75,120],[75,120],[75,120],[75,120],[75,120],[75,120],[75,120],[75,120]]
      },
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
    }
  },
  methods:{
    genRandomPeople(){
      this.formData.people = this.genRandomArray(this.randomRange.people);
    },
    genRandomSalary(){
      this.formData.salary = this.genRandomArray(this.randomRange.salary);
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
    genRandomArray(range){
      let arr = Array(12);
      for(let i = 0;i < 12;i++) {
        let rand = -1;
          rand = Math.random() * (range[i][1] - range[i][0] + 1) + range[i][1];
        arr[i] = Math.floor(rand);
      }
      return arr;
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
    }
  }
}
</script>

<style scoped>

</style>