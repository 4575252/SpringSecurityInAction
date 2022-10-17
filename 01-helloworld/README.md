# 关于本项目
[![Language](https://img.shields.io/badge/Language-Java_8_121-007396?color=orange&logo=java)](https://github.com/4575252/SpringBootBook)
[![Framework](https://img.shields.io/badge/Framework-Spring_Boot_2.7.4-6DB33F?logo=spring)](https://github.com/4575252/SpringBootBook)
[![Lombok](https://img.shields.io/badge/Lombok-Spring_Boot_1.18.20-pink?logo=lombok)](https://github.com/4575252/SpringBootBook)
[![Swagger2](https://img.shields.io/badge/Swagger2-Knife4j_3.0.2-blue?logo=swagger)](https://github.com/4575252/SpringBootBook)

在helloWorld的基础上引入security，访问有临时口令要求
- hello world 实验

## 在helloWorld的基础上引入security
操作过程
- 搭建helloworld，引入lombok和swagger
- 测试/hello正常访问
- 引入spring security，再次访问/hello，用user和控制台临时密码登录测试！
- 测试security默认的/logout退出

```xml
<!-- 引入security启动器 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```


# 待研究
[一个基于 Spring Boot 2 + Redis + Vue 的商城管理系统](https://mp.weixin.qq.com/s/RFjm3P_yEHAXwKBvqNKi-w)

