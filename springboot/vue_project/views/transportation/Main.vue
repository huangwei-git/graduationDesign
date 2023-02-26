<template>
  <div id="root">
    <!-- 搜索栏 -->
    <div style="margin-bottom: 10px;">

      <el-input
          :clearable="true"
          place class="sl-no"
          v-model="searchName"
          suffix-icon="el-icon-search"
          style="margin-left:  5px;width: 180px"
          placeholder="请输入">
      </el-input>

      <el-button-group style="margin-left: 5px">
        <el-button icon="el-icon-search" type="primary" @click="search">搜索</el-button>
        <el-button icon="el-icon-refresh" type="primary" @click="resetSearch" style="margin-left: 5px">重置</el-button>
      </el-button-group>
    </div>

    <div >
      <el-button-group>
        <el-button style="background-color:e1f3d8" type="warning" @click="addTable" icon="el-icon-plus">新增</el-button>
        <el-button style="background-color:950842" type="warning" icon="el-icon-delete" @click="clearFilter">清除过滤器</el-button>
        <el-button style="background-color:950842" type="warning" icon="el-icon-upload2">导入数据</el-button>
        <el-button style="background-color:950842" type="warning" icon="el-icon-download">导出数据</el-button>
      </el-button-group>

      <div class="sl-no" style="display: inline-block;float: right">
        <el-select v-model="sortField" placeholder="排序" style="margin-left: 5px;width: 150px">
          <el-option label="字段1" value="uid"></el-option>
          <el-option label="字段2" value="name"></el-option>
        </el-select>
        <el-button-group style="margin-left: 5px">
          <el-button autofocus icon="el-icon-arrow-up">升序</el-button>
          <el-button icon="el-icon-arrow-down" style="margin-left: 5px" @click="downSortTable">降序</el-button>
        </el-button-group>
      </div>
    </div>

    <!--暂时隐藏-->
    <el-empty description="暂无数据" v-show="false && (tableData.length==0 && !loading)"></el-empty>
    <el-table
        v-show="true || (tableData.length > 0 || loading)"
        ref="mutiTable"
        v-loading="loading = false"
        :data="tableData"
        stripe
        border
        @selection-change="handleSelectionChange"
        :default-sort = "{prop: 'id', order: 'inscending'}"
        :cell-style="{ textAlign: 'center' }"
        :header-cell-style="{ textAlign: 'center',background:'#f5f7fa',color:'#950842' }"
        style="margin-top: 10px;"
        @row-click="recordRowInfo"
    >
      <el-table-column fixed align="center" type="selection" width="45"></el-table-column>
      <el-table-column fixed type="index" width="60%">
        <template slot="header" slot-scope="scope">序号</template>
      </el-table-column>

      <el-table-column prop="materialId" label="材料编号" width="70%">
        <template slot-scope="scope">
          <el-tag>{{scope.row.materialId}}</el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="name"  label="材料名称" width="100%"></el-table-column>
      <el-table-column prop="cost"  label="材料成本" width="100%"></el-table-column>
      <el-table-column prop="price"  label="材料单价" width="100%"></el-table-column>
      <el-table-column prop="ocuppy"  label="空间占用率" width="100%"></el-table-column>

      <el-table-column prop="operate" width="180%" align="right">
        <template slot="header" slot-scope="scope">操作</template>
        <template slot-scope="scope">
          <el-button icon="el-icon-edit" @click="editTable(scope.row)" size="small">编辑</el-button>
          <el-button icon="el-icon-delete" type="danger" @click="deleteUser" size="small">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

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
        title="提示"
        :visible.sync="centerDialogVisible"
        width="23%"
        center>
      <el-form :model="form" :rules="rules"  ref="addForm" label-width="100px" class="demo-ruleForm">
        <el-form-item v-model="rules.name" label="物品名称" prop="name">
          <el-col :span="20">
            <el-input v-model="form.name"></el-input>
          </el-col>
        </el-form-item>

        <el-form-item label="成本" prop="cost">
          <el-radio-group v-model="form.cost">
            <el-radio label="A" name="sex"></el-radio>
            <el-radio label="B" name="sex"></el-radio>
            <el-radio label="C" name="sex"></el-radio>
            <el-radio label="D" name="sex"></el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="单价" prop="price">
          <el-select style="width: 200px">
            <el-option label="选择1" value="值1"></el-option>
            <el-option label="选择2" value="值2"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="saveOrUpdate()">{{addBtnText}}</el-button>
          <el-button @click="resetForm('ruleForm')">重置</el-button>
        </el-form-item>

      </el-form>
    </el-dialog>

  </div>
</template>

<script>

export default {
  name: "Main",
  methods:{
    //====Axios====
    dataFormat(res){
      if(res.code == 200){
        this.tableData = res.data;

      }else{
        console.log(res.msg)
        alert("获取数据失败");
      }
    },
    loadPost(){
      this.$axios.post(this.$httpUrl+'/material/listPage',this.pageData)
          .then(res => res.data)
          .then(res => {
            this.loading = true;
            this.pageData.totalPage = res.total;
            this.dataFormat(res);
          });
    },
    search(){
      this.pageData.params.materialId = this.materialId;
      this.pageData.params.name = this.name;
      this.pageData.params.cost = this.cost;
      this.pageData.params.price = this.price;
      this.pageData.params.volume = this.volume;
      this.loadPost();
    },
    resetSearch(){
      this.pageData.params.materialId = '';
      this.pageData.params.name = '';
      this.pageData.params.cost = '';
      this.pageData.params.price = '';
      this.pageData.params.volume = '';

      this.searchMid = '';
      this.searchName = '';
      this.searchCost = '';
      this.searchPrice = '';
      this.searchOccupy = '';

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
      this.form.state = 'leisure';
    },
    // 点击 编辑
    editTable(row) {
      this.addBtnText = '修改';
      this.isClickEditBtn = true;
      this.form.materialId = row.materialId;
      this.form.name = row.name;
      this.form.cost = row.job;
      this.form.price = row.sex;
      this.form.volume = row.phone;
      this.centerDialogVisible = true;
    },
    // 新增用户
    saveForm(){
      this.$confirm('是否确认提交?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(()=>{
        this.$axios.post(this.$httpUrl+'/material',this.form)
            .then(res => res.data)
            .then(res => {
              if (res.code == 200) {
                this.$notify({
                  title: '成功',
                  message: '添加成功',
                  type: 'success'
                });
              } else {
                this.$notify.error({
                  title: '错误',
                  message: '添加失败'
                });
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
      this.$confirm('是否确认修改?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(()=>{
        this.$axios.put(this.$httpUrl+'/material',this.form)
            .then(res => res.data)
            .then(res => {
              if(res.code == 200){
                this.$notify({
                  title: '成功',
                  message: '添加成功',
                  type: 'success'
                });
              }else{
                this.$notify.error({
                  title: '错误',
                  message: '添加失败'
                });
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
    // 删除用户
    deleteUser(){
      this.$confirm(`是否要将${this.clickedRow.name}的信息从表中移除？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$axios.delete(this.$httpUrl+`/user/${this.clickedRow.uid}`)
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
    resetForm(ref){
      this.form.materialId = '';
      this.form.name = '';
      this.form.cost = '';
      this.form.price = '';
      this.form.volume = '';
    },
    saveOrUpdate(){
      if(this.form.uid == '') this.saveForm();
      else this.updateForm();
    },
    //====列表条件过滤====
    filterLocation(value, row){
      return row.locSendId === value;
    },
    filterState(value, row) {
      return row.state === value;
    },
    filterJob(value, row){
      return row.job === value;
    },
    clearFilter(){
      this.$refs.mutiTable.clearFilter();
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
    // 保存被点击的行信息
    recordRowInfo(row){
      this.clickedRow = row;
    },
  },
  data(){
    return {
      tableData:[],
      input:'',
      searchMid:'',
      searchName:'',
      searchCost:'',
      searchPrice:'',
      searchOccupy:'',
      loading: true,
      clickedRowInfo:null,
      pageData:{
        pageNum:1,
        totalPage:22,
        pageSize:5,
        params:{
          materialId:'',
          name:'',
          cost:'',
          post:'',
          volume:'',
          sortField:'',
          sortDirection:'',
        },
      },
      centerDialogVisible:false,
      isClickEditBtn:false,
      addBtnText:'提交',
      sortField:'uid',//排序字段
      sortDirection:'0',// 0升序，1降序
      clickedRow:'',// 获取被点击的行信息
      form:{
        materialId:'',
        name:'',
        cost:'',
        price:'',
        volume:'',
      },
      defaultLocation:'',
      rules: {
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' },
          { min: 1, max: 8, message: '长度为 1 到 8 ', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '手机号不能为空', trigger: 'blur' },
          {pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/ ,message: '请输入正确的手机号', trigger: 'blur'}
        ],
        sex:[
          { required: true}
        ],
        job:[
          {required: true}
        ],
        email:[
          { required: true, message: '请输入邮箱地址', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur'] }
        ]
      },
      // 对话框
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
    }

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