# 校园失物招领系统

基于 Java + Servlet + JSP + MySQL + Apache Lucene 的校园失物招领系统。

## 技术栈

- Java 17
- Jakarta Servlet 6.0 + JSP
- Maven 3
- MySQL 8.0
- Apache Lucene 9.9 (全文检索)
- Apache Commons FileUpload (文件上传)
- OWASP Java Encoder (XSS 防护)

## 核心功能

- **失物发布**: 物品名称、地点、时间、图片上传
- **招领发布**: 拾到物品信息、联系方式
- **智能匹配**: 系统通过 Apache Lucene 自动匹配相似物品
- **认领申请与审核**: 用户申请认领，管理员审核确认
- **公告栏与搜索**: 按类别、地点过滤，支持 Lucene 全文模糊搜索
- **XSS 过滤**: 所有用户输入均通过 OWASP Encoder 进行过滤

## 数据库表

- `users` - 用户表
- `lost_items` - 失物表
- `found_items` - 招领表
- `claim_requests` - 认领申请表

## 运行方式

1. 确保已安装 Java 17 和 Maven
2. 配置数据库连接：
   - 设置环境变量 `SUPABASE_DB_URL`、`SUPABASE_DB_USER`、`SUPABASE_DB_PASSWORD`
   - 或修改 `src/main/resources/db.properties`
3. 编译运行：
   ```bash
   mvn clean package
   mvn tomcat10:run
   ```
4. 或部署 WAR 到 Tomcat 10 / Jetty 11

## 默认页面

- `/login` - 登录
- `/register` - 注册
- `/dashboard` - 首页仪表盘
- `/lost/list` - 失物列表
- `/found/list` - 招领列表
- `/search` - 搜索
- `/match` - 智能匹配
- `/claim/list` - 认领申请
