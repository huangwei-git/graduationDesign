package com.songlian.logistics.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.songlian.logistics.common.QueryPageParam;
import com.songlian.logistics.common.Result;
import com.songlian.logistics.pojo.Material;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 黄玮
 * @since 2023-02-17
 */
public interface MaterialService extends IService<Material> {
    public Result pageList(QueryPageParam query);

    public Result materialCount(QueryPageParam query);

    public Result materialCountOfPieCharts(Integer mid,String name);

    public void exportData(HttpServletResponse response) throws IOException;

}
