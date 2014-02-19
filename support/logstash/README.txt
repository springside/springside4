Logstash Demo
===================

* error_collector: 从不同机器上收集日志文件中的所有错误日志，输出到一个文件中。
* safe_shipper: logstash中file input的sincedb功能演示，如何保证所有的日志文件(包括rotate的文件)都能被logstash收集到, 包括logstash收集节点中途停掉又恢复的情况。
* metrics_shipper: 从业务日志中分析各种业务事件发生的TPS，输出到Graphite

演示准备：   
* 下载 最新版的logstash-*.jar 放到本目录。

error_collector的演示流程：

1. 运行 子目录中的shipper.bat 与 collector.bat,
2. 在showcase中产生错误日志(建议使用Hystrix演示来产生)
3. 访问 http://localhost:9292/ 访问KabanaGUI
4. 查看 子目录中的showcase_error_2014-xx-xx.log

safe_shipper的演示流程： 不能在Windows上运行。

metrics_shipper的演示流程：
1. 运行 子目录中的shipper.bat
2. 启动Graphite(optional)
3. 在showcase中访问和修改用户，产生业务日志
4. 在logstash的窗口中，可以看到业务日志被收集，并且每10秒产生一条metric汇总信息
5. 在Graphite的dashboard查看结果。




