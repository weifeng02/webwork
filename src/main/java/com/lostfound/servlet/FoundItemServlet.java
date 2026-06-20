package com.lostfound.servlet;

import com.lostfound.dao.FoundItemDao;
import com.lostfound.model.FoundItem;
import com.lostfound.model.User;
import com.lostfound.util.XssFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.UUID;

/**
 * 招领管理 Servlet
 * 处理招领发布、编辑、删除、列表查看、详情查看等操作
 * 支持文件上传（物品图片），使用 Servlet 3.0 Multipart 机制
 * 访问路径：/found/new, /found/edit, /found/delete, /found/list, /found/detail
 */
@WebServlet(urlPatterns = {"/found/new", "/found/edit", "/found/delete", "/found/list", "/found/detail"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,          // 文件大小阈值：1MB 以上写入临时文件
    maxFileSize = 5 * 1024 * 1024,          // 单个文件最大限制：5MB
    maxRequestSize = 10 * 1024 * 1024       // 请求总大小限制：10MB
)
public class FoundItemServlet extends HttpServlet {
    // 招领数据访问对象
    private FoundItemDao foundItemDao;
    // 图片上传目录（相对 webapp 根目录）
    private static final String UPLOAD_DIR = "uploads";

    // Servlet 初始化时创建 DAO 实例
    @Override
    public void init() {
        foundItemDao = new FoundItemDao();
    }

    // GET 请求：处理查看、列表、编辑页面、删除操作
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath(); // 获取请求路径
        // 获取当前 Session（用于检查登录状态）
        HttpSession session = req.getSession(false);
        User user = session != null ? (User) session.getAttribute("user") : null;

        try {
            if ("/found/new".equals(path)) {
                // 显示发布招领表单
                req.getRequestDispatcher("/WEB-INF/jsp/found_item_form.jsp").forward(req, resp);
            } else if ("/found/edit".equals(path)) {
                // 获取招领 ID 并查询详情，转发到编辑表单
                UUID id = UUID.fromString(req.getParameter("id"));
                FoundItem item = foundItemDao.findById(id);
                req.setAttribute("item", item);
                req.getRequestDispatcher("/WEB-INF/jsp/found_item_form.jsp").forward(req, resp);
            } else if ("/found/delete".equals(path)) {
                // 删除招领记录并跳转回列表
                UUID id = UUID.fromString(req.getParameter("id"));
                foundItemDao.delete(id);
                resp.sendRedirect(req.getContextPath() + "/found/list");
            } else if ("/found/detail".equals(path)) {
                // 查看招领详情
                UUID id = UUID.fromString(req.getParameter("id"));
                FoundItem item = foundItemDao.findById(id);
                req.setAttribute("item", item);
                req.getRequestDispatcher("/WEB-INF/jsp/found_item_detail.jsp").forward(req, resp);
            } else {
                // 默认：招领列表，支持筛选条件
                String category = req.getParameter("category");
                String location = req.getParameter("location");
                String status = req.getParameter("status");
                java.util.List<FoundItem> items;
                if (category != null || location != null || status != null) {
                    // 有筛选条件则使用组合查询
                    items = foundItemDao.findByFilter(category, location, status);
                } else {
                    // 无筛选条件则查询全部
                    items = foundItemDao.findAll();
                }
                req.setAttribute("foundItems", items);
                req.getRequestDispatcher("/WEB-INF/jsp/found_items.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            throw new ServletException("Found item operation failed", e);
        }
    }

    // POST 请求：处理招领发布/编辑表单提交
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
            // 获取 ID 参数（编辑时存在，新建时生成新 UUID）
            String idParam = req.getParameter("id");
            UUID id = idParam != null && !idParam.isEmpty() ? UUID.fromString(idParam) : UUID.randomUUID();
            // 获取表单参数并过滤 XSS（防止恶意脚本注入）
            String itemName = XssFilter.sanitize(req.getParameter("itemName"));
            String description = XssFilter.sanitize(req.getParameter("description"));
            String category = XssFilter.sanitize(req.getParameter("category"));
            String location = XssFilter.sanitize(req.getParameter("location"));
            String contact = XssFilter.sanitize(req.getParameter("contact")); // 招领特有的联系方式
            String status = XssFilter.sanitize(req.getParameter("status"));
            if (status == null || status.isEmpty()) status = "pending"; // 默认状态

            // 处理图片上传（使用 Servlet 3.0 Part API）
            String imageUrl = null;
            Part filePart = req.getPart("image");
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String ext = fileName.substring(fileName.lastIndexOf('.'));
                String savedName = UUID.randomUUID().toString() + ext; // 使用 UUID 避免重名
                String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdirs(); // 目录不存在则创建
                filePart.write(uploadPath + File.separator + savedName);
                imageUrl = UPLOAD_DIR + "/" + savedName; // 保存相对路径
            }

            // 创建招领对象
            FoundItem item = new FoundItem();
            item.setId(id);
            item.setUserId(user.getId()); // 当前登录用户作为发布者
            item.setItemName(itemName);
            item.setDescription(description);
            item.setCategory(category);
            item.setLocation(location);
            item.setFoundTime(Instant.now()); // 当前时间
            item.setImageUrl(imageUrl);
            item.setContact(contact); // 拾到者的联系方式
            item.setStatus(status);
            item.setCreatedAt(Instant.now());

            // 区分编辑和新建操作
            if ("/found/edit".equals(path)) {
                // 编辑时保留原有图片（如果未上传新图片）
                FoundItem existing = foundItemDao.findById(id);
                if (existing != null) {
                    if (imageUrl == null) item.setImageUrl(existing.getImageUrl());
                    foundItemDao.update(item);
                }
            } else {
                // 新建：插入新记录
                foundItemDao.insert(item);
            }
            // 操作完成后重定向到列表页
            resp.sendRedirect(req.getContextPath() + "/found/list");
        } catch (Exception e) {
            throw new ServletException("Save found item failed", e);
        }
    }
}
