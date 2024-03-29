package com.songlian.logistics.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.songlian.logistics.common.QueryPageParam;
import com.songlian.logistics.common.Result;
import com.songlian.logistics.pojo.OrderDetail;
import com.songlian.logistics.pojo.OrderForm;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 黄玮
 * @since 2023-02-17
 */
public interface OrderDetailService extends IService<OrderDetail> {

    public Result createOrderDetail(Long id, int materialId, List<HashMap> list);

    public Result createOrderDetailByAppoint(String tspMethod, OrderForm orderForm, List<HashMap> list);

    public Result pageList(QueryPageParam query);

    void exportData(HttpServletResponse response) throws IOException;
}
