<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Mini-Web 登录页</title>
<%@ include file="/common/meta.jsp"%>

<link href="${ctx}/css/style.css" type="text/css" rel="stylesheet" />
<link href="${ctx}/js/validate/jquery.validate.css" type="text/css"
	rel="stylesheet" />

<link href="${ctx}/css/blueprint/screen.css" type="text/css"
	rel="stylesheet" media="screen, projection" />
<link href="${ctx}/css/blueprint/print.css" type="text/css"
	rel="stylesheet" media="print" />
<!--[if lt IE 8]><link href="${ctx}/css/blueprint/blueprint/ie.css" type="text/css" rel="stylesheet" media="screen, projection"><![endif]-->

<script src="${ctx}/js/jquery-min.js" type="text/javascript"></script>
<script src="${ctx}/js/validate/jquery.validate.js"
	type="text/javascript"></script>
<script src="${ctx}/js/validate/messages_cn.js" type="text/javascript"></script>
<script>
	$(document).ready(function() {
		$("#loginForm").validate();
	});
</script>
</head>
<body>
	<div class="container">
		<%@ include file="/common/header.jsp"%>
		<div id="content">
			<div class="span-24 last">
				<%
					if (request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME) != null) {
				%>
				<div class="error">登录失败，请重试.</div>
				<%
					}
				%>
				<form id="loginForm" action="login.action" method="post"
					style="margin-top: 1em">
					<table class="noborder">
						<tr>
							<td><label for="username">用户名:</label>
							</td>
							<td><input type='text' id='username' name='username' class="required" value="${username}"
								<s:if test="not empty param.error">value=''</s:if> />
							</td>
						</tr>
						<tr>
							<td><label for="password">密码:</label>
							</td>
							<td><input type='password' id='password' name='password'
								class="required" value="${password}" />
							</td>
						</tr>
						<tr>
							<td colspan="2" align="right">
								<input type="checkbox" id="rememberMe" name="rememberMe" value="${rememberMe}" /> 
								<label for="rememberMe">记住我</label> <input value="登录" type="submit" class="button" />
							</td>
						</tr>
					</table>
				</form>
				<div>
					(管理员<b>admin/admin</b>, 普通用户<b>user/user</b>)
				</div>
			</div>
		</div>
		<%@ include file="/common/footer.jsp"%>
	</div>
</body>
</html>

