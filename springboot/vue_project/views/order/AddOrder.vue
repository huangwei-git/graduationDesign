<template>
  <div id="root">


    <el-card style="display: inline-block;width:650px;height: 650px;padding: 10px">
      <div id="main" style="width:600px;height: 600px"></div>
    </el-card>

    <el-card style="display: inline-block;width:850px;height: 650px;padding: 10px">
      <div class="sl-no" style="text-align: center;margin-bottom: 20px">
        <el-select v-model="selectedMaterial" placeholder="请选择物品">
          <el-option
              v-for="item in storeInfo"
              :key="item.mid"
              :label="item.name"
              :value="item">
            <span style="float: left">{{ item.name }}</span>
            <span style="float: right; color: #8492a6; font-size: 13px">{{ item.count }}</span>
          </el-option>
        </el-select>
      </div>

      <div style="text-align: center;width: 100%">
        <el-form label-width="80px" :model="formData" style="width: 500px;display: inline-block">
          <el-form-item v-for="(item,index) in demandPointsData" :label="item.name">
            <el-slider
                :disabled="selectedMaterial == ''"
                :max="selectedMaterial.count"
                v-model="formData.num[index]"
                show-input>
            </el-slider>
          </el-form-item>
          <el-form-item>
            <el-button :disabled="selectedMaterial == ''" @click="computeRestNumber">计算待分配的物品数</el-button>
          </el-form-item>
          <el-form-item>
            <el-button :disabled="selectedMaterial == ''" @click="getSolution">提交</el-button>
          </el-form-item>
        </el-form>
      </div>

    </el-card>

  </div>
</template>

<script>
import * as echarts from 'echarts';
export default {
  name: "AddOrder",
  methods:{
    dataFormat(res){
      if(res.code == 200){
        this.storeInfo = res.data;
        console.log(this.storeInfo)
      }else{
        alert(res.msg);
      }
      this.$nextTick(()=>{
      })
    },
    loadPost(){
      this.$axios.get(this.$httpUrl+'/material/getAllMaterialCount',)
          .then(res => res.data)
          .then(res => {
            this.dataFormat(res);
          })
    },
    getData(){
      console.log(this.formData.num);
      console.log(this.selectedMaterial);
    },
    recordRowInfo(row){
      if(this.selectedMaterial != '') {
        this.selectedRow = row;
        this.selectRowNum = this.demandPointsData[row.index].value;
      }
    },
    computeRestNumber(){
      let sum = 0;
      for (let i = 0; i < this.formData.num.length; i++) {
        sum += this.formData.num[i];
      }
      this.restNumber = this.selectedMaterial.count - sum;
      if(this.restNumber >= 0) this.$message.info(`待分配量：${this.restNumber}`);
      else this.$message.warning(`待分配量：${this.restNumber}`);
      return this.restNumber;
    },
    getSolution() {
      let sum = 0;
      let need = [];
      let mid = [];
      for (let i = 0; i < this.formData.num.length; i++) {
        sum += this.formData.num[i];
        need[i] = this.formData.num[i];
        mid[i] = this.demandPointsData[i].locId;
      }
      console.log("need:",need);
      console.log("mid:",mid);
      if(this.selectedMaterial.count - sum < 0){
        this.$message.error("超出库存数量！");
      }else{
        console.log(this.selectedMaterial);
        this.$axios.post(this.$httpUrl + "/order/comptude",{need:need,locId:mid,mid:this.selectedMaterial.mid});
      }
    }
  },
  data(){
    return {
      transportPointsData:[],
      demandPointsData:[],
      formData:{num:[]},
      materialInfo:'',
      totalDemands:'',
      storeInfo:'',
      restNumber:0,
      selectedMaterial:'',
    }
  },
  mounted() {
    var locationChartDom = document.getElementById('main');
    var locationChart = echarts.init(locationChartDom);
    var locationOption;

    locationOption = {
      title: {
        text: '运输中心与需求地的位置关系图',
      },
      tooltip: {
        trigger:'item',
        showContent: true,
      },
      animationDurationUpdate: 1500,
      animationEasingUpdate: 'quinticInOut',
      series: [
        {
          type: 'graph',
          layout: 'none',
          symbolSize: 80,
          roam: false,
          label: {
            show: true,
            normal:{
              show:true,
              textStyle:{
                fontSize:15,
              }
            }
          },
          edgeSymbol: ['circle', 'arrow'],
          edgeSymbolSize: [4, 10],
          edgeLabel: {
            fontSize: 20
          },
          data: [],
          lineStyle: {
            opacity: 0.9,
            width: 2,
            curveness: 0
          },
        },
        {
          type: 'graph',
          layout: 'none',
          symbolSize: 70,
          label: {
            show: true,
            normal:{
              show:true,
              textStyle:{
                fontSize:15,
              }
            }
          },
          edgeSymbol: ['circle', 'arrow'],
          edgeSymbolSize: [4, 10],
          edgeLabel: {
            fontSize: 20
          },
          data: [],
          lineStyle: {
            opacity: 0.9,
            width: 2,
            curveness: 0
          }
        }
      ]
    };

    this.$axios.get(this.$httpUrl + "/location")
        .then(res => res.data)
        .then(res => {
          let cnt = 0;
          for(let i = 0;i < res.total;i++){
            let item = {
              'name':res.data[i].name,
              'x':res.data[i].xpos,
              'y':res.data[i].ypos,
              locId:res.data[i].locId,
            };
            if(res.data[i].type == 0) this.transportPointsData.push(item);
            else{
              this.demandPointsData.push(item);
            }
          }
          this.totalDemands = this.demandPointsData.length;
          locationOption.series[0].data = this.transportPointsData;
          locationOption.series[1].data = this.demandPointsData;
        }).then(() => {
          console.log(this.demandPointsData)
      locationOption && locationChart.setOption(locationOption);
    })

    locationChart.on('click',(query)=>{
      console.log(query.data);
    })
  },
  beforeMount() {
    this.loadPost();
  },
}
</script>

<style scoped>

</style>