<%--
  失物发布/编辑表单页面 (lost_item_form.jsp)
  功能：用于发布新失物信息或编辑已有失物信息
  访问路径：/lost/new (发布) / /lost/edit (编辑)
  参数：item - 编辑时的失物对象，为null时则为新建模式
  表单提交：POST方法，包含图片上传，支持multipart/form-data
--%>
<!-- 引入公共头部模板，根据编辑/新建模式动态设置页面标题 -->
<jsp:include page="header.jsp">
    <jsp:param name="title" value="${item != null ? '编辑失物' : '发布失物'}"/>
</jsp:include>

<!-- 失物表单容器：限制最大宽度为600px，居中显示 -->
<div class="auth-container" style="max-width:600px;">
    <!-- 页面标题：根据编辑/新建模式动态显示 -->
    <h2>${item != null ? '编辑失物' : '发布失物'}</h2>
    <!-- 失物表单：根据编辑/新建模式动态设置提交URL，支持文件上传 -->
    <form action="${pageContext.request.contextPath}${item != null ? '/lost/edit' : '/lost/new'}" method="post" enctype="multipart/form-data">
        <!-- 编辑模式隐藏字段：传递失物ID，用于后端识别要编辑的记录 -->
        <c:if test="${item != null}">
            <input type="hidden" name="id" value="${item.id}">
        </c:if>
        <!-- 物品名称输入组：必填，用于标识物品 -->
        <div class="form-group">
            <label>物品名称</label>
            <input type="text" name="itemName" required value="${item != null ? item.itemName : ''}">
        </div>
        <!-- 物品描述输入组：必填，多行文本，用于详细描述物品特征 -->
        <div class="form-group">
            <label>物品描述</label>
            <textarea name="description" required>${item != null ? item.description : ''}</textarea>
        </div>
        <!-- 物品类别选择组：必填，下拉选择，便于分类筛选和匹配 -->
        <div class="form-group">
            <label>类别</label>
            <select name="category" required>
                <option value="">请选择</option>
                <option value="电子产品" ${item != null && item.category == '电子产品' ? 'selected' : ''}>电子产品</option>
                <option value="证件" ${item != null && item.category == '证件' ? 'selected' : ''}>证件</option>
                <option value="书包" ${item != null && item.category == '书包' ? 'selected' : ''}>书包</option>
                <option value="钱包" ${item != null && item.category == '钱包' ? 'selected' : ''}>钱包</option>
                <option value="钥匙" ${item != null && item.category == '钥匙' ? 'selected' : ''}>钥匙</option>
                <option value="衣物" ${item != null && item.category == '衣物' ? 'selected' : ''}>衣物</option>
                <option value="其他" ${item != null && item.category == '其他' ? 'selected' : ''}>其他</option>
            </select>
        </div>
        <!-- 丢失地点输入组：必填，用于标记失物位置 -->
        <div class="form-group">
            <label>丢失地点</label>
            <input type="text" name="location" required value="${item != null ? item.location : ''}">
        </div>
        <!-- 物品图片上传组：可选，用于直观识别物品，编辑时显示当前图片 -->
        <div class="form-group">
            <label>物品图片</label>
            <input type="file" name="image" accept="image/*">
            <!-- 编辑模式：显示当前已上传的图片路径信息 -->
            <c:if test="${item != null && item.imageUrl != null}">
                <p style="margin-top:8px;font-size:0.85rem;color:var(--text-muted)">当前图片: ${item.imageUrl}</p>
            </c:if>
        </div>
        <!-- 状态选择组：编辑模式下可用，控制物品是否已找回 -->
        <div class="form-group">
            <label>状态</label>
            <select name="status">
                <option value="pending" ${item != null && item.status == 'pending' ? 'selected' : ''}>待找回</option>
                <option value="found" ${item != null && item.status == 'found' ? 'selected' : ''}>已找回</option>
            </select>
        </div>
        <!-- 提交按钮：全宽样式，显示"更新"或"发布" -->
        <button type="submit" class="btn-primary" style="width:100%">${item != null ? '更新' : '发布'}</button>
    </form>
</div>

<!-- 引入公共底部模板，关闭页面结构 -->
<jsp:include page="footer.jsp" />