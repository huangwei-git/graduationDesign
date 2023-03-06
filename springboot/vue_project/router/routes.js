import VueRouter from "vue-router";
import Home from "../views/home/Home.vue";
import Login from "../views/login/Login.vue"

import UserInfo from "../views/User/UserInfo.vue"
import UserEcharts from "../views/User/UserEcharts";
import LocationInfo from "../views/inventory/LocationInfo.vue"
import MaterialCount from "../views/material/MaterialCount.vue"
import MaterialInfo from "../views/material/materialInfo.vue"
import StoreInfo from "../views/inventory/StoreInfo";
import OrderList from "../views/order/OrderList";
import OrderDetailList from "../views/order/OrderDetailList";
import LocationEcharts from "../views/inventory/LocationEcharts";
import EmpArrange from "../views/empArrange/EmpArrange";
import PersonalHomepage from "../views/personalPage/PersonalHomepage";

export default new VueRouter({
    routes:[
        // {path:'/route',component:'componentName'},
        {
            path:'/',
            redirect:{
                name:"login"
            },
            component:Home,
            children:[
                {
                    path:'userInfo',
                    component:UserInfo,
                    meta:{
                        title:'员工信息'
                    }
                },
                {
                    path:'userEcharts',
                    component:UserEcharts,
                    meta:{
                        title:'图表分析'
                    }
                },
                {
                    path:'locationInfo',
                    component:LocationInfo,
                    meta:{
                        title:'地址信息'
                    }
                },
                {
                    path:'materialInfo',
                    component:MaterialInfo,
                    meta:{
                        title:'物品信息'
                    }
                },
                {
                    path:'materialCount',
                    component:MaterialCount,
                    meta:{
                        title:'物品数量统计'
                    }
                },
                {
                    path:'storeInfo',
                    component:StoreInfo,
                    meta:{
                        title:'仓库信息'
                    }
                },
                {
                    path:'orderList',
                    component:OrderList,
                    meta:{
                        title:'订单列表'
                    }
                },
                {
                    path:'orderDetailList',
                    component:OrderDetailList,
                    meta:{
                        title:'订单详情'
                    }
                },
                {
                    path:"locationEcharts",
                    component:LocationEcharts,
                    meta:{
                        title:'地图'
                    }
                },
                {
                    path:'empArrange',
                    component:EmpArrange,
                    meta:{
                        title:'员工安排'
                    }
                },
                {
                    path:'personalPage',
                    component:PersonalHomepage,
                    meta:{
                        title:'个人主页'
                    }
                },
            ]
        },
        {
            path:'/login',
            component:Login,
            name:"login",
            meta:{
                title:'登录'
            }
        },
    ]
})
