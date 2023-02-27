<template>
  <el-menu :default-openeds="[]"
           background-color="rgb(149,8,66)"
           text-color="#fff"
           active-text-color="rgb(255,222,173)"
           :collapse-transition="false"
           :collapse="isCollapse"
           router>

    <div class="sl-no" style="height: 60px;line-height: 60px;text-align: center;color: #fff;font-size: 22px;">
      <img src="../../src/assets/helloworld.png" alt="logo" style="width: 45px;position: relative;top: 8px">
      <b v-show="logoTextShow">&nbsp;&nbsp;智慧物流平台</b>
    </div>

    <el-submenu index="1">
      <template slot="title">
        <i class="el-icon-user"></i>
        <span class="" slot="title">员工管理</span>
      </template>
        <el-menu-item index="/userInfo"><span>员工信息</span></el-menu-item>
        <el-menu-item index="/userEcharts"><span>图表分析</span></el-menu-item>
    </el-submenu>

    <el-submenu index="2">
      <template slot="title">
        <i class="el-icon-office-building"></i>
        <span slot="title">仓库管理</span>
      </template>
        <el-menu-item index="locationInfo"><span>地址信息</span></el-menu-item>
        <el-menu-item index="storeInfo"><span>仓库信息</span></el-menu-item>
    </el-submenu>

    <el-submenu index="3">
      <template slot="title">
        <i class="el-icon-box"></i>
        <span slot="title">物品管理</span>
      </template>
        <el-menu-item index="/materialInfo"><span>物品信息</span></el-menu-item>
        <el-menu-item index="/materialCount"><span>数量统计</span></el-menu-item>
    </el-submenu>

    <el-submenu index="4">
      <template slot="title">
        <i class="el-icon-notebook-2"></i>
        <span slot="title">订单管理</span>
      </template>
        <el-menu-item index="/orderList"><span>查看订单</span></el-menu-item>
        <el-menu-item index="/orderDetailList"><span>订单详情</span></el-menu-item>
    </el-submenu>

    <el-submenu index="5">
      <template slot="title">
        <i class="el-icon-cpu"></i>
        <span slot="title">资源配置</span>
      </template>
        <el-menu-item index="/empArrange"><span>员工安排</span></el-menu-item>
    </el-submenu>

  </el-menu>
</template>

<script>
export default {
  name: "Aside",
  data(){
    return {
      logoTextShow:true,
      isCollapse:false,
      collapseBtnClass:'el-icon-s-fold',
      sideNavState:'收起',
    }
  },
  methods:{

  },
  mounted() {
    this.$bus.$on("sendCollapseBtnClass",data => {
      this.collapseBtnClass = data;
    });
    this.$bus.$on("sendSideNavState",data => {
      this.sideNavState = data;
    });
    this.$bus.$on("sendIsCollapse",data => {
      this.isCollapse = data;
      console.log(this.isCollapse);
    });
  }
}
</script>

<style scoped>
  .el-menu{
    min-height: 100%;
    overflow-x: hidden;
  }

  .el-submenu__title i{
    color: rgb(255,222,173);
  }

  .el-submenu .el-menu-item:hover{
    background: #fc4668 !important;
  }
  .el-submenu .el-menu-item span{
    padding-left: 30px;
  }
</style>