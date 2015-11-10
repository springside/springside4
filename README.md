SpringSide是以Spring Framework为核心的，Pragmatic风格的JavaEE应用参考示例，是JavaEE世界中的主流技术选型，最佳实践的总结与演示。
  
  1. BootApi - 基于Spring Boot的Web Service应用, 可以用于SOA服务，或Ajax页面的后台.
  3. BootWeb - 基于Spring Boot的Web应用, 典型的增删改查管理.
  3. Showcase - 更多的示例.
 

## 主要用例

全部示例以一个P2P图书馆展开，P2P图书馆避免了中央式图书馆所需的场地和图书管理员，大家把图书登记在应用里互相借阅。


**图书借阅流程**

1. 用户浏览图书

2. 用户发起借阅请求，也可以取消借阅请求

3. 图书拥有者在交接图书后确认借阅，也可以拒绝借阅请求

4. 图书拥有者在收回图书后确认归还

**图书管理流程**

用户可以自行上传，修改，删除自己的图书。

**用户管理流程**

用户可以注册，登录与注销。

## 快速开始

1. 运行根目录下的quick-start.sh 或 quick-start.bat
   * 将modules安装到本地maven仓库
   * 以开发模式启动BootApi应用

2. 访问 http://localhost:8080/，按上面的提示体验。


-------------------------------
Offical Site: http://springside.io

Document: https://github.com/springside/springside4/wiki