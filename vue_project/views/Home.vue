<template>
  <div>
    <el-container style="min-height: 100vh">
      <el-aside :width="sideWidth.valueOf() + 'px'">
        <el-menu :default-openeds="['1']"
                  background-color="rgb(149,8,66)"
                 text-color="#fff"
                 active-text-color="#67C23A"
                 :collapse-transition="false"
                 :collapse="isCollapse"
                 :unique-opened="true"
        >
          <div style="height: 60px;line-height: 60px;text-align: center;color: #fff;font-size: 22px;">
            <img src="../src/assets/helloworld.png" alt="logo" style="width: 45px;position: relative;top: 8px">
            <b v-show="logoTextShow">&nbsp;&nbsp;智慧物流平台</b>
          </div>

          <el-submenu index="1">
            <template slot="title">
              <i class="el-icon-user"></i>
              <span slot="title">员工管理</span>
            </template>
            <el-menu-item-group>
              <template slot="title">分组一</template>
              <el-menu-item index="1-1"><router-link :to="{name:'empInfo'}">员工信息</router-link></el-menu-item>
            </el-menu-item-group>
          </el-submenu>

          <el-submenu index="2">
            <template slot="title">
              <i class="el-icon-office-building"></i>
              <span slot="title">仓库管理</span>
            </template>
            <el-menu-item-group>
              <template slot="title">分组一</template>
              <el-menu-item index="2-1">选项1</el-menu-item>
              <el-menu-item index="2-2">选项2</el-menu-item>
            </el-menu-item-group>
            <el-menu-item-group title="分组2">
              <el-menu-item index="2-3">选项3</el-menu-item>
            </el-menu-item-group>
            <el-submenu index="2-4">
              <template slot="title">选项4</template>
              <el-menu-item index="2-4-1">选项4-1</el-menu-item>
            </el-submenu>
          </el-submenu>

          <el-submenu index="3">
            <template slot="title">
              <i class="el-icon-box"></i>
              <span slot="title">物品管理</span>
            </template>
            <el-menu-item-group>
              <template slot="title">分组一</template>
              <el-menu-item index="3-1">选项1</el-menu-item>
              <el-menu-item index="3-2">选项2</el-menu-item>
            </el-menu-item-group>
            <el-menu-item-group title="分组2">
              <el-menu-item index="3-3">选项3</el-menu-item>
            </el-menu-item-group>
            <el-submenu index="3-4">
              <template slot="title">选项4</template>
              <el-menu-item index="3-4-1">选项4-1</el-menu-item>
            </el-submenu>
          </el-submenu>

          <el-submenu index="4">
            <template slot="title">
              <i class="el-icon-notebook-2"></i>
              <span slot="title">订单管理</span>
            </template>
            <el-menu-item-group>
              <template slot="title">分组一</template>
              <el-menu-item index="3-1">选项1</el-menu-item>
              <el-menu-item index="3-2">选项2</el-menu-item>
            </el-menu-item-group>
          </el-submenu>

          <el-submenu index="5">
            <template slot="title">
              <i class="el-icon-guide"></i>
              <span slot="title">运输计划</span>
            </template>
            <el-menu-item-group>
              <template slot="title">分组一</template>
              <el-menu-item index="3-1">选项1</el-menu-item>
              <el-menu-item index="3-2">选项2</el-menu-item>
            </el-menu-item-group>
          </el-submenu>

          <el-submenu index="6">
            <template slot="title">
              <i class="el-icon-pie-chart"></i>
              <span slot="title">图表分析</span>
            </template>
            <el-menu-item-group>
              <template slot="title">分组一</template>
              <el-menu-item index="3-1">选项1</el-menu-item>
              <el-menu-item index="3-2">选项2</el-menu-item>
            </el-menu-item-group>
          </el-submenu>
        </el-menu>
      </el-aside>

      <el-container>
        <el-header>
          <div style="flex:1;font-size: 18px">
            <span :class="collapseBtnClass" style="cursor: pointer" @click="collapse">
              <span style="color: #aaa"> {{sideNavState}}</span>
            </span>
          </div>

          <el-dropdown style="width: 70px;">
            <div style="cursor: pointer">
              <span>宋濂</span>
              <i class="el-icon-arrow-down" style="margin-left: 5px"></i>
            </div>
            <el-dropdown-menu align="center" slot="dropdown">
              <el-dropdown-item>个人主页</el-dropdown-item>
              <el-dropdown-item>退出</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </el-header>

        <!--<el-main>-->
          <router-view></router-view>
        <!--  &lt;!&ndash; 搜索栏 &ndash;&gt;-->
        <!--  <div style="margin-bottom: 20px">-->
        <!--    <el-date-picker-->
        <!--        @change="getDateRange"-->
        <!--        :editable="false"-->
        <!--        v-model="date"-->
        <!--        type="daterange"-->
        <!--        start-placeholder="开始日期"-->
        <!--        end-placeholder="结束日期"-->
        <!--        :default-time="['00:00:00', '23:59:59']">-->
        <!--    </el-date-picker>-->
        <!--    <el-input placeholder="请输入内容" :suffix-icon="inputIconClass" v-model="input" class="input-with-select">-->
        <!--      <el-select @change="getChangeValue" v-model="select" slot="prepend" placeholder="请选择">-->
        <!--        <el-option label="姓名" :value="1"></el-option>-->
        <!--        <el-option label="工号" :value="2"></el-option>-->
        <!--        <el-option label="仓库" :value="3"></el-option>-->
        <!--      </el-select>-->
        <!--    </el-input>-->
        <!--    <el-button type="primary" style="margin-left: 5px">搜索</el-button>-->
        <!--  </div>-->
        <!--  <el-button-group style="margin-left: 5px;">-->
        <!--    <el-button type="primary" icon="el-icon-plus">新增员工</el-button>-->
        <!--    <el-button type="primary" icon="el-icon-minus" @click="handlerDeleteAllSelected" :disabled="handleDisable">批量删除</el-button>-->
        <!--    <el-button type="primary" icon="el-icon-upload2">导入数据</el-button>-->
        <!--    <el-button type="primary" icon="el-icon-download">导出数据</el-button>-->
        <!--  </el-button-group>-->

        <!--  <el-table-->
        <!--      ref="mutiTable"-->
        <!--      :data="tableData"-->
        <!--      stripe-->
        <!--      @selection-change="handleSelectionChange"-->
        <!--      :default-sort = "{prop: 'id', order: 'inscending'}"-->
        <!--      :cell-style="{ textAlign: 'center' }"-->
        <!--      :header-cell-style="{ textAlign: 'center' }"-->
        <!--      @row-click="storeClickedRowInfo"-->
        <!--  >-->
        <!--    <el-table-column align="center" type="selection" width="45"></el-table-column>-->
        <!--    <el-table-column type="index" width="50">-->
        <!--      <template slot="header" slot-scope="scope">序号</template>-->
        <!--    </el-table-column>-->
        <!--    <el-table-column prop="id" sortable label="工号" width="140"></el-table-column>-->
        <!--    <el-table-column prop="date" sortable label="日期" width="140"></el-table-column>-->
        <!--    <el-table-column prop="name" sortable label="姓名" width="120"></el-table-column>-->
        <!--    <el-table-column prop="address" label="地址"></el-table-column>-->
        <!--    <el-table-column prop="operate" width="120px" align="right">-->
        <!--      <template slot="header" slot-scope="scope">操作</template>-->
        <!--      <template slot-scope="scope">-->
        <!--        <el-button @click="handleClickLook(scope.row)" type="text" size="small">查看</el-button>-->
        <!--        <el-button type="text" size="small">编辑</el-button>-->
        <!--        <el-button type="text" size="small">删除</el-button>-->
        <!--      </template>-->
        <!--    </el-table-column>-->
        <!--  </el-table>-->

        <!--  &lt;!&ndash; 分页 &ndash;&gt;-->
        <!--  <div style="padding: 10px 0;text-align: center">-->
        <!--    <el-pagination-->
        <!--        background-->
        <!--        @size-change="handleSizeChange"-->
        <!--        @current-change="handleCurrentChange"-->
        <!--        :current-page.sync="currentPage"-->
        <!--        :page-size="10"-->
        <!--        layout="total,prev, pager, next, jumper"-->
        <!--        :total="totalPage"-->
        <!--        :hide-on-single-page="true">-->
        <!--    </el-pagination>-->
        <!--  </div>-->
        <!--</el-main>-->
      </el-container>
    </el-container>
  </div>
</template>

<script>
export default {
  name: "Home",
  data(){
    return {
      isCollapse:false,
      sideNavState:'收起',
      collapseBtnClass:'el-icon-s-fold',
      logoTextShow:true,
      sideWidth:220,
      // tableData:[
      //   {
      //     id:"2019024044",
      //     date: '2022-01-14',
      //     name: '宋濂',
      //     address: '广东省深圳市南山区粤海街道'
      //   },{
      //     id:"2019024048",
      //     date: '2021-03-15',
      //     name: '黄玮',
      //     address: '广东省深圳市南山区粤海街道'
      //   },{
      //     id:"2019024046",
      //     date: '2001-01-17',
      //     name: '柳帝',
      //     address: '广东省深圳市南山区粤海街道'
      //   },{
      //     id:"2019024047",
      //     date: '2019-08-13',
      //     name: '马克思',
      //     address: '广东省深圳市南山区粤海街道'
      //   },{
      //     id:"2019024048",
      //     date: '2018-06-01',
      //     name: '牛肉丸',
      //     address: '广东省深圳市南山区粤海街道'
      //   },{
      //     id:"2019024049",
      //     date: '2017-12-12',
      //     name: '牛蛙',
      //     address: '广东省深圳市南山区粤海街道'
      //   },
      // ],
      // input:'',
      // select:1,
      // date:'',
      // currentPage:1,
      // totalPage:22,
      // inputIconClass:'el-icon-user',
      // handleDisable:true,
      // currentSelected:[],
      // clickedRowInfo:null,
    }
  },
  methods:{
    collapse(){
      this.isCollapse = !this.isCollapse;
      this.logoTextShow = !this.logoTextShow;
      if(this.isCollapse){
        this.collapseBtnClass = 'el-icon-s-unfold';
        this.sideWidth = 64;
        this.sideNavState = '展开'
      }else{
        this.collapseBtnClass = 'el-icon-s-fold';
        this.sideWidth = 220;
        this.sideNavState = '收起'
      }
    },
    // handleSizeChange(val) {
    //   console.log(`每页 ${val} 条`);
    // },
    // handleCurrentChange(val) {
    //   console.log(`当前页: ${val}`);
    // },
    // getChangeValue(){
    //   if(this.select === 2){
    //     this.inputIconClass = 'el-icon-search';
    //   }else if(this.select === 3){
    //     this.inputIconClass = 'el-icon-location-outline';
    //   }else{
    //     this.inputIconClass = 'el-icon-user';
    //   }
    // },
    // getDateRange(){
    //   console.log(this.date);
    // },
    // handleClickLook(row) {
    //   console.log(row.id);
    //   console.log(row.date);
    //   console.log(row.name);
    //   console.log(row.address);
    // },
    // handleSelectionChange(val) {
    //   if(val.length) this.handleDisable = false;
    //   else this.handleDisable = true;
    //   this.currentSelected = val;
    // },
    // handlerDeleteAllSelected(){
    //   this.$confirm('此操作将永久删除所选记录, 是否继续?', '提示', {
    //     confirmButtonText: '确定',
    //     cancelButtonText: '取消',
    //     type: 'warning'
    //   }).then(() => {
    //     console.log("需要删除：")
    //     this.currentSelected.forEach(item => {
    //       console.log(item.id,item.date,item.name)
    //     })
    //     this.$notify.success({
    //       title:"删除成功！",
    //       message:"删除"+this.currentSelected.length+"条记录",
    //     })
    //     console.log("共"+this.currentSelected.length+"个")
    //   }).catch(() => {
    //     this.$notify.info({
    //       type: 'info',
    //       message: '已取消删除',
    //     });
    //   });
    // },
    // storeClickedRowInfo(row,column,event){
    //   if(column.property === "operate"
    //       && event.target.innerText.includes("删除")){
    //     this.$confirm(`是否要将${row.name}的信息从表中移除？`, '提示', {
    //       confirmButtonText: '确定',
    //       cancelButtonText: '取消',
    //       type: 'warning'
    //     }).then(() => {
    //       this.$notify.success({
    //         title:"删除成功！",
    //         message:row.id + " " + row.name,
    //       })
    //       console.log("共"+this.currentSelected.length+"个")
    //     }).catch(() => {
    //       this.$notify.info({
    //         type: 'info',
    //         message: '已取消删除',
    //       });
    //     });
    //   }
    // }
  }
}
</script>

<style scoped>
  .el-header {
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    color: #333;
    font-size: 12px;
    line-height: 60px;
    display: flex;
  }

  .el-aside {
    color: #333;
    /*background-color:rgb(149,8,66);*/
    box-shadow: 2px 0 6px rgb(0 21 41 / 35%);
  }

  .el-menu{
    min-height: 100%;
    overflow-x: hidden;
  }

  /*.el-select{*/
  /*  width: 90px;*/
  /*}*/

  /*.input-with-select,.el-input-group__prepend {*/
  /*  width: 300px;*/
  /*  margin-left: 5px;*/
  /*  background-color: #fff;*/
  /*}*/
</style>