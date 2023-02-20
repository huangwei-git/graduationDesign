<template>
  <div id="root">
    <!-- 搜索栏 -->
    <div style="margin-bottom: 10px;" class="sl-no">


      <div>
        <el-input placeholder="请输入内容"  :maxlength="searchLength" show-word-limit v-model="searchValue" class="input-with-select">
          <el-select @change="lengthLimit" style="width: 115px" v-model="searchSelect" slot="prepend" placeholder="请选择">
            <el-option  label="地点名称" value="1"></el-option>
            <el-option  label="地点ID" value="2"></el-option>
          </el-select>
        </el-input>
        <el-form :model="searchPos" :rules="rules" ref="posForm" style="margin-left: 5px;width: 80px;display: inline-block;">
          <el-form-item prop="xpos">
            <el-input v-model="searchPos.xpos" placeholder="x坐标" :maxlength="4"></el-input>
          </el-form-item>
        </el-form>

        <el-form :model="searchPos" :rules="rules" ref="posForm" style="margin-left: 5px;width: 80px;display: inline-block;">
          <el-form-item prop="ypos">
            <el-input v-model="searchPos.ypos" placeholder="y坐标" :maxlength="4"></el-input>
          </el-form-item>
        </el-form>

        <el-select v-model="searchType" placeholder="地点类型" style="margin-left: 5px;width: 150px;">
          <el-option value="0" label="运输中心"></el-option>
          <el-option value="1" label="需求地"></el-option>
        </el-select>

        <el-button-group style="margin-left: 5px">
          <el-button icon="el-icon-search" type="primary" @click="search">搜索</el-button>
          <el-button icon="el-icon-refresh" type="primary" @click="resetSearch" style="margin-left: 5px">重置</el-button>
        </el-button-group>
      </div>
    </div>

    <div >
      <el-button-group>
        <el-button type="warning" @click="addTable" icon="el-icon-plus">新增记录</el-button>
        <el-button type="warning" icon="el-icon-upload2">导入数据</el-button>
        <el-button type="warning" icon="el-icon-download">导出数据</el-button>
      </el-button-group>

      <div class="sl-no" style="display: inline-block;float: right">
        <el-select v-model="sortField" placeholder="排序" style="margin-left: 5px;width: 150px">
          <el-option label="地点ID" value="locId"></el-option>
          <el-option label="名称" value="name"></el-option>
          <el-option label="坐标" value="xpos"></el-option>
          <el-option label="地点类型" value="type"></el-option>
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
          style="margin-top: 10px;width: 661px;"
          @row-click="recordRowInfo"
      >

        <el-table-column type="index" width="60%">
          <template slot="header" slot-scope="scope">序号</template>
        </el-table-column>

        <el-table-column prop="locId" label="地点ID" width="100%">
          <template slot-scope="scope">
            <el-tag>{{scope.row.locId}}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="name"  label="地点名称" width="100px"></el-table-column>
        <el-table-column  label="地点类型" width="120px">
          <template slot-scope="scope">
            <el-tag v-show="scope.row.type == 0" type="danger">运输中心</el-tag>
            <el-tag v-show="scope.row.type == 1" type="warning">需求地</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="坐标" width="100px">
          <template slot-scope="scope">
            ( {{scope.row.xpos}} , {{scope.row.ypos}} )
          </template>
        </el-table-column>

        <el-table-column prop="operate" width="180px" align="right">
          <template slot="header" slot-scope="scope">操作</template>
          <template slot-scope="scope">
            <el-button icon="el-icon-edit" @click="editTable(scope.row)" size="small">编辑</el-button>
            <el-button icon="el-icon-delete" type="danger" @click="deleteLocation" size="small">删除</el-button>
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
        title="地点信息"
        :visible.sync="centerDialogVisible"
        width="20%"
        center>
      <el-form :model="form" :rules="rules" ref="addForm" label-width="100px" class="demo-ruleForm">
        <el-form-item label=地点名称 prop="name">
          <el-input v-model="form.name"></el-input>
        </el-form-item>

        <el-form-item label="地点类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio label="运输中心" value="0"></el-radio>
            <el-radio label="需求地" value="1"></el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="x坐标" prop="xpos">
          <el-input v-model="form.xpos"></el-input>
        </el-form-item>

        <el-form-item label="y坐标" prop="ypos">
          <el-input v-model="form.ypos"></el-input>
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
  name: "MaterfialInfo",
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
      this.$axios.post(this.$httpUrl+'/location/listPage',this.pageData)
          .then(res => res.data)
          .then(res => {
            this.loading = true;
            this.pageData.totalPage = res.total;
            this.dataFormat(res);
          })
    },
    search(){
      console.log("search")
      if(this.searchSelect == 1){
        this.pageData.params.name = this.searchValue;
      }else{
        let reg = /^[1-9]+[0-9]*$/
        if(reg.test(this.searchValue) || this.searchValue == ''){
          this.pageData.params.locId = this.searchValue;
        }else{
          this.$message.error('输入不合法，请输入大于0的数字！');
          return ;
        }
      }
      this.pageData.params.xpos = this.searchPos.xpos;
      this.pageData.params.ypos = this.searchPos.ypos;
      this.pageData.params.type = this.searchType;
      console.log(this.pageData.params);
      
      this.loadPost();
    },
    resetSearch(){
      this.pageData.params.locId = '';
      this.pageData.params.name = '';
      this.pageData.params.xpos = '';
      this.pageData.params.ypos = '';
      this.pageData.params.type = '';

      this.searchMid = '';
      this.searchValue = '';
      this.searchPos.xpos = '';
      this.searchPos.ypos = '';
      this.searchType = '';

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
      this.form.locId = row.locId;
      this.form.name = row.name;
      this.form.xpos = row.xpos;
      this.form.ypos = row.ypos;
      this.form.type = row.type == 0?'运输中心':'需求地';
      this.centerDialogVisible = true;
    },
    // 新增用户
    saveForm(){
      this.$confirm('是否确认提交?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(()=>{
        this.$refs.addForm.validate(valid=>{
          this.$axios.post(this.$httpUrl+'/location',this.form)
              .then(res => res.data)
              .then(res => {
                if (res.code == 200) {
                  console.log("sss");
                  this.$notify({
                    title: '成功',
                    message: '添加成功',
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
                      message: '添加失败'
                    });
                  }
                }
              })
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
        this.$axios.put(this.$httpUrl+'/location',this.form)
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
    deleteLocation(){
      this.$confirm(`是否要将${this.clickedRow.name}的信息从表中移除？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$axios.delete(this.$httpUrl+`/location/${this.clickedRow.locId}`)
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
      this.form.locId = '';
      this.form.name = '';
      this.form.xpos = '';
      this.form.ypos = '';
      this.form.type = '';
    },
    saveOrUpdate(){
      if(this.form.type == '运输中心') this.form.type = 0;
      else this.form.type = 1;

      if(this.form.locId == '') this.saveForm();
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
      locationList:[],
      input:'',
      searchMid:'',
      searchValue:'',
      searchPos:{
        xpos:'',
        ypos:'',
      },
      searchType:'',
      searchSelect:'1',
      searchLength:'',
      loading: true,
      minypos:2,
      typeStep:0.1,
      clickedRowInfo:null,
      pageData:{
        pageNum:1,
        totalPage:22,
        pageSize:5,
        params:{
          locId:'',
          name:'',
          xpos:'',
          ypos:'',
          type:'',
          sortField:'',
          sortDirection:'',
        },
      },
      centerDialogVisible:false,
      isClickEditBtn:false,
      addBtnText:'提交',
      sortField:'locId',//排序字段
      sortDirection:'0',// 0升序，1降序
      clickedRow:'',// 获取被点击的行信息
      form:{
        locId:'',
        name:'',
        xpos:'',
        ypos:'',
        type:'',
      },
      defaultLocation:'',
      rules: {
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' },
          { min: 1, max: 8, message: '长度为 1 到 8 ', trigger: 'blur' },
          { pattern:/^\S*$/, message: '不能包含空格', trigger: 'blur' },
        ],
        xpos:[
          { required: true, message: '请输入x坐标', trigger: 'blur' },
          { pattern: /^[0-9]*$/, message: '必须为数字' ,trigger:['blur','change']},
          { min: 1, max: 4, message: '范围为1-9999', trigger: 'blur' }
        ],
        ypos:[
          { required: true, message: '请输入y坐标', trigger: 'blur' },
          { pattern: /^[0-9]*$/, message: '必须为数字' ,trigger:['blur','change']},
          { min: 1, max: 4, message: '范围为1-9999', trigger: 'blur' }
        ],
        type:[
          { required: true, message: '请选择地点类型', trigger: 'blur' },
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

</style>