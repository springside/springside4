  SpringSide is a Spring Framework based JavaEE application reference architecture. 
  
  It shows the mainstream technologies and pragmatic practice in JavaEE world.  
  
  1. BootApi - a Spring Boot based WebService application, it can be used as SOA or the backend of Ajax application.
  3. BootWeb — a Spring Boot base Web application, an simple CRUD web application.
  3. Showcase - Other Advanced Examples
 

## 主要用例

全部示例以一个P2P图书馆展开，P2P图书馆避免了中央式图书馆所需的场地和图书管理员，大家直接把图书放到应用里互相借阅。


**图书借阅流程**

1. 用户浏览图书

2. 用户发起借阅请求，也可以取消借阅请求

3. 图书拥有者在交接图书后确认借阅，也可以拒绝借阅请求

4. 图书拥有者在收回图书后确认归还

**图书管理流程**

用户可以自行上传，修改，删除自己的图书。

**用户管理流程**

用户可注册，登录与注销。

## 快速开始

1. 运行根目录下的quick-start.sh 或 quick-start.bat
   * 将modules安装到本地maven仓库
   * 初始化生产环境下的数据库
   * 以开发模式启动BootApi应用

2. 访问 http://localhost:8080/


-------------------------------
Offical Site: http://springside.io

Document: https://github.com/springside/springside4/wiki