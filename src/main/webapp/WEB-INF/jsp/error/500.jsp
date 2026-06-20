<%--
  500错误页面 (500.jsp)
  功能：当服务器发生内部错误时显示友好错误提示
  访问路径：自动触发，当服务器处理请求时发生异常
  特点：保留页面风格一致性，使用红色主题色表示严重错误，提供返回首页的导航入口
  位置：位于 error/ 目录下，由全局错误处理配置指向
  注意：500错误通常需要管理员检查服务器日志
--%>
<!-- 引入公共头部模板（上级目录），设置页面标题为"500" -->
<jsp:include page="../header.jsp">
    <jsp:param name="title" value="500"/>
</jsp:include>

<!-- 错误提示区域：居中布局，显示500错误代码和友好提示 -->
<div style="text-align:center;margin:80px 0;">
    <!-- 错误代码：大号字体显示500，使用红色（danger色）表示服务器错误 -->
    <h1 style="font-size:4rem;color:var(--danger);">500</h1>
    <!-- 错误描述：说明服务器发生内部错误 -->
    <p style="font-size:1.2rem;color:var(--text-muted);">服务器内部错误</p>
    <!-- 返回首页按钮：提供导航到系统首页的入口，方便用户继续操作 -->
    <a href="${pageContext.request.contextPath}/dashboard" class="btn-primary" style="margin-top:24px;">返回首页</a>
</div>

<!-- 引入公共头部模板（上级目录），关闭页面结构 -->
<jsp:include page="../footer.jsp" />