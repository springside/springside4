<%@ page contentType="text/html;charset=UTF-8"%>

<html>
<head>
	<title>Home</title>
	<script>
		$(document).ready(function() {
			$("#index-tab").addClass("active");
		});
	</script>
</head>
<body>
	<h1>What is it?</h1>
	<p style="padding: 15px;">各式主流的、实用的、好玩的开源项目大派对。</p>
	
	<h1>What is new?</h1>
	<h2>4.2版</h2>
	<ul>
		<li>性能监控新增Metrics/Graphite演示。</li>
		<li>新增Hystrix演示。</li>
	</ul>
	<h2>4.1版</h2>
	<ul>
		<li>新增Redis演示。</li>
		<li>新增性能监控演示。</li>
	</ul>
	<h2>4.0版</h2>
	<ul>
		<li>CSS 大装修完毕。</li>
		<li>CXF的SOAP WebService 与 MyBais从Mini-Service搬了过来。</li>
		<li>Shiro的授权演示搬了过来。</li>
	</ul>
</body>
</html>