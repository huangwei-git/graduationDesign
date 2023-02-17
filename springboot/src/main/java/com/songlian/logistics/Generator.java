package com.songlian.logistics;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;

import java.nio.file.Paths;

public class Generator {
    public static void main(String[] args) {
        AutoGenerator autoGenerator = new AutoGenerator();

        DataSourceConfig dataSource = new DataSourceConfig();
        dataSource.setDriverName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/logistics");
        dataSource.setUsername("songlian");
        dataSource.setPassword("HuangWei17");
        autoGenerator.setDataSource(dataSource);
        // 设置全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        String path = Paths.get(System.getProperty("user.dir"),"../demo04_generator/src/main/java").toString();
        //globalConfig.setOutputDir(System.getProperty("user.dir") + "../demo04_generator/src/main/java");
        globalConfig.setOutputDir(path);
        globalConfig.setOpen(true);                    // 设置生成完毕后是否打开生成代码所在目录
        globalConfig.setAuthor("宋黄玮");                  // 设置作者
        globalConfig.setFileOverride(true);             // 设置是否覆盖原始生成的文件
        globalConfig.setMapperName("%sDao");            // 设置数据层结构名，%s为占位符，代指模块名称
        globalConfig.setIdType(IdType.ASSIGN_ID);       // 设置ID生成策略
        autoGenerator.setGlobalConfig(globalConfig);    // 在代码自动生成器对象中，添加配置
        // 设置包名相关配置
        PackageConfig packageInfo = new PackageConfig();
        packageInfo.setParent("com.songlian.logistics");               // 设置生成的包名，与代码所在位置不冲突，二者叠加组成完整路径
        packageInfo.setEntity("pojo");                  // 设置实体类包名
        packageInfo.setMapper("dao");                   // 设置数据层包名
        autoGenerator.setPackageInfo(packageInfo);      // 在代码自动生成器对象中，添加配置
        // 策略配置
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setInclude("delivery","demand",// 设置当前参与生成的表名，参数为可变参数
                "inventory","manager","material","order_detail","order_form","transporter");
        strategyConfig.setRestControllerStyle(true);    // 设置是否启用Restful
        strategyConfig.setVersionFieldName("deleted");  // 设置逻辑删除字段名
        strategyConfig.setEntityLombokModel(true);      // 设置是否启用lombok
        autoGenerator.setStrategy(strategyConfig);      // 在代码自动生成器对象中，添加配置
        // 执行生成代码
        autoGenerator.execute();
    }
}
