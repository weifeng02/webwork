package com.lostfound.servlet;

import com.lostfound.dao.LostItemDao;
import com.lostfound.dao.FoundItemDao;
import com.lostfound.model.LostItem;
import com.lostfound.model.FoundItem;
import com.lostfound.util.LuceneSearch;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.util.*;

/**
 * 智能匹配 Servlet
 * 使用 Apache Lucene 全文检索引擎自动匹配失物与招领
 * 为每条待找回的失物，查找物品名称、类别、地点相似的待认领招领
 * 访问路径：/match
 */
@WebServlet("/match")
public class MatchServlet extends HttpServlet {
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

    // GET 请求：执行智能匹配
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 获取 URL 参数（可选）：指定某条失物 ID，则仅匹配该失物
        String lostItemId = req.getParameter("lostItemId");
        try {
            // 查询所有待找回的失物和待认领的招领
            List<LostItem> lostItems = lostItemDao.findByStatus("pending");
            List<FoundItem> foundItems = foundItemDao.findByStatus("pending");
            // 重建 Lucene 全文索引（仅包含待处理的记录）
            LuceneSearch.getInstance().rebuildIndex(lostItems, foundItems);

            // 为每条待找回的失物查找可能的匹配招领
            // 使用 Map 存储结果：key = 失物ID，value = 匹配的招领列表
            Map<String, List<FoundItem>> matches = new HashMap<>();
            for (LostItem lost : lostItems) {
                // 构建搜索关键词：物品名称 + 类别 + 丢失地点
                String keyword = lost.getItemName() + " " + lost.getCategory() + " " + lost.getLocation();
                // 使用 Lucene 模糊搜索，最多返回10条结果
                List<LuceneSearch.SearchResult> results = LuceneSearch.getInstance().fuzzySearch(keyword, 10);
                List<FoundItem> matched = new ArrayList<>();
                for (LuceneSearch.SearchResult sr : results) {
                    // 仅筛选招领类型（排除失物自身）
                    if ("found".equals(sr.getType())) {
                        FoundItem fi = foundItemDao.findById(UUID.fromString(sr.getId()));
                        // 排除已处理的招领，且状态为待认领
                        if (fi != null && !fi.getId().equals(lost.getId()) && fi.getStatus().equals("pending")) {
                            matched.add(fi);
                        }
                    }
                }
                // 如果有匹配结果，存入 Map
                if (!matched.isEmpty()) {
                    matches.put(lost.getId().toString(), matched);
                }
            }

            // 将匹配结果存入 request 属性，供 JSP 页面展示
            req.setAttribute("lostItems", lostItems);
            req.setAttribute("foundItems", foundItems);
            req.setAttribute("matches", matches);

            // 如果指定了特定失物 ID，则额外查询该失物的匹配结果
            if (lostItemId != null && !lostItemId.isEmpty()) {
                LostItem target = lostItemDao.findById(UUID.fromString(lostItemId));
                if (target != null) {
                    // 构建该失物的搜索关键词
                    String keyword = target.getItemName() + " " + target.getCategory() + " " + target.getLocation();
                    List<LuceneSearch.SearchResult> results = LuceneSearch.getInstance().fuzzySearch(keyword, 10);
                    List<FoundItem> matched = new ArrayList<>();
                    for (LuceneSearch.SearchResult sr : results) {
                        if ("found".equals(sr.getType())) {
                            FoundItem fi = foundItemDao.findById(UUID.fromString(sr.getId()));
                            if (fi != null && fi.getStatus().equals("pending")) {
                                matched.add(fi);
                            }
                        }
                    }
                    // 存储特定失物的匹配结果
                    req.setAttribute("targetLost", target);
                    req.setAttribute("matchedFound", matched);
                }
            }

            // 转发到匹配结果展示页面
            req.getRequestDispatcher("/WEB-INF/jsp/matches.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException("Matching failed", e);
        }
    }
}
