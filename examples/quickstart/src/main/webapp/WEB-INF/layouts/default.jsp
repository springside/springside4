<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="sitemesh" uri="http://www.opensymphony.com/sitemesh/decorator" %>  
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html>
<head>
<title>QuickStart示例:<sitemesh:title/></title>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="description" content="">
<meta name="viewport" content="width=device-width">

<link rel="shortcut icon" type="image/x-icon" href="${ctx}/static/images/favicon.ico">
<link rel="stylesheet"  href="${ctx}/static/bootstrap/2.3.1/css/bootstrap.min.css">
<link rel="stylesheet"  href="${ctx}/static/styles/default.css"> 

<!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
<!--[if lt IE 9]>
	<script src="${ctx}/static/bootstrap/2.3.1/html5/html5shiv.js"></script>
<![endif]-->

<script src="//ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js"></script>
<script>window.jQuery || document.write('<script src="${ctx}/static/jquery/jquery-1.8.3.min.js"><\/script>')</script>
<sitemesh:head/>
</head>

<body>
	 <!--[if lt IE 7]>
          <p class="chromeframe">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> or <a href="http://www.google.com/chromeframe/?redirect=true">activate Google Chrome Frame</a> to improve your experience.</p>
      <![endif]-->
        
	<div id="wrap">
		<%@ include file="/WEB-INF/layouts/header.jsp"%>
		<div class="container">
			<sitemesh:body/>
		</div>
		<div id="push"></div>
	</div>
	<%@ include file="/WEB-INF/layouts/footer.jsp"%>
	
	<script src="${ctx}/static/jquery-validation/1.12.0/jquery.validate.js"></script>
	<script src="${ctx}/static/jquery-validation/1.12.0/messages_zh.js"></script>
	<script src="${ctx}/static/bootstrap/2.3.1/js/bootstrap.min.js"></script>
    
</body>
</html>