import Vue from 'vue'
import App from './App.vue'
import Vuex from "vuex";
import router from "../router/routes.js";
import VueRouter from "vue-router";
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import './assets/globale.css'

Vue.config.productionTip = false

Vue.use(ElementUI)
Vue.use(VueRouter)
Vue.use(Vuex)
import store from '../store/store.js'

new Vue({
  render: h => h(App),
  store,
  router:router,
  beforeCreate() {
    Vue.prototype.$bus = this;
  },mounted() {
  }
}).$mount('#app')
