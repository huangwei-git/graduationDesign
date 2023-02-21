<template>
  <div id="root">
    <!-- 搜索栏 -->
    <div style="margin-bottom: 10px;">
      <el-date-picker
          v-model="date"
          :editable="false"
          :clearable="true"
          @change="getDateRange"
          unlink-panels
          :disabled="select == 2"
          type="datetimerange"
          value-format="yyyy-MM-ddThh:mm"
          start-placeholder="开始日期"
          range-separator="至"
          end-placeholder="结束日期">
      </el-date-picker>

      <el-input placeholder="请输入内容"
                :clearable="true"
                :suffix-icon="inputIconClass"
                v-model="input"
                style="width: 280px"
                class="sl-no input-with-select"
                @keyup.enter.native="search">
        <el-select class="sl-no" style="width: 100px"  @change="getChangeValue" v-model="select" slot="prepend" placeholder="请选择">
          <el-option label="姓名" :value="1"></el-option>
          <el-option label="工号" :value="2"></el-option>
        </el-select>
      </el-input>
      <el-input
          :clearable="true"
          v-show="this.select != 2"
          place class="sl-no"
          v-model="phone"
          suffix-icon="el-icon-phone-outline"
          style="margin-left:  5px;width: 180px"
          placeholder="手机号">
      </el-input>
      <el-select
          :clearable="true"
          v-show="this.select != 2"
          style="margin-left:  5px;width: 95px" class="sl-no"
          v-model="selectSex"
          placeholder="性别">
        <el-option value="男" label="男">
          <span style="float: left">男</span>
          <span style="float: right; color: #8492a6; font-size: 8px">
            <i class="el-icon-male"></i>
          </span>
        </el-option>
        <el-option value="女" label="女">
          <span style="float: left">女</span>
          <span style="float: right; color: #8492a6; font-size: 8px">
            <i class="el-icon-female"></i>
          </span>
        </el-option>
      </el-select>
      <el-select
          :clearable="true"
          v-show="this.select != 2"
          style="margin-left:  5px;width: 140px" class="sl-no"
          v-model="pos"
          placeholder="运输中心">
        <el-option
            v-for="item in locationTable"
            :key="item.locId"
            :label="item.name"
            :value="item.locId">
        </el-option>
      </el-select>
      <el-select
          :clearable="true"
          v-show="this.select != 2"
          style="margin-left:  5px;width: 140px" class="sl-no"
          v-model="state"
          placeholder="状态">
        <el-option label="leisure" value="leisure"></el-option>
        <el-option label="work" value="work"></el-option>
        <el-option label="rest" value="rest"></el-option>
      </el-select>
      <el-select
          :clearable="true"
          v-show="this.select != 2"
          style="margin-left:  5px;width: 140px" class="sl-no"
          v-model="job"
          placeholder="职位">
        <el-option label="管理员" value="0"></el-option>
        <el-option label="运输员" value="1"></el-option>
      </el-select>

      <el-button icon="el-icon-search" type="primary" @click="search" style="margin-left: 5px">搜索</el-button>
      <el-button icon="el-icon-refresh" type="primary" @click="resetSearch" style="margin-left: 5px">重置</el-button>

    </div>

    <div >
      <el-button-group>
        <el-button type="warning" @click="addUser" icon="el-icon-plus">新增员工</el-button>
        <el-button type="warning" icon="el-icon-minus" @click="handlerDeleteAllSelected" :disabled="handleDisable">批量删除</el-button>
        <el-button type="warning" icon="el-icon-delete" @click="clearFilter">清除过滤器</el-button>

        <el-upload
            :show-file-list="false"
            accept="xlsx"
            :on-success="handlerSuccessUpload"
            action="http://localhost:9090/user/import"
          style="display: inline-block"
        >
        <el-button type="warning" icon="el-icon-upload2">导入数据</el-button>
        </el-upload>

        <el-button type="warning" icon="el-icon-download" @click="exportData">导出数据</el-button>
      </el-button-group>

      <div class="sl-no" style="display: inline-block;float: right">
        <el-select v-model="sortValue" placeholder="排序" style="margin-left: 5px;width: 150px">
          <el-option label="工号" value="uid"></el-option>
          <el-option label="姓名" value="name"></el-option>
          <el-option label="性别" value="sex"></el-option>
          <el-option label="配送中心" value="locSendId"></el-option>
          <el-option label="入职时间" value="dateOfEntry"></el-option>
          <el-option label="状态" value="state"></el-option>
          <el-option label="职位" value="job"></el-option>
        </el-select>
        <el-button-group style="margin-left: 5px">
          <el-button autofocus icon="el-icon-arrow-up" @click="upSortTable">升序</el-button>
          <el-button icon="el-icon-arrow-down" style="margin-left: 5px" @click="downSortTable">降序</el-button>
        </el-button-group>
      </div>
    </div>

    <div class="empty" style="display: flex;text-align:center;justify-content: center;width: 100%">
      <el-empty description="暂无数据" v-show="(tableData.length==0 && !loading)"></el-empty>
    </div>
    <el-table
        v-show="(tableData.length > 0 || loading)"
        ref="mutiTable"
        v-loading="loading"
        :data="tableData"
        stripe
        border
        @selection-change="handleSelectionChange"
        :default-sort = "{prop: 'job', order: 'inscending'}"
        :cell-style="{ textAlign: 'center' }"
        :header-cell-style="{ textAlign: 'center',background:'#f5f7fa',color:'#950842' }"
        style="margin-top: 10px;"
        @row-click="recordRowInfo"
    >
      <el-table-column fixed align="center" type="selection" width="45"></el-table-column>
      <el-table-column fixed type="index" width="60%">
        <template slot="header" slot-scope="scope">序号</template>
      </el-table-column>

      <el-table-column prop="uid" label="工号" width="70%">
        <template slot-scope="scope">
          <el-tag>{{scope.row.uid}}</el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="name"  label="姓名" width="130%"></el-table-column>
      <el-table-column prop="sex"  label="性别" width="80%"></el-table-column>
      <el-table-column prop="phone"  label="手机号" width="180%">
        <template slot-scope="scope">
          <i class="el-icon-phone-outline"></i>
          <span> {{scope.row.phone}}</span>
        </template>
      </el-table-column>
      <el-table-column prop="email"  label="邮箱" width="250%">
        <template slot-scope="scope">
          <i class="el-icon-message"></i>
          <span> {{scope.row.email}}</span>
        </template>
      </el-table-column>

      <el-table-column prop="dateOfEntry"  label="入职时间" width="230%">
        <template slot-scope="scope">
          <i class="el-icon-time"></i>
          <span> {{scope.row.dateOfEntry}}</span>
        </template>
      </el-table-column>

      <el-table-column prop="locSendId"  label="所在配送中心编号" width="150%"
                       :filter-method="filterLocation"
                       :filters="locations"
                       filter-placement="bottom">
        <template slot-scope="scope">
          <i v-show="locationMap[scope.row.locSendId]!=undefined" class="el-icon-location-outline"></i>
          <span> {{locationMap[scope.row.locSendId]}}</span>
        </template>
      </el-table-column>

      <el-table-column
          prop="state"
          label="状态"
          :filters="[{ text: 'leisure', value: 'leisure' },
                     { text: 'work', value: 'work' },
                     { text: 'rest', value: 'rest' }]"
          :filter-method="filterState"
          filter-placement="bottom">
        <template slot-scope="scope">
          <el-tag
              :type="scope.row.state === 'leisure' ? 'success' :
                     scope.row.state === 'work'? 'danger' : 'info'"
              disable-transitions
              effect="plain"
              style="width: 60px;font-weight: bold;"
          >
            {{scope.row.state}}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column
          prop="job"
          label="职位"
          :filters="[{ text: '管理员', value: '管理员' },
                     { text: '运输员', value: '运输员' }]"
          :filter-method="filterJob"
          filter-placement="bottom">
        <template slot-scope="scope">
          <el-tag
              :type="scope.row.job === '管理员' ? 'danger' : 'info'"
              disable-transitions
              style="width: 60px;font-weight: bold;"
          >
            {{scope.row.job}}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column fixed="right" prop="operate" width="180%" align="right">
        <template slot="header" slot-scope="scope">操作</template>
        <template slot-scope="scope">
          <el-button icon="el-icon-edit" @click="editUser(scope.row)" size="small">编辑</el-button>
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
          :page-sizes="[10,20,20,30,40,50,100]"
          layout="total,sizes,prev, pager, next, jumper"
          :total="pageData.totalPage"
          :hide-on-single-page="false">
      </el-pagination>
    </div>

    <el-dialog
        title="用户信息"
        :visible.sync="centerDialogVisible"
        width="23%"
        center>
      <el-form :model="form" :rules="rules"  ref="addForm" label-width="100px" class="demo-ruleForm">
        <el-form-item v-model="rules.name" label="姓名" prop="name">
          <el-col :span="20">
            <el-input v-model="form.name"></el-input>
          </el-col>
        </el-form-item>
        <el-form-item v-model="rules.phone" label="手机" prop="phone">
          <el-col :span="20">
            <el-input v-model="form.phone" maxlength="11"></el-input>
          </el-col>
        </el-form-item>
        <el-form-item
            prop="email"
            label="邮箱">
          <el-col :span="20">
            <el-input v-model="form.email"></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-radio-group v-model="form.sex">
            <el-radio label="男" name="sex"></el-radio>
            <el-radio label="女" name="sex"></el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="职位" prop="job">
          <el-radio-group v-model="form.job">
            <el-radio label="管理员" value="0" name="job"></el-radio>
            <el-radio label="运输员" value="1" name="job"></el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-show="form.job !== '管理员'" label="运输中心" prop="locSendId">
          <el-select v-model="form.locSendId" style="width: 200px">
            <el-option
              v-for="item of locationTable"
              :key="item.locId"
              :label="item.name"
              :value="item.locId"></el-option>
          </el-select>
        </el-form-item><el-form-item v-show="isClickEditBtn" label="状态" prop="state">
          <el-select v-model="form.state" style="width: 200px">
            <el-option label="leisure" value="leisure"></el-option>
            <el-option label="work" value="work"></el-option>
            <el-option label="rest" value="rest"></el-option>
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

        let set = new Set();
        let setFilter = new Set();
        this.tableData.forEach(item => {
          // 格式化处理时间
          item.dateOfEntry = item.dateOfEntry.replaceAll('T',' ');

          // 职位处理
          if(item.job == 0){
            item.job = "管理员";
            item.locSendId = '*';
          }
          else item.job = "运输员";

          // 获取“运输中心”字段的过滤器数值
          set.add(item.locSendId);
        })
        set.forEach(item => {
          if(item > 0)
            setFilter.add({text:this.locationMap[item],value:item});
        })
        this.locations = [...setFilter];
        this.loading = false;
      }else{
        alert("获取数据失败");
      }
    },
    loadGet(){
      this.$axios.get(this.$httpUrl + '/user')
          .then(res=>res.data)
          .then(res=>{
            this.loading = true;
            dataFormat(res);
            this.loading = false;
          })
    },
    loadPost(){
      this.loadGetLocation()
      this.$axios.post(this.$httpUrl+'/user/listPage',this.pageData)
          .then(res =>{ this.loadGetLocation();return res.data;})
          .then(res => {
            this.loading = true;
            this.pageData.totalPage = res.total;
            this.dataFormat(res);
          });
    },
    loadGetLocation(){
      this.$axios.get(this.$httpUrl+'/location/type?type=0')
          .then(res => res.data)
          .then(res => {
            this.locationTable = res.data;
            this.form.locSendId = this.defaultLocation = res.data[0].name;
            // 运输中心处理
            for(let i = 0;i < this.locationTable.length;i++){
              this.locationMap[this.locationTable[i].locId] = this.locationTable[i].name;
            }
          })
    },
    search(){
      if(this.select === 1){
        this.pageData.params.name = this.input;
        this.pageData.params.uid = '';
      }else if(this.select === 2) {
        this.pageData.params.name = '';
        this.pageData.params.uid = this.input;
      }
      this.pageData.params.phone = this.phone;
      this.pageData.params.position = this.pos;
      this.pageData.params.state = this.state;
      this.pageData.params.job = this.job;
      this.pageData.params.selectSex = this.selectSex;
      if(this.date != null && this.date != ''){
        this.pageData.params.startTime = this.date[0];
        this.pageData.params.endTime = this.date[1];
      }
      this.loadPost();
    },
    resetSearch(){
      this.pageData.params.uid = '';
      this.pageData.params.name = '';
      this.pageData.params.phone = '';
      this.pageData.params.job = '';
      this.pageData.params.state = '';
      this.pageData.params.position = '';
      this.pageData.params.startTime = '';
      this.pageData.params.endTime = '';
      this.pageData.params.selectSex = '';

      this.phone = '';
      this.date = '';
      this.pos = '';
      this.state = '';
      this.job = '';
      this.selectSex = '';

      this.loadPost();
    },
    exportData(){
      window.open("http://localhost:9090/user/export")
    },
    //====排序====
    upSortTable(){
      this.pageData.params.sortField = this.sortValue;
      this.pageData.params.sortDirection = '0';
      this.loadPost();
    },
    downSortTable(){
      this.pageData.params.sortField = this.sortValue;
      this.pageData.params.sortDirection = '1';
      this.loadPost();
    },
    //====添加 修改 删除====
    // 点击 新增
    addUser(){
      this.centerDialogVisible = true;
      this.addBtnText = '提交';
      this.isClickEditBtn = false;
      this.form.state = 'leisure';
    },
    // 点击 编辑
    editUser(row) {
      this.addBtnText = '修改';
      this.isClickEditBtn = true;
      this.form.uid = row.uid;
      this.form.name = row.name;
      this.form.job = row.job;
      this.form.sex = row.sex;
      this.form.phone = row.phone;
      this.form.email = row.email;
      this.form.locSendId = row.locSendId == '*'?this.defaultLocation:row.locSendId;
      this.form.state = row.state;
      if(this.form.job == '管理员') this.form.locSendId = this.defaultLocation;
      console.log(this.form);
      this.centerDialogVisible = true;
    },
    // 新增用户
    saveForm(){
      if(this.form.job === '管理员'){
        this.form.job = 0;
        this.form.locSendId = 0;
      }else this.form.job = 1;
      this.$confirm('是否确认提交?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(()=>{
        this.$axios.post(this.$httpUrl+'/user',this.form)
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
        this.$refs.addForm.validate(valid => {
          if(valid){
            this.$axios.put(this.$httpUrl+'/user',this.form)
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
          }
        })
        if(this.form.job == '管理员'){
          this.form.job = 0;
          this.form.locSendId = 0;
        }else this.form.job  = 1;
      }).then(() => {
        this.loadPost();
        this.centerDialogVisible = false;
        //this.resetForm();
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
      this.form.uid = '';
      this.form.name = '';
      this.form.sex = '';
      this.form.email = '';
      this.form.phone = '';
      this.form.job = '';
      this.form.state = '';
      this.form.locSendId = this.defaultLocation;
    },
    saveOrUpdate(){
      if(this.form.uid == ''){
        console.log(1)
        this.saveForm();
      }else{
        console.log(2)
        this.updateForm()
      }
    },
    //====文件导入成功====
    handlerSuccessUpload(){
      this.$message.success("文件导入成功！")
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
    // 控制搜索栏中，标签样式
    getChangeValue(){
      if(this.select === 2){
        this.inputIconClass = 'el-icon-postcard';
      }else{
        this.inputIconClass = 'el-icon-user';
      }
    },
    // 获得选择的时间范围
    getDateRange(){
      if(this.date != null){
          this.pageData.params.startTime = this.date[0];
          this.pageData.params.endTime = this.date[1];
        }else{
          this.date = '';
          this.pageData.params.startTime = '';
          this.pageData.params.endTime = '';
        }
      console.log(this.pageData.params.startTime)
      console.log(this.pageData.params.endTime)
    },
    handleSelectionChange(val) {
      if(val.length) this.handleDisable = false;
      else this.handleDisable = true;
      this.currentSelected = val;
    },
    // “批量删除”操作
    handlerDeleteAllSelected(){
      this.$confirm('此操作将永久删除所选记录, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$notify.error({
          title: '删除失败',
          message: '权限不足'
        });
        //console.log("需要删除：")
        //this.currentSelected.forEach(item => {
        //  console.log(item.id,item.date,item.name,item)
        //})
        //this.$notify.success({
        //  title:"删除成功！",
        //  message:"删除"+this.currentSelected.length+"条记录",
        //})
        //console.log("共"+this.currentSelected.length+"个")
      }).catch(() => {
        this.$notify.info({
          type: 'info',
          message: '已取消删除',
        });
      });
    },
    // 获取被点击的行信息
    recordRowInfo(row){
      this.clickedRow = row;
    },
  },
  data(){
    return {
      locations:[],
      locationMap:[],
      rate:3,
      tableData:[],
      sideNavState:'收起',
      sideWidth:220,
      input:'',
      select:1,
      phone:'',
      pos:'',
      state:'',
      job:'',
      date:['',''],
      loading: true,
      inputIconClass:'el-icon-user',
      handleDisable:true,
      currentSelected:[],
      clickedRowInfo:null,
      selectSex:'',
      pageData:{
        pageNum:1,
        totalPage:22,
        pageSize:20,
        params:{
          uid:'',
          name:'',
          phone:'',
          job:'',
          state:'',
          position:'',
          startTime:'',
          endTime:'',
          selectSex:'',
          sortField:'',
          sortDirection:'',
        },
      },
      centerDialogVisible:false,
      locationTable:[],
      isClickEditBtn:false,
      addBtnText:'提交',
      sortValue:'uid',//排序字段
      sortDirection:'0',// 0升序，1降序
      clickedRow:'',// 获取被点击的行信息
      form:{
        uid:'',
        name:'',
        sex:'',
        email:'',
        phone:'',
        state:'',
        job:'',
        locSendId:'',
        position:'',
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
    this.loadGetLocation();
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