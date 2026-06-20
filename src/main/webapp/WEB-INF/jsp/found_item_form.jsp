<%--
  招领发布/编辑表单页面 (found_item_form.jsp)
  功能：用于发布新招领信息或编辑已有招领信息
  访问路径：/found/new (发布) / /found/edit (编辑)
  参数：item - 编辑时的招领对象，为null时则为新建模式
  表单提交：POST方法，包含图片上传，支持multipart/form-data
  特点：包含联系方式字段，用于失主联系
--%>
<!-- 引入公共头部模板，根据编辑/新建模式动态设置页面标题 -->
<jsp:include page="header.jsp">
    <jsp:param name="title" value="${item != null ? '编辑招领' : '发布招领'}"/>
</jsp:include>

<!-- 招领表单容器：限制最大宽度为600px，居中显示 -->
<div class="auth-container" style="max-width:600px;">
    <!-- 页面标题：根据编辑/新建模式动态显示 -->
    <h2>${item != null ? '编辑招领' : '发布招领'}</h2>
    <!-- 招领表单：根据编辑/新建模式动态设置提交URL，支持文件上传 -->
    <form action="${pageContext.request.contextPath}${item != null ? '/found/edit' : '/found/new'}" method="post" enctype="multipart/form-data">
        <!-- 编辑模式隐藏字段：传递招领ID，用于后端识别要编辑的记录 -->
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
        <!-- 拾取地点输入组：必填，用于标记物品发现位置 -->
        <div class="form-group">
            <label>拾取地点</label>
            <input type="text" name="location" required value="${item != null ? item.location : ''}">
        </div>
        <!-- 联系方式输入组：必填，用于失主联系招领人 -->
        <div class="form-group">
            <label>联系方式</label>
            <input type="text" name="contact" required value="${item != null ? item.contact : ''}" placeholder="手机号/微信">
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
        <!-- 状态选择组：编辑模式下可用，控制物品是否已归还 -->
        <div class="form-group">
            <label>状态</label>
            <select name="status">
                <option value="pending" ${item != null && item.status == 'pending' ? 'selected' : ''}>待认领</option>
                <option value="returned" ${item != null && item.status == 'returned' ? 'selected' : ''}>已归还</option>
            </select>
        </div>
        <!-- 提交按钮：全宽样式，显示"更新"或"发布" -->
        <button type="submit" class="btn-primary" style="width:100%">${item != null ? '更新' : '发布'}</button>
    </form>
</div>

<!-- 引入公共底部模板，关闭页面结构 -->
<jsp:include page="footer.jsp" />