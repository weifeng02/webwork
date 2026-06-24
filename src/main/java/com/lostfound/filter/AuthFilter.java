package com.lostfound.filter;

import com.lostfound.model.User;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;

import java.io.IOException;

/**
 * 认证过滤器（AuthFilter）
 * 拦截所有受保护页面的请求，检查用户是否已登录
 * 未登录用户将被重定向到登录页面
 * 白名单页面（登录、注册、静态资源等）无需认证即可访问
 * 拦截路径：/dashboard, /lost/*, /found/*, /claim/*, /match, /search
 */
@WebFilter(urlPatterns = {"/dashboard", "/lost/*", "/found/*", "/claim/*", "/match", "/search"})
public class AuthFilter implements Filter {

    /**
     * 过滤器初始化
     * @param filterConfig 过滤器配置对象
     */
    @Override
    public void init(FilterConfig filterConfig) {}

    /**
     * 执行过滤逻辑：检查用户登录状态
     * @param req Servlet 请求对象
     * @param resp Servlet 响应对象
     * @param chain 过滤器链，用于继续传递请求
     * @throws IOException IO 异常
     * @throws ServletException Servlet 异常
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        // 将通用请求转换为 HTTP 请求
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        // 获取当前 Session（不创建新 Session）
        HttpSession session = request.getSession(false);
        // 从 Session 中获取用户对象
        User user = session != null ? (User) session.getAttribute("user") : null;

        // 获取当前请求路径
        String path = request.getServletPath();
        // 白名单页面放行：登录、注册、首页、静态资源（CSS、JS、图片）
        if (path.startsWith("/login") || path.startsWith("/register") || path.equals("/")
                || path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/uploads/")) {
            chain.doFilter(req, resp); // 继续传递请求
            return;
        }

        // 未登录用户重定向到登录页面
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // 已登录用户，继续传递请求到目标 Servlet 或 JSP
        chain.doFilter(req, resp);
    }

    /**
     * 过滤器销毁
     */
    @Override
    public void destroy() {}
}
