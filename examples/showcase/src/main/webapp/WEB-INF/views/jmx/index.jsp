<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
<title>JMX演示用例</title>
</head>

<body>
	<h2>JMX演示用例</h2>

	<h3>技术说明：</h3>
	<ul>
		<li>服务端演示使用Spring annotation定义MBean</li>
	</ul>

	<h3>用户故事：</h3>
	<div>
		使用JMX动态配置查看和配置Log4J日志等级。<br /> 客户端可使用JConsole或JManager, 远程进程URL为 localhost:2099
		或完整版的service:jmx:rmi:///jndi/rmi://localhost:2099/jmxrmi
	</div>
</body>
</html>