<%--
  认领申请列表页面 (claim_requests.jsp)
  功能：展示所有认领申请记录，支持管理员审核操作
  访问路径：/claim/list
  数据：claims - 认领申请列表
  权限：管理员可审核通过/拒绝待处理的申请；所有用户可删除
  状态：pending（待审核）/ approved（已通过）/ rejected（已拒绝）
--%>
<!-- 引入公共头部模板，设置页面标题为"认领申请" -->
<jsp:include page="header.jsp">
    <jsp:param name="title" value="认领申请"/>
</jsp:include>

<!-- 页面标题区域：显示认领申请列表标题 -->
<div class="section-header">
    <h1>认领申请</h1>
</div>

<!-- 认领申请数据表格：展示所有认领申请的详细信息 -->
<table class="data-table">
    <!-- 表格头部：定义各列标题 -->
    <thead>
        <tr>
            <th>申请ID</th>
            <th>失物</th>
            <th>招领</th>
            <th>申请人</th>
            <th>说明</th>
            <th>状态</th>
            <th>时间</th>
            <th>操作</th>
        </tr>
    </thead>
    <!-- 表格主体：循环渲染每条认领申请记录 -->
    <tbody>
        <c:forEach var="c" items="${claims}">
            <tr>
                <!-- 申请ID：认领申请的唯一标识 -->
                <td>${c.id}</td>
                <!-- 关联失物：显示失物名称或ID（名称优先） -->
                <td>${c.lostItemName != null ? c.lostItemName : c.lostItemId}</td>
                <!-- 关联招领：显示招领名称或ID（名称优先） -->
                <td>${c.foundItemName != null ? c.foundItemName : c.foundItemId}</td>
                <!-- 申请人：显示用户名称或ID（名称优先） -->
                <td>${c.username != null ? c.username : c.userId}</td>
                <!-- 补充说明：申请人提交的认领理由和物品特征描述 -->
                <td>${c.message}</td>
                <!-- 状态标签：显示当前申请状态（待审核/已通过/已拒绝） -->
                <td><span class="card-status status-${c.status}">${c.status}</span></td>
                <!-- 申请时间：记录申请提交的时间 -->
                <td>${c.createdAt}</td>
                <!-- 操作按钮区：管理员审核操作 + 删除操作 -->
                <td>
                    <!-- 管理员审核按钮：仅管理员且状态为pending时显示通过/拒绝按钮 -->
                    <c:if test="${sessionScope.user != null && sessionScope.user.role == 'admin' && c.status == 'pending'}">
                        <!-- 通过表单：POST提交到 /claim/approve，传递申请ID -->
                        <form action="${pageContext.request.contextPath}/claim/approve" method="post" style="display:inline;">
                            <input type="hidden" name="id" value="${c.id}">
                            <button type="submit" class="btn-success" style="padding:4px 12px;font-size:0.8rem;">通过</button>
                        </form>
                        <!-- 拒绝表单：POST提交到 /claim/reject，传递申请ID -->
                        <form action="${pageContext.request.contextPath}/claim/reject" method="post" style="display:inline;">
                            <input type="hidden" name="id" value="${c.id}">
                            <button type="submit" class="btn-danger" style="padding:4px 12px;font-size:0.8rem;">拒绝</button>
                        </form>
                    </c:if>
                    <!-- 删除链接：所有用户均可删除，带确认对话框防止误操作 -->
                    <a href="${pageContext.request.contextPath}/claim/delete?id=${c.id}" class="btn-danger" style="padding:4px 12px;font-size:0.8rem;" onclick="return confirm('确认删除？')">删除</a>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>
<!-- 无数据提示：当认领申请列表为空时显示 -->
<c:if test="${empty claims}">
    <p style="color:var(--text-muted);margin-top:20px;">暂无认领申请</p>
</c:if>

<!-- 引入公共底部模板，关闭页面结构 -->
<jsp:include page="footer.jsp" />