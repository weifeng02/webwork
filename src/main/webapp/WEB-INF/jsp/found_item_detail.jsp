<%--
  招领详情页面 (found_item_detail.jsp)
  功能：展示单个招领的详细信息，包含物品图片、描述、拾取地点、联系方式等
  访问路径：/found/detail?id={招领ID}
  数据：item - 招领对象
  操作：编辑、删除、返回列表
  注意：招领详情包含联系方式，便于失主联系
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
    <!-- 物品描述：详细说明物品特征和拾取情况 -->
    <div class="detail-meta-item">
        <label>描述</label>
        <span>${item.description}</span>
    </div>
    <!-- 物品类别：用于分类识别 -->
    <div class="detail-meta-item">
        <label>类别</label>
        <span>${item.category}</span>
    </div>
    <!-- 拾取地点：标记物品发现位置 -->
    <div class="detail-meta-item">
        <label>拾取地点</label>
        <span>${item.location}</span>
    </div>
    <!-- 拾取时间：记录物品拾取的具体时间 -->
    <div class="detail-meta-item">
        <label>拾取时间</label>
        <span>${item.foundTime}</span>
    </div>
    <!-- 联系方式：失主可通过此联系方式联系招领人，重要信息 -->
    <div class="detail-meta-item">
        <label>联系方式</label>
        <span>${item.contact}</span>
    </div>
    <!-- 发布者信息：显示发布者的用户名，匿名则显示"匿名" -->
    <div class="detail-meta-item">
        <label>发布者</label>
        <span>${item.username != null ? item.username : '匿名'}</span>
    </div>
    <!-- 发布时间：记录该招领信息的创建时间 -->
    <div class="detail-meta-item">
        <label>发布时间</label>
        <span>${item.createdAt}</span>
    </div>
</div>

<!-- 操作按钮区域：编辑、删除、返回列表 -->
<div style="display:flex;gap:12px;margin-top:24px;">
    <a href="${pageContext.request.contextPath}/found/edit?id=${item.id}" class="btn-secondary">编辑</a>
    <a href="${pageContext.request.contextPath}/found/delete?id=${item.id}" class="btn-danger" onclick="return confirm('确认删除？')">删除</a>
    <a href="${pageContext.request.contextPath}/found/list" class="btn-primary">返回列表</a>
</div>

<!-- 引入公共底部模板，关闭页面结构 -->
<jsp:include page="footer.jsp" />