Logstash Usage Demo
===================

* error_collector: 从不同机器上收集日志文件中的所有错误日志，输出到一个文件中。
* safe_log_collector: logstash中file input的sincedb功能演示，如何保证所有的日志文件（包括rotate的文件）都能被logstash收集到（包括logstash收集节点中途停掉又恢复的情况）。

错误日志收集的演示流程：
1. 下载 logstash-1.3.2-flatjar.jar 放到本目录，将子目录中的shipper.conf.example 复制成 shipper.conf, 修改log日志路径。
2. 运行 shipper 与 collector, 同时在showcase中产生错误日志
3. 访问 http://localhost:9292/ 访问GUI