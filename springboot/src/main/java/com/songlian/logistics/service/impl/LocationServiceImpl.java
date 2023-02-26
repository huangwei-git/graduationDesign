package com.songlian.logistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.songlian.logistics.common.Constants;
import com.songlian.logistics.common.QueryPageParam;
import com.songlian.logistics.common.Result;
import com.songlian.logistics.dao.LocationDao;
import com.songlian.logistics.exception.RequestExpcetion;
import com.songlian.logistics.exception.ServiceException;
import com.songlian.logistics.pojo.Location;
import com.songlian.logistics.pojo.Location;
import com.songlian.logistics.service.LocationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 黄玮
 * @since 2023-02-17
 */
@Service
public class LocationServiceImpl extends ServiceImpl<LocationDao, Location> implements LocationService {
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

            Integer mid = s2i((String) query.getParams().get("locId"), -1);
            if(mid != null && mid == -1){
                throw new RequestExpcetion(Constants.CODE_401,"locId参数错误");
            }

            Integer xpos = s2i((String) query.getParams().get("xpos"), -1);
            if(xpos != null && xpos == -1){
                throw new RequestExpcetion(Constants.CODE_401,"xpos参数错误");
            }

            Integer ypos = s2i((String) query.getParams().get("ypos"), -1);
            if(ypos != null && ypos == -1){
                throw new RequestExpcetion(Constants.CODE_401,"ypos参数错误");
            }

            Integer type = s2i((String) query.getParams().get("type"), -1);
            if(type!= null && type == -1){
                throw new RequestExpcetion(Constants.CODE_401,"type参数错误");
            }

            // 初始化分页信息
            Page<Location> page = new Page<>();
            page.setSize(size);
            page.setCurrent(num);
            // 查询条件
            LambdaQueryWrapper<Location> lqw = new LambdaQueryWrapper();
            if(!sortField.equals("null") && StringUtils.isNotBlank(sortField)){
                if(sortDirection.equals("1")){
                    if(sortField.equals("locId"))lqw.orderByDesc(Location::getLocId);
                    else if(sortField.equals("name"))lqw.orderByDesc(Location::getName);
                    else if(sortField.equals("xpos"))lqw.orderByDesc(Location::getXpos);
                    else if(sortField.equals("ypos"))lqw.orderByDesc(Location::getYpos);
                    else if(sortField.equals("type"))lqw.orderByDesc(Location::getType);
                }else{
                    if(sortField.equals("locId"))lqw.orderByAsc(Location::getLocId);
                    else if(sortField.equals("name"))lqw.orderByAsc(Location::getName);
                    else if(sortField.equals("xpos"))lqw.orderByAsc(Location::getXpos);
                    else if(sortField.equals("ypos"))lqw.orderByAsc(Location::getYpos);
                    else if(sortField.equals("type"))lqw.orderByAsc(Location::getType);
                }
            }
            lqw.eq(mid != null,Location::getLocId,mid);
            lqw.like(!name.equals("null") && StringUtils.isNoneBlank(name),Location::getName,name);
            lqw.eq(xpos != null ,Location::getXpos,xpos);
            lqw.eq(ypos != null, Location::getYpos, ypos);
            lqw.eq(type != null,Location::getType,type);

            IPage iPage = this.page(page, lqw);
            return Result.success(iPage.getRecords(),iPage.getTotal());
        }catch (Exception e){
            if (e instanceof ServiceException) throw new RequestExpcetion(Constants.CODE_401,e.getMessage());
            else throw new ServiceException(Constants.CODE_500,"LocationService分页请求异常");
        }
    }

    private Integer s2i(String field,Integer errValue) {
        Integer res = null;
        if (!field.equals("null") && StringUtils.isNotBlank(field)) {
            try {
                res = Integer.parseInt(field);
            }catch (Exception e){
                return errValue;
            }
        }
        return res;
    }
    private Double s2d(String field,Double errValue) {
        Double res = null;
        if (!field.equals("null") && StringUtils.isNotBlank(field)) {
            try {
                res = Double.parseDouble(field);
            }catch (Exception e){
                return errValue;
            }
        }
        return res;
    }
}
