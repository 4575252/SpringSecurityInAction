package com.iyyxx.token.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iyyxx.token.domain.User;
import com.iyyxx.token.mapper.UserMapper;
import com.iyyxx.token.service.UserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息 服务实现类
 * </p>
 *
 * @author 林智辉
 * @blog http://iyyxx.com
 * @since 2022-10-12
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
