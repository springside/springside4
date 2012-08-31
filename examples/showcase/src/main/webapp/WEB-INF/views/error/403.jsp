<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ page import="org.apache.shiro.authc.ExcessiveAttemptsException"%>
<%@ page import="org.apache.shiro.authc.IncorrectCredentialsException"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
	<title>403 - 用户权限不足</title>
</head>

<body>
	<h2>403 - 用户权限不足.</h2>
	<p><a href="<c:url value="/"/>">返回首页</a></p>
</body>
</html>
