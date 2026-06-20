<%--
  招领列表页面 (found_items.jsp)
  功能：展示所有招领信息，支持按类别、地点、状态筛选
  访问路径：/found/list
  数据：foundItems - 招领列表
  参数：category, location, status - 筛选条件
--%>
<!-- 引入公共头部模板，设置页面标题为"招领列表" -->
<jsp:include page="header.jsp">
    <jsp:param name="title" value="招领列表"/>
</jsp:include>

<!-- 区域标题栏：左侧标题，右侧发布按钮 -->
<div class="section-header">
    <h1>招领列表</h1>
    <a href="${pageContext.request.contextPath}/found/new" class="btn-primary">发布招领</a>
</div>

<!-- 筛选栏区域：提供类别、地点、状态三种筛选条件 -->
<div class="filter-bar">
    <!-- 筛选表单：GET方法提交到 /found/list，保持筛选条件在URL中便于分享和分页 -->
    <form action="${pageContext.request.contextPath}/found/list" method="get" style="display:flex;gap:12px;flex-wrap:wrap;">
        <!-- 类别筛选下拉框：选择物品类别进行过滤，保留当前选中状态 -->
        <select name="category">
            <option value="">全部类别</option>
            <option value="电子产品" ${param.category == '电子产品' ? 'selected' : ''}>电子产品</option>
            <option value="证件" ${param.category == '证件' ? 'selected' : ''}>证件</option>
            <option value="书包" ${param.category == '书包' ? 'selected' : ''}>书包</option>
            <option value="钱包" ${param.category == '钱包' ? 'selected' : ''}>钱包</option>
            <option value="钥匙" ${param.category == '钥匙' ? 'selected' : ''}>钥匙</option>
            <option value="衣物" ${param.category == '衣物' ? 'selected' : ''}>衣物</option>
            <option value="其他" ${param.category == '其他' ? 'selected' : ''}>其他</option>
        </select>
        <!-- 地点筛选输入框：按地点关键词过滤，保留当前输入值 -->
        <input type="text" name="location" placeholder="地点" value="${param.location}">
        <!-- 状态筛选下拉框：选择处理状态进行过滤，保留当前选中状态 -->
        <select name="status">
            <option value="">全部状态</option>
            <option value="pending" ${param.status == 'pending' ? 'selected' : ''}>待认领</option>
            <option value="returned" ${param.status == 'returned' ? 'selected' : ''}>已归还</option>
        </select>
        <!-- 筛选提交按钮：触发筛选条件应用 -->
        <button type="submit" class="btn-secondary">筛选</button>
    </form>
</div>

<!-- 招领卡片网格区域：展示所有符合筛选条件的招领 -->
<div class="card-grid">
    <!-- 循环渲染招领列表：每个招领为一个卡片 -->
    <c:forEach var="item" items="${foundItems}">
        <div class="card">
            <!-- 物品图片展示区：有图片则显示，无图片则显示占位符 -->
            <c:choose>
                <c:when test="${item.imageUrl != null}">
                    <img src="${pageContext.request.contextPath}/${item.imageUrl}" class="card-img" alt="${item.itemName}">
                </c:when>
                <c:otherwise>
                    <div class="card-img-placeholder">无图片</div>
                </c:otherwise>
            </c:choose>
            <!-- 卡片内容区：物品名称、地点、类别、发布者、状态和操作按钮 -->
            <div class="card-body">
                <div class="card-title">${item.itemName}</div>
                <div class="card-meta">地点: ${item.location}</div>
                <div class="card-meta">类别: ${item.category}</div>
                <div class="card-meta">发布者: ${item.username != null ? item.username : '匿名'}</div>
                <!-- 状态标签：根据状态值显示不同样式（pending/returned） -->
                <span class="card-status status-${item.status}">${item.status}</span>
                <!-- 操作按钮区：详情、编辑、删除 -->
                <div class="card-actions">
                    <a href="${pageContext.request.contextPath}/found/detail?id=${item.id}" class="btn-primary">详情</a>
                    <a href="${pageContext.request.contextPath}/found/edit?id=${item.id}" class="btn-secondary">编辑</a>
                    <a href="${pageContext.request.contextPath}/found/delete?id=${item.id}" class="btn-danger" onclick="return confirm('确认删除？')">删除</a>
                </div>
            </div>
        </div>
    </c:forEach>
    <!-- 无数据提示：当招领列表为空时显示 -->
    <c:if test="${empty foundItems}">
        <p style="color:var(--text-muted)">暂无招领信息</p>
    </c:if>
</div>

<!-- 引入公共底部模板，关闭页面结构 -->
<jsp:include page="footer.jsp" />