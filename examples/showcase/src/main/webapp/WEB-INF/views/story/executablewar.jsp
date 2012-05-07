<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
	<title>可执行War演示</title>
	<script>
		$(document).ready(function() {
			$("#executablewar-tab").addClass("active");
		});
	</script>
</head>

<body>
	<h2>可运行War演示</h2>

	<h3>技术说明：</h3>
	<ul>
		<li>可运行war使用嵌入式的Jetty，简便地在容器外运行web应用.</li>
	</ul>

	<h3>用户故事：</h3>
	<ul>
		<li>在showcase中打包可运行war包的命令：<br/>
		mvn clean package -Pstandalone</li>
		
		<li>运行war包的命令: <br/>java -jar showcase-4.x.x.standalone.war</li>
	</ul>
</body>
</html>