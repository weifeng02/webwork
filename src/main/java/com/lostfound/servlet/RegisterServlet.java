package com.lostfound.servlet;

import com.lostfound.dao.UserDao;
import com.lostfound.model.User;
import com.lostfound.util.XssFilter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

/**
 * 用户注册 Servlet
 * 处理用户注册请求，验证用户名唯一性，创建新用户
 * 访问路径：/register
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    // 用户数据访问对象
    private UserDao userDao;

    // Servlet 初始化时创建 DAO 实例
    @Override
    public void init() {
        userDao = new UserDao();
    }

    // GET 请求：显示注册页面
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
    }

    // POST 请求：处理注册表单提交
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8"); // 设置 UTF-8 编码
        // 获取表单参数并过滤 XSS（防止恶意脚本注入）
        String username = XssFilter.sanitize(req.getParameter("username"));
        String password = req.getParameter("password");
        String email = XssFilter.sanitize(req.getParameter("email"));
        String phone = XssFilter.sanitize(req.getParameter("phone"));
        String role = req.getParameter("role");
        // 角色校验：只允许 user 或 admin，默认 user
        if (role == null || (!role.equals("admin") && !role.equals("user"))) {
            role = "user";
        }

        try {
            // 检查用户名是否已存在
            if (userDao.findByUsername(username) != null) {
                req.setAttribute("error", "Username already exists");
                req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
                return;
            }

            // 创建新用户并保存（生产环境密码应使用 BCrypt 加密）
            User user = new User(UUID.randomUUID(), username, password, email, phone, role, Instant.now());
            userDao.insert(user);
            // 注册成功后重定向到登录页面
            resp.sendRedirect(req.getContextPath() + "/login");
        } catch (Exception e) {
            throw new ServletException("Registration failed", e);
        }
    }
}
