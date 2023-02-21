<template>
  <div id="root">
    <!--row 1-->
    <el-row :gutter="10">

      <el-col :span="10">
        <el-card class="box-card">
          <div v-for="o in 4" :key="o" class="text item">
            {{'列表内容 ' + o }}
          </div>
        </el-card>
      </el-col>

      <el-col :span="7">
        <el-card class="box-card">
          <div v-for="o in 4" :key="o" class="text item">
            {{'列表内容 ' + o }}
          </div>
        </el-card>

      </el-col>

      <el-col :span="7">
        <el-card class="box-card">
          <div v-for="o in 4" :key="o" class="text item">
            {{'列表内容 ' + o }}
          </div>
        </el-card>
      </el-col>

    </el-row>

    <br>

    <!-- row 2-->
    <el-row :gutter="10">

        <el-col :span="10">
          <el-card class="box-card">
            <div id="user-count" style="width: 100%;height: 500px"></div>
          </el-card>
        </el-col>

      <el-col :span="7">
        <el-card class="box-card">
          <div id="sex-count" style="width: 100%;height: 500px"></div>
        </el-card>
      </el-col>

      <el-col :span="7">
        <el-card class="box-card">
          <div id="month-count" style="width: 100%;height: 500px"></div>
        </el-card>
      </el-col>

    </el-row>

  </div>
</template>

<script>
import * as echarts from 'echarts';

export default {
  name: "UserEcharts",
  data(){
    return{
    }
  },
  mounted() {
    var userChartDom = document.getElementById('user-count');
    var userChart = echarts.init(userChartDom);
    var userCount;

    userCount = {
      toolbox: {
        show: true,
        feature: {
          magicType: { type: ['line', 'bar'] },
          saveAsImage: {}
        }
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          // Use axis to trigger tooltip
          type: 'line' // 'shadow' as default; can also be 'line' or 'shadow'
        }
      },
      legend: {},
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'value'
      },
      yAxis: {
        type: 'category',
        data: []
      },
      series: [
        {
          name: '',
          type: 'bar',
          stack: 'total',
          label: {
            show: true
          },
          emphasis: {
            focus: 'series'
          },
          data: []
        },
        {
          name: '',
          type: 'bar',
          stack: 'total',
          label: {
            show: true
          },
          emphasis: {
            focus: 'series'
          },
          data: []
        },
        {
          name: '',
          type: 'bar',
          stack: 'total',
          label: {
            show: true
          },
          emphasis: {
            focus: 'series'
          },
          data: []
        },
      ]
    };

    this.$axios.get(this.$httpUrl + "/user/getCountData")
        .then(res => res.data)
        .then(res => {
          userCount.yAxis.data = res.data["locName"];
          userCount.series[0].name = 'Leisure';
          userCount.series[0].data = res.data["leisure"];

          userCount.series[1].name = 'Work';
          userCount.series[1].data = res.data["work"];

          userCount.series[2].name = 'Rest';
          userCount.series[2].data = res.data["rest"];

        }).then(()=>{
          userCount && userChart.setOption(userCount);
    });

    //==================
    sexCount = {
      title: {
        text: '男女员工性别比例',
        left: 'center'
      },
      tooltip: {
        trigger: 'item'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '性别',
          type: 'pie',
          radius: '100%',
          data: [
            { value: 1048, name: 'Search Engine' },
            { value: 735, name: 'Direct' },
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          },label:{ // 属性值标签
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
        },

      ]
    };var sexChartDom = document.getElementById('sex-count');
    var sexChart = echarts.init(sexChartDom);
    var sexCount;

    sexCount = {
      title: {
        text: '男女比例',
        left: 'center'
      },
      tooltip: {
        trigger: 'item'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '性别',
          type: 'pie',
          radius: '50%',
          data: [
            { value: 1048, name: 'Search Engine' },
            { value: 735, name: 'Direct' },
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    };

    this.$axios.get(this.$httpUrl + "/user/getCountSexRate")
        .then(res => res.data)
        .then(res => {
          sexCount.series[0].data[0] = {name:"女",value:res.data.female},
          sexCount.series[0].data[1] = {name:"男",value:res.data.male}
        }).then(()=>{
          sexCount && sexChart.setOption(sexCount);
    });
    //==================

    var monthChartDom = document.getElementById('month-count');
    var monthChart = echarts.init(monthChartDom);
    var monthsData;

    monthsData = {
      toolbox: {
        show: true,
        feature: {
          magicType: { type: ['line', 'bar'] },
          saveAsImage: {}
        }
      },
      title: {
        text: '各月份员工入职人数',
        subtext: '2022-01-01 至 2023-02-28',
        left: 'center'
      },
      tooltip:{
        trigger:'item',
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: ['1', '2', '3', '4', '5', '6', '7','8','9','10','11','12']
      },
      legend:{
        orient: 'vertical',
        left:'left'
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          data: '',
          type: 'line',
          markPoint: {
            data: [
              { type: 'max', name: 'Max' },
              { type: 'min', name: 'Min' }
            ]
          },
          markLine: {
            data: [{ type: 'average', name: 'Avg' }]
          }
        }
      ]
    };
    this.$axios.get(this.$httpUrl + "/user/getMonthData")
        .then(res => res.data)
        .then(res => {
          monthsData.series[0].data = res.data;
        }).then(() => {
          monthsData && monthChart.setOption(monthsData);
    });


  }
}
</script>

<style scoped>
.chart-div > div{
  display: inline-block;
}
</style>