<template>
  <div class="root">
    <div id="arrangeData" style="width: 1200px;height: 450px;margin-top: 20px"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts';
export default {
  name: "ArrangeCharts",
  data(){
    return{
      arrangeData:[]
    }
  },
  methods:{
    initArrangeCharts(){
      var arrangeChartDom = document.getElementById('arrangeData');
      var arrangeChart = echarts.init(arrangeChartDom);
      var arrangeOption;

      arrangeOption = {
        title: {
          left: 'center',
          text: '各时间应安排的人数'
        },
        tooltip:{
          trigger: 'axis',
        },
        toolbox: {
          show: true,
          feature: {
            mark: {
              show: true
            },
            dataView: { //数据视图
              show: true,
              readOnly: true//是否只读
            },
            magicType: {//切换图表
              show: true,
              type: ['line', 'bar']//图表
            },
            restore: {//还原原始图表
              show: false
            },
            saveAsImage: {//保存图片
              show: true
            }
          }
        },
        xAxis: {
          boundaryGap: false,
          type: 'category',
          data: ['00:00', '02:00', '04:00', '06:00', '08:00', '10:00', '12:00', '14:00', '16:00', '18:00', '20:00', '22:00']
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            data: this.arrangeData,
            type: 'bar',
            label: {
              show: true,
              position: 'top',
              valueAnimation: true
            }
          }
        ]
      };

      arrangeOption && arrangeChart.setOption(arrangeOption);

    },
    pushArrangeData(data){
      this.arrangeData = data;
      this.initArrangeCharts();
    }

  },
  beforeMount() {
  },
  mounted() {
    this.$bus.$on("genRandomArrange",this.pushArrangeData);
  }
}
</script>

<style scoped>

</style>