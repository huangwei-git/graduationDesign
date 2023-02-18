<template>
  <div id="root">
    <!-- 搜索栏 -->
    <div style="margin-bottom: 20px">
      <el-date-picker
          class="sl-no"
          @change="getDateRange"
          :editable="false"
          v-model="date"
          type="daterange"
          format="yyyy-MM-dd"
          value-format="yyyy-MM-dd"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="['00:00:00', '23:59:59']">
      </el-date-picker>
      <el-input placeholder="请输入内容"
                :suffix-icon="inputIconClass"
                v-model="input"
                class="sl-no input-with-select"
                @keyup.enter.native="search">
        <el-select class="sl-no"  @change="getChangeValue" v-model="select" slot="prepend" placeholder="请选择">
          <el-option class="sl-no" label="姓名" :value="1"></el-option>
          <el-option label="电话" :value="2"></el-option>
          <el-option label="工号" :value="3"></el-option>
        </el-select>
      </el-input>
        <el-select style="margin-left:  5px;width: 150px" class="sl-no"  @change="getChangeValue" v-model="selectSex" placeholder="性别">
          <el-option
              v-for="item in sexs"
              :key="item.value"
              :label="item.label"
              :value="item.value">
          </el-option>
        </el-select>
      <el-button type="primary" @click="search" style="margin-left: 5px">搜索</el-button>
      <el-button type="danger" @click="resetSearch" style="margin-left: 5px">重置</el-button>
    </div>
    <el-button-group style="margin-left: 5px;">
      <el-button type="primary" @click="centerDialogVisible = true;" icon="el-icon-plus">新增员工</el-button>
      <el-button type="primary" icon="el-icon-minus" @click="handlerDeleteAllSelected" :disabled="handleDisable">批量删除</el-button>
      <el-button type="primary" icon="el-icon-delete" @click="clearFilter">清除过滤器</el-button>
      <el-button type="primary" icon="el-icon-upload2">导入数据</el-button>
      <el-button type="primary" icon="el-icon-download">导出数据</el-button>
    </el-button-group>

    <el-empty description="暂无数据" v-show="(tableData.length==0 && !loading)"></el-empty>
    <el-table
        v-show="(tableData.length > 0 || loading)"
        ref="mutiTable"
        v-loading="loading"
        :data="tableData"
        stripe
        border
        @selection-change="handleSelectionChange"
        :default-sort = "{prop: 'id', order: 'inscending'}"
        :cell-style="{ textAlign: 'center' }"
        :header-cell-style="{ textAlign: 'center',background:'#f3f6fd',color:'#555' }"
        @row-click="storeClickedRowInfo"
        style="margin-top: 10px;"
    >
      <el-table-column align="center" type="selection" width="45"></el-table-column>
      <el-table-column type="index" width="100">
        <template slot="header" slot-scope="scope">序号</template>
      </el-table-column>
      <el-table-column prop="uid" sortable label="工号" width="150%">
        <template slot-scope="scope">
          <el-tag>{{scope.row.uid}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="name" sortable label="姓名" width="150%"></el-table-column>
      <el-table-column prop="sex" sortable label="性别" width="120%"></el-table-column>
      <el-table-column prop="locSendId" sortable label="所在配送中心编号" width="180%"
                       :filter-method="filterLocation"
                       :filters="locations"
                       filter-placement="bottom">
        <template slot-scope="scope">
          <span> {{locationMap[scope.row.locSendId]}}</span>
        </template>
      </el-table-column>
      <el-table-column prop="phone" sortable label="手机号" width="200%">
        <template slot-scope="scope">
          <i class="el-icon-phone-outline"></i>
          <span> {{scope.row.phone}}</span>
        </template>
      </el-table-column>

      <el-table-column prop="dateOfEntry" sortable label="入职时间" width="230%">
        <template slot-scope="scope">
          <i class="el-icon-time"></i>
          <span> {{scope.row.dateOfEntry}}</span>
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
              :type="scope.row.job === '管理员' ? 'danger' : 'primary'"
              disable-transitions
              effect="dark"
              style="width: 60px;font-weight: normal;"
          >
            {{scope.row.job}}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="operate" width="170%" align="right">
        <template slot="header" slot-scope="scope">操作</template>
        <template slot-scope="scope">
          <el-button @click="handleClickLook(scope.row)" type="text" size="small">查看</el-button>
          <el-button type="text" size="small">编辑</el-button>
          <el-button type="text" size="small">删除</el-button>
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
          :page-sizes="[2,5,,10,15,20]"
          layout="total,sizes,prev, pager, next, jumper"
          :total="pageData.totalPage"
          :hide-on-single-page="false">
      </el-pagination>
    </div>

    <el-dialog
        title="提示"
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
            <el-input v-model="form.phone"></el-input>
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
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm('ruleForm')">提交</el-button>
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
          let len = item.dateOfEntry.indexOf('T');
          item.dateOfEntry = item.dateOfEntry.slice(0,len);

          // 职位处理
          if(item.job == 0){
            item.job = "管理员";
            item.locSendId = '*';
          }
          else item.job = "运输员";


          // 运输中心处理
          for(let i = 0;i < this.locationTable.length;i++){
            this.locationMap[this.locationTable[i].locId] = this.locationTable[i].name;
          }
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
          })
    },
    search(){
      if(this.select === 1){
        this.pageData.params.name = this.input;
        this.pageData.params.phone = '';
        this.pageData.params.uid = '';
      }else if(this.select === 2){
        this.pageData.params.name = '';
        this.pageData.params.phone = this.input;
        this.pageData.params.uid = '';
      }else{
        this.pageData.params.name = '';
        this.pageData.params.phone = '';
        this.pageData.params.uid = this.input;
      }
      this.pageData.params.startTime = this.date[0];
      this.pageData.params.endTime = this.date[1];
      this.pageData.params.selectSex = this.selectSex;
      this.loadPost();
    },
    resetSearch(){
      this.pageData.params.name = '';
      this.pageData.params.startTime = '';
      this.pageData.params.endTime = '';
      this.pageData.params.uid = '';
      this.pageData.params.phone = '';
      this.pageData.params.selectSex = '';
      this.loadPost();
    },
    //====添加====
    addUser(){
      this.centerDialogVisible = true;
      this.$nextTick(()=>{
        this.resetForm();
      })
    },
    submitForm(ref){
      if(this.form.job === '管理员'){
        this.form.job = 0;
        this.form.locSendId = 0;
      }else this.form.job = 1;
      console.log(this.form);
      this.$confirm('是否确认提交?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(()=>{
        this.$axios.post(this.$httpUrl+'/user',this.form)
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

      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });

    },
    resetForm(ref){
      this.form.name = '';
      this.form.sex = '';
      this.form.email = '';
      this.form.phone = '';
      this.form.job = '';
      this.form.locSendId = this.defaultLocation;
      console.log(1);
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
      this.loadPost();
    },
    handleCurrentChange(val) {
      this.pageData.pageNum = val;
      console.log(this.pageData);
      this.loadPost();
    },
    // 控制搜索栏中，标签样式
    getChangeValue(){
      console.log(this.select)
      if(this.select === 2){
        this.inputIconClass = 'el-icon-phone-outline';
      }else if(this.select === 3){
        this.inputIconClass = 'el-icon-location-information';
      }else{
        this.inputIconClass = 'el-icon-user';
      }
    },
    // 获得选择的时间范围
    getDateRange(){
      this.pageData.params = {
        startTime:this.date[0],
        endTime:this.date[1],
      }
    console.log(this.date[0] === this.tableData[0].dateOfEntry);
    },
    // “查看”操作
    handleClickLook(row) {
      console.log(row.uid);
      console.log(row.name);
      console.log(row.locSendId);
      console.log(row.phone);
      console.log(row.dateOfEntry);
      console.log(row.email);
      console.log(row.state);
      console.log(row.job);
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
        console.log("需要删除：")
        this.currentSelected.forEach(item => {
          console.log(item.id,item.date,item.name,item)
        })
        this.$notify.success({
          title:"删除成功！",
          message:"删除"+this.currentSelected.length+"条记录",
        })
        console.log("共"+this.currentSelected.length+"个")
      }).catch(() => {
        this.$notify.info({
          type: 'info',
          message: '已取消删除',
        });
      });
    },
    // “删除”操作
    storeClickedRowInfo(row,column,event){
      if(column.property === "operate"
          && event.target.innerText.includes("删除")){
        this.$confirm(`是否要将${row.name}的信息从表中移除？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$notify.success({
            title:"删除成功！",
            message:row.id + " " + row.name,
          })
          console.log(row.name)
        }).catch(() => {
          this.$notify.info({
            type: 'info',
            message: '已取消删除',
          });
        });
      }
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
      date:['',''],
      loading: true,
      inputIconClass:'el-icon-user',
      handleDisable:true,
      currentSelected:[],
      clickedRowInfo:null,
      selectSex:'',
      sexs:[
        {label:'男',value:'男',},
        {label:'女',value:'女',}
      ],
      pageData:{
        pageNum:1,
        totalPage:22,
        pageSize:5,
        params:{
          name:'',
          startTime:'',
          endTime:'',
          uid:'',
          phone:'',
          selectSex:''
        },
      },
      centerDialogVisible:false,
      locationTable:[],
      form:{
        name:'',
        sex:'',
        email:'',
        phone:'',
        job:'',
        locSendId:'',
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
            { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
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