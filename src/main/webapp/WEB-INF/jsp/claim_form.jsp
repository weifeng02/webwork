<%--
  认领申请表单页面 (claim_form.jsp)
  功能：失主向招领者提交认领申请，包含补充说明以证明所有权
  访问路径：/claim/new?lostItemId={失物ID}&foundItemId={招领ID}
  参数：lostItemId - 关联的失物ID，foundItemId - 关联的招领ID
  表单提交：POST方法到 /claim/new，创建新的认领申请
  注意：只有登录用户才能提交认领申请
--%>
<!-- 引入公共头部模板，设置页面标题为"申请认领" -->
<jsp:include page="header.jsp">
    <jsp:param name="title" value="申请认领"/>
</jsp:include>

<!-- 认领表单容器：限制最大宽度为600px，居中显示 -->
<div class="auth-container" style="max-width:600px;">
    <!-- 页面标题：显示申请认领标题 -->
    <h2>申请认领</h2>
    <!-- 认领申请表单：提交到 /claim/new 创建新认领申请 -->
    <form action="${pageContext.request.contextPath}/claim/new" method="post">
        <!-- 关联失物ID隐藏字段：用于后端关联对应的失物记录 -->
        <input type="hidden" name="lostItemId" value="${lostItemId}">
        <!-- 关联招领ID隐藏字段：用于后端关联对应的招领记录 -->
        <input type="hidden" name="foundItemId" value="${foundItemId}">
        <!-- 补充说明输入组：必填，多行文本，用于描述物品特征以证明所有权 -->
        <div class="form-group">
            <label>补充说明</label>
            <textarea name="message" required placeholder="请描述物品特征以证明所有权..."></textarea>
        </div>
        <!-- 提交按钮：全宽样式，提交认领申请 -->
        <button type="submit" class="btn-primary" style="width:100%">提交申请</button>
    </form>
</div>

<!-- 引入公共底部模板，关闭页面结构 -->
<jsp:include page="footer.jsp" />