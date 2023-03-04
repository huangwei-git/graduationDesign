<template>
  <div class="root">
    <div id="peopleData" style="width: 500px;height: 320px;margin-top: 0px"></div>
    <div id="salaryData" style="width: 500px;height: 320px;margin-top: -20px"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts';
export default {
  name: "RandomDataCharts",
  data(){
    return{
      peopleData:[],
      salaryData:[],
      arrangeData:[]
    }
  },
  methods:{
    initRandomPeopleCharts(){
      var peopleDataChartDom = document.getElementById('peopleData');
      var peopleDataChart = echarts.init(peopleDataChartDom);
      var poepleOption;

      poepleOption = {
        title: {
          left: 'center',
          text: '各时间段人数'
        },
        tooltip:{
          trigger: 'axis',
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: ['00:00', '02:00', '04:00', '06:00', '08:00', '10:00', '12:00', '14:00', '16:00', '18:00', '20:00', '22:00']
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '随机人数数据',
            data: this.peopleData,
            type: 'line',
            smooth: true,
            areaStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [{
                  offset: 0,  color: '#15ef0e'  // 100% 处的颜色
                }, {
                  offset: 1, color: '#ffffff' //   0% 处的颜色
                }],
                global: false // 缺省为 false
              }
            },
            label: {
              show: true,
              position: 'top',
              valueAnimation: true
            }
          },
        ]
      };

      poepleOption && peopleDataChart.setOption(poepleOption);

    },
    initRandomSalaryCharts(){
      var salaryDataChartDom = document.getElementById('salaryData');
      var salaryDataChart = echarts.init(salaryDataChartDom);
      var salaryOption;

      salaryOption = {
        title: {
          left: 'center',
          text: '各时间段费用'
        },
        tooltip:{
          trigger: 'axis',
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: ['00:00', '02:00', '04:00', '06:00', '08:00', '10:00', '12:00', '14:00', '16:00', '18:00', '20:00', '22:00']
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '随机费用数据',
            data: this.salaryData,
            type: 'line',
            smooth: true,
            areaStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [{
                  offset: 0,  color: '#ffff00'  // 100% 处的颜色
                }, {
                  offset: 1, color: '#ffffff' //   0% 处的颜色
                }],
                global: false // 缺省为 false
              }
            },
            label: {
              show: true,
              position: 'top',
              valueAnimation: true
            }
          }
        ]
      };

      salaryOption && salaryDataChart.setOption(salaryOption);

    },
    pushPeopleData(data){
      this.peopleData = data;
      this.initRandomPeopleCharts();
    },
    pushSalaryData(data){
      this.salaryData = data;
      //this.initRandomPeopleCharts();
      this.initRandomSalaryCharts();
    },

  },
  beforeMount() {
  },
  mounted() {
    this.$bus.$on("genRandomPeople",this.pushPeopleData);
    this.$bus.$on("genRandomSalary",this.pushSalaryData);
  }
}
</script>

<style scoped>

</style>