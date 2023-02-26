package com.songlian.logistics.controller;


import com.songlian.logistics.common.QueryPageParam;
import com.songlian.logistics.common.Result;
import com.songlian.logistics.pojo.OrderDetail;
import com.songlian.logistics.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 黄玮
 * @since 2023-02-17
 */
@RestController
@RequestMapping("/orderDetail")
public class OrderDetailController {
    @Autowired
    private OrderDetailService orderDetailService;


    @PostMapping("/listPage")
    public Result pageList(@RequestBody QueryPageParam query){
        Result res = orderDetailService.pageList(query);
        return res;
    }
}

