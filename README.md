# 关于本项目
[![Language](https://img.shields.io/badge/Language-Java_8_121-007396?color=orange&logo=java)](https://github.com/4575252/SpringBootBook)
[![Framework](https://img.shields.io/badge/Framework-Spring_Boot_2.7.4-6DB33F?logo=spring)](https://github.com/4575252/SpringBootBook)
[![Lombok](https://img.shields.io/badge/Lombok-Spring_Boot_1.18.20-pink?logo=lombok)](https://github.com/4575252/SpringBootBook)
[![Swagger2](https://img.shields.io/badge/Swagger2-Knife4j_3.0.2-blue?logo=swagger)](https://github.com/4575252/SpringBootBook)

>三更草堂的大师傅将SpringSecurity复杂的技术，以快速入门+认证+授权三步完成了讲解，整体花费了2天听课和实验，收获很多

# 学习步骤
一、作者先在hello world的基础上，引入SpringSecurity做成quickStart，自带登录退出，一个全局的权限控制有了！

然后分成两个大章节进行介绍，包括认证和授权

二、首先是认证
- 先做了基础环境配置，包括MySQL、redis、jwt等工具包
- 1、在认证责任链的末端，替换交换凭据和UserDetailsService接口，实现数据库单表的账号密码认证，当然这时没有加密算法，密码是`{noop}明文`
- 然后是补充密码加密存储，依托spring环境，比较简单实现，在springConfig中声明一个passwordEncoder的bean即可。
- 2、第二步骤采用的Security的Basic+FormLogin默认方式，这里新增了登录口，并重写configure接口，实现自定义登录接口！
- 当然，这里对登录口进行了方向，不然就进入死循环了
- 3、配置了认证过滤器filter，校验请求头是否有token，没有就放行（后面还有其他关卡），有就校验token合法性、是否redis缓存，这里暂时不授权
- 4、补充退出功能，从SecurityContextHolder中获得当前用户，然后清理redis（jwt无法清理、SecurityContextHolder下次登录会覆盖，所以都不进行逆操作）

三、接下来是授权：
- 1、先进行简单尝试，开启授权并对认证链末端改造
  - SecurityConfig开启授权开关
  - 前端控制器的方法添加资源控制
  - 改造认证链末端的交换凭据和接口进行`硬编码`授权
  - 测试OK
- 2、在上面的基础上开启RBAC模型改造
  - mysql表结构改造，引入经典的5张表，当然user表已经有了
  - 导入menu的mybatis实体、mapper和自定义查询（权限的来源）
  - 替换上面认证链末端接口的硬编码授权，改为数据库查询
  - 完成测试

最后，进行了常用补充：
- 自定义异常处理，包括认证失败和权限不足的json异常返回
- 跨域问题处理，这里做了mvc和Security的两个操作，不过后者没有触发所以测试很难推进，先待定
- 自定义权限校验方法，这个应该是个重点，原生的设计会造就很多的代码，权限模型也比较简单不一定适合大规模系统！
- 补充了CSRF、认证成功或失败或退出的处理器

# 收获
收获很多，具体如下：
- 首先是掌握了SpringSecurity，特别是前后端分离下的应用，当然前端部分有提供demo，但需要自行研究
- 源代码的阅读技巧，一小部分，作者在B站有其他课程可供深入学习
- IDEA使用技巧
- free mybatis tools插件的介绍
- jwt在分布式环境的token解决方案
- 学习过程中，找到了OneDark皮肤，阅读代码真的是个享受啊~