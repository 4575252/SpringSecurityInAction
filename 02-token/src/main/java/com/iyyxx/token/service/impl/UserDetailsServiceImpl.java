package com.iyyxx.token.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.iyyxx.token.domain.LoginUser;
import com.iyyxx.token.domain.User;
import com.iyyxx.token.mapper.MenuMapper;
import com.iyyxx.token.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 用户信息 服务实现类
 * </p>
 *
 * @author 林智辉
 * @blog http://iyyxx.com
 * @since 2022-10-17
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MenuMapper menuMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户信息
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, username);
        User user = userMapper.selectOne(queryWrapper);
        if(Objects.isNull(user)){
            throw new RuntimeException("用户未找到");
        }

        List<String> permissionKeyList =  menuMapper.selectPermsByUserId(user.getId());
//        List<String> list = new ArrayList<>(Arrays.asList("test"));

        // 把数据封装成UserDetails返回
        return new LoginUser(user, permissionKeyList);
    }
}
