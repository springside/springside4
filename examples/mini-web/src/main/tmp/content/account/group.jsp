<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.SecurityUtils" %>
<%@ include file="/common/taglibs.jsp" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Mini-Web权限组管理</title>
	<%@ include file="/common/meta.jsp" %>
	
	<link href="${ctx}/css/style.css" type="text/css" rel="stylesheet"/>
	
	<link href="${ctx}/css/blueprint/screen.css" type="text/css" rel="stylesheet" media="screen, projection"/>
	<link href="${ctx}/css/blueprint/print.css" type="text/css" rel="stylesheet" media="print"/>
	<!--[if lt IE 8]><link href="${ctx}/css/blueprint/blueprint/ie.css" type="text/css" rel="stylesheet" media="screen, projection"><![endif]-->
</head>

<body>
<div class="container">
	<%@ include file="/common/header.jsp" %>
	<div id="content" class="span-24 last prepend-top">
		<div id="message"><s:actionmessage theme="custom" cssClass="success"/></div>
		<div>你好, <shiro:principal/>.</div>
		<div>
			<table id="contentTable">
			<tr>
				<th>名称</th>
				<th>授权</th>
				<th>操作</th>
			</tr>

			<s:iterator value="allGroupList">
				<tr>
					<td>${name}</td>
					<td>${permissionNames}</td>
					<td>&nbsp;
						<shiro:hasPermission name="group:view">
							<shiro:lacksPermission name="group:edit">
								<a href="group!input.action?id=${id}">查看</a>&nbsp;
							</shiro:lacksPermission>
        				</shiro:hasPermission>
        					
        				<shiro:hasPermission name="group:edit">
							<a href="group!input.action?id=${id}" id="editLink-${name}">修改</a>&nbsp;
							<a href="group!delete.action?id=${id}" id="deleteLink-${name}">删除</a>
						</shiro:hasPermission>
					</td>
				</tr>
			</s:iterator>
			</table>
		</div>

		<div>
			<shiro:hasPermission name="group:edit">
				<a href="group!input.action">增加新权限组</a>
			</shiro:hasPermission>
		</div>
	</div>
	<%@ include file="/common/footer.jsp" %>
</div>
</body>
</html>
