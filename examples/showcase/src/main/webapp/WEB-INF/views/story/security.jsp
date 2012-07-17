<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
	<title>安全高级演示</title>
	<script>
		$(document).ready(function() {
			$("#security-tab").addClass("active");
		});
	</script>
</head>

<body>
	<h2>Shiro高级演示</h2>
	<ul>
		<li>常用密码存储方式演示:salt + 1024次hash的sha1</li>
		<li>Shiro与REST接口的HttpBasic认证(并缓存认证数据)与权限控制演示</li>
		<li>Shiro使用Ehcache RMI集群作为认证与权限数据的缓存</li>
		<li>根据用户状态抛出用户已被冻结的异常</li>
	</ul>
	<hr/>
	<h2>安全高级演示</h2>
	<ul>
		<li>SHA-1与MD5消息摘要演示，支持Salt与迭代Hash(见Digests及其测试用例)</li>
		<li>HMAC-SHA1共享密钥消息签名演示.(见Cryptos及其测试用例)</li>
		<li>AES的共享密钥消息加密演示，支持IV(初始向量).(见Cryptos及其测试用例)</li>
	</ul>
</body>
</html>