package com.songlian.logistics.common;

import lombok.Data;

@Data
public class Result {
    private int code;   // 200成功 400失败
    private String msg;
    private int total; //总记录数
    private Object data;

    public static Result result(int code, String msg, int total, Object data) {
        Result res = new Result();

        res.setCode(code);
        res.setMsg(msg);
        res.setTotal(total);
        res.setData(data);

        return res;
    }

    public static Result fail() {
        return result(400, "失败", 0, null);
    }

    public static Result fail(String msg) {
        return result(400, msg, 0, null);
    }

    public static Result success() {
        return result(200, "成功", 0, null);
    }

    public static Result success(Object data) {
        return result(200, "成功", 0, data);
    }

    public static Result success(Object data,int total) {
        return result(200, "成功", total, data);
    }


}
