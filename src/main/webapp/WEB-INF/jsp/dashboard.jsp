<%--
  系统首页/仪表盘 (dashboard.jsp)
  功能：展示系统概览，包括最近发布的失物和招领信息
  访问路径：/dashboard
  数据：recentLost - 最近失物列表，recentFound - 最近招领列表
  角色：所有用户均可访问
--%>
<!-- 引入公共头部模板，设置页面标题为"首页" -->
<jsp:include page="header.jsp">
    <jsp:param name="title" value="首页"/>
</jsp:include>

<!-- 首页头部区域：展示系统标题和简介描述 -->
<div class="dashboard-header">
    <h1>校园失物招领系统</h1>
    <p>快速发布失物或招领信息，系统自动匹配，找回遗失物品</p>
</div>

<!-- 最近失物展示区：以卡片网格形式展示最近发布的失物信息 -->
<div class="section">
    <!-- 区域标题和发布按钮：左侧标题，右侧快速发布入口 -->
    <div class="section-header">
        <h2>最近失物</h2>
        <a href="${pageContext.request.contextPath}/lost/new" class="btn-primary">发布失物</a>
    </div>
    <!-- 失物卡片网格：循环渲染最近失物列表 -->
    <div class="card-grid">
        <c:forEach var="item" items="${recentLost}">
            <div class="card">
                <!-- 物品图片展示：有图片则显示，无图片则显示占位符 -->
                <c:choose>
                    <c:when test="${item.imageUrl != null}">
                        <img src="${pageContext.request.contextPath}/${item.imageUrl}" class="card-img" alt="${item.itemName}">
                    </c:when>
                    <c:otherwise>
                        <div class="card-img-placeholder">无图片</div>
                    </c:otherwise>
                </c:choose>
                <!-- 卡片内容区：物品名称、地点、类别、状态和操作按钮 -->
                <div class="card-body">
                    <div class="card-title">${item.itemName}</div>
                    <div class="card-meta">地点: ${item.location}</div>
                    <div class="card-meta">类别: ${item.category}</div>
                    <span class="card-status status-${item.status}">${item.status}</span>
                    <div class="card-actions">
                        <a href="${pageContext.request.contextPath}/lost/detail?id=${item.id}" class="btn-primary">查看详情</a>
                    </div>
                </div>
            </div>
        </c:forEach>
        <!-- 无数据提示：当最近失物列表为空时显示 -->
        <c:if test="${empty recentLost}">
            <p style="color:var(--text-muted)">暂无失物信息</p>
        </c:if>
    </div>
</div>

<!-- 最近招领展示区：以卡片网格形式展示最近发布的招领信息 -->
<div class="section">
    <!-- 区域标题和发布按钮：左侧标题，右侧快速发布入口 -->
    <div class="section-header">
        <h2>最近招领</h2>
        <a href="${pageContext.request.contextPath}/found/new" class="btn-primary">发布招领</a>
    </div>
    <!-- 招领卡片网格：循环渲染最近招领列表 -->
    <div class="card-grid">
        <c:forEach var="item" items="${recentFound}">
            <div class="card">
                <!-- 物品图片展示：有图片则显示，无图片则显示占位符 -->
                <c:choose>
                    <c:when test="${item.imageUrl != null}">
                        <img src="${pageContext.request.contextPath}/${item.imageUrl}" class="card-img" alt="${item.itemName}">
                    </c:when>
                    <c:otherwise>
                        <div class="card-img-placeholder">无图片</div>
                    </c:otherwise>
                </c:choose>
                <!-- 卡片内容区：物品名称、地点、类别、状态和操作按钮 -->
                <div class="card-body">
                    <div class="card-title">${item.itemName}</div>
                    <div class="card-meta">地点: ${item.location}</div>
                    <div class="card-meta">类别: ${item.category}</div>
                    <span class="card-status status-${item.status}">${item.status}</span>
                    <div class="card-actions">
                        <a href="${pageContext.request.contextPath}/found/detail?id=${item.id}" class="btn-primary">查看详情</a>
                    </div>
                </div>
            </div>
        </c:forEach>
        <!-- 无数据提示：当最近招领列表为空时显示 -->
        <c:if test="${empty recentFound}">
            <p style="color:var(--text-muted)">暂无招领信息</p>
        </c:if>
    </div>
</div>

<!-- 引入公共底部模板，关闭页面结构 -->
<jsp:include page="footer.jsp" />
