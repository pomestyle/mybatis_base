package com.udeam.config;

import com.udeam.xml.xmlConfigerBuilder;
import com.udeam.interfaces.impl.DefaultSqlSessionFactory;
import com.udeam.interfaces.SqlSessionFactory;

import java.io.InputStream;

/**
 * 用来解析xml文件
 * 将信息封装到 Configration对象中
 * 将mapper sql信息封装到 MappedStatement 对象中
 */
public class SqlSessionFactoryBuilder {

    private Configration configration;

    public SqlSessionFactoryBuilder() {
        this.configration = new Configration();
    }

    public SqlSessionFactory build(InputStream inputStream) throws Exception {
        //1 解析配置文件,封装Configuration
        xmlConfigerBuilder xmlConfigerBuilder = new xmlConfigerBuilder(configration);
        //2 解析
        configration = xmlConfigerBuilder.parseConfigration(inputStream);

        // 3 创建SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(configration);
        return sqlSessionFactory;
    }

}
