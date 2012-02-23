<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.SecurityUtils" %>
<%@ include file="/common/taglibs.jsp" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Mini-Web 帐号管理</title>
	<%@ include file="/common/meta.jsp" %>
	
	<link href="${ctx}/css/style.css" type="text/css" rel="stylesheet"/>	
	
	<link href="${ctx}/css/blueprint/screen.css" type="text/css" rel="stylesheet" media="screen, projection"/>
	<link href="${ctx}/css/blueprint/print.css" type="text/css" rel="stylesheet" media="print"/>
	<!--[if lt IE 8]><link href="${ctx}/css/blueprint/blueprint/ie.css" type="text/css" rel="stylesheet" media="screen, projection"><![endif]-->

	<script src="${ctx}/js/jquery-min.js" type="text/javascript"></script>
	<script src="${ctx}/js/table.js" type="text/javascript"></script>
</head>

<body>
<div class="container">
	<%@ include file="/common/header.jsp" %>
	<div id="content" class="span-24 last prepend-top">
		<div id="message"><s:actionmessage theme="custom" cssClass="success"/></div>
		<div id="filter">
			<form id="mainForm" action="user.action" method="get">
				<input type="hidden" name="page.pageNo" id="pageNo" value="${page.pageNo}"/>
				<input type="hidden" name="page.orderBy" id="orderBy" value="${page.orderBy}"/>
				<input type="hidden" name="page.orderDir" id="orderDir" value="${page.orderDir}"/>	
				你好, <shiro:principal/>.&nbsp;&nbsp;
				登录名: <input type="text" name="filter_EQS_loginName" value="${param['filter_EQS_loginName']}" size="9"/>
				姓名或Email: <input type="text" name="filter_LIKES_name_OR_email" value="${param['filter_LIKES_name_OR_email']}" size="9"/>
				<input type="button" value="搜索" onclick="search();"/>
			</form>
		</div>
		<div>
			<table id="contentTable">
				<tr>
					<th><a href="javascript:sort('loginName','asc')">登录名</a></th>
					<th><a href="javascript:sort('name','asc')">姓名</a></th>
					<th><a href="javascript:sort('email','asc')">电邮</a></th>
					<th>权限组</th>
					<th>操作</th>
				</tr>

				<s:iterator value="page.result">
					<tr>
						<td>${loginName}&nbsp;</td>
						<td>${name}&nbsp;</td>
						<td>${email}&nbsp;</td>
						<td>${groupNames}&nbsp;</td>
						<td>&nbsp;
							<shiro:hasPermission name="user:view">
								<shiro:lacksPermission name="user:edit">
									<a href="user!input.action?id=${id}">查看</a>&nbsp;
								</shiro:lacksPermission>
        					</shiro:hasPermission>
        					
        					<shiro:hasPermission name="user:edit">
								<a href="user!input.action?id=${id}">修改</a>&nbsp;
								<a href="user!delete.action?id=${id}">删除</a>
							</shiro:hasPermission>
						</td>
					</tr>
				</s:iterator>
			</table>
		</div>
		<div>
			第${page.pageNo}页, 共${page.totalPages}页
			<a href="javascript:jumpPage(1)">首页</a>
			<s:if test="page.paginator.hasPrePage"><a href="javascript:jumpPage(${page.prePage})">上一页</a></s:if>
			<s:if test="page.paginator.hasNextPage"><a href="javascript:jumpPage(${page.nextPage})">下一页</a></s:if>
			<a href="javascript:jumpPage(${page.totalPages})">末页</a>

			<shiro:hasPermission name="user:edit">
				<a href="user!input.action">增加新用户</a>
			</shiro:hasPermission>
		</div>
	</div>
	<%@ include file="/common/footer.jsp" %>
</div>
</body>
</html>
