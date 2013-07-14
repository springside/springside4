<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
	<title>JMS演示</title>
	<script>
		$(document).ready(function() {
			$("#redis-tab").addClass("active");
		});
	</script>
</head>

<body>
	<h1>Redis演示</h1>
	
	<h2>用户故事：</h2>
	所有演示在src/**/demo/redis/ 目录中独立运行main函数.
	<ul>
		<li>高性能Counter的benchmark测试用例</li>
		<li>高性能Session的benchmark测试用例</li>
		<li>高性能Scheduler的benchmark测试用例</li>
		<li>集群内master选举功能演示</li>
	</ul>
</body>
</html>