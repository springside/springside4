<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Showcase示例:<sitemesh:write property='title' /></title>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<meta http-equiv="Cache-Control" content="no-store" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />

<link href="${ctx}/static/blueprint/1.0.1/screen.css" type="text/css" rel="stylesheet" media="screen, projection" />
<link href="${ctx}/static/blueprint/1.0.1/print.css" type="text/css" rel="stylesheet" media="print" />
<!--[if lt IE 8]><link href="${ctx}/static/blueprint/1.0.1/ie.css" type="text/css" rel="stylesheet" media="screen, projection"><![endif]-->
<link href="${ctx}/static/showcase.min.css" type="text/css" rel="stylesheet" />

<script src="${ctx}/static/jquery/1.7.1/jquery.min.js" type="text/javascript"></script>

<sitemesh:write property='head' />
</head>

<body>
	<div class="container">
		<%@ include file="/WEB-INF/layouts/header.jsp"%>
		<div id="content">
			<%@ include file="/WEB-INF/layouts/left.jsp"%>
			<div class="span-18 last prepend-top">
				<sitemesh:write property='body' />
			</div>
		</div>
		<%@ include file="/WEB-INF/layouts/footer.jsp"%>
	</div>
</body>
</html>