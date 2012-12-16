<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
	<title>Web Service高级演示</title>
	<script>
		$(document).ready(function() {
			$("#webservice-tab").addClass("active");
		});
	</script>
</head>

<body>
	<h1>Web Service 演示</h1>

	<h2>1. 传统SOAP Web Service演示</h2>
	<ul>
		<li>服务端是基于CXF的JAX-WS演示, 见CXF自动生成的<a href="${ctx}/cxf/">wsdl文件列表</a></li>
		<li>客户端见Functional Test用例, 或SoapUI工具.</li>
	</ul>
		
	<h2>2. 基于JAX-RS实现的Restful演示</h2>
	<ul>
		<li>服务端是基于CXF的JAX-WS演示, CXF自动生成的<a href="${ctx}/cxf/">wadl文件列表</a></li>	
		<li>演示地址：<a href="${ctx}/cxf/rest/user/1.xml">/cxf/rest/user/1.xml</a> 与 <a href="${ctx}/cxf/rest/user/1.json">/cxf/rest/user/1.json</a></li>
	</ul>	

	<h2>3. SpringMVC Restful Service高级演示</h2>
	服务端:
	<ul>
		<li>集成Shiro进行HttpBasic的认证 </li>
		<li>XML与JSON两种格式的序列化</li>
		<li>演示地址：<a href="${ctx}/api/v1/user/1.xml">/api/v1/user/1.xml</a> 与 <a href="${ctx}/api/v1/user/1.json">/api/v1/user/1.json</a> (用浏览器访问时需要认证admin/admin)</li>
	</ul>
	客户端:
	<ul>
		<li>RestTemplate设置HttpHeaders的写法，用原始的exchange()方法及用ClientHttpRequestInterceptor两种方法</li>
		<li>RestTemplate底层使用用默认的JDK HttpConnection，也可设置为Apache HttpClient4</li>
		<li>RestTemplate设置超时控制</li>
		<li>客户端同样见Functional Test用例UserRestFt.java</li>
	</ul>
	
</body>
</html>