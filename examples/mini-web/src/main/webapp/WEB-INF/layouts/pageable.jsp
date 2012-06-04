<%@ page language="java" pageEncoding="UTF-8" %>
<p>${page.number + 1}/${page.totalPages}页 &nbsp; 共${page.totalElements }条</p>
<div class="pagination pagination-left">
	<ul>
		<c:if test="${!page.firstPage}">
			<li><a href='<c:url value="?page=0" />'>&laquo;</a></li>
		</c:if>
		<c:if test="${page.firstPage}">
        	<li class="disabled"><a href='#'>&laquo;</a></li>
		</c:if>
               
        <c:if test="${!page.firstPage}">
        	<li><a href='<c:url value="?page=${page.number - 1}" />'> 上一页</a></li>
		</c:if>
        <c:if test="${page.firstPage}">
        	<li class="disabled"><a href='#'> 上一页</a></li>
        </c:if>
               
		<c:if test="${!page.lastPage}">
        	<li><a href='<c:url value="?page=${page.number + 1}" />'> 下一页</a></li>
		</c:if>
        <c:if test="${page.lastPage}">
        	<li class="disabled"><a href='#'> 下一页</a></li>
		</c:if>
                   
		<c:if test="${!page.lastPage}">
        	<li><a href='<c:url value="?page=${page.totalPages - 1}" />'>&raquo;</a></li>
		</c:if>
        <c:if test="${page.lastPage}">
        	<li class="disabled"><a href='#'>&raquo;</a></li>
		</c:if>
	</ul>
</div>

