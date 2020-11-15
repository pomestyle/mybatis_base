package com.udeam.test.mapper;

import com.udeam.pojo.User;

import java.util.List;

public interface UserMapper {

    /**
     * 查询所有
     *
     * @return
     */
    List<User> selectList2();


    /**
     * 查询单个 根据条件
     *
     * @param user
     * @return
     */
    User selectOne2(User user);

    Integer delete(User user);

    Integer insert(User user);

    Integer deleteById(Integer id);

    Integer update(User user);
}
