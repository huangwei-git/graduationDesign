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
        :data="tableData2"
        stripe
        @selection-change="handleSelectionChange"
        :default-sort = "{prop: 'id', order: 'inscending'}"
        :cell-style="{ textAlign: 'center' }"
        :header-cell-style="{ textAlign: 'center' }"
        @row-click="storeClickedRowInfo"
    >
      <el-table-column align="center" type="selection" width="45"></el-table-column>
      <el-table-column type="index" width="100">
        <template slot="header" slot-scope="scope">序号</template>
      </el-table-column>
      <el-table-column prop="name" sortable label="姓名" width="220"></el-table-column>
      <el-table-column prop="sex" sortable label=性别" width="140"></el-table-column>
      <el-table-column prop="locSendId" sortable label="所在配送中心编号" width="220"></el-table-column>
      <el-table-column prop="phone" sortable label="手机号" width="280"></el-table-column>
      <el-table-column prop="dateOfEntry" sortable label="入职时间" width="280"></el-table-column>
      <el-table-column prop="state" label="状态"></el-table-column>

      <el-table-column prop="operate" width="120px" align="right">
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
      rate:3,
      tableData2:[],
      tableData:[
        {
          name: '宋濂',
          sex: '男',
          locSendId: '2',
          phone:'17606022336',
          dateOfEntry:'1999-02-08',
          state: 'work',
        },{
          name: '黄玮',
          sex:"男",
          locSendId: '1',
          phone:'17606603210',
          dateOfEntry:'2001-01-17',
          state: 'leisure',
        },{
          name: '柳帝',
          sex:"女",
          locSendId: '1',
          phone:'13823764112',
          dateOfEntry:'2001-01-06',
          state: 'leisure',
        },{
          name:"牛蛙",
          sex: '男',
          phone:'13509655113',
          dateOfEntry:'2000-10-25',
          locSendId: '2',
          state: 'rest',
        },
      ],
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
      this.$axios.get(this.$httpUrl + '/transporter')
          .then(res=>res.data)
          .then(res=>{
            this.tableData2 = res;
            console.log(res);
      })
    },
    handleSizeChange(val) {
      console.log(`每页 ${val} 条`);
    },
    handleCurrentChange(val) {
      console.log(`当前页: ${val}`);
    },
    getChangeValue(){
      if(this.select === 2){
        this.inputIconClass = 'el-icon-search';
      }else if(this.select === 3){
        this.inputIconClass = 'el-icon-locSendId-outline';
      }else{
        this.inputIconClass = 'el-icon-user';
      }
    },
    getDateRange(){
      console.log(this.date);
    },
    handleClickLook(row) {
      console.log(row.id);
      console.log(row.date);
      console.log(row.name);
      console.log(row.address);
    },
    handleSelectionChange(val) {
      if(val.length) this.handleDisable = false;
      else this.handleDisable = true;
      this.currentSelected = val;
    },
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
          console.log(row.id + " " + row.name)
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