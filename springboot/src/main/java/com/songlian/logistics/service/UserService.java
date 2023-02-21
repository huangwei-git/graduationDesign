package com.songlian.logistics.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.songlian.logistics.common.QueryPageParam;
import com.songlian.logistics.common.Result;
import com.songlian.logistics.pojo.User;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
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
public interface UserService extends IService<User> {

    public Result pageList(QueryPageParam query);

    public void exportData(HttpServletResponse response) throws Exception;

    public Result importData(MultipartFile file) throws Exception;
}
