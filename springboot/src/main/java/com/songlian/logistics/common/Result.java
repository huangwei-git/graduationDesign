package com.songlian.logistics.common;

import lombok.Data;

@Data
public class Result {
    private Integer code;   // 200成功 400失败 500错误
    private String msg;
    private Long total; //总记录数
    private Object data;

    public static Result result(int code, String msg, long total, Object data) {
        Result res = new Result();

        res.setCode(code);
        res.setMsg(msg);
        res.setTotal(total);
        res.setData(data);

        return res;
    }

    public static Result fail() {
        return result(Constants.CODE_400, "失败", 0, null);
    }

    public static Result fail(String msg) {
        return result(Constants.CODE_400, msg, 0, null);
    }

    public static Result success() {
        return result(Constants.CODE_200, "成功", 0, null);
    }

    public static Result success(Object data) {return result(Constants.CODE_200, "成功", 0, data);}

    public static Result success(String msg,Object data) {return result(Constants.CODE_200, msg, 0, data);}

    public static Result success(Object data,long total) {
        return result(Constants.CODE_200, "成功", total, data);
    }

    public static Result success(String msg,Object data,long total) {return result(Constants.CODE_200, msg, total, data);}

    public static Result error(int code,String msg){return result(code,msg,0,null);}

    public static Result serviceError(String msg){return result(Constants.CODE_500,msg,0,null);}

    public static Result requestError(String msg){return result(Constants.CODE_401,msg,0,null);}
}
