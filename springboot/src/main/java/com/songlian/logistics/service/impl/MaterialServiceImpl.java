package com.songlian.logistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.songlian.logistics.dao.MaterialDao;
import com.songlian.logistics.pojo.Material;
import com.songlian.logistics.service.MaterialService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 黄玮
 * @since 2023-02-17
 */
@Service
public class MaterialServiceImpl extends ServiceImpl<MaterialDao, Material> implements MaterialService {

    @Resource
    private MaterialDao materialDao;

    @Override
    public IPage genPage(IPage<Material> page) {
        return materialDao.genPage(page);
    }
}
