<template>
  <div class="wrapper sl-no">
    <div style="margin:200px;background-color: #fff;width: 350px;height: 300px;padding: 20px;border-radius: 10px">
      <div style="margin: 20px;text-align: center;font-size: 24px">
        <b>登 录</b>
      </div>
      <el-form :model="user" ref="loginForm" :rules="rules">
        <el-form-item prop="uid">
          <el-input size="medium" style="margin: 0px 0" prefix-icon="el-icon-user" v-model="user.uid"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input size="medium" style="margin: 0px 0" prefix-icon="el-icon-lock" show-password v-model="user.password"></el-input>
        </el-form-item>
      </el-form>
      <div style="margin: 10px 0;text-align: center">
        <el-button type="primary" size="small" @keyup.native.enter="login" @click="login" aria-autocomplete="none">登录</el-button>
        <el-button type="warning" size="small" aria-autocomplete="none">注册</el-button>
      </div>
    </div>
  </div>
</template>

<script>
// import axios from 'axios'
export default {
  name: "Login",
  data(){
    return{
      user:{
        uid:'',
        password:'',
      },
      rules:{
        uid:[
          {required:true, message:"账号不能为空", trigger:'blur'},
          {min:1, max: 8,message: '长度为1到8个字符', trigger: 'blur'}
        ],
        password:[
          {required:true, message:"密码不能为空", trigger:'blur'},
          {min:6, max: 16,message: '长度为6到18个字符', trigger: 'blur'}
        ]
      }
    }
  },
  methods:{
    login(){
      this.$refs.loginForm.validate(valid => {
        if(valid){
          this.$axios.post(this.$httpUrl + "/account/login",this.user)
              .then(res => res.data)
              .then(res => {
                if(res.code == 200){
                  // 将用户信息存储到浏览器
                  //localStorage.setItem("user",JSON.stringify(res.data.name));
                  sessionStorage.setItem("username",JSON.stringify(res.data.name));
                  sessionStorage.setItem("uid",JSON.stringify(this.uid));
                  sessionStorage.setItem("password",JSON.stringify(this.password));
                  // 页面跳转
                  this.$router.push("/home");
                  this.$notify({
                    title: '登录成功',
                    message: `你好，${res.data.name}登录成功`,
                    type: 'success'
                  });
                }else {
                  this.$notify.error({
                    title: '登录错误',
                    message: res.msg
                  });
                }
              })
        }
      })
    }
  }
}
</script>

<style scoped>
  .wrapper{
    height: 100vh;
    background-image: linear-gradient(to bottom right,#fc4668,#3f5ef8);
    overflow: hidden;
    display: flex;
    justify-content:center;
    align-items:center;
  }
</style>