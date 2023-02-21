package com.songlian.logistics.service.impl;

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

import java.util.HashMap;
import java.util.List;

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
            System.out.println("query = " + query);
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
        String name = (String) query.getParams().get("name");
        name = name.trim();
        if(StringUtils.isBlank(name) && name.equals("null")) name=null;

        Integer mid = s2i((String) query.getParams().get("materialId"), -1);
        if (mid != null && mid == -1) return Result.fail("materialId参数错误");

        if(name!=null) name = "%" + name + "%";
        List<HashMap> HashMap = materialDao.getMaterialCount(mid,name);
        return Result.success(HashMap);
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
