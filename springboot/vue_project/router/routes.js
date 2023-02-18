import VueRouter from "vue-router";
import Home from "../views/Home.vue";

export default new VueRouter({
    routes:[
        // {path:'/route',component:'componentName'},
        {path:'/home',component:Home,name:"home"},
    ]
})