<%--
  404错误页面 (404.jsp)
  功能：当用户访问不存在的页面时显示友好错误提示
  访问路径：自动触发，当请求的资源不存在时
  特点：保留页面风格一致性，提供返回首页的导航入口
  位置：位于 error/ 目录下，由全局错误处理配置指向
--%>
<!-- 引入公共头部模板（上级目录），设置页面标题为"404" -->
<jsp:include page="../header.jsp">
    <jsp:param name="title" value="404"/>
</jsp:include>

<!-- 错误提示区域：居中布局，显示404错误代码和友好提示 -->
<div style="text-align:center;margin:80px 0;">
    <!-- 错误代码：大号字体显示404，使用主题色 -->
    <h1 style="font-size:4rem;color:var(--primary);">404</h1>
    <!-- 错误描述：说明页面未找到的原因 -->
    <p style="font-size:1.2rem;color:var(--text-muted);">页面未找到</p>
    <!-- 返回首页按钮：提供导航到系统首页的入口，方便用户继续操作 -->
    <a href="${pageContext.request.contextPath}/dashboard" class="btn-primary" style="margin-top:24px;">返回首页</a>
</div>

<!-- 引入公共底部模板（上级目录），关闭页面结构 -->
<jsp:include page="../footer.jsp" />