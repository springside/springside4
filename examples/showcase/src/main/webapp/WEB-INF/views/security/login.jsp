<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ page import="org.apache.shiro.authc.LockedAccountException "%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
	<title>Showcase 登录页面</title>
	<script src="${ctx}/static/jquery-validation/1.9.0/jquery.validate.min.js" type="text/javascript"></script>
	<script src="${ctx}/static/jquery-validation/1.9.0/messages_cn.js" type="text/javascript"></script>
	<link href="${ctx}/static/jquery-validation/1.9.0/validate.css" type="text/css" rel="stylesheet" />
	
	<script>
		$(document).ready(function() {
			$("#loginForm").validate();
		});
	</script>
</head>

<body>
	<h3><small>登录页</small></h3>
	<form:form id="loginForm"  action="${ctx}/login" method="post">
			<%
			String error = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
			if(error != null){
				if(error.contains("DisabledAccountException")){
			%>		
				<div class="error prepend-top" >用户已被屏蔽,请登录其他用户.</div>
			<% 
				}else{
			%>
				<div class="error prepend-top" >登录失败，请重试.</div>
			<%
				}
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
					<input type="password" id="password" name="password" size="50" class="required span2"/>
				</div>
			</div>
		<div class="control-group">
			<div class="controls">
				<label class="checkbox" for="rememberMe"> <input type="checkbox" id="rememberMe" name="rememberMe"/> 记住我</label>
				<p class="help-block">(管理员<b>admin/admin</b>, 普通用户<b>user/user</b>)</p>
			</div>
		</div>
		<div class="form-actions">
			<input id="submit" class="btn btn-primary" type="submit" value="登录"/>			
		</div>
	</form:form>
</body>
</html>
