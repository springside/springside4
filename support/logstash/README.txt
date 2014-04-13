Logstash Demo
===================

* error_collector: 演示从不同机器上收集日志文件中的所有错误日志，输出到一个中央文件中。
* safe_shipper: 演示file input的sincedb功能，保证所有的日志文件(包括rotate的文件)都能被收集到, 即使logstash收集节点中途被停掉再恢复。
* metrics_shipper: 演示从业务日志中分析各种业务事件的TPS，输出到Graphite。

演示准备：   
* 下载最新版的logstash-*.jar，放到本目录

error_collector的演示流程：
1. 运行子目录中的shipper.bat 与 collector.bat
2. 在showcase中产生错误日志(建议使用Hystrix演示来产生, 见C:\tmp\logs\showcase.log)
3. 访问http://localhost:9292/ 访问KabanaGUI
4. 查看子目录中的showcase_error_2014-xx-xx.log

safe_shipper的演示流程： 不能在Windows上运行。

metrics_shipper的演示流程：
1. 运行子目录中的shipper.bat
2. 启动Graphite
3. 在showcase中访问和修改用户，产生业务日志(见C:\tmp\logs\business.log)
4. 在logstash的窗口中，可以看到业务日志被收集，并且每10秒产生一条metric汇总日志
5. 访问Graphite的dashboard查看结果

如果未安装Graphite，请将配置中Graphite部分用#注释。




