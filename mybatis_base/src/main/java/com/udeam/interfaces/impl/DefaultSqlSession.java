package com.udeam.interfaces.impl;

import com.udeam.config.Configration;
import com.udeam.config.MappedStatement;
import com.udeam.interfaces.Excutor;
import com.udeam.interfaces.SqlSession;

import java.beans.IntrospectionException;
import java.lang.reflect.*;
import java.sql.SQLException;
import java.util.List;


/**
 * 默认的sqlsession实现类
 */
public class DefaultSqlSession implements SqlSession {


    private Configration configration;

    //sql执行器
    private Excutor simpleExcutor = new SimpleExcutor();

    public DefaultSqlSession(Configration configration) {

        this.configration = configration;
    }

    @Override
    public <E> List<E> selectList(String statementId, Object... params) throws IllegalAccessException, IntrospectionException, InstantiationException, SQLException, InvocationTargetException, NoSuchFieldException {

        //根据 statementId 获取 MappedStatement 对象
        MappedStatement mappedStatement = configration.getMapperStamentMap().get(statementId);

        //sql 执行器
        List<Object> query = simpleExcutor.query(configration, mappedStatement, params);
        return (List<E>) query;
    }

    @Override
    public <T> T selectOne(String statementId, Object... params) throws IllegalAccessException, IntrospectionException, InstantiationException, SQLException, InvocationTargetException, NoSuchFieldException {
        List<Object> objects = selectList(statementId, params);
        if (objects==null || objects.size() == 0){
            return null;
        }
        if (objects.size()>1){
            throw new RuntimeException("存在多个值!");
        }

        return (T) objects.get(0);
    }

    @Override
    public Integer delete(String statementId, Object... params) throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        //根据 statementId 获取 MappedStatement 对象
        MappedStatement mappedStatement = configration.getMapperStamentMap().get(statementId);
        int delete = simpleExcutor.delete(configration, mappedStatement, params);
        return delete;
    }

    @Override
    public Integer update(String statementId, Object... params) throws IllegalAccessException, NoSuchFieldException, SQLException {
        //根据 statementId 获取 MappedStatement 对象
        MappedStatement mappedStatement = configration.getMapperStamentMap().get(statementId);
        int update = simpleExcutor.update(configration, mappedStatement, params);
        return update;
    }

    @Override
    public void close() throws Exception {
        simpleExcutor.close();
    }


    /**
     * 使用JDK动态代理来执行mapper
     */
    @Override
    public <T> T getMapper(Class<?> mapperClass) {

        Object o = Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //获取class的权限定命名
                String className = method.getDeclaringClass().getName();
                //获取方法名
                String name = method.getName();
                //拼接statementid 从map中获取sql 入参类型,返回类型
                String statementid = className + "." + name;
                //MappedStatement mappedStatement = configration.getMapperStamentMap().get(statementid);
            /*    //入参类型
                Class<?> paramType = mappedStatement.getParamType();
                //出参类型
                Class<?> resultType = mappedStatement.getResultType();
*/
                //判断是否实现泛型类型参数化
                Type genericReturnType = method.getGenericReturnType();
                if (genericReturnType instanceof ParameterizedType) {
                    //还是去执行查询方法
                    return selectList(statementid, args);
                }
                return selectOne(statementid, args);


            }
        });


        return (T) o;
    }




}
