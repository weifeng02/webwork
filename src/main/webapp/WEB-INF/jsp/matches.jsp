<%--
  智能匹配页面 (matches.jsp)
  功能：为失物寻找可能匹配的招领信息，或展示全部匹配结果
  访问路径：/match 或 /match?lostItemId={失物ID}
  数据：targetLost - 目标失物（单条匹配模式），matchedFound - 匹配到的招领列表
        lostItems - 所有失物（全部匹配模式），matches - 匹配结果映射
  逻辑：当传入lostItemId时，展示单条失物的匹配结果；否则展示全部匹配
--%>
<!-- 引入公共头部模板，设置页面标题为"智能匹配" -->
<jsp:include page="header.jsp">
    <jsp:param name="title" value="智能匹配"/>
</jsp:include>

<!-- 页面标题区域：显示智能匹配功能标题 -->
<div class="section-header">
    <h1>智能匹配</h1>
</div>

<!-- 选择判断：根据是否传入目标失物，显示不同的匹配结果 -->
<c:choose>
    <!-- 单条失物匹配模式：当传入特定失物ID时，展示该失物的匹配结果 -->
    <c:when test="${targetLost != null}">
        <div class="section">
            <!-- 显示当前匹配的失物名称和匹配说明 -->
            <h2>失物: ${targetLost.itemName}</h2>
            <p style="color:var(--text-muted);margin-bottom:20px;">系统为您找到以下可能匹配的招领信息</p>
            <!-- 匹配卡片网格：展示与目标失物匹配的所有招领 -->
            <div class="card-grid">
                <!-- 循环渲染匹配到的招领列表 -->
                <c:forEach var="item" items="${matchedFound}">
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
                        <!-- 卡片内容区：物品名称、地点、类别、联系方式、状态和操作 -->
                        <div class="card-body">
                            <div class="card-title">${item.itemName}</div>
                            <div class="card-meta">地点: ${item.location}</div>
                            <div class="card-meta">类别: ${item.category}</div>
                            <div class="card-meta">联系方式: ${item.contact}</div>
                            <!-- 状态标签：显示招领当前状态 -->
                            <span class="card-status status-${item.status}">${item.status}</span>
                            <!-- 操作按钮：查看详情和申请认领 -->
                            <div class="card-actions">
                                <a href="${pageContext.request.contextPath}/found/detail?id=${item.id}" class="btn-primary">查看详情</a>
                                <a href="${pageContext.request.contextPath}/claim/new?lostItemId=${targetLost.id}&foundItemId=${item.id}" class="btn-success">申请认领</a>
                            </div>
                        </div>
                    </div>
                </c:forEach>
                <!-- 无匹配提示：当没有匹配的招领时显示 -->
                <c:if test="${empty matchedFound}">
                    <p style="color:var(--text-muted)">暂无可匹配招领</p>
                </c:if>
            </div>
        </div>
    </c:when>
    <!-- 全部匹配模式：未传入特定失物时，展示系统所有失物与招领的匹配关系 -->
    <c:otherwise>
        <div class="section">
            <!-- 显示全部匹配模式标题和说明 -->
            <h2>全部匹配</h2>
            <p style="color:var(--text-muted);margin-bottom:20px;">系统自动为每条失物匹配可能对应的招领信息</p>
            <!-- 循环遍历所有失物，检查是否有匹配结果 -->
            <c:forEach var="lost" items="${lostItems}">
                <!-- 仅当该失物有匹配结果时，才展示匹配区域 -->
                <c:if test="${matches[lost.id] != null && !empty matches[lost.id]}">
                    <!-- 单条失物匹配结果区域：展示失物名称和对应匹配的招领列表 -->
                    <div style="margin-bottom:32px;">
                        <!-- 失物标题：显示物品名称、类别和地点，底部带分隔线 -->
                        <h3 style="margin-bottom:12px;border-bottom:1px solid var(--border);padding-bottom:8px;">
                            失物: ${lost.itemName} (${lost.category}) - ${lost.location}
                        </h3>
                        <!-- 匹配招领卡片网格：展示该失物匹配的所有招领 -->
                        <div class="card-grid">
                            <!-- 循环渲染该失物匹配到的招领列表 -->
                            <c:forEach var="found" items="${matches[lost.id]}">
                                <div class="card">
                                    <!-- 物品图片展示区：有图片则显示，无图片则显示占位符 -->
                                    <c:choose>
                                        <c:when test="${found.imageUrl != null}">
                                            <img src="${pageContext.request.contextPath}/${found.imageUrl}" class="card-img" alt="${found.itemName}">
                                        </c:when>
                                        <c:otherwise>
                                            <div class="card-img-placeholder">无图片</div>
                                        </c:otherwise>
                                    </c:choose>
                                    <!-- 卡片内容区：物品名称、地点、类别、联系方式、状态和操作 -->
                                    <div class="card-body">
                                        <div class="card-title">${found.itemName}</div>
                                        <div class="card-meta">地点: ${found.location}</div>
                                        <div class="card-meta">类别: ${found.category}</div>
                                        <div class="card-meta">联系方式: ${found.contact}</div>
                                        <!-- 状态标签：显示招领当前状态 -->
                                        <span class="card-status status-${found.status}">${found.status}</span>
                                        <!-- 操作按钮：查看详情和申请认领 -->
                                        <div class="card-actions">
                                            <a href="${pageContext.request.contextPath}/found/detail?id=${found.id}" class="btn-primary">查看</a>
                                            <a href="${pageContext.request.contextPath}/claim/new?lostItemId=${lost.id}&foundItemId=${found.id}" class="btn-success">申请认领</a>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </c:if>
            </c:forEach>
            <!-- 无匹配结果提示：当没有任何匹配时显示 -->
            <c:if test="${empty matches}">
                <p style="color:var(--text-muted)">暂无智能匹配结果</p>
            </c:if>
        </div>
    </c:otherwise>
</c:choose>

<!-- 引入公共底部模板，关闭页面结构 -->
<jsp:include page="footer.jsp" />