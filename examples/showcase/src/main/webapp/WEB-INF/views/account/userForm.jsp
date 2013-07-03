<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springside.org.cn/tags/form" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
	<title>综合演示用例</title>
	<script src="${ctx}/static/jquery-validation/1.11.1/jquery.validate.min.js" type="text/javascript"></script>
	<script src="${ctx}/static/jquery-validation/1.11.1/messages_bs_zh.js" type="text/javascript"></script>
	<link href="${ctx}/static/jquery-validation/1.11.1/validate.css" type="text/css" rel="stylesheet" />
	<script>
		$(document).ready(function() {
			$("#account-tab").addClass("active");
			
			//为inputForm注册validate函数
			$("#inputForm").validate({
				rules: {
					loginName: {
						remote: "${ctx}/account/user/checkLoginName?oldLoginName=" + encodeURIComponent('${user.loginName}')
					},
					roleList:"required"
				},
				messages: {
					loginName: {
						remote: "用户登录名已存在"
					}
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					if ( element.is(":checkbox") )
						error.appendTo(element.parent().next());
					else
						error.insertAfter(element);
				}
			});
		});
	</script>
</head>

<body>
	<h1>综合演示用例</h1>
	<form:form id="inputForm" modelAttribute="user" action="${ctx}/account/user/update" method="post" class="form-horizontal">
		<input type="hidden" name="id" value="${user.id}"/>
		<fieldset>
			<legend><small>管理用户</small></legend>
			<div id="messageBox" class="alert alert-error input-large controls" style="display:none">输入有误，请先更正。</div>
			<div class="control-group">
				<label for="loginName" class="control-label">登录名:</label>
				<div class="controls">
					<input type="text" id="loginName" name="loginName" value="${user.loginName}" class="input-large required"/>
				</div>
			</div>
			<div class="control-group">
				<label for="name" class="control-label">用户名:</label>
				<div class="controls">
					<input type="text" id="name" name="name"  value="${user.name}" class="input-large required"/>
				</div>
			</div>
			<div class="control-group">
				<label for="plainPassword" class="control-label">密码:</label>
				<div class="controls">
					<input type="password" id="plainPassword" name="plainPassword" class="input-large" placeholder="...Leave it blank if no change"/>
				</div>
			</div>
			<div class="control-group">
				<label for="groupList" class="control-label">角色:</label>
				<div class="controls">
					<form:bscheckboxes path="roleList" items="${allRoles}" itemLabel="name" itemValue="id" />
				</div>
			</div>	
			<div class="control-group">
				<label for="status" class="control-label">状态:</label>
				<div class="controls">
					<form:bsradiobuttons path="status" items="${allStatus}" labelCssClass="inline"/>
				</div>
			</div>
			<div class="form-actions">
				<input id="submit_btn" class="btn btn-primary" type="submit" value="提交"/>&nbsp;	
				<input id="cancel_btn" class="btn" type="button" value="返回" onclick="history.back()"/>
				<p class="help-block">(保存后将发送JMS消息通知改动，而消息接收者将发送提醒邮件)</p>			
			</div>
		</fieldset>
	</form:form>
</body>
</html>
