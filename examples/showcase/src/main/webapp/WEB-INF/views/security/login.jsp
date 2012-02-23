<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ page import="org.apache.shiro.authc.ExcessiveAttemptsException"%>
<%@ page import="org.apache.shiro.authc.IncorrectCredentialsException"%>
<%@ page import="java.util.concurrent.atomic.AtomicLong"%>
<%@page import="org.springside.examples.showcase.security.FormAuthenticationWithLockFilter"%>
<%@page import="org.apache.commons.lang.StringUtils"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<html>
<head>
	<title>Showcase 登录页</title>
	<script src="${ctx}/static/jquery-validation/1.8.0/jquery.validate.min.js" type="text/javascript"></script>
	<script src="${ctx}/static/jquery-validation/1.8.0/messages_cn.js" type="text/javascript"></script>
	<script>
		$(document).ready(function() {
			$("#loginForm").validate();
		});
	</script>
</head>

<body>
		<h2>Showcase登录页</h2>
		
		<%
		String error = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
		if(error != null){
		    if (error.equals(ExcessiveAttemptsException.class.getName())) {
		%>
			<div class="error">账户已被锁，请联系管理员.</div>
		<%
			}else if(error.equals(IncorrectCredentialsException.class.getName())) {
		%>
			<div class="error">密码错误，还有<%=FormAuthenticationWithLockFilter.accountLockMap.get(request.getParameter(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM)).get() %>次重试机会</div>
		<%
			}else{
		%>
			<div class="error">登录失败，请重试.</div>
		<%
			}
		}
		%>
	
		<form id="loginForm" action="login.action" method="post">
			<fieldset>
				<p>
					<label for="username">用户名:</label>
					
						<input type='text' name='username' size='10' class="required"
						<s:if test="not empty param.error">
							value=''</s:if> />
					
				</p>
				<p>
					<label for="password">密码:</label>
					<input type='password' size='10' name='password' class="required"/>
				</p>
				<p>
					<input type="checkbox" id="remember_me" name="remember_me"/>
						<label for="remember_me">两周内记住我</label>
						
					
				</p>
				<p><input value="登录" type="submit"/></p>
			</fieldset>
		</form>
		<div>（管理员<b>admin/admin</b> ,普通用户<b>user/user</b>）<a href="logout.action">退出登录</a></div>
</body>
</html>

