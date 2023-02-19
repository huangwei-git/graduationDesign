package com.songlian.logistics.exception;

import com.songlian.logistics.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

public class GlobalExceptionHandler {

    /**
     * 如果抛出ServiceException，则调用此方法
     * @param se 业务异常
     * @return
     */
    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public Result handler(ServiceException se){
        return Result.error(se.getMessage());
    }
}
