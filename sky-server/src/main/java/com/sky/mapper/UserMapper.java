package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {

    @Select("select * from user where openid=#{openid}")
    public User getUserByOpenid(String openid);

    /**
     * 添加新用户
     * @param user
     */
    void insert(User user);

    @Select("select * from user where id = #{userId}")
    User getById(Long userId);
    /**
     * 动态查找用户
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
