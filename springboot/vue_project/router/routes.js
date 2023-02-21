import VueRouter from "vue-router";
import Home from "../views/home/Home.vue";
import Login from "../views/login/Login.vue"

import UserInfo from "../views/home/Main.vue"
import LocationInfo from "../views/inventory/LocationInfo.vue"
import MaterialCount from "../views/material/MaterialCount.vue"
import MaterialInfo from "../views/material/materialInfo.vue"
import StoreInfo from "../views/inventory/StoreInfo";

export default new VueRouter({
    routes:[
        // {path:'/route',component:'componentName'},
        {
            path:'/',
            component:Home,
            name:"home",
            redirect:"/userInfo",
            children:[
                {
                    path:'userInfo',
                    component:UserInfo
                },
                {
                    path:'locationInfo',
                    component:LocationInfo
                },
                {
                    path:'materialInfo',
                    component:MaterialInfo
                },
                {
                    path:'materialCount',
                    component:MaterialCount
                },
                {
                    path:'storeInfo',
                    component:StoreInfo
                },
            ]
        },
        {
            path:'/login',
            component:Login,
            name:"login"
        },
    ]
})