Logstash Demo
===================

* error_collector: 演示从不同机器上收集日志文件中的所有错误日志，输出到一个中央文件中。
* metrics_shipper: 演示从业务日志中分析各种业务事件的TPS，输出到Graphite。

演示准备：   
* 下载最新版的logstash(www.logstash.net)，解压并设置系统变量PATH指向其bin目录。

error_collector的演示流程：
1. 运行子目录中的shipper.bat 与 collector.bat
2. 在showcase中产生错误日志(建议使用Hystrix演示来产生, 见C:\tmp\logs\showcase.log，注2)
3. 访问http://localhost:9292/ 访问KibanaGUI
4. 查看子目录中的showcase_error_2014-xx-xx.log

metrics_shipper的演示流程：
1. 运行子目录中的shipper.bat
2. 启动Graphite(可选)
3. 在showcase中访问和修改用户，产生业务日志(见C:\tmp\logs\business.log，注2)
4. 在logstash的窗口中，可以看到业务日志被收集，并且每10秒产生一条metric汇总日志
5. 访问Graphite的dashboard查看结果(可选)

注1：如果未安装Graphite，请将配置中Graphite部分用#注释，仅看Console的输出。
注2：在window上，/tmp 不一定是c:\tmp，也可能在其他盘。




