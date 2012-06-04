<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
	<title>权限组管理</title>
	<script>
		$(document).ready(function() {
			//聚焦第一个输入框
			$("#name").focus();
			
			//active tab
			$("#group-tab").addClass("active");
			
			//为inputForm注册validate函数
			$("#inputForm").validate({
				rules:{
					name:{
						remote: "${ctx}/account/group/checkGroupName?oldName="+encodeURIComponent('${group.name}')
					}
				},
				messages:{
					name:{remote:"权限名已存在"}
				},
				errorContainer: "#messageBox"
			});
		});
	</script>
</head>

<body>
	<form:form id="inputForm" modelAttribute="group" action="${ctx}/account/group/save/${user.id}" method="post" class="form-horizontal">
		<input type="hidden" name="id" value="${group.id}"/>
		<fieldset>
			<legend><small>管理权限组</small></legend>
			<div id="messageBox" class="alert alert-error" style="display:none">输入有误，请先更正。</div>
			
			<div class="control-group">
				<label for="name" class="control-label">名称:</label>
				<div class="controls">
					<input type="text" id="name" name="name" size="50" class="required" value="${group.name}"/>
				</div>
			</div>
			<div class="control-group">
				<label for="permissionList" class="control-label">权限列表:</label>
				<div class="controls">
					<form:checkboxes path="permissionList" items="${allPermissions}" itemLabel="displayName" itemValue="value" />
				</div>
			</div>	
			<div class="form-actions">
				<input id="submit" class="btn btn-primary" type="submit" value="提交"/>&nbsp;	
				<input id="cancel" class="btn" type="button" value="返回" onclick="history.back()"/>
			</div>
		</fieldset>
	</form:form>
</body>
</html>
