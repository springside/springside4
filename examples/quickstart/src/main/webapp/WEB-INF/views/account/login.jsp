<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ page import="org.apache.shiro.authc.ExcessiveAttemptsException"%>
<%@ page import="org.apache.shiro.authc.IncorrectCredentialsException"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<html>
<head>
	<title>登录页</title>
	<script>
		$(document).ready(function() {
			$("#loginForm").validate();
		});
	</script>
</head>

<body>
    <form id="loginForm" action="${ctx}/login" method="post" class="input-form " style="max-width: 300px;">
	   	<%
		String error = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
		if(error != null){
		%>
			<div class="alert alert-error controls">
				<button class="close" data-dismiss="alert">×</button>登录失败，请重试.
			</div>
		<%
		}
		%>
    
		<h3>登录</h3>
		<input type="text" id="username" name="username"  value="${username}" class="input-block-level" placeholder="Login Name"/>
		<input type="password" id="password" name="password"  class="input-block-level" placeholder="Password"/>
		<label class="checkbox">
			<input type="checkbox" id="rememberMe" name="rememberMe" value="remember-me"/> Remember me
		</label>
		
		<button class="btn btn-primary" type="submit">登录</button>
		<a class="btn" href="${ctx}/register">注册</a>
	</form>
	<p style="text-align: center;">(管理员: admin/admin, 普通用户: user/user)</p>
</body>
</html>
