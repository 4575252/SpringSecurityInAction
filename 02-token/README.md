# 关于本项目
[![Language](https://img.shields.io/badge/Language-Java_8_121-007396?color=orange&logo=java)](https://github.com/4575252/SpringBootBook)
[![Framework](https://img.shields.io/badge/Framework-Spring_Boot_2.7.4-6DB33F?logo=spring)](https://github.com/4575252/SpringBootBook)
[![Lombok](https://img.shields.io/badge/Lombok-Spring_Boot_1.18.20-pink?logo=lombok)](https://github.com/4575252/SpringBootBook)
[![Swagger2](https://img.shields.io/badge/Swagger2-Knife4j_3.0.2-blue?logo=swagger)](https://github.com/4575252/SpringBootBook)

在helloWorld的基础上，除了引入security，还增强token管理，同时用到redis、jwt等组件,在SpringSecurity的认证链上对登录认证、用户服务及校验、授权做修改！

>SpringSecurity实则是一套过滤器链，本次实践对用户校验过滤器做了扩展实现RBAC数据库校验，然后在前后端分离框架提供登录验证接口、JWT凭据校验过滤器，解决分布式、无session依赖下的认证完整性，毕竟天然没有了CSRF攻击。

## 一、在helloWorld的基础上引入security
helloworld工程中已演示，这里不赘述

```xml
<!-- 引入security启动器 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```


## 二、认证
SpringSecurity的认证有一套默认机制，也就是认证链、过滤器链，具体可看三更的材料，在本文档里有提供。

该认证链规范，易于扩展，相对也复杂、冗长，目前前后端分离框架，需要对该认证链做一下修改。
- 1、除了helloworld中引入的security的依赖，还需要引入mysql、redis等基础环境，这是主流配置，高效。
- 2、认证链末端的【临时用户认证】替换为【依赖数据库的账号密码认证】
- 3、密码认证级别采用Spring推荐的BCrypt加密算法
- 4、提供认证接口，也就是mvc中的controller，调用service方法进行操作，
  - 4.1、根据账号、密码，依据Security的authenticationManager进行认证
  - 4.2、认证通过后，将用户id进行JWT加密，作为令牌提供给前端，并进行redis缓存
  - 4.3、如果认证失败，则抛出异常，框架会统一处理，前端也相应的对错误代码进行处理和展示。
- 5、认证过滤的实现，认证成功后，前端将token放入请求头，这样后续的操作都会被通过！
  - 注意点：认证过滤继承OncePer避免被多次调用，另外需要将这个过滤器放在【用户密码验证过滤器】之前！


### 2.1、引入基础配置（含mysql、redis等配置）
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

###  2.2、实现账号口令从数据库获取
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


### 2.3、实现密码加密存储
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

### 2.4、登录接口实现
这块比较复杂，主要包括以下几块：
- 定义LoginController，作为入口，映射为`/user/login`，并在SecurityConfig.java中放行
- LoginController的登录接口调用LoginService实现，并返回ResponseResult（code，msg，data）
- LoginService的实现类，做了关键登录相关操作，很重要，具体如下：
  - 根据用户密码做认证，认证接口在并在SecurityConfig中重写并赋予@Bean注解，这里就可以注入了
  - 认证失败就抛出RuntimeException，框架去捕获，前端统一处理
  - 认证成功就依据用户ID生成JWT凭据，并放入redis提升重复存取的效率，再返回给前端！
> 技巧1：`CTRL`+`ALT`+鼠标点击接口，可以查看实现类
> 技巧2：`CTRL`+`P` 用在参数上，可以列出所有提示


### 2.5、认证过滤器
用户登录成功后获得了JWT的token，在后续的请求头中会投放这个数据，服务端需要有个过滤器来做统一判断、缓存，具体实现如下：
- 用户请求头带了JWT的token
- 过滤器集成OncePer过滤器，避免反复使用。
- 过滤器解析token获取id，没有token就往后扔即可，后面还有其他过滤器，比如登录验证做处理
- 解析id成功后，去redis获取用户信息LoginUser，然后存放到SecurityContextHolder，这样整个认证链都会轻松获取数据
- 当然这边有个了授权的TODO任务


### 2.6、配置过滤器
前端：当用户调用登录接口进行POST账号、密码，认证通过后取得token，后续的请求放入了header
后端：配置认证过滤继承OncePer避免被多次调用，另外需要将这个过滤器放在【用户密码验证过滤器】之前！


### 2.7、退出
前端：调用/user/logout接口即可，当然header中的token还是要的~
后端：方法从controller穿透到service，首先从SecurityContextHolder中取出Authentication，强转为LoginUser，然后根据userid清除redis中的缓存用户信息！

注意：下次登录获取到新的token，但旧的token还是可以继续使用的，这个跟JWT的过期算法有关

### 2.8、补充JWT
JWT全称Json Web Token,用于解决分布式环境session共享麻烦的问题，一般解决方案是持久化session或客户端缓存！

本章JWT仅缓存用户ID，服务端不加密仅签名，不能有效控制风险，【可以用对称加密对subject做一次加密】，另外增加SSL控制是更完美了！

缺陷：JWT因为不在服务器上保存导致不可管理，所以时间不能过长，目前设置1小时，退出后如果再有效期间内还是可以使用的！

本章做了redis缓存，且顺序靠前，所以退出后清理redis，相应的JWT临时不可用，但重新登录后，新旧JWT都是有效的！在过期时间前

另外，jwt保存到cookie比local storage会稍微更安全一点点

具体资料可以参考：
- [JSON Web Token 入门教程](https://www.ruanyifeng.com/blog/2018/07/json_web_token-tutorial.html)
- [JSON Web Tokens (JWT) 在线解密](https://www.box3.cn/tools/jwt.html)
- [理解 JWT 的使用场景和优劣](https://www.cnkirito.moe/jwt-learn-3/)




## 三、授权
授权基本流程：
- 

### 2.1、简单授权
授权基本流程：
- 开启授权：SecurityConfig配置类开启@EnableGlobalMethodSecurity(prePostEnabled = true)注解
- 资源权限：controller的方法加上 @PreAuthorize("hasAuthority('test')")注解
- 交互凭据：LoginUser继承了UserDetails，增加权限实参和临时参数，并在构造函数提供注入，提供权限get
- 扩展过滤：之前过滤器中对UsernamePasswordAuthenticationToken的授权为null，现在可以从loginUser直接取得了
- 登录控制器增加授权，临时硬编码，下一章进行RBAC改造，具体如下：
```java
// TODO 临时硬编码，下一章用RBAC模型从数据库中获取
List<String> list = new ArrayList<>(Arrays.asList("test"));
```
>上面资源权限主要用@PreAuthorize注解，其实还有另外两种，只是这个比较常用，另外他的参数是个SPL表达式，ctrl+鼠标点击可以查看，还有其他方法，后续还可以自行扩展方便灵活！

### 2.2、基于RBAC模型的授权管理
RBAC模型，主要三张主表用户、权限、角色和两张关联表为主，脚本在资源包里。

操作步骤：
- 导入sql表结构到mysql中，创建测试数据（权限相关）
- 导入menu的实体和mapper
- 生产mapper文件和方法，这里可以用【[free mybatis tools](https://www.huangchaoyu.com/2019/12/11/free-mybatis-plugin%E7%9A%84%E4%BD%BF%E7%94%A8%E6%96%B9%E6%B3%95/)】插件，在接口和方法上自动生成，具体sql可以在navicat先测试通过再拷过来
- 配置mapper的目录，这里用了默认的mapper文件夹可以不配置，不过application文件中还是做了默认配置
- 最后改造上一章留下的todo， UserDetailsServiceImpl

### 2.3、认证鉴权的统一异常处理
新建两个handler，分别是认证失败和权限不足的异常捕获，然后在SecurityConfig进行装载
- 新建AccessDeniedHandlerImpl implements AccessDeniedHandler
- 新建AuthenticationEntryPointImpl implements AuthenticationEntryPoint
- SecurityConfig在configure方法下扩展
```java
http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint).
        accessDeniedHandler(accessDeniedHandler);
```

### 2.4、跨域配置
按作者的思路，前后端分离部署，访问是跨域的，需要开启mvc和安全控制两个开关，实测只要开启mvc即可，这里保留意见，具体如下：

```java
//mvc config重写这个方法
@Override
protected void addCorsMappings(CorsRegistry registry) {
      // 设置允许跨域的路径
      registry.addMapping("/**")
      // 设置允许跨域请求的域名
      .allowedOriginPatterns("*")
      // 是否允许cookie
      .allowCredentials(true)
      // 设置允许的请求方式
      .allowedMethods("GET", "POST", "DELETE", "PUT")
      // 设置允许的header属性
      .allowedHeaders("*")
      // 跨域允许时间
      .maxAge(3600);
      }
      
//SecurityConfig的configure方法中，开启csrf；
//        http.csrf();
```

### 2.5、自定义权限校验
自定义一个bean，指定别名，配置好方法并接收参数，与SecurityContextHolder进行比对，当然资源配置那块要用`@ex`
```java
@Component("ex")
public class SGExpressionRoot {

    public boolean hasAuthority(String authority){
        //获取当前用户的权限
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        List<String> permissions = loginUser.getPermissions();
        //判断用户权限集合中是否存在authority
        return permissions.contains(authority);
    }
}
```
在SPEL表达式中使用 @ex相当于获取容器中bean的名字未ex的对象。然后再调用这个对象的hasAuthority方法

~~~java
    @RequestMapping("/hello")
    @PreAuthorize("@ex.hasAuthority('system:dept:list')")
    public String hello(){
        return "hello";
    }
~~~

