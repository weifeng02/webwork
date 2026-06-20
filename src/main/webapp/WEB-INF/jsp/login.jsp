<%--
  用户登录页面 (login.jsp)
  功能：提供用户登录表单，验证用户身份并创建会话
  访问路径：/login
  表单提交：POST方法到 /login 进行身份验证
  登录成功后：跳转至首页，用户信息存入 sessionScope.user
--%>
<!-- 引入公共头部模板，设置页面标题为"登录" -->
<jsp:include page="header.jsp">
    <jsp:param name="title" value="登录"/>
</jsp:include>

<!-- 登录表单区域：居中布局，包含用户名和密码输入 -->
<div class="auth-container">
    <h2>登录</h2>
    <!-- 错误信息提示：当后端验证失败时，显示错误消息 -->
    <c:if test="${error != null}">
        <p class="error">${error}</p>
    </c:if>
    <!-- 登录表单：提交到 /login 进行身份验证，使用POST方法确保安全性 -->
    <form action="${pageContext.request.contextPath}/login" method="post">
        <!-- 用户名输入组：必填字段，文本类型输入 -->
        <div class="form-group">
            <label>用户名</label>
            <input type="text" name="username" required placeholder="请输入用户名">
        </div>
        <!-- 密码输入组：必填字段，密码类型输入，隐藏明文 -->
        <div class="form-group">
            <label>密码</label>
            <input type="password" name="password" required placeholder="请输入密码">
        </div>
        <!-- 提交按钮：全宽样式，触发登录验证 -->
        <button type="submit" class="btn-primary" style="width:100%">登录</button>
    </form>
    <!-- 注册引导：为未注册用户跳转到注册页面 -->
    <p style="text-align:center;margin-top:20px;color:var(--text-muted);">还没有账号？<a href="${pageContext.request.contextPath}/register" style="color:var(--primary)">立即注册</a></p>
</div>

<!-- 引入公共底部模板，关闭页面结构 -->
<jsp:include page="footer.jsp" />
