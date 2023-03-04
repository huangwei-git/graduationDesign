<template>
  <div id="root">
    <!--=========顶部按钮组============-->
        <el-button icon="el-icon-document-add" type="success" @click="createDialogVisible = true" >创建订单</el-button>
        <el-button icon="el-icon-view" type="success" @click="displayDialogVisible = true" style="margin-left: 5px">查看方案</el-button>
    <!--==========查看方案(运输问题)===========-->
        <!--width="1200"-->
    <el-dialog
        title="查看方案"
        :visible.sync="displayDialogVisible"
        :fullscreen="true"
        style="padding: 0px 0px;text-align: center"
        :before-close="handleClose">
      <div id="autoDistributeDiv" v-show="caseaderValue[0] == 0">
        <el-empty v-show="tableData == ''" description="还未创建订单">
          <el-button @click="createDialogVisible = true;displayDialogVisible = false;" type="info">创建订单</el-button>
        </el-empty>


        <el-table v-for="(tbdata,index) in tableData" :data="tbdata"
                  :row-class-name="tableRowClassName"
                  :cell-style="{ textAlign: 'center' }"
                  :header-cell-style="{ textAlign: 'center',background:'#f5f7fa',color:'#950842' }">

          <el-table-column :label="tableHeader[index]">
            <el-table-column v-for="item in fieldMap" :prop="item.prop" :label="item.label"></el-table-column>
          </el-table-column>

        </el-table>

        <div v-show="tableData != ''" style="margin-top: 20px;">
          <el-button type="primary" @click="createDialogVisible = true;displayDialogVisible = false;isFormChanged=false">上一步</el-button>
          <el-button type="danger" @click="createOrder">创建订单</el-button>
        </div>
      </div>

      <!--==========TSP=========-->
      <div id="autoDistributeDiv" v-show="caseaderValue[0] == 1">
        <div style="display: inline-block">
          <TSGraph/>
        </div>
        <div style="display: inline-block">
          <ACOGraph/>
        </div>
        <div style="display: inline-block">
          <GAGraph/>
        </div>
        <div style="display: inline-block">
          <IPGraph/>
        </div>

        <div STYLE="margin-top: 20px">
          <el-select v-model="selectTspMethod">
            <el-option value="TS" label="禁忌搜索"></el-option>
            <el-option value="ACO" label="蚁群算法"></el-option>
            <el-option value="GA" label="遗传算法"></el-option>
            <el-option value="IP" label="整数规划"></el-option>
          </el-select>

          <el-button type="primary" @click="createDialogVisible = true;displayDialogVisible = false;isFormChanged=false" style="margin-left: 10px">上一步</el-button>
          <el-button type="danger" @click="createOrder">创建订单</el-button>

        </div>
      </div>
    </el-dialog>

    <!--=========创建订单================-->
    <el-dialog
        title="创建订单"
        :visible.sync="createDialogVisible"
        width="1100px"
        :before-close="handleClose">

      <div style="margin:0 -20px;margin-top: -15px">
      <div class="sl-no" style="text-align: center;margin-bottom: 10px;display: flex;justify-content: space-around">
        <el-cascader :options="options" v-model="caseaderValue" @change="showCaseaderValue" :show-all-levels="false"></el-cascader>

        <el-select v-model="selectedMaterial" value-key="name" placeholder="请选择物品" @change="initSelect">
          <el-option
              v-for="item in storeInfo"
              :key="item.mid"
              :label="item.name"
              :value="{name:item.name,count:item.count,mid:item.mid}">
            <span style="float: left">{{ item.name }}</span>
            <span style="float: right; color: #8492a6; font-size: 13px">{{ item.count }}</span>
          </el-option>
        </el-select>

        <div style="display: inline-block">
          <span style="height:32px;line-height: 32px">剩余可分配数量：</span>
          <el-tag :type="restValue > 0?'warning':'danger'" effect="dark" style="width: 50px;text-align: center">
            {{restValue}}
          </el-tag>
        </div>

        <div style="display: inline-block">
          <span style="height:32px;line-height: 32px">已分配数量：</span>
          <el-tag type="success" effect="dark" style="width: 50px;text-align: center">
            {{isNaN(selectedMaterial.count - restValue)?0:selectedMaterial.count - restValue}}
          </el-tag>
        </div>
      </div>

      </div>

      <div  style="margin:30px -20px;margin-bottom: -30px">
      <div style="width: 100%;display: inline-block;text-align: center">
        <el-form label-width="80px" :inline="true" :model="formData" style="width: 100%;display: inline-block" label-position="left">
          <el-form-item v-for="(item,index) in demandPointsData" :label="item.name" style="width: 30%;display: inline-block">

            <el-input-number
                style=""
                @click.native="mutexLocker(index)"
                :disabled="(selectedMaterial == '' || mutex != index)"
                v-model="formData.num[index]"
                :min="0"
                :step="5"
                :max="formData.max[index]"
                label="描述文字">
            </el-input-number>

          </el-form-item>
          <el-form-item>
            <el-button type="primary" :disabled="selectedMaterial == '' || restValue == selectedMaterial.count" @click="getSolution">下一步</el-button>
            <el-button type="warning" :disabled="selectedMaterial == ''" class="test" @click="initSelect">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      </div>
    </el-dialog>
    <!--=========================-->

  </div>
</template>

<script>
import * as echarts from 'echarts';
import TSGraph from "./GraphEcharts/TSGraph";
import GAGraph from "./GraphEcharts/GAGraph";
import IPGraph from "./GraphEcharts/IPGraph";
import ACOGraph from "./GraphEcharts/ACOGraph";

export default {
  name: "AddOrder",
  methods:{
    dataFormat(res){
      if(res.code == 200){
        this.allCountInfo = res.data;
      }else{
        alert(res.msg);
      }
    },
    loadPost(){
      this.$axios.get(this.$httpUrl+'/material/getAllMaterialCount',)
          .then(res => res.data)
          .then(res => {
            this.dataFormat(res);
          }).then(()=>{
            this.restValue = 0;
            this.selectedMaterial = '';
      })
    },
    loadLocation(){
      this.$axios.get(this.$httpUrl + "/location")
          .then(res => res.data)
          .then(res => {
            let cnt = 0;
            for(let i = 0;i < res.total;i++){
              // 级联表单
              let childOp = {value:res.data[i].locId,label:res.data[i].name};
              // 用于生成方案时，获取地址名映射
              let item = {
                'name':res.data[i].name,
                'x':res.data[i].xpos,
                'y':res.data[i].ypos,
                locId:res.data[i].locId,
              };
              if(res.data[i].type == 0){
                this.transportPointsData.push(item);
                this.options[1].children.push(childOp);
              }
              else{
                this.demandPointsData.push(item);
              }
            }
            this.totalDemands = this.demandPointsData.length;
          })
    },
    loadSelectedStore(){
      this.$axios.get()
    },
    getSolution(){
      if(this.caseaderValue[0] == 0) this.autoDistributionSolution();
      else this.appointDistributionSolution();
    },
    // 自动分配时，表单提交调用此方法
    autoDistributionSolution() {
      // 如果表单的值未改变，则不计算
      if(!this.isFormChanged){
        this.displayDialogVisible = true;
        this.createDialogVisible = false;
        return;
      }
      let sum = 0;
      let need = [];
      let locId = [];
      for (let i = 0; i < this.formData.num.length; i++) {
        sum += this.formData.num[i];
        need[i] = this.formData.num[i];
        locId[i] = this.demandPointsData[i].locId;
      }
      if(this.selectedMaterial.count - sum < 0){
        this.$message.error("超出库存数量！");
      }else{
        this.$axios.post(this.$httpUrl + "/order/compute",{need:need,locId:locId,mid:this.selectedMaterial.mid})
            .then(res => res.data)
            .then(res => {
              this.fieldMap = res.data.fieldMap;
              this.tableData = res.data.tableDatas;
              this.tableHeader[0] = "初始分配方案";
              this.rowTotal = res.total;
              for(let i = 1;i < this.tableData.length - 1;i++){
                this.tableHeader[i] = "第"+i+"此调整后的分配方案";
              }
              this.tableHeader[this.tableData.length - 1] = "最终分配方案";
              this.orderKey = res.data.key;
            }).then(()=>{
              this.displayDialogVisible = true;
              this.createDialogVisible = false;
        })
      }
    },
    // 指定分配时，表单提交调用此方法
    appointDistributionSolution(){
      // 如果表单的值未改变，则不计算
      if(!this.isFormChanged){
        this.displayDialogVisible = true;
        this.createDialogVisible = false;
        return;
      }
      let sum = 0;
      let need = [];
      let locId = [];
      for (let i = 0; i < this.formData.num.length; i++) {
        sum += this.formData.num[i];
        if(this.formData.num[i] != 0){
          need.push({"locId":this.demandPointsData[i].locId, "amount":this.formData.num[i]});
        }
      }
      if(this.selectedMaterial.count - sum < 0){
        this.$message.error("超出库存数量！");
      }else{
        console.log(need);
        this.$axios.post(this.$httpUrl + "/order/tsp",{need:need,locId:this.caseaderValue[1],mid:this.selectedMaterial.mid})
            .then(res => res.data)
            .then(res => {
              console.log("tsp result: ",res);
              this.pathPoints = res.data.points;
              // 遗传算法
              this.GAtsp.min = res.data.GAmin.toFixed(3);
              this.GAtsp.path = res.data.GApath;
              // 蚁群算法
              this.ACOtsp.min = res.data.ACOmin.toFixed(3);
              this.ACOtsp.path = res.data.ACOpath;
              // 禁忌搜索算法
              this.TStsp.min = res.data.TSmin.toFixed(3);
              this.TStsp.path = res.data.TSpath;
              // 数学规划
              this.IPtsp.min = res.data.IPmin.toFixed(3);
              this.IPtsp.path = res.data.IPpath;


              setTimeout(()=>{
                this.$bus.$emit("selectTS",{min:this.TStsp.min, points:this.pathPoints, links:this.TStsp.path});
                this.$bus.$emit("selectGA",{min:this.GAtsp.min, points:this.pathPoints, links:this.GAtsp.path});
                this.$bus.$emit("selectACO",{min:this.ACOtsp.min, points:this.pathPoints, links:this.ACOtsp.path});
                this.$bus.$emit("selectIP",{min:this.IPtsp.min, points:this.pathPoints, links:this.IPtsp.path});
              },500);
            }).then(()=>{
          this.displayDialogVisible = true;
          this.createDialogVisible = false;
        })
      }
    },
    createOrder(){
      this.$axios.post(this.$httpUrl + "/order/genOrderFromBuffer",{state:1,key:this.orderKey,mid:this.selectedMaterial.mid,amount:this.selectedMaterial.count})
          .then(res => res.data)
          .then(res => {
                if(res.code == 200){
                  this.$notify({
                    title:'订单创建成功',
                    message:'订单号：'+res.data.id,
                    type:'success'
                  })
                }else{
                  this.$notify.error("订单创建失败")
                }//else
          }).then(()=>{
            this.displayDialogVisible = false;
            this.initSelect();
      }).then(()=>{
          this.$bus.$emit('submitOrder',"成功创建！");
      })
    },
    initSelect(){
      this.restValue = this.selectedMaterial.count;
      for (let i = 0;i < this.demandPointsData.length;i++){
        this.formData.num[i] = this.formData.max[i] = 0;
        this.mutex = 0;
      }
      this.mutex = -1;
    },
    tableRowClassName({row, rowIndex}) {
      if (rowIndex == this.rowTotal) {
        return 'warning-row';
      } else if (rowIndex === 3) {
        return 'success-row';
      }
      return '';
    },
    mutexLocker(index){
      if(this.selectedMaterial == '' || this.mutex == index) return ;
      this.restValue -= (this.newValue - this.oldValue);
      this.formData.max[index] = this.formData.num[index] + this.restValue;
      this.currentIndex = index;
      this.oldValue = this.formData.num[index];
      this.newValue = this.formData.num[index];
      this.mutex = index;
    },
    reloadPost(msg){
      this.loadPost();
    },
    handleClose(done) {
      if(this.tableData == '' && this.displayDialogVisible) return done();
      else if(this.selectedMaterial == '') return done();
      this.$confirm('确认关闭？')
          .then(_ => {
            done();
          })
          .catch(_ => {});
    },
    showCaseaderValue(){
      this.selectedMaterial = '';
      this.initSelect();
      this.restValue = 0;
      if(this.caseaderValue[0] == '0') this.storeInfo = this.allCountInfo;
      else{
        this.$axios.post(this.$httpUrl + "/material/getSelectMaterialCount",{locId:this.caseaderValue[1]})
            .then(res=>res.data)
            .then(res=>{
              this.storeInfo = res.data;
            })
      }
    },
  },
  data(){
    return {
      fieldMap:[],
      tableData: [],
      tableHeader:[],
      rowTotal:0,
      orderKey:'',
      transportPointsData:[],
      demandPointsData:[],
      formData:{num:[],max:[]},
      isFormChanged:false,
      materialInfo:'',
      totalDemands:'',
      pathPoints:[],
      GAtsp:{min:-1,path:[]},
      ACOtsp:{min:-1,path:[]},
      TStsp:{min:-1,path:[]},
      IPtsp:{min:-1,path:[]},
      selectTspMethod:"TS", // 选择使用哪种TSP算法
      checkedTS:false,
      checkedACO:false,
      checkedGA:false,
      checkedIP:false,
      options:[
        {value:0,label: "自动分配"},
        {value:1,label: "单点配送",children:[]}
      ],//级联表单数据
      caseaderValue:[0,0],//级联表单绑定数据
      allCountInfo:'',
      storeInfo:'',//库存信息
      selectedMaterial:'',//选择的材料
      mutex:-1,
      currentIndex:0,
      oldValue:0,
      newValue:0,
      restValue:0,
      createDialogVisible:false,
      displayDialogVisible:false,
      // 计算选择的点的距离
    }
  },
  watch:{
    'formData.num':{
      handler(newVal,oldVal){
        this.newValue = newVal[this.currentIndex];
        this.isFormChanged = true;
      }
    },
    createDialogVisible:{
      handler(newVal,oldVal){
        if(newVal == false && this.displayDialogVisible == false){
          this.selectedMaterial = '';
          this.initSelect();
        }
      }
    },
    checkedTS:{
      handler(n,o){
        if(n) this.$bus.$emit("selectTS",{min:this.TStsp.min, points:this.pathPoints, links:this.TStsp.path});
      }
    },
    checkedACO:{
      handler(n,o){
        if(n) this.$bus.$emit("selectACO",{min:this.ACOtsp.min, points:this.pathPoints, links:this.ACOtsp.path});
      }
    },
    checkedGA:{
      handler(n,o){
        if(n) this.$bus.$emit("selectGA",{min:this.GAtsp.min, points:this.pathPoints, links:this.GAtsp.path});
      }
    },
    checkedIP:{
      handler(n,o){
        if(n) this.$bus.$emit("selectIP",{min:this.IPtsp.min, points:this.pathPoints, links:this.IPtsp.path});
      }
    },
  },
  components:{
    TSGraph,
    GAGraph,
    IPGraph,
    ACOGraph
  },
  mounted() {
    this.$bus.$on("orderListLoaded",this.reloadPost);
  },
  beforeMount() {
    this.loadLocation();
    this.loadPost();
  },
}
</script>

<style scoped>

</style>