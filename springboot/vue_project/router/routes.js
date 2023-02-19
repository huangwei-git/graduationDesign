import VueRouter from "vue-router";
import Home from "../views/Home.vue";
import Login from "../views/login/Login"

export default new VueRouter({
    routes:[
        // {path:'/route',component:'componentName'},
        {path:'/home',component:Home,name:"home"},
        {path:'/login',component:Login,name:"login"},
    ]
})