import axios from 'axios'
import {error} from "@babel/eslint-parser/lib/convert";

const request = axios.create({
    baseURL: '/test',
    timeout:5000
})

// request 拦截器
//      请求发送前做一些处理：如统一加token
request.interceptors.request.use(config => {
    // 处理请求头
    config.headers['Content-Type'] = 'application/json;charset=uft-8';

    //config.headers['token'] = user.token;
    return config;
},error => {
    console.log(error);
    return Promise.reject(error);
});

// response 拦截器
//      可以在接口响应后统一处理结果
request.interceptors.response.use(response => {
    let res = response.data;
    // 文件
    if(response.config.responseType === 'blob'){
        return res;
    }
    // 字符串数据
    if(typeof res === 'string'){
        res = res ? JSON.parse(res) : res;
    }
    return res;
},error => {
    console.log('err' + error);
    return Promise.reject(error);
})

export default request;