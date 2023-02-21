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
          <div class="sl-no" style="height: 60px;line-height: 60px;text-align: center;color: #fff;font-size: 22px;">
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
          <div style="flex:1;font-size: 18px;">
            <span :class="collapseBtnClass" style="cursor: pointer;color: rgb(149,8,66)" @click="collapse">
              <span class="sl-no" style="color: rgb(149,8,66)"> {{sideNavState}}</span>
            </span>
          </div>

          <el-dropdown style="width: 100px;">
            <div style="cursor: pointer">
              <el-avatar
                         class="sl-no"
                         size="medium"
                         style="position: relative;
                         top: 10px;
                         right: 5px;
                         font-size: 18px;
                         background-color: rgb(149,8,66);
              ">
                {{username.substring(0,1)}} </el-avatar>
              <span style="font-size: 18px">{{username}}</span>
              <i class="el-icon-arrow-down sl-no" style="margin-left: 5px"></i>
            </div>
            <el-dropdown-menu align="center" slot="dropdown">
              <el-dropdown-item>个人主页</el-dropdown-item>
              <el-dropdown-item>退出</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </el-header>

        <el-main>
          <router-view />
        </el-main>
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
      username:"宋濂",
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
  }
}
</script>

<style scoped>
  .el-header {
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    color: #333;
    line-height: 60px;
    display: flex;
  }

  .el-aside {
    color: #333;
    box-shadow: 2px 0 6px rgb(0 21 41 / 35%);
  }

  .el-menu{
    min-height: 100%;
    overflow-x: hidden;
  }
</style>