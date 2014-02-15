Logstash Demo
===================

* error_collector: 从不同机器上收集日志文件中的所有错误日志，输出到一个文件中。
* safe_log_collector: logstash中file input的sincedb功能演示，如何保证所有的日志文件(包括rotate的文件)都能被logstash收集到, 包括logstash收集节点中途停掉又恢复的情况。

error_collector的演示流程：
1. 下载 最新版的logstash-*.jar 放到本目录。
2. 运行 子目录中的shipper.bat 与 collector.bat, 同时在showcase中产生错误日志(建议使用Hystrix演示来产生)
3. 访问 http://localhost:9292/ 访问GUI
4. 查看 子目录中的showcase_error_2014-xx-xx.log

safe_log_collector的演示流程：
不能在Windows上运行

