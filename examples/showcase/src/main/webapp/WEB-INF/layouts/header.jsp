<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<div id="header" class="row">
	<div><h1>Showcase示例<small>--开源项目大派对</small></h1></div>
	<div class="pull-right">
		<shiro:guest><a href="${ctx}/login">登录</a></shiro:guest>
		<shiro:user>你好, <shiro:principal property="name"/> <a href="${ctx}/logout">退出登录</a></shiro:user>
	</div>
</div>