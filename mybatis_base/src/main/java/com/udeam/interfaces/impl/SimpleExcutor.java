package com.udeam.interfaces.impl;

import com.udeam.config.Configration;
import com.udeam.config.MappedStatement;
import com.udeam.config.BoundSql;
import com.udeam.interfaces.Excutor;
import com.udeam.utils.GenericTokenParser;
import com.udeam.utils.ParameterMapping;
import com.udeam.utils.ParameterMappingTokenHandler;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 具体的sql执行器 (mybatis 中有三个)
 */
public class SimpleExcutor implements Excutor {

    private Connection connection;

    @Override
    public <E> List<E> query(Configration configration, MappedStatement mappedStatement, Object[] params) throws SQLException, IllegalAccessException, InstantiationException, NoSuchFieldException, IntrospectionException, InvocationTargetException {

        //1 获取连接
        connection = configration.getDataSource().getConnection();

        //2 获取sql select * from user where id = #{id} and name = #{name}
        String sql = mappedStatement.getSql();
        //对sql进行处理    //转换sql语句： select * from user where id = ? and name = ? ，转换的过程中，还需要对#{}里面的值进行解析存储
        BoundSql boundSql = getBoundSql(sql);

        //最终的sql
        String finalSql = boundSql.getSqlText();

        // 3 预编译对象
        PreparedStatement preparedStatement = connection.prepareStatement(finalSql);


        //获取传入的参数类型
        Class<?> paramType = mappedStatement.getParamType();


        // 4 获取传入参数
        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();

        //设置参数
        for (int i = 0; i < parameterMappingList.size(); i++) {
            String content = parameterMappingList.get(i).getContent();
            //反射设置值
            Field declaredField = paramType.getDeclaredField(content);
            //强制访问
            declaredField.setAccessible(true);
            Object o = declaredField.get(params[0]);
            //占位符设置值  列是从1开始的
            preparedStatement.setObject(i + 1, o);
            System.out.println(" 当前[查询]属性是 " + content + " 值是 : " + o);
        }

        // 5. 执行sql
        ResultSet resultSet = preparedStatement.executeQuery();
        //返回的参数类型
        Class<?> resultType = mappedStatement.getResultType();

        ArrayList<E> objects = new ArrayList<>();
        while (resultSet.next()) {
            //创建对象
            Object o = (E) resultType.newInstance();

            //获取数据库返回的列值 元数据
            ResultSetMetaData metaData = resultSet.getMetaData();
            //返回列总数
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                //获取列值
                String columnName = metaData.getColumnName(i);
                //获取值
                Object value = resultSet.getObject(columnName);
                //使用内省技术 也可以使用反射技术
                //创建属性描述器，为属性生成读写方法
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultType);
                //获取写方法
                Method writeMethod = propertyDescriptor.getWriteMethod();
                // 向类中写入值
                writeMethod.invoke(o, value);
            }

            objects.add((E) o);
        }


        return objects;
    }


    @Override
    public void close() throws Exception {
        connection.close();
        System.out.println("关闭连接");
    }

    @Override
    public int delete(Configration configration, MappedStatement mappedStatement, Object[] params) throws SQLException, NoSuchFieldException, IllegalAccessException, InstantiationException {
        //1 获取连接
        connection = configration.getDataSource().getConnection();

        //2 获取sql
        String sql = mappedStatement.getSql();
        //对sql进行处理    //转换sql语句： select * from user where id = ? and name = ?
        BoundSql boundSql = getBoundSql(sql);

        //最终的sql
        String finalSql = boundSql.getSqlText();

        // 3 预编译对象
        PreparedStatement preparedStatement = connection.prepareStatement(finalSql);

        //获取传入的参数类型
        Class<?> paramType = mappedStatement.getParamType();

        // 4 获取传入参数
        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();

        //设置参数
        for (int i = 0; i < parameterMappingList.size(); i++) {
            String content = parameterMappingList.get(i).getContent();
            //反射设置值
            Field declaredField = paramType.getDeclaredField(content);
            //强制访问
            declaredField.setAccessible(true);
            Object o = declaredField.get(params[0]);
            //占位符设置值  列是从1开始的
            preparedStatement.setObject(i + 1, o);
            System.out.println(" 当前[删除]属性是 " + content + " 值是 : " + o);
        }

        // 5. 执行sql
        int resultSet = preparedStatement.executeUpdate();
        if (resultSet == 1) {
            return 1;
        }
        return -1;
    }

    @Override
    public int update(Configration configration, MappedStatement mappedStatement, Object[] params) throws SQLException, IllegalAccessException, NoSuchFieldException {
        //1 获取连接
        connection = configration.getDataSource().getConnection();

        //2 获取sql
        String sql = mappedStatement.getSql();
        //对sql进行处理    //转换sql语句： select * from user where id = ? and name = ?
        BoundSql boundSql = getBoundSql(sql);

        //最终的sql
        String finalSql = boundSql.getSqlText();

        // 3 预编译对象
        PreparedStatement preparedStatement = connection.prepareStatement(finalSql);

        //获取传入的参数类型
        Class<?> paramType = mappedStatement.getParamType();

        // 4 获取传入参数
        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();

        //设置参数
        for (int i = 0; i < parameterMappingList.size(); i++) {
            String content = parameterMappingList.get(i).getContent();
            //反射设置值
            Field declaredField = paramType.getDeclaredField(content);
            //强制访问
            declaredField.setAccessible(true);
            Object o = declaredField.get(params[0]);
            //占位符设置值  列是从1开始的
            preparedStatement.setObject(i + 1, o);
            System.out.println(" 当前[更新]属性是 " + content + " 值是 : " + o);
        }


        // 5. 执行sql
        int resultSet = preparedStatement.executeUpdate();
        if (resultSet == 1) {
            return 1;
        }

        return -1;

    }

    @Override
    public int insert(Configration configration, MappedStatement mappedStatement, Object[] params) throws IllegalAccessException, NoSuchFieldException, SQLException {

        //1 获取连接
        connection = configration.getDataSource().getConnection();

        //2 获取sql
        String sql = mappedStatement.getSql();
        //对sql进行处理    //转换sql语句： select * from user where id = ? and name = ?
        BoundSql boundSql = getBoundSql(sql);

        //最终的sql
        String finalSql = boundSql.getSqlText();

        // 3 预编译对象
        PreparedStatement preparedStatement = connection.prepareStatement(finalSql);

        //获取传入的参数类型
        Class<?> paramType = mappedStatement.getParamType();

        // 4 获取传入参数
        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();

        //设置参数
        for (int i = 0; i < parameterMappingList.size(); i++) {
            String content = parameterMappingList.get(i).getContent();
            //反射设置值
            Field declaredField = paramType.getDeclaredField(content);
            //强制访问
            declaredField.setAccessible(true);
            Object o = declaredField.get(params[0]);
            //占位符设置值  列是从1开始的
            preparedStatement.setObject(i + 1, o);
            System.out.println(" 当前[新增]属性是 " + content + " 值是 : " + o);
        }


        // 5. 执行sql
        int resultSet = preparedStatement.executeUpdate();
        if (resultSet == 1) {
            return 1;
        }

        return -1;

    }


    /**
     * 完成对#{}的解析工作：1.将#{}使用？进行代替，2.解析出#{}里面的值进行存储
     * @param sql
     * @return
     */
    private BoundSql getBoundSql(String sql) {
        //标记处理类：配置标记解析器来完成对占位符的解析处理工作
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", parameterMappingTokenHandler);
        //解析出来的sql
        String parseSql = genericTokenParser.parse(sql);
        //#{}里面解析出来的参数名称
        List<ParameterMapping> parameterMappings = parameterMappingTokenHandler.getParameterMappings();

        BoundSql boundSql = new BoundSql(parseSql,parameterMappings);
        return boundSql;

    }
}
