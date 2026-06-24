package com.lostfound.servlet;

import com.lostfound.dao.ClaimRequestDao;
import com.lostfound.dao.LostItemDao;
import com.lostfound.dao.FoundItemDao;
import com.lostfound.model.ClaimRequest;
import com.lostfound.model.User;
import com.lostfound.util.XssFilter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

/**
 * 认领申请管理 Servlet
 * 处理认领申请的新建、管理员审核（通过/拒绝）、列表查看和删除
 * 审核通过后自动更新失物和招领状态为已找回/已归还
 * 访问路径：/claim/new, /claim/approve, /claim/reject, /claim/list, /claim/delete
 */
@WebServlet(urlPatterns = {"/claim/new", "/claim/approve", "/claim/reject", "/claim/list", "/claim/delete"})
public class ClaimRequestServlet extends HttpServlet {
    // 认领申请数据访问对象
    private ClaimRequestDao claimRequestDao;
    // 失物数据访问对象（审核通过后更新状态）
    private LostItemDao lostItemDao;
    // 招领数据访问对象（审核通过后更新状态）
    private FoundItemDao foundItemDao;

    // Servlet 初始化时创建 DAO 实例
    @Override
    public void init() {
        claimRequestDao = new ClaimRequestDao();
        lostItemDao = new LostItemDao();
        foundItemDao = new FoundItemDao();
    }

    // GET 请求：处理列表查看、删除、新建表单页面
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath(); // 获取请求路径
        // 获取当前 Session，检查用户登录状态
        HttpSession session = req.getSession(false);
        User user = session != null ? (User) session.getAttribute("user") : null;

        try {
            if ("/claim/list".equals(path)) {
                // 管理员查看所有申请，普通用户查看自己的申请
                java.util.List<ClaimRequest> requests;
                if (user != null && user.isAdmin()) {
                    requests = claimRequestDao.findAll(); // 管理员：全部申请
                } else if (user != null) {
                    requests = claimRequestDao.findByUserId(user.getId()); // 普通用户：仅自己的申请
                } else {
                    resp.sendRedirect(req.getContextPath() + "/login");
                    return;
                }
                req.setAttribute("claims", requests);
                req.getRequestDispatcher("/WEB-INF/jsp/claim_requests.jsp").forward(req, resp);
            } else if ("/claim/delete".equals(path)) {
                // 删除认领申请
                UUID id = UUID.fromString(req.getParameter("id"));
                claimRequestDao.delete(id);
                resp.sendRedirect(req.getContextPath() + "/claim/list");
            } else {
                // /claim/new：显示认领申请表单
                // 从 URL 参数中获取关联的失物 ID 和招领 ID
                String lostItemId = req.getParameter("lostItemId");
                String foundItemId = req.getParameter("foundItemId");
                req.setAttribute("lostItemId", lostItemId);
                req.setAttribute("foundItemId", foundItemId);
                req.getRequestDispatcher("/WEB-INF/jsp/claim_form.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            throw new ServletException("Claim request operation failed", e);
        }
    }

    // POST 请求：处理新建申请、管理员审核（通过/拒绝）
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8"); // 设置 UTF-8 编码
        String path = req.getServletPath();
        // 检查用户是否已登录
        HttpSession session = req.getSession(false);
        User user = session != null ? (User) session.getAttribute("user") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            if ("/claim/new".equals(path)) {
                // 新建认领申请
                UUID lostItemId = UUID.fromString(req.getParameter("lostItemId"));
                UUID foundItemId = UUID.fromString(req.getParameter("foundItemId"));
                // 过滤 XSS（用户输入的申请说明）
                String message = XssFilter.sanitize(req.getParameter("message"));

                // 创建认领申请对象
                ClaimRequest request = new ClaimRequest();
                request.setId(UUID.randomUUID()); // 生成新 UUID
                request.setLostItemId(lostItemId); // 关联的失物
                request.setFoundItemId(foundItemId); // 关联的招领
                request.setUserId(user.getId()); // 当前申请人
                request.setStatus("pending"); // 默认状态：待审核
                request.setMessage(message); // 申请说明
                request.setCreatedAt(Instant.now());
                claimRequestDao.insert(request); // 保存到数据库
                resp.sendRedirect(req.getContextPath() + "/claim/list");
            } else if ("/claim/approve".equals(path)) {
                // 管理员审核通过：更新申请状态为 approved
                UUID id = UUID.fromString(req.getParameter("id"));
                claimRequestDao.updateStatus(id, "approved");
                // 审核通过后，同步更新失物和招领的状态
                ClaimRequest cr = claimRequestDao.findById(id);
                if (cr != null) {
                    // 失物状态更新为已找回
                    com.lostfound.model.LostItem lost = lostItemDao.findById(cr.getLostItemId());
                    if (lost != null) { lost.setStatus("found"); lostItemDao.update(lost); }
                    // 招领状态更新为已归还
                    com.lostfound.model.FoundItem found = foundItemDao.findById(cr.getFoundItemId());
                    if (found != null) { found.setStatus("returned"); foundItemDao.update(found); }
                }
                resp.sendRedirect(req.getContextPath() + "/claim/list");
            } else if ("/claim/reject".equals(path)) {
                // 管理员审核拒绝：更新申请状态为 rejected
                UUID id = UUID.fromString(req.getParameter("id"));
                claimRequestDao.updateStatus(id, "rejected");
                resp.sendRedirect(req.getContextPath() + "/claim/list");
            }
        } catch (Exception e) {
            throw new ServletException("Claim request save failed", e);
        }
    }
}
