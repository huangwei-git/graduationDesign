<template>
  <div id="root">

    <el-button icon="el-icon-full-screen" type="info" @click="dialogVisible=true">打开地图</el-button>

    <el-dialog
        :visible.sync="dialogVisible"
        title="坐标地图"
        @open="open()"
        :fullscreen="true"
        :modal="false"
        center
        >
    <div style="margin-bottom: 20px;margin-top: -25px" class="sl-no">
        <el-button-group style="margin-left: 5px">
          <el-button icon="el-icon-place" type="success" @click="calcDist = false;point={name:'',x:'',y:''};" style="margin-left: 0px">查看坐标</el-button>
          <el-button icon="el-icon-cpu" type="success" @click="calcDist = true;point={x:'',y:''}" style="margin-left: 0px">计算距离</el-button>
        </el-button-group>

      <span style="position: absolute;right: 20px;line-height: 40px">
        <el-tag :type="calcDist?'danger':'primary'" effect="dark" style="font-size: 16px">{{msg}}</el-tag>
      </span>
    </div>

      <el-card style="display: inline-block;width:100%;padding: 10px">
        <div id="graph" style="width:100%;height: 745px"></div>
      </el-card>
    </el-dialog>
  </div>


</template>

<script>
import * as echarts from 'echarts';
var locationOption;
var locationChart;

export default {
  name: "LocationEcharts",
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
    initEcharts(){
      var locationChartDom = document.getElementById('graph');
      //var locationChart = echarts.init(locationChartDom);
      locationChart = echarts.init(locationChartDom);


      locationOption = {
        title: {
          text: '运输中心与需求地的位置关系图',
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
          },
        ]
      };

      this.$axios.get(this.$httpUrl + "/location")
          .then(res => res.data)
          .then(res => {
            console.log(res.data);
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
        this.point = {name:query.name,x:query.data.x , y:query.data.y}
        if(!this.calcDist) this.msg = this.point.name + "：("+ this.point.x + ', ' + this.point.y + ')';
      })
    },
    open(){
      this.$nextTick(() => {
        //  执行echarts方法
        this.initEcharts()
      })
    }
  },
  data() {
      return {
        dialogVisible: false,
        transportPointsData: [],
        demandPointsData: [],
        // 计算选择的点的距离
        point: {
          name:'',
          x: '',
          y: ''
        },
        calcDist: false,
        msg: '点击图中的位置',
      }
  },
  watch:{
    point:{
      deep:true,
      handler(n,o){
        if(o.x != '' && n.x!=''){
          if(this.calcDist){
            let dist = Math.sqrt((n.x - o.x) * (n.x - o.x) + (n.y - o.y) * (n.y - o.y));
            //this.msg = "距离："+ Math.floor(dist)
            this.msg = n.name+"-"+o.name+"："+ Math.floor(dist)
          }
        }
      }
    },
  },
  mounted() {

  },
}
</script>

<style scoped>

</style>