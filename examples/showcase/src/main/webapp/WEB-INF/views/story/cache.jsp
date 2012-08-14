<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
	<title>Cache演示</title>
	<script>
		$(document).ready(function() {
			$("#cache-tab").addClass("active");
		});
	</script>
</head>

<body>
	<h1>Cache演示</h1>

	<h2>技术说明：</h2>
	<ul>
		<li>演示基于Guava的单JVM内的，简单Cache</li>
		<li>演示基于Ehcache的JVM内的，可集群共享的，功能丰富的Cache</li>
		<li>演示基于Memcached的中央式cache，使用Spymemcached客户端</li>	
	</ul>

	<h2>用户故事：</h2>
	<ul>
		<li>GuavaCacheDemo.java演示了Guava Cache的使用</li>
		<li>EhcacheDemo.java演示了Ehcache与Spring的集成</li>
		<li>在AccountEffectiveService.java中演示了对Memcached的使用</li>
	</ul>
</body>
</html>