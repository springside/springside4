<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
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
	<h1>Web Service 演示</h1>

	<h2>传统SOAP Web Service演示</h2>
	<ul>
		<li>服务端是基于CXF的JAX-WS演示. 详见由CXF自动生成的<a href="${ctx}/soap">服务信息及wsdl文件</a>.</li>
		<li>客户端见Functional Test用例.</li>
	</ul>
		
	<h2>Restful Service高级演示</h2>
	<ul>
		<li>集成Shiro进行HttpBasic的认证</li>
		<li>XML与JSON两种格式</li>
		<li>RestTemplate设置HttpHeaders的写法，用原始的exchange()方法及用ClientHttpRequestInterceptor两种方法</li>
		<li>RestTemplate默认使用JDK HttpConnection，设置使用Apache HttpClient4作为底层</li>
		<li>客户端同样见Functional Test用例</li>
	</ul>
</body>
</html>