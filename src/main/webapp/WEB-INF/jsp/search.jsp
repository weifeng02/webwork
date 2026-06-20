<%--
  搜索页面 (search.jsp)
  功能：提供全局搜索功能，按关键词搜索失物和招领信息
  访问路径：/search
  参数：q - 搜索关键词，type - 搜索类型（all/lost/found）
  数据：lostResults - 失物搜索结果，foundResults - 招领搜索结果
--%>
<!-- 引入公共头部模板，设置页面标题为"搜索" -->
<jsp:include page="header.jsp">
    <jsp:param name="title" value="搜索"/>
</jsp:include>

<!-- 页面标题区域：显示搜索功能标题 -->
<div class="section-header">
    <h1>搜索失物与招领</h1>
</div>

<!-- 搜索栏区域：包含搜索输入框和类型选择 -->
<div class="search-bar">
    <!-- 搜索表单：GET方法提交到 /search，包含关键词和类型筛选 -->
    <form action="${pageContext.request.contextPath}/search" method="get" style="display:flex;gap:12px;width:100%;">
        <!-- 搜索关键词输入框：必填，支持按物品名称、描述等搜索 -->
        <input type="text" name="q" placeholder="输入关键词搜索..." value="${keyword}" required>
        <!-- 搜索类型下拉框：选择搜索范围（全部、仅失物、仅招领） -->
        <select name="type">
            <option value="all" ${type == 'all' ? 'selected' : ''}>全部</option>
            <option value="lost" ${type == 'lost' ? 'selected' : ''}>失物</option>
            <option value="found" ${type == 'found' ? 'selected' : ''}>招领</option>
        </select>
        <!-- 搜索提交按钮：触发搜索查询 -->
        <button type="submit" class="btn-primary">搜索</button>
    </form>
</div>

<!-- 搜索结果区域：仅当有关键词时才显示搜索结果 -->
<c:if test="${keyword != null && !empty keyword}">
    <!-- 失物搜索结果区域：展示匹配的失物列表 -->
    <div class="section">
        <h2>失物结果</h2>
        <div class="card-grid">
            <!-- 循环渲染失物搜索结果 -->
            <c:forEach var="item" items="${lostResults}">
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
                    <!-- 卡片内容区：物品名称、地点、类别、状态和详情链接 -->
                    <div class="card-body">
                        <div class="card-title">${item.itemName}</div>
                        <div class="card-meta">地点: ${item.location}</div>
                        <div class="card-meta">类别: ${item.category}</div>
                        <span class="card-status status-${item.status}">${item.status}</span>
                        <div class="card-actions">
                            <a href="${pageContext.request.contextPath}/lost/detail?id=${item.id}" class="btn-primary">详情</a>
                        </div>
                    </div>
                </div>
            </c:forEach>
            <!-- 无失物结果提示：当失物搜索结果为空时显示 -->
            <c:if test="${empty lostResults}">
                <p style="color:var(--text-muted)">无失物匹配结果</p>
            </c:if>
        </div>
    </div>

    <!-- 招领搜索结果区域：展示匹配的招领列表 -->
    <div class="section">
        <h2>招领结果</h2>
        <div class="card-grid">
            <!-- 循环渲染招领搜索结果 -->
            <c:forEach var="item" items="${foundResults}">
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
                    <!-- 卡片内容区：物品名称、地点、类别、状态和详情链接 -->
                    <div class="card-body">
                        <div class="card-title">${item.itemName}</div>
                        <div class="card-meta">地点: ${item.location}</div>
                        <div class="card-meta">类别: ${item.category}</div>
                        <span class="card-status status-${item.status}">${item.status}</span>
                        <div class="card-actions">
                            <a href="${pageContext.request.contextPath}/found/detail?id=${item.id}" class="btn-primary">详情</a>
                        </div>
                    </div>
                </div>
            </c:forEach>
            <!-- 无招领结果提示：当招领搜索结果为空时显示 -->
            <c:if test="${empty foundResults}">
                <p style="color:var(--text-muted)">无招领匹配结果</p>
            </c:if>
        </div>
    </div>
</c:if>

<!-- 引入公共底部模板，关闭页面结构 -->
<jsp:include page="footer.jsp" />