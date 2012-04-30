<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sitemesh" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Showcase示例:<sitemesh:title/></title>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<meta http-equiv="Cache-Control" content="no-store" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />

<link href="${ctx}/static/bootstrap/2.0.3/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="${ctx}/static/showcase.min.css" type="text/css" rel="stylesheet" />
<script src="${ctx}/static/jquery/1.7.1/jquery.min.js" type="text/javascript"></script>

<sitemesh:head />

</head>

<body>
	<div class="container">
		<%@ include file="/WEB-INF/layouts/header.jsp"%>
		<div class="row">
			<%@ include file="/WEB-INF/layouts/left.jsp"%>
			<div id="main" class="span10">
				<sitemesh:body />
			</div>
		</div>
		<%@ include file="/WEB-INF/layouts/footer.jsp"%>
	</div>
	<script src="${ctx}/static/bootstrap/2.0.3/js/bootstrap.min.js" type="text/javascript"></script>
	
</body>
</html>