package com.iyyxx.token.controller;

import com.iyyxx.token.domain.ResponseResult;
import com.iyyxx.token.domain.User;
import com.iyyxx.token.service.LoginServcie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {

    @Autowired
    private LoginServcie loginServcie;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/user/login")
    public ResponseResult login(@RequestBody User user){

        return loginServcie.login(user);
    }

    @GetMapping("/user/logout")
    public ResponseResult logout(){
        return loginServcie.logout();
    }
}