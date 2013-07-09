<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.springside.examples.showcase.demos.hystrix.web.HystrixController,com.netflix.hystrix.*,com.netflix.hystrix.HystrixCommandMetrics.HealthCounts,com.netflix.hystrix.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%!  %>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
	<title>Web Service高级演示</title>
	<script>
		$(document).ready(function() {
			$("#hystrix-tab").addClass("active");
		});
	</script>
</head>

<body>
	<h1>Hystrix 演示</h1>
	
	<p><a href="https://github.com/Netflix/Hystrix" target="_blank">Netflix Hystrix</a> 是一个延迟与容错类库，通过独立访问远程系统、服务和第三方库的节点，在复杂的分布式系统里停止雪崩及提供恢复能力。</p>

	<h2>演示操作</h2>
	<ul>
		<li>依赖资源当前状态为<%= HystrixController.status %>：切换为<a href="${ctx}/story/hystrix/status/normal">正常</a>、<a href="${ctx}/story/hystrix/status/timeout">超时</a>、<a href="${ctx}/story/hystrix/status/fail">失败</a>。</li>
		<li>Hystrix服务当前异常处理方式为<%= HystrixController.fallback %>：切换为<a href="${ctx}/story/hystrix/fallback/exception">抛出异常</a>、<a href="${ctx}/story/hystrix/fallback/dummy">返回默认用户</a>、<a href="${ctx}/story/hystrix/fallback/standby">访问备用节点</a>。</li>
		<li>访问服务：<a href="${ctx}/hystrix/service/1" target="_blank">Hystrix服务</a>、<a href="${ctx}/hystrix/resource/1" target="_blank">依赖资源</a>、<a href="${ctx}/hystrix/resource/standby/1" target="_blank">资源的备用节点</a>。</li>
	</ul>
	<h2>主要用户故事</h2>
	<ul>
		<li> 在默认的正常状态，访问Hystrix服务和依赖资源，均返回正常结果。</li>
		<li> 将资源状态切换为"超时"，访问依赖资源，需要在10秒后才返回结果。</li>
		<li> 访问Hystrix服务，2秒后超时，返回503。</li>
		<li> 三次超时后满足短路条件(60秒滚动窗口内起码有3个请求，50%失败)，再次刷新服务，立即返回503。</li>
		<li> 10秒短路保护期内，所有访问都立即返回503，不会访问依赖资源。</li>
		<li> 保护期过后，会放行一个请求，如果还是超时，则继续保持短路状态。如果成功则重置所有计数器。</li>
	</ul>
	
	<h2>其他用户故事</h2>
	<ul>
		<li> 将资源状态切换为"失败"，访问依赖资源，立即返回500错误，访问Hystrix服务，即时返回503错误。</li>
		<li> 将异常处理方式切换为"返回默认用户"，访问Hystrix服务，返回默认用户。</li>
		<li> 将异常处理方式切换为"访问备用节点"，访问Hystrix服务，返回正确结果。</li>
	</ul>
	
	<h2>监控结果 </h2>
	<form><input type="submit" value="刷新"/></form>
	<%
		HystrixCommandKey key = HystrixCommandKey.Factory.asKey("GetUserCommand");
		HystrixCommandMetrics metrics = HystrixCommandMetrics.getInstance(key);
		if (metrics!=null){
			HealthCounts counts = metrics.getHealthCounts();
			HystrixCircuitBreaker circuitBreaker = HystrixCircuitBreaker.Factory.getInstance(key);
			
		
	%>
		<ul>
			<li>是否短路：<%= circuitBreaker.isOpen() %></li>
			<li>窗口内请求：<%= counts.getTotalRequests()%>，失败百分比：<%= counts.getErrorPercentage() %>%
			<li>窗口成功：<%= metrics.getRollingCount(HystrixRollingNumberEvent.SUCCESS) %>， 超时：<%= metrics.getRollingCount(HystrixRollingNumberEvent.TIMEOUT) %>，失败：<%= metrics.getRollingCount(HystrixRollingNumberEvent.FAILURE) %>，短路：<%= metrics.getRollingCount(HystrixRollingNumberEvent.SHORT_CIRCUITED) %></li>
			<li>50%延时：<%= metrics.getTotalTimePercentile(50) +"ms"%>，90%延时：<%= metrics.getTotalTimePercentile(90) +"ms"%>，100%延时：<%= metrics.getTotalTimePercentile(100) +"ms"%></li>
		</ul>
	<%} %>
</body>
</html>