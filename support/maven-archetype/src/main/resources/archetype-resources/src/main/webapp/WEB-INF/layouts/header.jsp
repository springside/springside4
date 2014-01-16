#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${symbol_dollar}{pageContext.request.contextPath}"/>
<div id="header">
	<div id="title">
	    <h1><a href="${symbol_dollar}{ctx}">QuickStart示例</a><small>--TodoList应用演示</small>
	    <shiro:user>
			<div class="btn-group pull-right">
				<a class="btn dropdown-toggle" data-toggle="dropdown" href="${symbol_pound}">
					<i class="icon-user"></i> <shiro:principal property="name"/>
					<span class="caret"></span>
				</a>
			
				<ul class="dropdown-menu">
					<shiro:hasRole name="admin">
						<li><a href="${symbol_dollar}{ctx}/admin/user">Admin Users</a></li>
						<li class="divider"></li>
					</shiro:hasRole>
					<li><a href="${symbol_dollar}{ctx}/api">APIs</a></li>
					<li><a href="${symbol_dollar}{ctx}/profile">Edit Profile</a></li>
					<li><a href="${symbol_dollar}{ctx}/logout">Logout</a></li>
				</ul>
			</div>
		</shiro:user>
		</h1>
	</div>
</div>