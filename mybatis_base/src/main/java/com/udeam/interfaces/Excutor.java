package com.udeam.interfaces;

import com.udeam.config.Configration;
import com.udeam.config.MappedStatement;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

/**
 * sql 执行器
 */
public interface Excutor {

    /**
     * 查询接口
     * @param configration 数据库配置类
     * @param mappedStatement mapper 信息对象
     * @param params 参数
     * @param <E>
     * @return
     */
    <E> List<E> query(Configration configration, MappedStatement mappedStatement,Object[] params) throws SQLException, IllegalAccessException, InstantiationException, NoSuchFieldException, IntrospectionException, InvocationTargetException;

    void close() throws Exception;

    int delete(Configration configration, MappedStatement mappedStatement, Object[] params) throws SQLException, NoSuchFieldException, IllegalAccessException, InstantiationException;

    int  update(Configration configration, MappedStatement mappedStatement,Object[] params) throws IllegalAccessException, NoSuchFieldException, SQLException;
}
