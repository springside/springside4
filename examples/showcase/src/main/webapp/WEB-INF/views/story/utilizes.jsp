<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
	<title>工具类演示</title>
	<script>
		$(document).ready(function() {
			$("#utilizes-tab").addClass("active");
		});
	</script>
</head>

<body>
	<h1>工具类演示</h1>
	<h2>XML/JSON操作演示</h2>
	<ul>
		<li>基于JAXB2.0的Java-XML绑定, 演示根元素是List, 属性是List与Map等特殊情况.</li>
		<li>万能老倌Dom4j.</li>
		<li>性能最快功能也最丰富的JSON转换类库Jackson.</li>
		<li>在JaxbDemo.java中演示XML与Java对象的转换及Dom4j的使用.</li>
		<li>在JsonDemo.java中演示Jackson远超同类JSON库的转化能力.</li>
	</ul>
	
	<hr/>
	
	<h2>Email演示</h2>
	<ul>
		<li>简单文本邮件演示.</li>
		<li>带附件的MIME邮件演示, 使用Freemarker模板创建HTML内容.</li>
		<li>在综合演示用例中保存用户时，发送两种邮件.<br /> 演示邮箱名为springside3.demo@gmail.com, 密码为demoforyou.</li>
	</ul>
	
	<hr/>
	
	<h2>日志演示</h2>
	<ul>
		<li>Log4JManager: 通过JMX动态查询与改变Logger的日志等级与Appender, 详见JMX页面.</li>
		<li>Log4MockjAppender: 在测试用例中验证日志的输出, 在Schedule测试用例使用MockAppender校验日志输出.</li>
	</ul>
	
	<hr/>
	
	<h2>其他常用工具</h2>
	<p>全部演示在org.springside.examples.showcase.utilities目录</p>
	<ul>
		<li>JodaTime: 日期操作.</li>
		<li>Dozer: 不同类型的对象互相复制与转换。</li>
		<li>Apache Commons IO: IO操作。</li>
		<li>Apache Commons Lang Validate: 防御式编程，校验输入参数.</li>
		<li>Apache Commons Lang StringUtils: 字符串操作.</li>
	</ul>
</body>
</html>