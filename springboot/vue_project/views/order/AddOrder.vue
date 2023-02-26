<template>
  <div id="root">

    <!--=========顶部按钮组============-->
        <el-button icon="el-icon-document-add" type="success" @click="createDialogVisible = true" >创建订单</el-button>
        <el-button icon="el-icon-view" type="success" @click="displayDialogVisible = true" style="margin-left: 5px">查看方案</el-button>
    <!--==========查看方案===========-->
    <el-dialog
        title="查看方案"
        :visible.sync="displayDialogVisible"
        width="0 auto"
        style="padding: 10px 20px;text-align: center"
        :before-close="handleClose">
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

    </el-dialog>
    <!--=========创建订单================-->
    <el-dialog
        title="创建订单"
        :visible.sync="createDialogVisible"
        width="400px"
        style="padding: 10px 20px"
        :before-close="handleClose">

      <el-card style="margin:0px -20px;margin-top: -30px">
      <div class="sl-no" style="text-align: center;margin-bottom: 10px">
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
      </div>

      <div style="text-align: center;padding-bottom: 10px;">
        <el-row style="margin-bottom: 10px">
          <el-col :span="15" style="text-align: right;height:32px;line-height: 32px">
            <span>剩余可分配数量：</span>
          </el-col>
          <el-col :span="9" style="text-align: left">
            <el-tag :type="restValue > 0?'warning':'danger'" effect="dark" style="width: 50px;text-align: center">
              {{restValue}}
            </el-tag>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="15" style="text-align: right;height:32px;line-height: 32px">
            <span>已分配数量：</span>
          </el-col>
          <el-col :span="9" style="text-align: left">
            <el-tag type="success" effect="dark" style="width: 50px;text-align: center">
              {{isNaN(selectedMaterial.count - restValue)?0:selectedMaterial.count - restValue}}
            </el-tag>
          </el-col>
        </el-row>
      </div>
      </el-card>

      <el-card  style="margin:0px -20px;margin-bottom: -30px">
      <div style="width: 100%">
        <el-form label-width="100px" :model="formData" style="width: 100%;display: inline-block" label-position="left">
          <el-form-item v-for="(item,index) in demandPointsData" :label="item.name">

            <el-input-number
                style="width: 90%"
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
      </el-card>
    </el-dialog>
    <!--=========================-->

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
          })
    },
    getSolution() {
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
        this.$axios.post(this.$httpUrl + "/order/comptude",{need:need,locId:locId,mid:this.selectedMaterial.mid})
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
      console.log(msg);
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
      storeInfo:'',
      selectedMaterial:'',
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