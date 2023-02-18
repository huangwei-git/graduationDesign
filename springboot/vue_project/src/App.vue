<template>
  <div id="app">
    <Home/>
  </div>
</template>

<script>
import Home from "../views/Home.vue";
export default {
  name: 'App',
  data(){
    return{
      value:'true',
    }
  },
  components: {
    Home,
  }
}
</script>

<style>
  #app{
  }
</style>
