package com.lostfound.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * 用户退出登录 Servlet
 * 销毁当前 Session，清除用户登录状态，然后重定向到登录页
 * 访问路径：/logout
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    // GET 请求：处理用户退出
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 获取当前 Session（如果存在），false 表示不创建新 Session
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate(); // 销毁 Session，清除所有属性
        }
        // 重定向到登录页面
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}
