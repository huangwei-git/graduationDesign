package com.songlian.logistics.common;

import lombok.Data;

import java.util.HashMap;

@Data
public class QueryPageParam {
    // 前端传通过请求体对象参数，key为pageSize和pageNum的值自动装配

    // 默认
    private static int PAGE_SIZE = 20;
    private static int PAGE_NUM = 1;

    private int pageSize = PAGE_SIZE;
    private int pageNum = PAGE_NUM;
    private HashMap param;
}
