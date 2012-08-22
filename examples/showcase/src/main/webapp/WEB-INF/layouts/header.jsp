<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<div id="header" class="span12 page-header">
	<h1>Showcase示例<small>--开源项目大派对</small></h1>
	<shiro:guest>
			<span class="pull-right"><a href="${ctx}/login">登录</a></span>
	</shiro:guest>
	<shiro:user>
		<span class="pull-right">你好, <shiro:principal property="name"/> <a href="${ctx}/logout">退出登录</a></span>
	</shiro:user>
		
</div>