<template>
  <div id="root">
    <div style="margin:5px;width: 891.5px;display: inline-block">
      <div style="margin-bottom: 10px;">
      最短距离：<el-tag type="danger" effect="dark">{{this.minLength}}</el-tag>
      </div>
      <div id="TSmain" style="height: 450px;border:solid #333333 1px"></div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts';
export default {
  name: "TSGraph",
  data(){
    return{
      pointsData:[],
      linksData:[],
      minLength:-1,
      firstDraw:true,
    }
  },
  methods:{
    loadGraphData(data){
      this.minLength = data.min;
      this.linksData = data.links;
      this.pointsData = data.points;
      this.initGraph();
    },
    initGraph(){

      var chartDom = document.getElementById('TSmain');
      var myChart = echarts.init(chartDom);
      var option;
      option = {
        title: {
          text: '禁忌搜索算法',
          left:'left'
        },
        tooltip: {
          trigger:'item',
          formatter: function (params) {
            return (
                '(' +
                params.data.x +
                ', ' +
                params.data.y + ')'
            );
          }
        },
        series: [
          {
            type: 'graph',
            layout: 'none',
            symbolSize: 40,
            roam: false,
            draggable:false,
            label: {
              fontSize:20,
              show: true
            },
            edgeSymbol: ['circle', 'arrow'],
            edgeSymbolSize: [4, 10],
            edgeLabel: {
              fontSize: 5
            },
            data: this.pointsData,
            links: this.linksData,
            lineStyle: {
              opacity: 0.9,
              width: 1,
              symbolSize: 1,
              curveness: 0.2,
              color:'#000'
            },
            itemStyle:{
              color:'#950842'
            }
          }
        ]
      };
      option && myChart.setOption(option);
    },
  },created() {
  },
  mounted() {
    this.$bus.$on("selectTS",this.loadGraphData);
  }
}
</script>

<style scoped>

</style>