Logstash Usage Demo
===================

* error_collector: 从不同机器上收集日志文件中的所有错误日志，输出到一个文件中。
* full_log_collector: logstash中file input的sincedb功能演示，如何保证所有的日志文件（包括rotate的文件）都能被logstash收集到（包括logstash收集节点中途停掉又恢复的情况）。