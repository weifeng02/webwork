<%--
  头部导航栏模板 (header.jsp)
  功能：定义页面公共头部、导航栏和用户认证区域
  被其他JSP页面通过 <jsp:include> 引入，统一页面风格和导航功能
  参数：title - 设置页面标题，默认为"校园失物招领系统"
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${param.title != null ? param.title : '校园失物招领系统'}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<!-- 导航栏区域：包含网站Logo、主导航链接和用户认证入口 -->
<nav class="navbar">
    <!-- 网站Logo和品牌链接，点击返回首页 -->
    <div class="nav-brand">
        <a href="${pageContext.request.contextPath}/dashboard">
            <span class="logo">LF</span>
            <span>校园失物招领</span>
        </a>
    </div>
    <!-- 主导航菜单：提供各主要功能页面的快速入口 -->
    <ul class="nav-links">
        <li><a href="${pageContext.request.contextPath}/dashboard">首页</a></li>
        <li><a href="${pageContext.request.contextPath}/lost/list">失物</a></li>
        <li><a href="${pageContext.request.contextPath}/found/list">招领</a></li>
        <li><a href="${pageContext.request.contextPath}/search">搜索</a></li>
        <li><a href="${pageContext.request.contextPath}/match">匹配</a></li>
        <li><a href="${pageContext.request.contextPath}/claim/list">认领</a></li>
        <!-- 管理员专属导航：仅当用户角色为admin时显示审核入口 -->
        <c:if test="${sessionScope.user != null && sessionScope.user.role == 'admin'}">
            <li><a href="${pageContext.request.contextPath}/claim/list">审核</a></li>
        </c:if>
    </ul>
    <!-- 用户区域：根据登录状态显示欢迎信息/退出按钮，或登录/注册入口 -->
    <div class="nav-user">
        <c:choose>
            <c:when test="${sessionScope.user != null}">
                <span>欢迎, ${sessionScope.user.username}</span>
                <a href="${pageContext.request.contextPath}/logout" class="btn-logout">退出</a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/login" class="btn-login">登录</a>
                <a href="${pageContext.request.contextPath}/register" class="btn-register">注册</a>
            </c:otherwise>
        </c:choose>
    </div>
</nav>
<!-- 页面主内容区域开始：所有子页面的具体内容将被插入到这里 -->
<main class="main-content">
