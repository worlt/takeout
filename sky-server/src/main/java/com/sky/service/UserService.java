package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

/**
 * @Author worlt
 * @Date 2025/4/1 上午9:36
 */
public interface UserService {

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    User wxLogin(UserLoginDTO userLoginDTO);
}
