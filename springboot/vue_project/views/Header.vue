<template>
  <div class="root">
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
  </div>
</template>

<script>
export default {
  name: "Header",
  data(){
    return {
      username:"宋濂",
      collapseBtnClass:'el-icon-s-fold',
      sideNavState:'收起',
      sideWidth:220,
      isCollapse:false
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
      console.log(111);
      this.$bus.$emit('sendCollapseBtnClass',this.collapseBtnClass);
      this.$bus.$emit('sendSideNavState',this.sideNavState);
      this.$bus.$emit('sendSideWidth',this.sideWidth);
      this.$bus.$emit('sendIsCollapse',this.isCollapse);
    },
  },

}
</script>

<style scoped>
  .root{
    width: 100%;
    color: #333;
    line-height: 60px;
    display: flex;
  }
</style>