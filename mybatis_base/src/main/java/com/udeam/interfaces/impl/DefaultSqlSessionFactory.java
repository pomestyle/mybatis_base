package com.udeam.interfaces.impl;


import com.udeam.config.Configration;
import com.udeam.interfaces.SqlSession;
import com.udeam.interfaces.SqlSessionFactory;

/**
 * 具体实现类 默认使用的实现类
 * 操作数据库增删改查
 * 封装CRUD操作
 *
 */
public class DefaultSqlSessionFactory  implements SqlSessionFactory {


    private Configration configration;

    public DefaultSqlSessionFactory(Configration configration) {
        this.configration = configration;
    }

    @Override
    public SqlSession openSqlSession() throws Exception {
        return new DefaultSqlSession(configration);
    }





}
