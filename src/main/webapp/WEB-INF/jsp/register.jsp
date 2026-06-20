<%--
  用户注册页面 (register.jsp)
  功能：提供新用户注册表单，收集用户基本信息并创建账户
  访问路径：/register
  表单提交：POST方法到 /register 进行注册处理
  支持角色：普通用户(user) 或 管理员(admin)
--%>
<!-- 引入公共头部模板，设置页面标题为"注册" -->
<jsp:include page="header.jsp">
    <jsp:param name="title" value="注册"/>
</jsp:include>

<!-- 注册表单区域：居中布局，包含多个用户注册信息字段 -->
<div class="auth-container">
    <h2>注册</h2>
    <!-- 错误信息提示：当后端验证失败时，显示错误消息（如用户名重复） -->
    <c:if test="${error != null}">
        <p class="error">${error}</p>
    </c:if>
    <!-- 注册表单：提交到 /register 进行用户注册，使用POST方法 -->
    <form action="${pageContext.request.contextPath}/register" method="post">
        <!-- 用户名输入组：必填，注册后的登录标识 -->
        <div class="form-group">
            <label>用户名</label>
            <input type="text" name="username" required placeholder="请输入用户名">
        </div>
        <!-- 密码输入组：必填，用于登录验证 -->
        <div class="form-group">
            <label>密码</label>
            <input type="password" name="password" required placeholder="请输入密码">
        </div>
        <!-- 邮箱输入组：必填，用于找回密码和系统通知 -->
        <div class="form-group">
            <label>邮箱</label>
            <input type="email" name="email" required placeholder="请输入邮箱">
        </div>
        <!-- 手机号输入组：可选，用于紧急联系 -->
        <div class="form-group">
            <label>手机号</label>
            <input type="text" name="phone" placeholder="请输入手机号">
        </div>
        <!-- 角色选择组：决定用户权限级别（普通用户或管理员） -->
        <div class="form-group">
            <label>角色</label>
            <select name="role">
                <option value="user">普通用户</option>
                <option value="admin">管理员</option>
            </select>
        </div>
        <!-- 提交按钮：全宽样式，触发注册处理 -->
        <button type="submit" class="btn-primary" style="width:100%">注册</button>
    </form>
    <!-- 登录引导：为已有账户用户跳转到登录页面 -->
    <p style="text-align:center;margin-top:20px;color:var(--text-muted);">已有账号？<a href="${pageContext.request.contextPath}/login" style="color:var(--primary)">立即登录</a></p>
</div>

<!-- 引入公共底部模板，关闭页面结构 -->
<jsp:include page="footer.jsp" />
