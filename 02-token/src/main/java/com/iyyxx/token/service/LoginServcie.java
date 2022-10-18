package com.iyyxx.token.service;

import com.iyyxx.token.domain.ResponseResult;
import com.iyyxx.token.domain.User;

/**
 * @className: LoginServcie
 * @description: TODO 类描述
 * @author: eric 4575252@gmail.com
 * @date: 2022/10/17/0017 16:27:50
 **/
public interface LoginServcie {
    ResponseResult login(User user);

    ResponseResult logout();
}
