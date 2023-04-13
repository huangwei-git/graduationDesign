package com.songlian.logistics.service.impl;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.songlian.logistics.common.Constants;
import com.songlian.logistics.common.QueryPageParam;
import com.songlian.logistics.common.Result;
import com.songlian.logistics.dao.MaterialDao;
import com.songlian.logistics.exception.ServiceException;
import com.songlian.logistics.pojo.Material;
import com.songlian.logistics.service.MaterialService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 黄玮
 * @since 2023-02-17
 */
@Service
public class MaterialServiceImpl extends ServiceImpl<MaterialDao, Material> implements MaterialService {

    @Autowired
    private MaterialDao materialDao;
    @Override
    public Result pageList(QueryPageParam query) {
        try {
            // 获取分页参数
            long size = (long) query.getPageSize();
            long num = (long) query.getPageNum();
            // 排序参数
            String sortField = (String) query.getParams().get("sortField");
            String sortDirection = (String) query.getParams().get("sortDirection");
            // 实例对象参数
            String name = (String) query.getParams().get("name");

            Integer mid = s2i((String) query.getParams().get("materialId"), -1);
            if (mid != null && mid == -1) return Result.fail("materialId参数错误");

            Integer price = s2i((String) query.getParams().get("price"), -1);
            if (price != null && price == -1) return Result.fail("price参数错误");

            Integer cost = s2i((String) query.getParams().get("cost"), -1);
            if (cost != null && cost == -1) return Result.fail("cost参数错误");

            Integer volume = s2i((String) query.getParams().get("volume"), -1);
            if (volume != null && volume == -1) return Result.fail("volume参数错误");

            // 初始化分页信息
            Page<Material> page = new Page<>();
            page.setSize(size);
            page.setCurrent(num);
            // 查询条件
            LambdaQueryWrapper<Material> lqw = new LambdaQueryWrapper();
            if (!sortField.equals("null") && StringUtils.isNotBlank(sortField)) {
                if (sortDirection.equals("1")) {
                    if (sortField.equals("materialId")) lqw.orderByDesc(Material::getMaterialId);
                    else if (sortField.equals("name")) lqw.orderByDesc(Material::getName);
                    else if (sortField.equals("cost")) lqw.orderByDesc(Material::getCost);
                    else if (sortField.equals("price")) lqw.orderByDesc(Material::getPrice);
                    else if (sortField.equals("volume")) lqw.orderByDesc(Material::getVolume);
                } else {
                    if (sortField.equals("materallowMultiQueries=trueallowMultiQueries=trueallowMultiQueries=trueialId")) lqw.orderByAsc(Material::getMaterialId);
                    else if (sortField.equals("name")) lqw.orderByAsc(Material::getName);
                    else if (sortField.equals("cost")) lqw.orderByAsc(Material::getCost);
                    else if (sortField.equals("price")) lqw.orderByAsc(Material::getPrice);
                    else if (sortField.equals("volume")) lqw.orderByAsc(Material::getVolume);
                }
            }
            lqw.eq(mid != null, Material::getMaterialId, mid);
            lqw.like(!name.equals("null") && StringUtils.isNoneBlank(name), Material::getName, name);
            lqw.eq(cost != null, Material::getCost, cost);
            lqw.eq(price != null, Material::getPrice, price);
            lqw.eq(volume != null, Material::getVolume, volume);

            IPage iPage = this.page(page, lqw);
            return Result.success(iPage.getRecords(), iPage.getTotal());
        } catch (Exception e) {
            throw new ServiceException(Constants.CODE_600, "MaterialService.listByPage:" + e.toString());
        }
    }

    @Override
    public Result materialCount(QueryPageParam query) {
        Integer pageNum = query.getPageNum();
        Integer pageSize = query.getPageSize();

        String name = (String) query.getParams().get("name");
        name = name.trim();
        if(StringUtils.isBlank(name) && name.equals("null")) name=null;

        Integer mid = s2i((String) query.getParams().get("materialId"), -1);
        if (mid != null && mid == -1) return Result.fail("materialId参数错误");

        if(name!=null) name = "%" + name + "%";
        List<HashMap> map = materialDao.getMaterialCount(mid,name,(pageNum- 1)*pageSize,pageSize);
        Integer total = materialDao.getMaterialCount(mid,name,0,Integer.MAX_VALUE).size();
        return Result.success(map,total);
    }

    @Override
    public Result materialCountOfPieCharts(Integer mid, String name) {
        try {
            try {
                List<Map> data = materialDao.getMaterialCountOfBarCharts(null,null);
                return Result.success(data);
            }catch (Exception e){
                return Result.fail("传入参数异常");
            }
        }catch (Exception e){
            return Result.error(Constants.CODE_500,"图形加载时出错");
        }
    }

    @Override
    public void exportData(HttpServletResponse response) throws IOException {
        // 1. 从数据库中查出所有数据
        List list = materialDao.getAllMaterialCount();

        // 2. 创建工具类，写出到浏览器
        // hutool.cn/docs/#/poi/Excel生成-ExcelWriter
        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        // 3. 定义表头映射字段
        excelWriter.addHeaderAlias("mid","材料ID");
        excelWriter.addHeaderAlias("name","材料名");
        excelWriter.addHeaderAlias("count","总数");
        // 4. 写出list内容到Excel文件使用默认样式，强制输出标题
        excelWriter.write(list,true);

        response.setContentType("application/vnd.openxmlformats-officedocument.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("材料数量统计","UTF-8");
        response.setHeader("Content-Disposition","attachment;filename=" + fileName + ".xlsx");


        ServletOutputStream out = response.getOutputStream();
        excelWriter.flush(out, true);
        out.close();
        excelWriter.close();
    }

    private Integer s2i(String field, Integer errValue) {
        Integer res = null;
        if (!field.equals("null") && StringUtils.isNotBlank(field)) {
            try {
                res = Integer.parseInt(field);
            } catch (Exception e) {
                return errValue;
            }
        }
        return res;
    }

    private Double s2d(String field, Double errValue) {
        Double res = null;
        if (!field.equals("null") && StringUtils.isNotBlank(field)) {
            try {
                res = Double.parseDouble(field);
            } catch (Exception e) {
                return errValue;
            }
        }
        return res;
    }
}
