<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
	<title>定时任务演示</title>
	<script>
		$(document).ready(function() {
			$("#schedule-tab").addClass("active");
		});
	</script>
</head>

<body>
	<h2>定时任务演示</h2>

	<h3>技术说明：</h3>
	<ul>
		<li>JDK5.0 ScheduledExecutorService的Timer式任务定义, 支持Graceful Shutdown演示.</li>
		<li>Spring的Cront式任务定义, 支持Graceful Shutdown演示.</li>
		<li>Quartz的Timer式与Cron式任务定义.</li>
		<li>Quartz的任务在内存或数据库中存储, 单机或集群执行演示.</li>
	</ul>

	<h3>用户故事：</h3>
	<ul>
		<li>简单的定时在Console打印当前用户数量.</li>
		<li>设法同时运行两个实例, 演示Quartz集群运行的效果.</li>
	</ul>
</body>
</html>