<%@ page contentType="text/html;charset=UTF-8" %>
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
        					$('#hystrixResponse').html('<strong>Status:</strong> '+jqXHR.status+', <strong>Content:</strong> ' + jqXHR.responseText );
        			});
        		}
        </script>
</head>

<body>
        <h1>Hystrix 演示</h1>
        
        <p><a href="https://github.com/Netflix/Hystrix" target="_blank">Netflix Hystrix</a> 是一个类库，通过控制远程系统、服务的访问，对延迟和故障提供更强大的容错能力，在复杂的分布式系统里停止雪崩式的错误。</p>

        <h2>演示操作</h2>
        <div><form action="${ctx}/hystrix/status" class="form-inline"><span class="help-inline">依赖资源状态:  </span><form:select id="status" path="statusHolder.value" items="${allStatus}" labelCssClass="inline"/> <input id="submit_btn" class="btn" type="submit" value="更新"/></form> </div>
        <div><input type="button" class="btn btn-primary" value="访问Hystrix服务" onclick="accessHystrixService();"/></div>
        <div>&nbsp;  </div>
        <div id="hystrixResponse"></div>
       	
        <h2>主要用户故事</h2>
		<ul>
			<li>在默认的正常状态，访问Hystrix服务和依赖资源，均返回正常结果。</li>
			<li>将资源状态切换为"超时"，依赖资源需要在15秒后才返回结果访问,此时访问Hystrix服务，3秒后超时返回503。</li>
			<li>两次超时后满足短路条件(60秒滚动窗口内起码有3个请求，50%失败)，再次访问服务，立即返回503。</li>
			<li>20秒短路保护期内，所有访问立即返回503，日志内不会显示访问依赖资源。</li>
			<li>保护期过后，放行一个请求，如果还是超时，则继续保持短路状态。如果成功则重置所有计数器。</li>
		</ul>

		<h2>其他用户故事</h2>
        <ul>
            <li>将资源状态切换为"服务端失败"，访问 Hystrix服务，即时返回500错误。</li>
            <li>将资源状态切换为"错误请求"，依赖资源返回400错误，访问Hystrix服务，返回500错误，但错误率变化统计无变化。</li>
            <li>使用JMeter高并发测Hystrix试线程池用光之后的保护.</li>
            <li>启动后切换为<a href="${ctx}/hystrix/disableIsolateThreadPool">调用者线程模式</a>， 依靠RestTemplate自身的超时机制，延长为10秒。</li>
        </ul>
        
        <h2>监控结果 </h2>
        <form><input type="submit" value="刷新" class="btn btn-success"/></form>
		<table class="table table-bordered">
			<tr><td>是否短路：${metrics['circuitOpen']}</td></tr>
			<tr><td>2分钟窗口内，请求数：${metrics['totalRequest']}， 失败百分比：${metrics['errorPercentage']}%，成功：${metrics['success']}， 超时：${metrics['timeout']}，失败：${metrics['failure']}，短路：${metrics['shortCircuited']}， 线程池满：${metrics['threadPoolRejected']}</td></tr>
			<tr><td>最快的50%请求延时：${metrics['latency50']}ms，90%请求延时：${metrics['latency90']}ms，100%请求延时：${metrics['latency100']}ms </td></tr>
		</table>
</body>
</html>