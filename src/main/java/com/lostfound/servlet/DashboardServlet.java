package com.lostfound.servlet;

import com.lostfound.dao.LostItemDao;
import com.lostfound.dao.FoundItemDao;
import com.lostfound.model.LostItem;
import com.lostfound.model.FoundItem;
import com.lostfound.util.LuceneSearch;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

/**
 * 仪表盘/首页 Servlet
 * 展示最近的失物和招领信息，并重建 Lucene 全文索引
 * 访问路径：/dashboard
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    // 失物数据访问对象
    private LostItemDao lostItemDao;
    // 招领数据访问对象
    private FoundItemDao foundItemDao;

    // Servlet 初始化时创建 DAO 实例
    @Override
    public void init() {
        lostItemDao = new LostItemDao();
        foundItemDao = new FoundItemDao();
    }

    // GET 请求：加载仪表盘数据
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 检查用户是否已登录（从 Session 中读取用户信息）
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            // 未登录则重定向到登录页
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            // 查询最近6条失物记录（使用 Stream API 限制数量）
            List<LostItem> recentLost = lostItemDao.findAll().stream().limit(6).collect(java.util.stream.Collectors.toList());
            // 查询最近6条招领记录
            List<FoundItem> recentFound = foundItemDao.findAll().stream().limit(6).collect(java.util.stream.Collectors.toList());

            // 将数据存入 request 属性，供 JSP 页面使用
            req.setAttribute("recentLost", recentLost);
            req.setAttribute("recentFound", recentFound);

            // 重建 Lucene 全文检索索引（确保索引与数据库数据同步）
            LuceneSearch.getInstance().rebuildIndex(lostItemDao.findAll(), foundItemDao.findAll());

            // 转发到仪表盘 JSP 页面
            req.getRequestDispatcher("/WEB-INF/jsp/dashboard.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException("Dashboard load failed", e);
        }
    }
}
