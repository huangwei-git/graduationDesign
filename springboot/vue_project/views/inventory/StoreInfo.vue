<template>
  <div id="root">
    <!-- 搜索栏 -->
    <div style="margin-bottom: 10px;" class="sl-no">

      <div class="search-div">
        <el-input placeholder="输入仓库名称"
                  v-model="searchInventoryName"
                  style="width: 200px;">
        </el-input>
        <el-input placeholder="输入物品名称"
                  v-model="searchMaterialName"
                  style="width: 200px;margin-left: 5px;">
        </el-input>


        <el-button-group style="margin-left: 5px">
          <el-button icon="el-icon-search" type="primary" @click="search">搜索</el-button>
          <el-button icon="el-icon-refresh" type="primary" @click="resetSearch" style="margin-left: 5px">重置</el-button>
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
          style="margin-top: 10px;width: 681px;"
          @row-click="recordRowInfo"
      >
        <el-table-column type="index" width="60%">
          <template slot="header" slot-scope="scope">序号</template>
        </el-table-column>

        <el-table-column prop="locId" v-if="false" label="lid" width="0"></el-table-column>
        <el-table-column prop="storeId" v-if="false" label="lid" width="0"></el-table-column>
        <el-table-column prop="materialId" v-if="false" label="mid" width="0"></el-table-column>
        <el-table-column prop="inventoryName"  label="仓库名称" width="170px"></el-table-column>
        <el-table-column prop="materialName" label="物品名称" width="150px"></el-table-column>
        <el-table-column prop="amount" label="物品数量" width="150px">
          <template slot-scope="scope">
            <el-tag style="width: 60px" :type="scope.row.amount == 0?'danger':'primary'">
              {{scope.row.amount}}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="operate" width="150px" align="right">
          <template slot="header" slot-scope="scope">操作</template>
          <template slot-scope="scope">
            <el-button icon="el-icon-plus" @click="editTable(scope.row)" size="small" style="margin-left: 5px">增加</el-button>
          </template>
        </el-table-column>
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
          :page-sizes="[5,10,15,20]"
          layout="total,sizes,prev, pager, next, jumper"
          :total="pageData.totalPage"
          :hide-on-single-page="false">
      </el-pagination>
    </div>


    <!-- 对话框 增、改信息 -->
    <el-dialog
        title="增加物品"
        :visible.sync="centerDialogVisible"
        width="18%"
        center>
      <div style="position: relative;top: -5px;left:48px;font-size: 11px;color: #ccc">
        当前商品数量：{{form.delta + clickedRow.amount}}
      </div>
      <el-form :model="form" ref="addForm"
               label-width="0px"
               class="demo-ruleForm"
              style="text-align: center">
        <el-form-item prop="delta">
          <el-input-number size="medium" v-model="form.delta" :min="1" :step="5"></el-input-number>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="updateForm">提交</el-button>
        </el-form-item>

      </el-form>
    </el-dialog>

  </div>
</template>

<script>

export default {
  name: "StoreInfo",
  methods:{
    //====Axios====
    dataFormat(res){
      if(res.code == 200){
        this.tableData = res.data;
      }else{
        alert(res.msg);
      }
      this.$nextTick(()=>{
        this.loading = false;
      })
    },
    loadPost(){
      this.$axios.post(this.$httpUrl+'/inventory/storeInfo',this.pageData)
          .then(res => res.data)
          .then(res => {
            this.loading = true;
            this.pageData.totalPage = res.total;
            this.dataFormat(res);
          })
    },
    search(){
      this.pageData.params.inventoryName = this.searchInventoryName;
      this.pageData.params.materialName = this.searchMaterialName;
      this.loadPost();
    },
    resetSearch(){
      this.pageData.params.inventoryName = '';
      this.pageData.params.materialName = '';

      this.searchInventoryName = '';
      this.searchMaterialName = '';

      this.loadPost();
    },
    //====排序====
    upSortTable(){
      this.pageData.params.sortField = this.sortField;
      this.pageData.params.sortDirection = '0';
      this.loadPost();
    },
    downSortTable(){
      this.pageData.params.sortField = this.sortField;
      this.pageData.params.sortDirection = '1';
      this.loadPost();
    },
    //====添加 修改 删除====
    // 点击 新增
    addTable(){
      this.centerDialogVisible = true;
      this.addBtnText = '提交';
      this.isClickEditBtn = false;
    },
    // 点击 编辑
    editTable(row) {
      this.addBtnText = '修改';
      this.isClickEditBtn = true;
      this.form.locSendId = row.locId;
      this.form.materialId = row.materialId;
      this.form.storeId = row.storeId;
      this.form.amount = row.amount;
      console.log("test",this.form);
      this.centerDialogVisible = true;
    },
    // 新增用户
    saveForm(){
      this.$confirm('是否确认提交?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(()=>{
        this.$axios.post(this.$httpUrl+'/inventory',this.form)
            .then(res => res.data)
            .then(res => {
              if (res.code == 200) {
                this.$notify({
                  title: '成功',
                  message: '增加成功',
                  type: 'success'
                });
              } else {
                console.log(res.code)
                if(res.code == 401){
                  this.$notify.error({
                    title:"错误",
                    message:res.msg
                  })
                }else{
                  this.$notify.error({
                    title: '错误',
                    message: '增加失败'
                  });
                }
              }
            })
      }).then(() => {
        this.loadPost();
        this.centerDialogVisible = false;
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });

    },
    // 修改用户
    updateForm(){
      this.form.amount = this.clickedRow.amount + this.form.delta;
      this.$confirm('是否确认提交?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(()=>{
        this.$axios.put(this.$httpUrl+'/inventory',this.form)
            .then(res => res.data)
            .then(res => {
              if(res.code == 200){
                this.$notify({
                  title: '成功',
                  message: '增加成功',
                  type: 'success'
                });
                setTimeout(()=>{
                  this.loadPost();
                },500);
              }else{
                this.$notify.error({
                  title: '错误',
                  message: '增加失败'
                });
              }
            })
      }).then(() => {
        this.centerDialogVisible = false;
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },
    // 删除用户
    deleteLocation(){
      this.$confirm(`是否要将${this.clickedRow.name}的信息从表中移除？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$axios.delete(this.$httpUrl+`/inventory/${this.clickedRow.locId}`)
            .then(res => res.data)
            .then(res => {
              if(res.code == 200){
                this.$notify.success({
                  title:"成功！",
                  message:`员工"${this.clickedRow.name}"已被删除`,
                })
              }else {
                this.$notify.error({
                  title:"失败！",
                  message:"服务器忙，请稍后重试",
                })
              }
            })
      }).then(() => {
        this.loadPost();
      }).catch(() => {
        this.$notify.info({
          type: 'info',
          message: '已取消删除',
        });
      });
    },
    resetForm(){
      this.form.locSendId = '';
      this.form.materialId = '';
      this.form.storeId = '';
      this.form.amount = '';
      this.form.delta = 5;
    },
    saveOrUpdate(){
      if(this.form.locSendId == '') this.saveForm();
      else this.updateForm();
    },
    //====更新价格限制====
    updateyposMin(){
      this.minypos = this.form.xpos + 1;
      if(this.form.ypos <= this.minypos){
        this.form.ypos = this.minypos;
      }
    },
    //====搜索栏字数限制====
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
    // 保存被点击的行信息
    recordRowInfo(row){
      this.clickedRow = row;
    },
  },
  data(){
    return {
      tableData:[],
      input:'',
      searchInventoryName:'',
      searchMaterialName:'',
      addMaterial:1,
      loading: true,
      clickedRowInfo:null,
      pageData:{
        pageNum:1,
        totalPage:22,
        pageSize:5,
        params:{
          inventoryName:'',
          materialName:'',
          amount:'',
        },
      },
      centerDialogVisible:false,
      isClickEditBtn:false,
      addBtnText:'提交',
      sortField:'',//排序字段
      sortDirection:'0',// 0升序，1降序
      clickedRow:'',// 获取被点击的行信息
      form:{
        locSendId:'',
        materialId:'',
        storeId:'',
        amount:'',
        delta:5
      },
      defaultLocation:'',
      rules: {
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' },
          { min: 1, max: 8, message: '长度为 1 到 8 ', trigger: 'blur' },
          { pattern:/^\S*$/, message: '不能包含空格', trigger: 'blur' },
        ],
      },
    }
  },
  beforeMount() {
    this.$nextTick(()=>{
      this.loadPost();
    })
  },
  watch:{
    centerDialogVisible:{
      handler(nval,oval){
        if(nval == false)
          this.resetForm();
      }
    },

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

.search-div > .el-form{
  height: 40px;
}

</style>