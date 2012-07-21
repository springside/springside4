<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%response.setStatus(200);%>

<!DOCTYPE html>
<html>
<head>
	<title>404 - 页面不存在</title>
</head>

<body>
<div>
	<div><h1>页面不存在.</h1></div>
	<div><a href="<c:url value="/"/>">返回首页</a></div>
</div>
</body>
</html>