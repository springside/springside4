<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ page import="org.apache.shiro.authc.ExcessiveAttemptsException"%>
<%@ page import="org.apache.shiro.authc.IncorrectCredentialsException"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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
	<form:form id="loginForm" action="${ctx}/login" method="post" class="form-horizontal">
	<%
	String error = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
	if(error != null){
	%>
		<div class="control-group">
		<div class="controls ">
		<div class="alert alert-error">
		<button class="close" data-dismiss="alert">×</button>
		登录失败，请重试.</div>
		</div>
		</div>
		
	<%
	}
	%>

			<div class="control-group">
				<label for="username" class="control-label">名称:</label>
				<div class="controls">
					<input type="text" id="username" name="username" size="50" value="${username}" class="required span2"/>
				</div>
			</div>
			<div class="control-group">
				<label for="password" class="control-label">密码:</label>
				<div class="controls">
					<input type="password" id="password" name="password" size="50"  class="required span2"/>
				</div>
			</div>
			<div class="control-group">
				<div class="controls">
				<label class="checkbox inline" for="rememberMe"> <input type="checkbox" id="rememberMe" name="rememberMe"/> 记住我</label>
				<input id="submit" class="btn" type="submit" value="登录"/>
				<p class="help-block">(管理员<b>admin/admin</b>, 普通用户<b>user/user</b>)</p>
				</div>
			</div>
		</div>
	</form:form>
</body>
</html>
