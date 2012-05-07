<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<title>Restful Service高级演示</title>
	<script>
		$(document).ready(function() {
			$("#webservice-tab").addClass("active");
		});
	</script>
</head>

<body>
		<h2>Restful Service 高级演示</h2>

		<h3>技术说明:</h3>
		<ul>
			<li>HttpBasic认证, 与Shiro权限控制结合.</li>
			<li>Multi-part演示.</li>
			<li>根据不同的输入参数, 返回不同编码格式(html/json),及格式特定的内容(html字符串/java对象)</li>
			<li>获取灵活的，不固定的输入参数.</li>
		</ul>
</body>
</html>