package com.lostfound.servlet;

import com.lostfound.dao.LostItemDao;
import com.lostfound.model.LostItem;
import com.lostfound.model.User;
import com.lostfound.util.XssFilter;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.io.File;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.UUID;

/**
 * 失物管理 Servlet
 * 处理失物发布、编辑、删除、列表查看、详情查看等操作
 * 支持文件上传（物品图片），使用 Servlet 3.0 Multipart 机制
 * 访问路径：/lost/new, /lost/edit, /lost/delete, /lost/list, /lost/detail
 */
@WebServlet(urlPatterns = {"/lost/new", "/lost/edit", "/lost/delete", "/lost/list", "/lost/detail"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,          // 文件大小阈值：1MB 以上写入临时文件
    maxFileSize = 5 * 1024 * 1024,          // 单个文件最大限制：5MB
    maxRequestSize = 10 * 1024 * 1024       // 请求总大小限制：10MB
)
public class LostItemServlet extends HttpServlet {
    // 失物数据访问对象
    private LostItemDao lostItemDao;
    // 图片上传目录（相对 webapp 根目录）
    private static final String UPLOAD_DIR = "uploads";

    // Servlet 初始化时创建 DAO 实例
    @Override
    public void init() {
        lostItemDao = new LostItemDao();
    }

    // GET 请求：处理查看、列表、编辑页面、删除操作
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 获取请求路径（Servlet 映射的 URL）
        String path = req.getServletPath();
        // 获取当前 Session（用于检查登录状态）
        HttpSession session = req.getSession(false);
        User user = session != null ? (User) session.getAttribute("user") : null;

        try {
            if ("/lost/new".equals(path)) {
                // 显示发布失物表单
                req.getRequestDispatcher("/WEB-INF/jsp/lost_item_form.jsp").forward(req, resp);
            } else if ("/lost/edit".equals(path)) {
                // 获取失物 ID 并查询详情，转发到编辑表单
                UUID id = UUID.fromString(req.getParameter("id"));
                LostItem item = lostItemDao.findById(id);
                req.setAttribute("item", item);
                req.getRequestDispatcher("/WEB-INF/jsp/lost_item_form.jsp").forward(req, resp);
            } else if ("/lost/delete".equals(path)) {
                // 删除失物记录并跳转回列表
                UUID id = UUID.fromString(req.getParameter("id"));
                lostItemDao.delete(id);
                resp.sendRedirect(req.getContextPath() + "/lost/list");
            } else if ("/lost/detail".equals(path)) {
                // 查看失物详情
                UUID id = UUID.fromString(req.getParameter("id"));
                LostItem item = lostItemDao.findById(id);
                req.setAttribute("item", item);
                req.getRequestDispatcher("/WEB-INF/jsp/lost_item_detail.jsp").forward(req, resp);
            } else {
                // 默认：失物列表，支持筛选条件
                String category = req.getParameter("category");
                String location = req.getParameter("location");
                String status = req.getParameter("status");
                java.util.List<LostItem> items;
                if (category != null || location != null || status != null) {
                    // 有筛选条件则使用组合查询
                    items = lostItemDao.findByFilter(category, location, status);
                } else {
                    // 无筛选条件则查询全部
                    items = lostItemDao.findAll();
                }
                req.setAttribute("lostItems", items);
                req.getRequestDispatcher("/WEB-INF/jsp/lost_items.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            throw new ServletException("Lost item operation failed", e);
        }
    }

    // POST 请求：处理失物发布/编辑表单提交
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
            String status = XssFilter.sanitize(req.getParameter("status"));
            if (status == null || status.isEmpty()) status = "pending"; // 默认状态

            // 处理图片上传（使用 Servlet 3.0 Part API）
            String imageUrl = null;
            Part filePart = req.getPart("image");
            if (filePart != null && filePart.getSize() > 0) {
                // 获取原始文件名
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                // 提取文件扩展名
                String ext = fileName.substring(fileName.lastIndexOf('.'));
                // 使用 UUID 作为保存文件名，避免重名冲突
                String savedName = UUID.randomUUID().toString() + ext;
                // 计算上传目录绝对路径（相对于 webapp 根目录）
                String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdirs(); // 目录不存在则创建
                // 保存文件到磁盘
                filePart.write(uploadPath + File.separator + savedName);
                // 保存相对路径到数据库
                imageUrl = UPLOAD_DIR + "/" + savedName;
            }

            // 创建失物对象
            LostItem item = new LostItem();
            item.setId(id);
            item.setUserId(user.getId()); // 当前登录用户作为发布者
            item.setItemName(itemName);
            item.setDescription(description);
            item.setCategory(category);
            item.setLocation(location);
            item.setLostTime(Instant.now()); // 当前时间
            item.setImageUrl(imageUrl);
            item.setStatus(status);
            item.setCreatedAt(Instant.now());

            // 区分编辑和新建操作
            if ("/lost/edit".equals(path)) {
                // 编辑时保留原有图片（如果未上传新图片）
                LostItem existing = lostItemDao.findById(id);
                if (existing != null) {
                    if (imageUrl == null) item.setImageUrl(existing.getImageUrl());
                    lostItemDao.update(item);
                }
            } else {
                // 新建：插入新记录
                lostItemDao.insert(item);
            }
            // 操作完成后重定向到列表页
            resp.sendRedirect(req.getContextPath() + "/lost/list");
        } catch (Exception e) {
            throw new ServletException("Save lost item failed", e);
        }
    }
}
