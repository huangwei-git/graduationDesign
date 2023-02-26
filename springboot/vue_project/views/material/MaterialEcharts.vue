<template>
  <div class="root">

      <div id="main" style="width: 500px;height: 400px"></div>


  </div>
</template>

<script>
import * as echarts from 'echarts';

export default {
  name: "MaterialEcharts",
  data(){
    return{
      piechartsData:'',
      dialogVisible: false,
    }
  },
  methods:{
    initEcharts(){
      var option;

      option = {
        title: {
          text: '物品库存占比',
          left: 'center'
        },
        tooltip: {
          trigger: 'item'
        },
        legend: {
          top: 'bottom'
        },
        series: [
          {
            name: '物品数量',
            type: 'pie',
            radius: '57%',
            data: this.piechartsData,
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            },
            label:{ // 属性值标签
              normal:{
                show:true,
                position:"outter",// 标签位置
                textStyle:{
                  fontWeight:300,
                  fontSize:13
                },
                formatter:'{d}%'
              }
            }
          }
        ]
      };

      var chartDom = document.getElementById('main');
      var myChart = echarts.init(chartDom);
      this.$axios.get(this.$httpUrl + "/material/piecharts")
          .then(res => res.data)
          .then(res => {
            if (res.code == 200) {
              option.series[0].data = res.data;
            } else {
              this.$message.error(res.msg);
            }
          }).then(()=>{
        myChart.setOption(option);
      })
    },
    open(){
      this.$nextTick(() => {
        //  执行echarts方法
        this.initEcharts()
      })
    }
  },
  beforeMount() {
  }
  ,mounted() {
    this.initEcharts()
  }
}
</script>

<style scoped>
  .text {
    font-size: 14px;
  }

  .item {
    padding: 18px 0;
  }

  .box-card {
    width: 480px;
  }
</style>