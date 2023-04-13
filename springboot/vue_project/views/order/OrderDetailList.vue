<template>
  <div id="root">
    <!-- 搜索栏 -->
    <div style="margin-bottom: 10px;" class="sl-no">

      <div>
        <el-input placeholder="请输入订单编号" v-model="searchOrderId" class="input-with-select"></el-input>
        <el-input placeholder="请输入物品名称" v-model="searchMaterialName" class="input-with-select"></el-input>
        <el-input placeholder="请输入运输地" v-model="searchSupplierName" class="input-with-select"></el-input>
        <el-input placeholder="请输入需求地" v-model="searchDemanderName" class="input-with-select"></el-input>


        <el-button-group style="margin-left: 5px">
          <el-button icon="el-icon-search" type="primary" @click="search">搜索</el-button>
          <el-button icon="el-icon-refresh" type="primary" @click="resetSearch" style="margin-left: 5px">重置</el-button>
        </el-button-group>
      </div>
    </div>

    <div >
        <el-button type="warning" icon="el-icon-download" @click="exportData" style="margin-left: 5px">导出数据</el-button>

      <div class="sl-no" style="display: inline-block;float: right">
        <el-select v-model="sortField" placeholder="排序" style="margin-left: 5px;width: 150px">
          <el-option label="订单编号" value="orderId"></el-option>
          <el-option label="物品名称" value="materialName"></el-option>
          <el-option label="数量" value="amount"></el-option>
          <el-option label="运输地" value="supplierName"></el-option>
          <el-option label="需求地" value="demanderName"></el-option>
          <el-option label="运费" value="toll"></el-option>
          <el-option label="总价" value="total"></el-option>
        </el-select>
        <el-button-group style="margin-left: 5px">
          <el-button autofocus icon="el-icon-sort-up" @click="upSortTable">升序</el-button>
          <el-button icon="el-icon-sort-down" style="margin-left: 5px" @click="downSortTable">降序</el-button>
        </el-button-group>
      </div>
    </div>

    <!--暂时隐藏-->
    <div class="empty" style="display: flex;text-align:center;justify-content: center;width: 100%">
      <el-empty description="暂无数据" v-show="(tableData.length==0 && !loading)"></el-empty>
    </div>
    <div class="tab_div" align="center">
      <el-table
          v-show="(tableData.length > 0 || loading)"
          ref="mutiTable"
          v-loading="loading"
          :data="tableData"
          stripe
          border
          :cell-style="{ textAlign: 'center' }"
          :header-cell-style="{ textAlign: 'center',background:'#f5f7fa',color:'#950842' }"
          style="margin-top: 10px;"
          @row-click="recordRowInfo"
      >

        <el-table-column type="index" width="60%">
          <template slot="header" slot-scope="scope">序号</template>
        </el-table-column>

        <el-table-column prop="orderId"  label="订单编号" width="200%"></el-table-column>
        <el-table-column prop="materialName"  label="物品名称"></el-table-column>
        <el-table-column prop="amount"  label="数量" width="90%"></el-table-column>
        <el-table-column prop="seq"  label="运输顺序" width="80%"></el-table-column>
        <el-table-column prop="supplierName"  label="发货地"></el-table-column>
        <el-table-column prop="demanderName"  label="收货地"></el-table-column>
        <el-table-column prop="cost"  label="物品成本"></el-table-column>
        <el-table-column prop="price"  label="物品价格"></el-table-column>
        <el-table-column prop="toll"  label="运费"></el-table-column>
        <el-table-column prop="profit"  label="利润"></el-table-column>
        <el-table-column prop="total"  label="总价"></el-table-column>

      </el-table>
    </div>


    <!-- 分页 -->
    <div style="padding: 10px 0;text-align: center">
      <el-pagination
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page.sync="pageData.pageNum"
          :page-size="pageData.pageSize"
          :page-sizes="[10,20,30,50,100]"
          layout="total,sizes,prev, pager, next, jumper"
          :total="pageData.totalPage"
          :hide-on-single-page="false">
      </el-pagination>
    </div>

  </div>
</template>

<script>


export default {
  name: "OrderDtailList",
  methods:{
    //====Axios====
    dataFormat(res){
      if(res.code == 200){
        this.tableData = res.data;
      }else{
        console.log(res.msg)
        alert("获取数据失败");
      }
      this.$nextTick(()=>{
        this.loading = false;
      })
    },
    loadPost(){
      this.$axios.post(this.$httpUrl+'/orderDetail/listPage',this.pageData)
          .then(res => res.data)
          .then(res => {
            this.loading = true;
            this.pageData.totalPage = res.total;
            this.dataFormat(res);
          })
    },
    search(){
      this.pageData.params.orderId = this.searchOrderId;
      this.pageData.params.materialName = this.searchMaterialName;
      this.pageData.params.supplierName = this.searchSupplierName;
      this.pageData.params.demanderName = this.searchDemanderName;
      this.loadPost();
    },
    resetSearch(){
      this.pageData.params.orderId = '';
      this.pageData.params.materialName = '';
      this.pageData.params.supplierName = '';
      this.pageData.params.demanderName = '';

      this.searchOrderId = '';
      this.searchMaterialName = '';
      this.searchSupplierName = '';
      this.searchDemanderName = '';

      this.loadPost();
    },
    //====排序====
    upSortTable(){
      this.pageData.params.sortField = this.sortField;
      this.pageData.params.sortDirection = 'ASC';
      this.loadPost();
    },
    downSortTable(){
      this.pageData.params.sortField = this.sortField;
      this.pageData.params.sortDirection = 'DESC';
      this.loadPost();
    },
    // 接收订单页面传来的订单号
    receiveOrderId(orderId){
      this.pageData.params.orderId = orderId;
    },
    //====分页查询====
    handleSizeChange(val) {
      this.pageData.pageSize = val;
      this.pageData.pageNum = 1;
      this.loadPost();
    },
    handleCurrentChange(val) {
      this.pageData.pageNum = val;
      this.loadPost();
    },
    // 导出数据
    exportData(){
      window.open(this.$httpUrl + "/orderDetail/export");
    },
    // 保存被点击的行信息
    recordRowInfo(row){
      this.clickedRow = row;
    },
  },
  data(){
    return {
      tableData:[],
      searchOrderId:'',
      searchMaterialName:'',
      searchSupplierName:'',
      searchDemanderName:'',
      loading: true,
      clickedRowInfo:null,
      pageData:{
        pageNum:1,
        totalPage:22,
        pageSize:10,
        params:{
          orderId:'',
          materialName:'',
          supplierName:'',
          demanderName:'',
          sortField:'orderId',
          sortDirection:'ASC',
        },
      },
      sortField:'orderId',//排序字段
      sortDirection:'ASC',// 0升序，1降序
      clickedRow:'',// 获取被点击的行信息
      // 对话框
    }
  },
  beforeMount() {
      let getOrderId = sessionStorage.getItem("orderId");
      if(getOrderId != null){
        this.searchOrderId = JSON.parse(sessionStorage.getItem("orderId"));
        this.search();
      }else{
        this.loadPost();
      }
  },
  mounted() {
    this.$bus.$on("clickOrderId",this.receiveOrderId)
    this.loadPost();
  }
}
</script>

<style scoped>
.el-menu{
  min-height: 100%;
  overflow-x: hidden;
}

.el-select{
  width: 90px;
}

.input-with-select,.el-input-group__prepend {
  width: 300px;
  margin-left: 5px;
  background-color: #fff;
}

</style>