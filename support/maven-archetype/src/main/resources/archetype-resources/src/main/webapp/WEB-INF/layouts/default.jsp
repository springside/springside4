#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="sitemesh" uri="http://www.opensymphony.com/sitemesh/decorator" %>  
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="ctx" value="${symbol_dollar}{pageContext.request.contextPath}" />

<!DOCTYPE html>
<html>
<head>
<title>${projectName}示例:<sitemesh:title/></title>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<meta http-equiv="Cache-Control" content="no-store" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />

<link type="image/x-icon" href="${symbol_dollar}{ctx}/static/images/favicon.ico" rel="shortcut icon">
<link href="${symbol_dollar}{ctx}/static/bootstrap/2.1.1/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="${symbol_dollar}{ctx}/static/jquery-validation/1.10.0/validate.css" type="text/css" rel="stylesheet" />
<link href="${symbol_dollar}{ctx}/static/styles/main.css" type="text/css" rel="stylesheet" />
<script src="${symbol_dollar}{ctx}/static/jquery/1.7.2/jquery.min.js" type="text/javascript"></script>
<script src="${symbol_dollar}{ctx}/static/jquery-validation/1.10.0/jquery.validate.min.js" type="text/javascript"></script>
<script src="${symbol_dollar}{ctx}/static/jquery-validation/1.10.0/messages_bs_zh.js" type="text/javascript"></script>


<sitemesh:head/>
</head>

<body>
	<div class="container">
		<%@ include file="/WEB-INF/layouts/header.jsp"%>
		<div id="content">
			<sitemesh:body/>
		</div>
		<%@ include file="/WEB-INF/layouts/footer.jsp"%>
	</div>
	<script src="${symbol_dollar}{ctx}/static/bootstrap/2.1.1/js/bootstrap.min.js" type="text/javascript"></script>
</body>
</html>