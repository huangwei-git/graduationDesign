<template>
  <div id="app">
      <router-view v-wechat-title="$route.meta.title" />
  </div>
</template>

<script>
import Home from "../views/home/Home.vue";
export default {
  name: 'App',
  data(){
    return{
      value:'true',
    }
  },
  components: {
    Home,
  },
  beforeDestroy() {
    sessionStorage.removeItem("userForm");
  }
}
</script>

<style>

</style>
