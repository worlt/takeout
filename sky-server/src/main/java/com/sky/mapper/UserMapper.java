package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Author worlt
 * @Date 2025/4/1 上午9:46
 */
@Mapper
public interface UserMapper {

    /**
     * 根据openid查询用户
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    public User getByOpenid(String openid);

    /**
     * 插入数据
     * @param user
     */
    void insert(User user);


    @Select("select * from user where id = #{id}")
    User getById(Long userId);


}
