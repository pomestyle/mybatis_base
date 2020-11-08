package com.udeam.test;

import com.udeam.interfaces.SqlSessionFactory;
import com.udeam.interfaces.SqlSession;
import com.udeam.io.Resource;
import com.udeam.config.SqlSessionFactoryBuilder;
import com.udeam.pojo.User;
import com.udeam.test.mapper.UserMapper;

import java.io.InputStream;
import java.util.List;

public class Test {



    @org.junit.Test
    public void test() throws Exception {
        String name = "sqlMapConfigration.xml";
        // 1 加载xml配置文件
        InputStream inputStream = Resource.inputStream(name);
        // 2 解析配置文件
           //初始化Configration 初始化容器 mapperStamentMap容器保存mapper中sql 信息
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        // 3 创建会话
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSqlSession();
        //4 查询
        User user = new User();
        user.setId(2);
        user.setName("小王2");

        //非代理模式
       /* User user1 = sqlSession.selectOne("User.selectOne", user);
        System.out.println(user1);
        List<User> usersList = sqlSession.selectList("User.selectList");
        System.out.println(usersList);*/
       //修改
        /*Integer user1 = sqlSession.update("User.update", user);
        System.out.println(user1);*/

        //删除
        user.setId(4);
        Integer user2 = sqlSession.delete("User.delete", user);
        System.out.println(user2);
        sqlSession.close();
        // 代理测试
        /*UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        System.out.println(userMapper.selectList2());*/

    }

}
