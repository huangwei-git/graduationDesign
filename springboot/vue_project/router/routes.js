import VueRouter from "vue-router";
import Home from "../views/home/Home.vue";
import Login from "../views/login/Login.vue"

import UserInfo from "../views/User/UserInfo.vue"
import UserEcharts from "../views/User/UserEcharts";
import LocationInfo from "../views/inventory/LocationInfo.vue"
import MaterialCount from "../views/material/MaterialCount.vue"
import MaterialInfo from "../views/material/materialInfo.vue"
import StoreInfo from "../views/inventory/StoreInfo";
import MaterialEcharts from "../views/material/MaterialEcharts";
import AddOrder from "../views/order/AddOrder";
import OrderList from "../views/order/OrderList";
import OrderDetailList from "../views/order/OrderDetailList";
import LocationEcharts from "../views/inventory/LocationEcharts";

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
                    path:'userEcharts',
                    component:UserEcharts
                },
                {
                    path:'locationInfo',
                    component:LocationInfo,
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
                {
                    path:'orderList',
                    component:OrderList,
                },
                {
                    path:'orderDetailList',
                    component:OrderDetailList
                },
                {
                    path:"locationEcharts",
                    component:LocationEcharts
                }
            ]
        },
        {
            path:'/login',
            component:Login,
            name:"login"
        },
    ]
})