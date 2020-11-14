package com.udeam.test;

import com.udeam.config.SqlSessionFactoryBuilder;
import com.udeam.interfaces.SqlSession;
import com.udeam.interfaces.SqlSessionFactory;
import com.udeam.io.Resource;
import com.udeam.pojo.User;
import com.udeam.test.mapper.UserMapper;
import org.junit.After;
import org.junit.Before;

import java.io.InputStream;
import java.util.List;

public class Test2 {

    static InputStream inputStream = null;
    static SqlSession sqlSession = null;
    static  UserMapper userMapper = null;

    @Before
    public void test0() throws Exception {
        String name = "sqlMapConfigration.xml";
        // 1 加载xml配置文件
        inputStream = Resource.inputStream(name);
        // 2 解析配置文件
        //初始化Configration 初始化容器 mapperStamentMap容器保存mapper中sql 信息
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        // 3 创建会话
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(inputStream);
        sqlSession = sqlSessionFactory.openSqlSession();
        userMapper = sqlSession.getMapper(UserMapper.class);
    }

    /**
     * 查询
     *
     * @throws Exception
     */
    @org.junit.Test
    public void test1() throws Exception {

        User user = new User();
        user.setId(2);
        user.setName("小王2");

        // 代理测试
         System.out.println(userMapper.selectList2());
         System.out.println(userMapper.selectOne2(user));

    }

    /**
     * 删除
     *
     * @throws Exception
     */
    @org.junit.Test
    public void test3() throws Exception {

        User user = new User();
        user.setId(2);
        user.setName("[mapper代理]小王2");

        System.out.println(userMapper.delete(user));


    }


    /**
     * 新增
     *
     * @throws Exception
     */
    @org.junit.Test
    public void test2() throws Exception {
        User user = new User();
        user.setName("我是 [mapper代理] 新增的小王鸭...");
        System.out.println(userMapper.insert(user));

    }

    /**
     * 修改
     *
     * @throws Exception
     */
    @org.junit.Test
    public void test4() throws Exception {
        User user = new User();
        user.setId(2);
        user.setName("我是[mapper代理]修改的小王2...");
        //修改
        System.out.println(userMapper.update(user));

    }




    @After
    public void test5() throws Exception {
        sqlSession.close();
    }


}
