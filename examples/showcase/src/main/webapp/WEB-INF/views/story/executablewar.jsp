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
	<h1>可运行War演示</h1>

	<h2>技术说明</h2>
	<ul>
		<li>可运行war使用嵌入式的Jetty，简便地在容器外运行web应用。</li>
	</ul>

	<h2>用户故事</h2>
		<p>在showcase中打包可运行war包的命令：</p>
		<blockquote>
		mvn clean package -Pstandalone
		</blockquote>
		<p>运行war包的命令： </p>
		<blockquote>
		java -jar showcase-4.x.x.standalone.war
		</blockquote>
	</ul>
</body>
</html>