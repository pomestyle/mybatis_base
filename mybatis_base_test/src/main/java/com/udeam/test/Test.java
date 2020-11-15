package com.udeam.test;

import com.udeam.config.SqlSessionFactoryBuilder;
import com.udeam.interfaces.SqlSession;
import com.udeam.interfaces.SqlSessionFactory;
import com.udeam.io.Resource;
import com.udeam.pojo.User;
import org.junit.After;
import org.junit.Before;

import java.io.InputStream;
import java.util.List;

public class Test {

    static InputStream inputStream = null;
    static SqlSession sqlSession = null;

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

        User user1 = sqlSession.selectOne("User.selectOne", user);
        System.out.println(user1 + " \n ");
        List<User> usersList = sqlSession.selectList("User.selectList");
        System.out.println(usersList);

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
        user.setName("小王2");

        //删除
        Integer user2 = sqlSession.delete("User.delete", user);
        System.out.println(user2);


    }

    /**
     * 根据id删除
     *
     * @throws Exception
     */
    @org.junit.Test
    public void test31() throws Exception {
        Integer user2 = sqlSession.delete("User.deleteById", 2);
        System.out.println(user2);
    }

    /**
     * 根据 name删除
     *
     * @throws Exception
     */
    @org.junit.Test
    public void test32() throws Exception {
        Integer user2 = sqlSession.delete("User.deleteByName", "删除");
        System.out.println(user2);
    }

    @org.junit.Test
    public void test33() throws Exception {
        User user2 = sqlSession.selectOne("User.selectById", 1);
        System.out.println(user2);
    }


    /**
     * 新增
     *
     * @throws Exception
     */
    @org.junit.Test
    public void test2() throws Exception {
        User user = new User();
        user.setName("我是新增的小王...");
        sqlSession.insert("User.insert", user);

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
        user.setName("小王2");
        //修改
        Integer user1 = sqlSession.update("User.update", user);
        System.out.println(user1);

    }




    @After
    public void test5() throws Exception {
        sqlSession.close();
    }


}
