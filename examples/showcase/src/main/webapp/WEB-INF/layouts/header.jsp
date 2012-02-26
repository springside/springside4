<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<div id="header" class="span-24 last">
	<div class="title">Showcase示例</div>
	<span class="subtitle">--开源项目大派对</span>
	<shiro:user>
		<span class="right">你好, <shiro:principal property="name"/> </span>
	</shiro:user>
		
</div>