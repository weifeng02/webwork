# 校园失物招领系统

基于 Java + Servlet + JSP + MySQL + Apache Lucene 的校园失物招领系统。
无需 Maven，直接下载 JAR 包放入 Tomcat 即可运行。

## 技术栈

- Java 17
- Jakarta Servlet 6.0 + JSP
- Tomcat 10 (或 11)
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

## 运行方式（纯 Tomcat，无需 Maven）

### 第一步：安装软件

1. 安装 Java 17 (JDK)
2. 安装 MySQL 8.0
3. 安装 Tomcat 10 或 Tomcat 11

### 第二步：下载依赖 JAR

打开 `lib/jar-dependencies.txt`，按照文件中的 URL 下载所有 JAR 包。

下载后，将以下 12 个 JAR 文件放入 `src/main/webapp/WEB-INF/lib/` 目录下：

```
mysql-connector-j-8.3.0.jar
jakarta.servlet-api-6.0.0.jar
jakarta.servlet.jsp-api-3.1.1.jar
jakarta.servlet.jsp.jstl-3.0.1.jar
lucene-core-9.9.0.jar
lucene-queryparser-9.9.0.jar
lucene-analysis-common-9.9.0.jar
lucene-highlighter-9.9.0.jar
commons-fileupload-1.5.jar
commons-io-2.15.1.jar
encoder-1.2.3.jar
encoder-jsp-1.2.3.jar
```

### 第三步：创建数据库

1. 打开 MySQL 客户端（如 MySQL Workbench、Navicat、或命令行 `mysql -u root -p`）
2. 执行以下命令：

```sql
CREATE DATABASE lostfound CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE lostfound;
```

3. 打开 `src/main/resources/schema.sql`，复制全部 SQL 内容，在 MySQL 中执行。

### 第四步：配置数据库连接

打开 `src/main/resources/db.properties`，修改为你的数据库信息：

```
db.url=jdbc:mysql://localhost:3306/lostfound?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.user=root
db.password=你的密码
```

### 第五步：编译 Java 代码

打开命令行，进入项目根目录（`campus-lost-found`），执行以下命令：

```bash
# Windows 命令
javac -d src/main/webapp/WEB-INF/classes -cp "src/main/webapp/WEB-INF/lib/*" src/main/java/com/lostfound/model/*.java src/main/java/com/lostfound/dao/*.java src/main/java/com/lostfound/servlet/*.java src/main/java/com/lostfound/filter/*.java src/main/java/com/lostfound/util/*.java

# 同时复制资源文件到 classes 目录
xcopy src\main\resources\* src\main\webapp\WEB-INF\classes\ /s /i

# Linux / Mac 命令
javac -d src/main/webapp/WEB-INF/classes -cp "src/main/webapp/WEB-INF/lib/*" src/main/java/com/lostfound/model/*.java src/main/java/com/lostfound/dao/*.java src/main/java/com/lostfound/servlet/*.java src/main/java/com/lostfound/filter/*.java src/main/java/com/lostfound/util/*.java

# 复制资源文件
cp -r src/main/resources/* src/main/webapp/WEB-INF/classes/
```

如果编译成功，你会看到 `src/main/webapp/WEB-INF/classes/` 目录下生成了 `.class` 文件。

### 第六步：部署到 Tomcat

1. 找到 Tomcat 安装目录（如 `C:\apache-tomcat-10.1.28`）
2. 进入 `webapps/` 目录
3. 将项目文件夹重命名为 `lostfound`，复制到 `webapps/lostfound/`
4. 最终 Tomcat 目录结构如下：

```
apache-tomcat-10.1.28/
├── webapps/
│   └── lostfound/                     ← 你的项目
│       ├── WEB-INF/
│       │   ├── web.xml
│       │   ├── jsp/                   ← JSP 页面
│       │   ├── lib/                   ← 12 个 JAR 包
│       │   ├── classes/               ← 编译后的 .class 文件
│       │   └── uploads/               ← 上传图片存储目录
│       └── css/style.css
```

### 第七步：启动 Tomcat

1. 进入 Tomcat 的 `bin/` 目录
2. 启动 Tomcat：
   - Windows: 双击 `startup.bat`
   - Linux/Mac: 运行 `./startup.sh`
3. 打开浏览器，访问 `http://localhost:8080/lostfound/login`

### 首次登录

- 先点击注册，注册一个账号（角色选 `admin` 即管理员）
- 然后用注册的账号密码登录

## 默认页面

- `/login` - 登录
- `/register` - 注册
- `/dashboard` - 首页仪表盘
- `/lost/list` - 失物列表
- `/found/list` - 招领列表
- `/search` - 搜索
- `/match` - 智能匹配
- `/claim/list` - 认领申请
