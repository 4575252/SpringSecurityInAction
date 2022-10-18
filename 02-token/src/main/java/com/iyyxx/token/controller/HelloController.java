package com.iyyxx.token.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@Api("Hello控制器")
@Slf4j
@RestController
public class HelloController {

    @ApiOperation(value = "无注解方式", notes = "多个参数，多种的查询参数类型")
    @GetMapping("/hello")
    public String hello() {
        log.info("通配符测试.{}.{}.{}", "A", "B", "C");
        return "Hello Spring Boot";
    }
}
