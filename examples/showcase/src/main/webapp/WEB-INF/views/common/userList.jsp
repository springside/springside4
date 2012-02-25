<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.SecurityUtils" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
	<title>综合演示用例</title>
</head>

<body>
		<h2>综合演示用例</h2>
		<%if(SecurityUtils.getSubject().getPrincipal()!=null){ %>
		<div>你好, 用户<%=SecurityUtils.getSubject().getPrincipal()%>登录.&nbsp;&nbsp;</div>
		<%} %>
		<div>
		
				<table>
					<tr>
						<th>登录名</th>
						<th>姓名</th>
						<th>电邮</th>
						<th>角色</th>
						<th>状态</th>
						<th>操作</th>
					</tr>
					<c:forEach items="${users}" var="user">
						<tr>
							<td>${user.loginName}&nbsp;</td>
							<td>${user.name}&nbsp;</td>
							<td>${user.email}&nbsp;</td>
							<td>${user.roleNames}&nbsp;</td>
							<td>${user.status}&nbsp;</td>
							<td><a href="${ctx}/common/user/update/${user.id}" id="editLink-${user.id}">修改</a></td>
						</tr>
					</c:forEach>
					
				</table>
		</div>
</body>
</html>
