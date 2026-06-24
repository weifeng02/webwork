package com.lostfound.servlet;

import com.lostfound.dao.LostItemDao;
import com.lostfound.dao.FoundItemDao;
import com.lostfound.model.LostItem;
import com.lostfound.model.FoundItem;
import com.lostfound.util.LuceneSearch;
import com.lostfound.util.XssFilter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.util.*;

/**
 * 全文搜索 Servlet
 * 使用 Apache Lucene 全文检索引擎实现模糊搜索
 * 当 Lucene 无结果时，回退到 MySQL LIKE 模糊匹配
 * 支持按类型筛选（全部/失物/招领）
 * 访问路径：/search
 */
@WebServlet("/search")
public class SearchServlet extends HttpServlet {
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

    // GET 请求：处理搜索请求
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 获取搜索关键词并过滤 XSS（防止搜索注入攻击）
        String keyword = XssFilter.sanitize(req.getParameter("q"));
        // 获取搜索类型：all（全部）、lost（仅失物）、found（仅招领）
        String type = req.getParameter("type");
        if (type == null || type.isEmpty()) type = "all"; // 默认搜索全部

        try {
            // 搜索结果存储容器
            List<LostItem> lostResults = new ArrayList<>();
            List<FoundItem> foundResults = new ArrayList<>();

            if (keyword != null && !keyword.isEmpty()) {
                // 第一步：使用 Apache Lucene 进行全文模糊搜索
                // 先重建索引，确保索引与数据库同步
                LuceneSearch.getInstance().rebuildIndex(lostItemDao.findAll(), foundItemDao.findAll());
                // 执行模糊搜索，最多返回50条结果
                List<LuceneSearch.SearchResult> luceneResults = LuceneSearch.getInstance().fuzzySearch(keyword, 50);

                // 根据搜索结果类型和筛选条件，查询对应数据库记录
                for (LuceneSearch.SearchResult sr : luceneResults) {
                    if ("lost".equals(sr.getType()) && ("all".equals(type) || "lost".equals(type))) {
                        // 搜索结果为失物类型，且用户筛选包含失物
                        LostItem item = lostItemDao.findById(UUID.fromString(sr.getId()));
                        if (item != null) lostResults.add(item);
                    } else if ("found".equals(sr.getType()) && ("all".equals(type) || "found".equals(type))) {
                        // 搜索结果为招领类型，且用户筛选包含招领
                        FoundItem item = foundItemDao.findById(UUID.fromString(sr.getId()));
                        if (item != null) foundResults.add(item);
                    }
                }

                // 第二步：Lucene 无结果时，回退到 MySQL LIKE 模糊匹配（兜底策略）
                if (lostResults.isEmpty() && foundResults.isEmpty()) {
                    if ("all".equals(type) || "lost".equals(type)) {
                        lostResults.addAll(lostItemDao.search(keyword)); // MySQL LIKE 搜索
                    }
                    if ("all".equals(type) || "found".equals(type)) {
                        foundResults.addAll(foundItemDao.search(keyword)); // MySQL LIKE 搜索
                    }
                }
            } else {
                // 无关键词时，列出所有记录（按类型筛选）
                if ("all".equals(type) || "lost".equals(type)) {
                    lostResults.addAll(lostItemDao.findAll());
                }
                if ("all".equals(type) || "found".equals(type)) {
                    foundResults.addAll(foundItemDao.findAll());
                }
            }

            // 将搜索结果存入 request 属性，供 JSP 页面展示
            req.setAttribute("lostResults", lostResults);
            req.setAttribute("foundResults", foundResults);
            req.setAttribute("keyword", keyword);
            req.setAttribute("type", type);
            // 转发到搜索结果显示页面
            req.getRequestDispatcher("/WEB-INF/jsp/search.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException("Search failed", e);
        }
    }
}
