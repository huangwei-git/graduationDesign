<template>
  <el-menu :default-openeds="['1']"
           background-color="rgb(149,8,66)"
           text-color="#fff"
           active-text-color="#ff0"
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
        <span slot="title">员工管理</span>
      </template>
      <el-menu-item-group>
        <template slot="title">分组一</template>
        <el-menu-item index="/">员工信息</el-menu-item>
      </el-menu-item-group>
    </el-submenu>

    <el-submenu index="2">
      <template slot="title">
        <i class="el-icon-office-building"></i>
        <span slot="title">坐标信息</span>
      </template>
      <el-menu-item-group>
        <template slot="title">分组一</template>
        <el-menu-item index="locationInfo">坐标管理</el-menu-item>
        <el-menu-item index="storeInfo">仓库信息</el-menu-item>
      </el-menu-item-group>
    </el-submenu>

    <el-submenu index="3">
      <template slot="title">
        <i class="el-icon-box"></i>
        <span slot="title">物品信息</span>
      </template>
      <el-menu-item-group>
        <template slot="title">分组1</template>
        <el-menu-item index="/materialInfo">物品信息</el-menu-item>
        <el-menu-item index="/materialCount">数量统计</el-menu-item>
      </el-menu-item-group>
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
</style>