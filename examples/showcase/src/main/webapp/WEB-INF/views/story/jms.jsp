<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
	<title>JMS演示</title>
	<script>
		$(document).ready(function() {
			$("#jms-tab").addClass("active");
		});
	</script>
</head>

<body>
	<h2>JMS演示</h2>

	<h3>技术说明：</h3>
	<ul>
		<li>演示基于ActiveMQ的JMS Topic/Queue应用</li>
		<li>演示基于Spring CachingConnectionFactory, JmsTemplate, DefaultMessageListener的应用</li>
		<li>演示使用默认值的Simple模式</li>
		<li>演示Advanced模式, 包括发送者的timeToLive等属性设置, 接受者的消息过滤器,消息确认模式与持久化订阅者</li>
	</ul>

	<h3>用户故事：</h3>
	<ul>
		<li>在综合演示用例中保存用户时,异步发送通知消息邮件</li>
		<li>在servers/activemq目录演示优化过的activemq.xml配置文件</li>
	</ul>
</body>
</html>