package com.lostfound.servlet;

import com.lostfound.dao.UserDao;
import com.lostfound.model.User;
import com.lostfound.util.XssFilter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;

/**
 * 用户登录 Servlet
 * 处理用户登录请求，验证用户名和密码，成功后创建 Session
 * 访问路径：/login
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    // 用户数据访问对象
    private UserDao userDao;

    // Servlet 初始化时创建 DAO 实例
    @Override
    public void init() {
        userDao = new UserDao();
    }

    // GET 请求：显示登录页面
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 转发到登录 JSP 页面
        req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
    }

    // POST 请求：处理登录表单提交
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8"); // 设置 UTF-8 编码，防止中文乱码
        // 获取表单参数并过滤 XSS
        String username = XssFilter.sanitize(req.getParameter("username"));
        String password = req.getParameter("password");

        try {
            // 根据用户名查询用户
            User user = userDao.findByUsername(username);
            // 验证用户存在且密码匹配（生产环境应使用 BCrypt 加密比较）
            if (user != null && user.getPassword().equals(password)) {
                // 创建 Session 并存储用户对象
                HttpSession session = req.getSession();
                session.setAttribute("user", user);
                // 重定向到仪表盘
                resp.sendRedirect(req.getContextPath() + "/dashboard");
            } else {
                // 登录失败，返回错误信息到登录页面
                req.setAttribute("error", "Invalid username or password");
                req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            throw new ServletException("Login failed", e);
        }
    }
}
