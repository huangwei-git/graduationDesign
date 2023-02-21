package com.songlian.logistics.service.impl;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.songlian.logistics.common.QueryPageParam;
import com.songlian.logistics.common.Result;
import com.songlian.logistics.dao.UserDao;
import com.songlian.logistics.exception.RequestExpcetion;
import com.songlian.logistics.pojo.Location;
import com.songlian.logistics.pojo.User;
import com.songlian.logistics.service.LocationService;
import com.songlian.logistics.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 黄玮
 * @since 2023-02-17
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

    @Resource
    private UserDao userDao;
    @Autowired
    private LocationService ls;

    private int saveNum = 0;

    @Override
    public Result pageList(QueryPageParam query) {
        try {

            // 获取参数
            long size = (long) query.getPageSize();
            long num = (long) query.getPageNum();
            String name = (String) query.getParams().get("name");
            String suid = (String) query.getParams().get("uid");
            Integer uid = null;

            if(suid.length() > 0){
                try {
                    uid = Integer.parseInt(suid);
                }catch (Exception e){ uid = -1;}
            }else uid = null;

            String sortField = (String) query.getParams().get("sortField");
            String sortDirection = (String) query.getParams().get("sortDirection");

            String phone = (String) query.getParams().get("phone");
            String sjob = (String) query.getParams().get("job");
            Integer job = null;
            try {
                job = Integer.parseInt(sjob);
            }catch (Exception e){}

            String state = (String) query.getParams().get("state");
            String sex = (String) query.getParams().get("selectSex");
            Integer position = null;
            try {
                position = (Integer) query.getParams().get("position");
            }catch (Exception e){}

            String startTime = (String) query.getParams().get("startTime");
            String endTime = (String) query.getParams().get("endTime");
            // 初始化分页信息
            Page<User> page = new Page<>();
            page.setSize(size);
            page.setCurrent(num);
            // 查询条件
            LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper();
            if(!sortField.equals("null") && StringUtils.isNotBlank(sortField)){
                if(sortDirection.equals("1")){
                    if(sortField.equals("uid"))lqw.orderByDesc(User::getUid);
                    else if(sortField.equals("name"))lqw.orderByDesc(User::getName);
                    else if(sortField.equals("sex"))lqw.orderByDesc(User::getSex);
                    else if(sortField.equals("locSendId"))lqw.orderByDesc(User::getLocSendId);
                    else if(sortField.equals("phone"))lqw.orderByDesc(User::getPhone);
                    else if(sortField.equals("dateOfEntry"))lqw.orderByDesc(User::getDateOfEntry);
                    else if(sortField.equals("state"))lqw.orderByDesc(User::getState);
                    else if(sortField.equals("job"))lqw.orderByDesc(User::getJob);
                }else{
                    if(sortField.equals("uid"))lqw.orderByAsc(User::getUid);
                    else if(sortField.equals("name"))lqw.orderByAsc(User::getName);
                    else if(sortField.equals("sex"))lqw.orderByAsc(User::getSex);
                    else if(sortField.equals("locSendId"))lqw.orderByAsc(User::getLocSendId);
                    else if(sortField.equals("phone"))lqw.orderByAsc(User::getPhone);
                    else if(sortField.equals("dateOfEntry"))lqw.orderByAsc(User::getDateOfEntry);
                    else if(sortField.equals("state"))lqw.orderByAsc(User::getState);
                    else if(sortField.equals("job"))lqw.orderByAsc(User::getJob);
                }
            }
            lqw.eq(uid != null,User::getUid,uid);
            lqw.like(!name.equals("null") && StringUtils.isNoneBlank(name),User::getName,name);
            lqw.eq(!sex.equals("null") && StringUtils.isNoneBlank(sex), User::getSex, sex);
            lqw.like(!phone.equals("null") && StringUtils.isNoneBlank(phone), User::getPhone, phone);
            lqw.eq(state != null && StringUtils.isNotBlank(state),User::getState,state);
            lqw.eq(job != null,User::getJob,job);
            lqw.eq( position != null,User::getLocSendId,position);
            lqw.ge(startTime!= null && StringUtils.isNotBlank(startTime),User::getDateOfEntry,startTime);
            lqw.le(endTime!= null && StringUtils.isNotBlank(endTime),User::getDateOfEntry,endTime);
            IPage iPage = this.page(page, lqw);
            return Result.success(iPage.getRecords(),iPage.getTotal());
        }catch (Exception e){
            System.out.println("UserController.listByPage:" + e);
            return Result.fail(e.toString());
        }
    }

    @Override
    public void exportData(HttpServletResponse response) throws Exception {
        // 1. 从数据库中查出所有数据
        List list = userDao.exportUserInfo();

        // 2. 创建工具类，写出到浏览器
        // hutool.cn/docs/#/poi/Excel生成-ExcelWriter
        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        // 3. 定义表头映射字段
        excelWriter.addHeaderAlias("uid","用户ID");
        excelWriter.addHeaderAlias("name","姓名");
        excelWriter.addHeaderAlias("sex","性别");
        excelWriter.addHeaderAlias("email","邮箱");
        excelWriter.addHeaderAlias("phone","手机号");
        excelWriter.addHeaderAlias("job","职位");
        excelWriter.addHeaderAlias("dateOfEntry","入职时间");
        excelWriter.addHeaderAlias("location","工作地点");
        excelWriter.addHeaderAlias("state","状态");
        // 4. 写出list内容到Excel文件使用默认样式，强制输出标题
        excelWriter.write(list,true);

        response.setContentType("application/vnd.openxmlformats-officedocument.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("员工信息","UTF-8");
        response.setHeader("Content-Disposition","attachment;filename=" + fileName + ".xlsx");


        ServletOutputStream out = response.getOutputStream();
        excelWriter.flush(out, true);
        out.close();
        excelWriter.close();
    }

    /**
     * Excel导入数据
     * @param file
     * @throws Exception
     */
    @Override
    public Result importData(MultipartFile file) throws Exception {
        LambdaQueryWrapper<Location> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Location::getType, 0);
        HashMap<String,Integer> locMapId = new HashMap<>();
        ls.list(lqw).forEach(loc -> {
            locMapId.put(loc.getName(),loc.getLocId());
        });
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);

        HashMap<String,Object> map = new HashMap();

        List<Map<String, Object>> list = reader.readAll();
        list.forEach(item -> {
            User user = new User();
            try {
                long phone = (long)item.get("手机号");
                user.setName((String) item.get("姓名"));
                user.setSex((String) item.get("性别"));
                user.setPhone(Long.toString((Long) item.get("手机号")));
                user.setEmail((String) item.get("邮箱"));
                String job = (String) item.get("职位");
                if(job.equals("管理员")) user.setJob(0);
                else user.setJob(1);
                if(user.getJob() == 0) user.setLocSendId(0);
                else{
                    String locName = (String) item.get("工作地点");
                    user.setLocSendId(locMapId.get(locName));
                }
            }catch (RequestExpcetion e){
                System.out.println("错辣！");
                throw e;
            }
            System.out.println("user = " + user);
            if(this.save(user)) saveNum++;
        });
        System.out.println(saveNum);
        return Result.success(null,saveNum);
    }

    @Override
    public Result userCountData() {
        LinkedList<Integer> leisure = new LinkedList<>();
        LinkedList<Integer> work = new LinkedList<>();
        LinkedList<Integer> rest = new LinkedList<>();
        LinkedList<String> loc = new LinkedList<>();
        List<HashMap> list1 = userDao.getNum("leisure");
        List<HashMap> list2 = userDao.getNum("work");
        List<HashMap> list3 = userDao.getNum("rest");
        for(int i = 0;i < list1.size();i++){
            leisure.addLast(Math.toIntExact((Long) list1.get(i).get("num")));
            work.addLast(Math.toIntExact((Long) list2.get(i).get("num")));
            rest.addLast(Math.toIntExact((Long) list3.get(i).get("num")));
            loc.addLast((String) list1.get(i).get("locName"));
        }
        Map<String, Object[]> map = new HashMap<>();
        map.put("locName",loc.toArray());
        map.put("leisure",leisure.toArray());
        map.put("work",work.toArray());
        map.put("rest",rest.toArray());
        return Result.success(map);
    }

    @Override
    public Result userSexCount() {
        Integer male = userDao.getSexNum("男");
        Integer female = userDao.getSexNum("女");
        Map<String,Integer> map = new HashMap<>();
        map.put("male",male);
        map.put("female",female);
        return Result.success(map);
    }
}
