<template>
  <div id="root">
    <!--row 1-->
    <div style="display: flex;justify-content: space-between">

      <div style="display: inline-block;width: 866px">
          <el-card class="sl-no" style="height: 233px;">
            <div slot="header" style="text-align: center">
              <h1>各工作状态的人数</h1>
            </div>
          <div style="display: flex;justify-content: space-between;margin: -20px">

              <el-card class="box-card el-card-state">
                <div>
                  <el-tag type="success" effect="dark" class="el-tag-state"> 空闲 </el-tag>
                </div>
                <div style="text-align: center">
                  <span style="font-size: 2em;color: #67c23a"><i class="el-icon-user sl-no"></i> {{leisureNum}}</span>
                </div>
              </el-card>
              <el-card class="box-card el-card-state">
                <div>
                  <el-tag type="danger" effect="dark" class="el-tag-state">工作</el-tag>
                </div>
                <div style="text-align: center">
                  <span style="font-size: 2em;color: #f76c6c"><i class="el-icon-user sl-no"></i> {{workNum}}</span>
                </div>
              </el-card>
              <el-card class="box-card el-card-state" style="margin-right: 5px">
                <div>
                  <el-tag type="warning" effect="dark" class="el-tag-state">休息</el-tag>
                </div>
                <div style="text-align: center">
                  <span style="font-size: 2em;color: #e6a23c"><i class="el-icon-user sl-no"></i> {{restNum}}</span>
                </div>
              </el-card>

          </div>
          </el-card>
      </div>

      <div style="display: inline-block;width: 776px">
        <el-card class="box-card sl-no">
          <div slot="header" style="text-align: center;">
            <h1>入职人数</h1>
          </div>
          <el-carousel :interval="2000" type="card" height="100px" trigger="click">
            <el-carousel-item
                v-for="item of monthNum"
                :key="item.name"
                style="padding: 10px;text-align: center;border-radius: 5px;"
                :style="{backgroundColor:color[item.name]}">
              <h3 style="line-height: 80px;color: white">{{item.name + 1}}月  {{item.value}} 人 <i v-show="monthsMaxNum == item.value" class="el-icon-s-flag" style="color: red"></i></h3>
            </el-carousel-item>
          </el-carousel>
        </el-card>
      </div>

    </div>

    <!-- row 2-->
    <div style="display: flex;justify-content: space-between;margin-top: 13px">

      <div style="width: 866px;display: inline-block">
          <el-card class="box-card" >
            <div id="user-count" style="width:100%;height: 573px;"></div>
          </el-card>
      </div>

      <div style="width: 776px;display: inline-block;">
        <el-card class="box-card">
          <div id="month-count" style="width:100%;height: 573px"></div>
        </el-card>
      </div>
    </div>

  </div>
</template>

<script>
import * as echarts from 'echarts';

export default {
  name: "UserEcharts",
  data(){
    return{
      leisureNum:0,
      workNum:0,
      restNum:0,
      monthNum:Array(12).fill('$'),
      monthsMaxNum:0,
      color: [
        '#187a2f',//1
        '#718933',
        '#e1c315',
        '#dd4c51',

        '#0aa3b5',//5
        '#3ba272',
        '#e65832',
        '#da0168',

        '#9a60b4',//9
        '#da1d23',
        '#bb764c',
        '#794752'
      ],
      cardIndex:0,
    }
  },
  mounted() {

    // =========各部门人数=========
    var userChartDom = document.getElementById('user-count');
    var userChart = echarts.init(userChartDom);
    var userCount;

    userCount = {
      title: {
        text: '各运输中心的人数',
        left: 'center',
        top: 0,
        textStyle: {
        }
      },
      color: [
        '#67c23a',
        '#f76c6c',
        '#e6a23c',
      ],
      toolbox: {
        show: true,
        feature: {
          dataView: { show: true, readOnly: true },
          magicType: { type: ['line', 'bar'] },
          restore:{},
          saveAsImage: {}
        }
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'line',
        },
      },
      legend: {
        orient:"vertical",
        left: 'left'
      },
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
          name: 'Leisure',
          type: 'bar',
          stack: 'total',
          label: {
            show: true,
            textStyle:{
              fontWeight:300,
              fontSize:15,
              color:'white',
            },
          },
          emphasis: {
            focus: 'series'
          },
          data: []
        },
        {
          name: 'Work',
          type: 'bar',
          stack: 'total',
          label: {
            show: true,
            textStyle:{
              fontWeight:300,
              fontSize:15,
              color:'white',
            },
          },
          emphasis: {
            focus: 'series'
          },
          data: []
        },
        {
          name: 'Rest',
          type: 'bar',
          stack: 'total',
          label: {
            show: true,
            textStyle:{
              fontWeight:300,
              fontSize:15,
              color:'white',
            },
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
          userCount.series[0].name = '空闲';
          userCount.series[0].data = res.data["leisure"];

          userCount.series[1].name = '工作';
          userCount.series[1].data = res.data["work"];

          userCount.series[2].name = '休息';
          userCount.series[2].data = res.data["rest"];

          for(let i = 0;i < res.data["leisure"].length;i++){
            this.leisureNum += res.data["leisure"][i];
            this.workNum += res.data["work"][i];
            this.restNum += res.data["rest"][i];
          }

        }).then(()=>{
          userCount && userChart.setOption(userCount);
    });

    //========男女比例==========
    /*
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
    */
    //=========各月份入职人数=========

    var monthChartDom = document.getElementById('month-count');
    var monthChart = echarts.init(monthChartDom);
    var monthsData;

    /*折线图*/
    //monthsData = {
    //  toolbox: {
    //    show: true,
    //    feature: {
    //      magicType: { type: ['line', 'bar'] },
    //      saveAsImage: {}
    //    }
    //  },
    //  title: {
    //    text: '各月份员工入职人数',
    //    subtext: '2022-01-01 至 2023-02-28',
    //    left: 'center'
    //  },
    //  tooltip:{
    //    trigger:'item',
    //  },
    //  xAxis: {
    //    type: 'category',
    //    boundaryGap: false,
    //    data: ['1', '2', '3', '4', '5', '6', '7','8','9','10','11','12']
    //  },
    //  legend:{
    //    orient: 'vertical',
    //    left:'left'
    //  },
    //  yAxis: {
    //    type: 'value'
    //  },
    //  series: [
    //    {
    //      data: '',
    //      type: 'line',
    //      markPoint: {
    //        data: [
    //          { type: 'max', name: 'Max' },
    //          { type: 'min', name: 'Min' }
    //        ]
    //      },
    //      markLine: {
    //        data: [{ type: 'average', name: 'Avg' }]
    //      }
    //    }
    //  ]
    //};

    monthsData = {
      title: {
        text: '不同月份的入职人数',
        left: 'center',
        top: 0,
        textStyle: {
        }
      },
      color: this.color,
      tooltip: {
        trigger: 'item',
      },
      legend: {
        orient:"vertical",
        left: 'left'
      },
      toolbox: {
        show: true,
        feature: {
          dataView: { show: true, readOnly: true },
          restore:{},
          saveAsImage: {}
        }
      },
      series: [
        {
          name: '入职人数',
          type: 'pie',
          radius: [50, 250],
          center: ['57%', '55%'],
          roseType: 'area',
          itemStyle: {
            borderRadius: 5
          },
          data: [
          ],
        }
      ]
    };


    this.$axios.get(this.$httpUrl + "/user/getMonthData")
        .then(res => res.data)
        .then(res => {
          //monthsData.series[0].data = res.data;
          for(let i = 0;i < 12;i++){
            monthsData.series[0].data[i] = {value:res.data[i],name:`${i+1}月`}
            this.monthNum[i] = {value:res.data[i],name:i}
            if(res.data[i] > this.monthsMaxNum) this.monthsMaxNum = res.data[i];
          }
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

.el-tag-state{
  font-size: 1.2em;
  padding:0px 5px;
  margin-bottom: 15px;
}

.el-card-state{
  display: inline-block;
  width: 33%;
  height: 160px;
  margin: 5px 0px;
  margin-left: 5px;
}

.el-carousel__item:nth-child(1+13n) {
  background-color: red;
}
</style>