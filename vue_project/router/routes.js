import VueRouter from "vue-router";
import Home from "../views/Home.vue";
import EmpInfo from "../src/components/Employee/Information.vue"

export default new VueRouter({
    routes:[
        // {path:'/route',component:'componentName'},
        {path:'/home',component:Home,name:"home"},
        {path:'/test',component:EmpInfo,name:"empInfo"},
    ]
})