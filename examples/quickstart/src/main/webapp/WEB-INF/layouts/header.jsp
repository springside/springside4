<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<div class="navbar navbar-inverse navbar-fixed-top">
	<div class="navbar-inner">
		<div class="container">
			<shiro:user>
				<button class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse"> 
					<span class="icon-bar"></span> 
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
				</button>
			</shiro:user>
			<a class="brand tip-bottom" title="QuickStart示例" href="${ctx}">QuickStart示例<small>--TodoList应用演示</small></a>
			<div class="nav-collapse collapse">
				<shiro:user>
					<ul class="nav pull-right" >
						<li class="divider-vertical"></li>
						<li class="dropdown">
							<a data-toggle="dropdown" class="dropdown-toggle" href="#"><shiro:principal property="name"/><b class="caret"></b></a>
							<ul class="dropdown-menu">
								<shiro:hasRole name="admin">
									<li><a href="${ctx}/admin/user">Admin Users</a></li>
									<li class="divider"></li>
								</shiro:hasRole>
								<li><a href="${ctx}/profile">Edit Profile</a></li>
								<li class="divider"></li>
								<li><a href="${ctx}/logout">Logout</a></li>
							</ul>
						</li>
					</ul>
				</shiro:user>
			</div><!--/.nav-collapse -->
		</div>
	</div>
</div>