<template>
  <div id="root">
    <!-- 搜索栏 -->
    <div style="margin-bottom: 10px;" class="sl-no">

      <div>
        <el-input placeholder="请输入内容"  :maxlength="searchLength" show-word-limit v-model="searchValue" class="input-with-select">
          <el-select @change="lengthLimit" style="width: 115px" v-model="searchSelect" slot="prepend" placeholder="请选择">
            <el-option  label="物品名称" value="1"></el-option>
            <el-option  label="物品编号" value="2"></el-option>
          </el-select>
        </el-input>

        <el-button-group style="margin-left: 5px">
          <el-button icon="el-icon-search" @keyup.enter="search" type="primary" @click="search">搜索</el-button>
          <el-button icon="el-icon-refresh" type="primary" @click="resetSearch" style="margin-left: 5px">重置</el-button>
        </el-button-group>
      </div>
    </div>

    <div >
      <el-button-group>
        <el-button type="warning" icon="el-icon-upload2">导入数据</el-button>
        <el-button type="warning" icon="el-icon-download">导出数据</el-button>
      </el-button-group>

      <div class="sl-no" style="display: inline-block;float: right">
        <el-select v-model="sortField" placeholder="排序" style="margin-left: 5px;width: 150px">
          <el-option label="编号" value="materialId"></el-option>
          <el-option label="名称" value="name"></el-option>
          <el-option label="总数" value="count"></el-option>
        </el-select>
        <el-button-group style="margin-left: 5px">
          <el-button autofocus icon="el-icon-arrow-up" @click="upSortTable">升序</el-button>
          <el-button icon="el-icon-arrow-down" style="margin-left: 5px" @click="downSortTable">降序</el-button>
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
          :default-sort = "{prop: 'mid', order: 'inscending'}"
          :cell-style="{ textAlign: 'center' }"
          :header-cell-style="{ textAlign: 'center',background:'#f5f7fa',color:'#950842' }"
          style="margin-top: 10px;width: 761px;"
          @row-click="recordRowInfo"
      >

        <el-table-column type="index" width="60%">
          <template slot="header" slot-scope="scope">序号</template>
        </el-table-column>

        <el-table-column prop="materialId" label="物品编号" width="100%">
          <template slot-scope="scope">
            <el-tag>{{scope.row.materialId}}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="name"  label="物品名称" width="100px"></el-table-column>
        <el-table-column prop="count"  label="库存数" width="120px"></el-table-column>

        <el-table-column prop="operate" width="180px" align="right">
          <template slot="header" slot-scope="scope">操作</template>
          <template slot-scope="scope">
            <el-button icon="el-icon-edit" @click="editTable(scope.row)" size="small">编辑</el-button>
            <el-button icon="el-icon-delete" type="danger" @click="deleteUser" size="small">删除</el-button>
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
          :page-sizes="[2,5,,10,15,20]"
          layout="total,sizes,prev, pager, next, jumper"
          :total="pageData.totalPage"
          :hide-on-single-page="false">
      </el-pagination>
    </div>


    <!-- 对话框 增、改信息 -->
    <el-dialog
        title="物品信息"
        :visible.sync="centerDialogVisible"
        width="20%"
        center>
      <el-form :model="form" :rules="rules"  ref="addForm" label-width="100px" class="demo-ruleForm">
        <el-form-item label=物品名称 prop="name">
          <el-input style="width: 180px" v-model="form.name"></el-input>
        </el-form-item>

        <el-form-item label="运输量/车" prop="count">
          <el-input-number v-model="form.count" :step="1" :min="1" step-strictly></el-input-number>
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
  name: "MaterfialCount",
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
      this.$axios.post(this.$httpUrl+'/material/count',this.pageData)
          .then(res => res.data)
          .then(res => {
            this.loading = true;
            console.log(res.data)
            this.pageData.totalPage = res.total;
            this.dataFormat(res);
          })
    },
    search(){
      if(this.searchSelect == 1){
        this.pageData.params.name = this.searchValue;
        this.pageData.params.materialId = '';
        this.loadPost();
      }else{
        let reg = /^[1-9]+[0-9]*$/
        if(reg.test(this.searchValue) || this.searchValue == ''){
          this.pageData.params.name = '';
          this.pageData.params.materialId = this.searchValue;
          this.loadPost();
        }else{
          this.$message.error('输入不合法，请输入大于0的数字！');
        }
      }
    },
    resetSearch(){
      this.pageData.params.materialId = '';
      this.pageData.params.name = '';
      this.pageData.params.count = '';

      this.searchMid = '';
      this.searchValue = '';
      this.searchCount = '';

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
      this.form.materialId = row.materialId;
      this.form.name = row.name;
      this.form.count = row.count;
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
                ;
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
        this.$axios.delete(this.$httpUrl+`/material/${this.clickedRow.materialId}`)
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
      this.form.count = '';
    },
    saveOrUpdate(){
      console.log(this.form);
      if(this.form.materialId == '') this.saveForm();
      else this.updateForm();
    },
    //====搜索栏字数限制====
    lengthLimit(){
      if(this.searchSelect == 1){
        this.searchLength = '';
      }else{
        this.searchLength = 6;
      }
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
      searchValue:'',
      searchCost:'',
      searchPrice:'',
      searchCount:'',
      searchSelect:'1',
      searchLength:'',
      loading: true,
      minPrice:2,
      countStep:0.1,
      clickedRowInfo:null,
      pageData:{
        pageNum:1,
        totalPage:22,
        pageSize:5,
        params:{
          materialId:'',
          name:'',
          count:'',
          sortField:'',
          sortDirection:'',
        },
      },
      centerDialogVisible:false,
      isClickEditBtn:false,
      addBtnText:'提交',
      sortField:'materialId',//排序字段
      sortDirection:'0',// 0升序，1降序
      clickedRow:'',// 获取被点击的行信息
      form:{
        materialId:'',
        name:'',
        count:'',
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

</style>