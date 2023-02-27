<template>
  <div id="root">
    <el-descriptions class="margin-top" title="个人信息" :column="4" size="medium" border direction="vertical" style="width: 100%">
      <template slot="extra">
        <el-button type="text" class="el-icon-plus" size="small" @click="addDialogVisible = true">添加管理员</el-button>
        <el-button type="text" class="el-icon-edit-outline" size="small" @click="dialogVisible = true">编辑</el-button>
      </template>

      <el-descriptions-item>
        <template slot="label">
          <i class="el-icon-coordinate"></i>
          ID
        </template>
        {{userForm.uid}}
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          <i class="el-icon-bank-card"></i>
          账号
        </template>
        {{userForm.username}}
      </el-descriptions-item>

      <el-descriptions-item>
        <template slot="label">
          <i class="el-icon-user"></i>
          姓名
        </template>
        {{userForm.name}}
      </el-descriptions-item>

      <el-descriptions-item>
        <template slot="label">
          <i class="el-icon-tickets"></i>
          权限
        </template>
        <el-tag size="small" type="danger">系统管理员</el-tag>
      </el-descriptions-item>

      <el-descriptions-item>
        <template slot="label">
          <i class="el-icon-location-outline"></i>
          位置
        </template>
        {{userForm.address}}
      </el-descriptions-item>

      <el-descriptions-item>
        <template slot="label">
          <i class="el-icon-mobile-phone"></i>
          手机号
        </template>
        {{userForm.phone}}
      </el-descriptions-item>

      <el-descriptions-item>
        <template slot="label">
          <i class="el-icon-message"></i>
          邮箱
        </template>
        {{userForm.email}}
      </el-descriptions-item>

      <el-descriptions-item>
        <template slot="label">
          <i class="el-icon-time"></i>
          创建时间
        </template>
        {{userForm.createTime}}
      </el-descriptions-item>
    </el-descriptions>

    <!--编辑用户的表单-->
    <el-dialog
        title="提示"
        :visible.sync="dialogVisible"
        width="580px">

      <el-form  class="sl-no" :rules="rules" ref="ruleForm" status-icon :model="form" :inline="true" label-position="right" style="text-align: center">
        <el-form-item label="ID" :label-width="formLabelWidth" prop="uid">
          <el-input class="form_input" size="small" :disabled="true" v-model="form.uid" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="账号" :label-width="formLabelWidth" prop="username">
          <el-input class="form_input" size="small" v-model="form.username" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="姓名" :label-width="formLabelWidth" prop="name">
          <el-input class="form_input" size="small" v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="位置" :label-width="formLabelWidth" prop="address">
          <el-input class="form_input" size="small" v-model="form.address" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="手机" :label-width="formLabelWidth" prop="phone">
          <el-input class="form_input" size="small" v-model.number="form.phone" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" :label-width="formLabelWidth" prop="email">
          <el-input class="form_input" size="small" v-model="form.email" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="原密码" :label-width="formLabelWidth" prop="oldPassword">
          <el-input class="form_input" size="small" v-model="form.oldPassword"  show-password autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="新密码" :label-width="formLabelWidth" prop="newPassword">
          <el-input class="form_input" size="small" v-model="form.newPassword"  show-password autocomplete="off"></el-input>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitUserForm">确 定</el-button>
      </div>
    </el-dialog>

    <!--添加用户的表单-->
    <el-dialog
        title="提示"
        :visible.sync="addDialogVisible"
        width="580px">

      <el-form  class="sl-no" :rules="rules" ref="ruleForm" status-icon :model="addForm" :inline="true" label-position="right" style="text-align: center">
        <el-form-item label="ID" :label-width="formLabelWidth" prop="uid">
          <el-input class="form_input" size="small" :disabled="true" v-model="addForm.uid" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="账号" :label-width="formLabelWidth" prop="username">
          <el-input class="form_input" size="small" v-model="addForm.username" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="姓名" :label-width="formLabelWidth" prop="name">
          <el-input class="form_input" size="small" v-model="addForm.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="位置" :label-width="formLabelWidth" prop="address">
          <el-input class="form_input" size="small" v-model="addForm.address" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="手机" :label-width="formLabelWidth" prop="phone">
          <el-input class="form_input" size="small" v-model.number="addForm.phone" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" :label-width="formLabelWidth" prop="email">
          <el-input class="form_input" size="small" v-model="addForm.email" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="密码" :label-width="formLabelWidth" prop="password">
          <el-input class="form_input" size="small" v-model="addForm.password"  show-password autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="确认密码" :label-width="formLabelWidth" prop="requirePassword">
          <el-input class="form_input" size="small" v-model="addForm.requirePassword"  show-password autocomplete="off"></el-input>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="addDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitAddUserForm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "PersonalHomepage",
  data(){
    // 验证两次密码是否相同
    var validatePass = (rule, value, callback) => {
          if (value === '') {
            callback(new Error('请再次输入密码'));
          } else if (value !== this.addForm.password) {
            callback(new Error('两次输入密码不一致!'));
          } else {
            callback();
          }
        };
    return{
      userForm:{},
      dialogVisible: false,
      addDialogVisible:false,
      formLabelWidth: '80px',
      // 编辑用户的表达
      form: {
        uid: '',
        username: '',
        name: '',
        address: '',
        phone: '',
        createTime: '',
        email: '',
        oldPassword: '',
        newPassword: '',
        password: '',
        deletd: '',
      },
      // 添加用户的表单
      addForm: {
        uid: null,
        username: '',
        name: '',
        address: '',
        phone: '',
        email: '',
        password: '',
        requirePassword: '',
        createTime: null,
        deletd: 0,
      },
      // 表单项填写规则
      rules: {
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' },
          { min: 2, max: 5, message: '长度在 2 到 8 个字符', trigger: 'blur' },
          { pattern:/^[\u4e00-\u9fa5]*[a-zA-Z]*$/, message: '请输入中文或英文', trigger: 'blur'}
        ],
        username: [
          { required: true, message: '请输入账号', trigger: 'blur' },
          { min: 3, max: 12, message: '长度为 3 到 12 ', trigger: 'blur' },
          {pattern: /^[a-zA-Z]+[a-zA-Z0-9]{2,11}$/ ,message: '请输入正确的账号', trigger: 'blur'}
        ],
        address: [
          { required: true, message: '请输入地址', trigger: 'blur' },
          { pattern:/^[\u4e00-\u9fa5]*[a-zA-Z]*$/, message: '请输入中文或英文', trigger: 'blur'}
        ],
        phone: [
          { required: true, message: '手机号不能为空', trigger: 'blur' },
          {pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/ ,message: '请输入正确的手机号', trigger: 'blur'}
        ],
        email: [
          { required: true, message: '请选择输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        oldPassword: [
          { required: true, message: '请输入密码以保存编辑', trigger: 'blur' },
        ],
        newPassword: [
          {  min: 5, max: 16, message: '长度在 5 到 16 个字符', trigger: 'blur' },
          { pattern:/^[a-zA-Z0-9\.]{5,16}$/, message: '5-16位英文、数字或点', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          {  min: 5, max: 16, message: '长度在 5 到 16 个字符', trigger: 'blur' },
          { pattern:/^[a-zA-Z0-9\.]{5,16}$/, message: '5-16位英文、数字或点', trigger: 'blur' }
        ],
        requirePassword: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          {  min: 5, max: 16, message: '长度在 5 到 16 个字符', trigger: 'blur' },
          { validator: validatePass, trigger: 'blur' }
        ],
      }
    }
  },
  watch:{
    dialogVisible:{
      handler(nval,oval){
        if(nval == true){
          this.form.uid = this.userForm.uid;
          this.form.username = this.userForm.username;
          this.form.name = this.userForm.name;
          this.form.address = this.userForm.address;
          this.form.phone = this.userForm.phone;
          this.form.email = this.userForm.email;
          this.form.createTime = this.userForm.createTime;
          this.form.deleted = this.userForm.deleted;
          this.form.newPassword = '';
        }
      }
    },
    addDialogVisible:{
      handler(nval,oval){
        if(nval == true){
          this.addForm.username = '';
          this.addForm.name = '';
          this.addForm.address = '';
          this.addForm.phone = '';
          this.addForm.email = '';
          this.addForm.password = '';
          this.addForm.requirePassword = '';
        }
      }
    },
  },
  methods:{
    isChange(){
        if(
          this.form.username != this.userForm.username ||
          this.form.name != this.userForm.name ||
          this.form.address != this.userForm.address ||
          this.form.phone != this.userForm.phone ||
          this.form.email != this.userForm.email ||
          this.form.newPassword != '') return true;
        else return false;
    },
    submitUserForm(){
      this.$refs.ruleForm.validate(valid => {
        if(valid){
        this.dialogVisible = false;
          if(!this.isChange()) return ;
          if(this.form.oldPassword != this.userForm.password){
            this.$notify.error({
              title: '修改失败',
              message: "密码错误"
            });
            return ;
          }
          let isPasswordChange = false;
          if(this.form.newPassword.length >= 5 && this.form.newPassword != this.form.oldPassword){
            this.form.password = this.form.newPassword;
            isPasswordChange = true;
          }else{
            this.form.password = this.form.oldPassword;
          }
          this.$axios.put(this.$httpUrl + "/account",this.form)
              .then(res => res.data)
              .then(res => {
                if(res.code == 200){
                  if(!isPasswordChange){
                    this.$notify({
                      title: '成功！',
                      message: `信息修改成功`,
                      type: 'success'
                    });
                  }else{
                    this.$notify({
                      title: '成功！',
                      message: `信息修改成功，请重新登陆`,
                      type: 'success'
                    });
                    sessionStorage.removeItem("userForm");
                    this.$router.push("/login");
                  }
                }else {
                  this.$notify.error({
                    title: '修改失败',
                    message: res.msg
                  });
                }
              })
        }
      });
    },
    submitAddUserForm(){
      this.$refs.ruleForm.validate(valid => {
        if(valid){
        this.addDialogVisible = false;
          this.$axios.post(this.$httpUrl + "/account",this.addForm)
              .then(res => res.data)
              .then(res => {
                if(res.code == 200){
                  this.$notify({
                    title: '成功！',
                    message: `管理员添加成功`,
                    type: 'success'
                  });
                }else {
                  this.$notify.error({
                    title: '添加失败',
                    message: res.msg
                  });
                }
              })
        }
      });
    },

  },
  beforeMount() {
    this.userForm = JSON.parse(sessionStorage.getItem("userForm"));
  }
}
</script>

<style scoped>
.form_input{
  width: 180px;
}
</style>