# 关于本项目
[![Language](https://img.shields.io/badge/Language-Java_8_121-007396?color=orange&logo=java)](https://github.com/4575252/SpringBootBook)
[![Framework](https://img.shields.io/badge/Framework-Spring_Boot_2.7.4-6DB33F?logo=spring)](https://github.com/4575252/SpringBootBook)
[![Lombok](https://img.shields.io/badge/Lombok-Spring_Boot_1.18.20-pink?logo=lombok)](https://github.com/4575252/SpringBootBook)
[![Swagger2](https://img.shields.io/badge/Swagger2-Knife4j_3.0.2-blue?logo=swagger)](https://github.com/4575252/SpringBootBook)

在helloWorld的基础上，除了引入security，还增强token管理，同时用到redis、jwt等组件,在SpringSecurity的认证链上对登录认证、用户服务及校验、授权做修改！

## 一、在helloWorld的基础上引入security
helloworld工程中已演示，这里不赘述

```xml
<!-- 引入security启动器 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```


## 二、引入基础配置（含mysql、redis等配置）
本章内容较多，具体如下：
- 引入依赖
  - redis
  - fastjson
  - jjwt
  - mybatis+
  - mysql driver
- 引入基础工具信息
  - config/RedisConfig.java，redis模板bean，设置序列化器，避免乱码
  - domain/ResponseResult.java，公共返回类，这是前后端分离项目常用
  - utils/FastJsonRedisSerializer.java，json序列化器
  - utils/JwtUtil，token工具
  - utils/RedisCache，redis工具
  - utils/WebUtils.java，方便往前端传送json
- 导入数据库表，sys_user，脚本在工程resources
- 导入user的mapper、service，并开启工程入口的mapper扫描，然后使用test测试，验证！

## 三、实现账号口令从数据库获取
>认证链的末端，UserDetail默认是内存临时用户认证，这里继承、重写，实现数据库认证方式。

做法如下：
- 创建一个LoginUser实现UserDetails接口，这个是交互实体
  - 将上面User实体作为它的属性
  - 对loginUser的账号密码等返回，使用user的数据
  - 对loginUser的各项判断均返回true，保障运行通畅，后续再根据需要扩展
- 创建一个类UserDetailsServiceImpl实现UserDetailsService接口
  - 重写loadUserByUsername方法，实现该功能
  - 注入userMapper，调用mapper查询用户名返回实体，非唯一报错 
- 页面端测试！
- 注意：这里因为没有密码加密器，所以数据库的明文密码需要加前缀`{noop}`


## 四、实现密码加密存储
采用Spring推荐的BCryptPasswordEncode，具体如下
- 创建一个类，继承WebSecurityConfigurerAdapter
- 存放下方代码，数据库密码更换为加密后的代码，具体可以执行test方法进行体验
```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }
}
```

## 五、登录接口实现
这块比较复杂，主要包括以下几块：
- 定义LoginController，作为入口，映射为`/user/login`，并在SecurityConfig.java中放行
- LoginController的登录接口调用LoginService实现，并返回ResponseResult（code，msg，data）
- LoginService的实现类，做了关键登录相关操作，很重要，具体如下：
  - 根据用户密码做认证，认证接口在并在SecurityConfig中重写并赋予@Bean注解，这里就可以注入了
  - 认证失败就抛出RuntimeException，框架去捕获，前端统一处理
  - 认证成功就依据用户ID生成JWT凭据，并放入redis提升重复存取的效率，再返回给前端！




