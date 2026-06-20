<%--
  失物详情页面 (lost_item_detail.jsp)
  功能：展示单个失物的详细信息，包含物品图片、描述、状态等
  访问路径：/lost/detail?id={失物ID}
  数据：item - 失物对象
  操作：编辑、删除、查找匹配
--%>
<!-- 引入公共头部模板，设置页面标题为物品名称 -->
<jsp:include page="header.jsp">
    <jsp:param name="title" value="${item.itemName}"/>
</jsp:include>

<!-- 详情头部区域：显示物品名称和当前状态标签 -->
<div class="detail-header">
    <h1>${item.itemName}</h1>
    <span class="card-status status-${item.status}">${item.status}</span>
</div>

<!-- 物品图片展示区：有图片则显示大图，无图片则显示占位符 -->
<c:choose>
    <c:when test="${item.imageUrl != null}">
        <img src="${pageContext.request.contextPath}/${item.imageUrl}" class="detail-img" alt="${item.itemName}">
    </c:when>
    <c:otherwise>
        <div class="card-img-placeholder" style="max-width:480px;height:180px;">无图片</div>
    </c:otherwise>
</c:choose>

<!-- 物品详细元数据区域：以键值对形式展示所有物品信息 -->
<div class="detail-meta">
    <!-- 物品描述：详细说明物品特征和丢失情况 -->
    <div class="detail-meta-item">
        <label>描述</label>
        <span>${item.description}</span>
    </div>
    <!-- 物品类别：用于分类识别 -->
    <div class="detail-meta-item">
        <label>类别</label>
        <span>${item.category}</span>
    </div>
    <!-- 丢失地点：标记物品丢失位置 -->
    <div class="detail-meta-item">
        <label>丢失地点</label>
        <span>${item.location}</span>
    </div>
    <!-- 丢失时间：记录物品丢失的具体时间 -->
    <div class="detail-meta-item">
        <label>丢失时间</label>
        <span>${item.lostTime}</span>
    </div>
    <!-- 发布者信息：显示发布者的用户名，匿名则显示"匿名" -->
    <div class="detail-meta-item">
        <label>发布者</label>
        <span>${item.username != null ? item.username : '匿名'}</span>
    </div>
    <!-- 发布时间：记录该失物信息的创建时间 -->
    <div class="detail-meta-item">
        <label>发布时间</label>
        <span>${item.createdAt}</span>
    </div>
</div>

<!-- 操作按钮区域：编辑、删除、查找匹配、返回列表 -->
<div style="display:flex;gap:12px;margin-top:24px;">
    <a href="${pageContext.request.contextPath}/lost/edit?id=${item.id}" class="btn-secondary">编辑</a>
    <a href="${pageContext.request.contextPath}/lost/delete?id=${item.id}" class="btn-danger" onclick="return confirm('确认删除？')">删除</a>
    <a href="${pageContext.request.contextPath}/match?lostItemId=${item.id}" class="btn-success">查找匹配</a>
    <a href="${pageContext.request.contextPath}/lost/list" class="btn-primary">返回列表</a>
</div>

<!-- 引入公共底部模板，关闭页面结构 -->
<jsp:include page="footer.jsp" />