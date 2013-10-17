<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.springside.examples.showcase.demos.hystrix.dependency.DependencyResourceController,com.netflix.hystrix.*,com.netflix.hystrix.HystrixCommandMetrics.HealthCounts,com.netflix.hystrix.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springside.org.cn/tags/form" %>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
        <title>Web Service高级演示</title>
        <script>
                $(document).ready(function() {
                        $("#hystrix-tab").addClass("active");
                });
                
            	function accessHystrixService() {
        		$.ajax("${ctx}/hystrix/user/1")
        				.done( function(data, textStatus, jqXHR) {
        					$('#hystrixResponse').removeClass("alert alert-error").addClass("alert alert-success");
        					$('#hystrixResponse').html('<strong>Status:</strong> '+jqXHR.status+', <strong>Content:</strong> ' + jqXHR.responseText );
        			}).fail( function(jqXHR, textStatus){
        					$('#hystrixResponse').removeClass("alert alert-success").addClass("alert alert-warning");
        					$('#hystrixResponse').html('<strong>Status:</strong> '+jqXHR.status+', <strong>Content:</strong> ' + jqXHR.statusText );
        			});
        		}
        </script>
</head>

<body>
        <h1>Hystrix 演示</h1>
        
        <p><a href="https://github.com/Netflix/Hystrix" target="_blank">Netflix Hystrix</a> 是一个延迟与容错类库，通过独立访问远程系统、服务和第三方库的节点，在复杂的分布式系统里停止雪崩及提供恢复能力。</p>

        <h2>演示操作</h2>
        <div><form action="${ctx}/hystrix/status" class="form-inline"><span class="help-inline">依赖资源状态: </span><form:bsradiobuttons id="status" path="statusHolder.value" items="${allStatus}" labelCssClass="inline"/> <input id="submit_btn" class="btn btn-primary" type="submit" value="更新"/></form> </div>
        <div><input type="button" class="btn" value="访问Hystrix服务" onclick="accessHystrixService();"/></div>
        <div>&nbsp;  </div>
        <div id="hystrixResponse"></div>
       	
        <h2>主要用户故事</h2>
		<ul>
			<li>在默认的正常状态，访问Hystrix服务和依赖资源，均返回正常结果。</li>
			<li>将资源状态切换为"超时"，依赖资源需要在15秒后才返回结果访问,此时访问Hystrix服务，3秒后超时，返回503。</li>
			<li>三次超时后满足短路条件(60秒滚动窗口内起码有3个请求，50%失败)，再次访问服务，立即返回503。</li>
			<li>10秒短路保护期内，所有访问都立即返回503，不会访问依赖资源。</li>
			<li>保护期过后，会放行一个请求，如果还是超时，则继续保持短路状态。如果成功则重置所有计数器。</li>
		</ul>

		<h2>其他用户故事</h2>
        <ul>
            <li>将资源状态切换为"服务端失败"，访问 Hystrix服务，即时返回500错误。</li>
            <li>将资源状态切换为"错误请求"，依赖资源返回400错误，访问Hystrix服务，返回500错误，但错误率变化统计无变化。</li>
            <li>切换为调用者线程模式。</li>
        </ul>
        
        <h2>监控结果 </h2>
        <form><input type="submit" value="刷新" class="btn"/></form>
		<ul>
			<li>是否短路：${metrics['circuitOpen']}</li>
			<li>3分钟窗口内，请求数：${metrics['totalRequest']}， 失败百分比：${metrics['errorPercentage']}%，成功：${metrics['success']}， 超时：${metrics['timeout']}，失败：${metrics['failure']}，短路：${metrics['shortCircuited']}， 线程池满：${metrics['threadPoolRejected']}
			<li>50%延时：${metrics['latency50']}ms，90%延时：${metrics['latency90']}ms，100%延时：${metrics['latency100']}ms</li>
		</ul>
</body>
</html>