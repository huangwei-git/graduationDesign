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
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="['00:00:00', '23:59:59']">
      </el-date-picker>
      <el-input placeholder="请输入内容" :suffix-icon="inputIconClass" v-model="input" class="sl-no input-with-select">
        <el-select class="sl-no"  @change="getChangeValue" v-model="select" slot="prepend" placeholder="请选择">
          <el-option class="sl-no" label="姓名" :value="1"></el-option>
          <el-option label="电话" :value="2"></el-option>
          <el-option label="仓库" :value="3"></el-option>
        </el-select>
      </el-input>
      <el-button type="primary" style="margin-left: 5px">搜索</el-button>
    </div>
    <el-button-group style="margin-left: 5px;">
      <el-button type="primary" icon="el-icon-plus">新增员工</el-button>
      <el-button type="primary" icon="el-icon-minus" @click="handlerDeleteAllSelected" :disabled="handleDisable">批量删除</el-button>
      <el-button type="primary" icon="el-icon-upload2">导入数据</el-button>
      <el-button type="primary" icon="el-icon-download">导出数据</el-button>
    </el-button-group>

    <el-table
        ref="mutiTable"
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
      <el-table-column prop="sex" sortable label=性别" width="120%"></el-table-column>
      <el-table-column prop="locSendId" sortable label="所在配送中心编号" width="180%"
                       :filter-method="filterLocation"
                       :filters="locations"
                       filter-placement="bottom">
        <template slot-scope="scope">
          <i class="el-icon-location-outline"></i>
          <span> {{scope.row.locSendId}}</span>
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

      <el-table-column prop="deleted" label="评分">
        <template slot-scope="scope">
          <el-rate v-model="scope.row.deleted"></el-rate>
        </template>
      </el-table-column>

      <el-table-column prop="operate" width="250%" align="right">
        <template slot="header" slot-scope="scope">操作</template>
        <template slot-scope="scope">
          <el-button @click="handleClickLook(scope.row)" type="success" size="small">查看</el-button>
          <el-button type="primary" size="small">编辑</el-button>
          <el-button type="danger" size="small">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div style="padding: 10px 0;text-align: center">
      <el-pagination
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page.sync="currentPage"
          :page-size="10"
          layout="total,prev, pager, next, jumper"
          :total="totalPage"
          :hide-on-single-page="true">
      </el-pagination>


    </div>
  </div>
</template>

<script>

export default {
  name: "Main",
  data(){
    return {
      locations:[],
      rate:3,
      tableData:[],
      sideNavState:'收起',
      sideWidth:220,
      input:'',
      select:1,
      date:'',
      currentPage:1,
      totalPage:22,
      inputIconClass:'el-icon-user',
      handleDisable:true,
      currentSelected:[],
      clickedRowInfo:null,
      // 对话框

    }
  },
  methods:{
    loadGet(){
      this.$axios.get(this.$httpUrl + '/user')
          .then(res=>res.data)
          .then(res=>{
            this.tableData = res.data;

            let set = new Set();
            let setFilter = new Set();
            this.tableData.forEach(item => {
              // 格式化处理时间
              let len = item.dateOfEntry.indexOf('T');
              item.dateOfEntry = item.dateOfEntry.slice(0,len);


              item.deleted = item.locSendId + 2;
              console.log()

              // 获取“运输中心”字段的过滤器数值
              set.add(item.locSendId);
            })
            set.forEach(item => {
              setFilter.add({text:item,value:item});
            })
            this.locations = [...setFilter];
      })
    },
    filterLocation(value, row){
      return row.locSendId === value;
    },
    filterState(value, row) {
      return row.state === value;
    },
    filterHandler(value, row, column) {
      const property = column['state'];
      console.log(property,value);
      return row[property] === value;
    },
    handleSizeChange(val) {
      console.log(`每页 ${val} 条`);
    },
    handleCurrentChange(val) {
      console.log(`当前页: ${val}`);
    },
    // 控制搜索栏中，标签样式
    getChangeValue(){
      if(this.select === 2){
        this.inputIconClass = 'el-icon-search';
      }else if(this.select === 3){
        this.inputIconClass = 'el-icon-locSendId-outline';
      }else{
        this.inputIconClass = 'el-icon-user';
      }
    },
    // 获得选择的时间范围
    getDateRange(){
      console.log(this.date);
    },
    // “查看”操作
    handleClickLook(row) {
      console.log(row.name);
      console.log(row.locSendId);
      console.log(row.dateOfEntry);
      console.log(row.phone);
      console.log(row.email);
      console.log(row.state);
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
  beforeMount() {
    this.loadGet();
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