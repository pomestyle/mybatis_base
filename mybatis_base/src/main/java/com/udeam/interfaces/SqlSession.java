package com.udeam.interfaces;


import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

/**
 * 数据库增删改查  CRUD操作接口
 */
public interface SqlSession {

    //查询多个
    public <E>List<E> selectList(String statementId,Object...params) throws IllegalAccessException, IntrospectionException, InstantiationException, SQLException, InvocationTargetException, NoSuchFieldException;

    //查询单个
    public <T> T  selectOne(String statementId,Object...params) throws IllegalAccessException, IntrospectionException, InstantiationException, SQLException, InvocationTargetException, NoSuchFieldException;

    //删除
    public Integer delete(String statementId,Object...params) throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException;

    //更新
    public Integer update(String statementId,Object...params) throws IllegalAccessException, NoSuchFieldException, SQLException;

    public void close() throws Exception;


    /**
     * Mapper代理接口
     * @param mapperClass
     * @param <T>
     * @return
     */
    public <T> T getMapper(Class<?> mapperClass);

}
