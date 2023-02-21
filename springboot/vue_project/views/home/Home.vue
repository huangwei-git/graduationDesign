<template>
  <div>
    <el-container style="min-height: 100vh">
      <el-aside :width="sideWidth.valueOf() + 'px'">
        <Aside/>
      </el-aside>

      <el-container>
        <el-header>
          <Header/>
        </el-header>

        <el-main>
          <!--<Main/>-->
          <!--<MaterialInfo/>-->
          <!--<LocationInfo/>-->
          <!--<MaterialStore/>-->
          <router-view/>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import Aside from "./Aside.vue"
import Header from "./Header.vue"
import Main from "../User/UserInfo.vue"
import MaterialInfo from '../material/materialInfo.vue'
import LocationInfo from "../inventory/LocationInfo.vue";
import MaterialStore from "../material/MaterialCount.vue";

export default {
  name: "Home",
  data(){
    return {
      sideWidth:220,
    }
  },
  methods:{

  },
  components:{
    Aside,
    Header,
    Main,
    MaterialStore,
    LocationInfo,
    MaterialInfo,
  },mounted() {
    this.$bus.$on("sendSideWidth",data => {
      this.sideWidth = data;
    })
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

</style>