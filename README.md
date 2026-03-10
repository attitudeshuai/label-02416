# 创新类的图书管理系统

一个现代化的创新类图书管理系统，采用前后端分离架构，提供高端、大气、时尚的用户界面。

## How to Run

### Docker 启动（推荐）

```bash
# 启动所有服务（无需额外配置，开箱即用）
docker-compose up --build -d

# 查看运行状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

### 本地启动

> 环境要求：JDK 11+（[下载地址](https://adoptium.net/)）、Node.js 14+、Docker（仅用于 MySQL）

#### 1. 启动数据库

本地启动 MySQL 容器，映射到宿主机 3306 端口（与后端默认配置一致）：

```bash
docker run -d \
  --name library-mysql \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -e MYSQL_DATABASE=library_db \
  -e MYSQL_CHARACTER_SET_SERVER=utf8mb4 \
  -e MYSQL_COLLATION_SERVER=utf8mb4_unicode_ci \
  -p 3306:3306 \
  -v $(pwd)/backend/src/main/resources/db/init.sql:/docker-entrypoint-initdb.d/init.sql:ro \
  mysql:8.0 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci --default-authentication-plugin=mysql_native_password
```

> 挂载 `init.sql` 会自动建表并插入测试数据（管理员/普通用户账号及示例图书）。
> 注意：`docker-compose` 方式将 MySQL 映射到宿主机 3416 端口，而本地启动方式映射到 3306 端口。两种方式不要混用，否则后端会连不上数据库。

#### 2. 启动后端
```bash
cd backend

# 数据库密码已在 application.yml 中默认配置为 123456，与上方 MySQL 容器一致
# JWT Secret 已有默认值，可直接启动；如需自定义可通过环境变量覆盖：
# export JWT_SECRET=your_secret_key
# export SPRING_DATASOURCE_PASSWORD=your_password

# 使用 Maven Wrapper，无需本地安装 Maven（首次运行会自动下载）
./mvnw clean package -DskipTests
java -jar target/library-backend-1.0.0.jar
```

#### 3. 启动前端
```bash
cd frontend
npm install
npm run serve
```

## Services

| 服务 | Docker Compose | 本地启动 | 说明 |
|------|---------------|---------|------|
| 前端 | http://localhost:8081 | http://localhost:8081 | Vue.js 前端应用 |
| 后端 | http://localhost:8416 | http://localhost:8416 | Spring Boot API |
| 数据库 | localhost:3416 | localhost:3306 | MySQL 数据库 |

## 测试账号

| 角色 | 用户名 | 密码 | 权限说明 |
|------|--------|------|----------|
| 管理员 | admin | admin123 | 图书管理、分类管理、查看所有借阅记录、系统统计 |
| 普通用户 | user | user123 | 浏览图书、借阅归还、查看个人借阅记录 |

## 题目内容

1. 实验名称   
Spring Boot2整合Mybatis框架 
2.实验目的 
1）掌握Spring Boot2集成Mybatis框架方法 
2）掌握Spring Boot2简单实践应用 
3.实验内容 
图书信息管理子系统，功能包括：1）用户注册、登录功能；2）管理员用户：图书信息的增、删、改、查，文件上传；普通用户：图书信息查询（单条件查询、多条件查询）。 
要求：1）后台采用数据库存储技术；2）前台采用视图技术；3）所有类、变量命名遵循课上要求的命名规则；4）U+平台按时提交实验报告与软件作品源代码。 

---

## ✨ 项目特色

- 🎨 高端时尚的UI设计，参考Apple、Stripe等顶级网站风格
- 🌈 动态渐变背景和流畅的动画效果
- 📱 响应式布局，支持多端访问
- 🔐 完整的用户认证系统（JWT Token + BCrypt密码加密 + 拦截器鉴权）
- 🛡️ 管理员/普通用户权限控制（接口级别 + 前端菜单级别）
- � 数据可视化统计图表（ECharts））
- 🐳 Docker一键部署
- ✅ 单元测试覆盖（18个测试用例全部通过）

## 📚 功能模块

### 用户功能
- ✅ 用户注册与登录
- ✅ 图书浏览与搜索（综合搜索 / 精确搜索，按分类筛选）
- ✅ 图书借阅与归还（身份从JWT自动提取，防伪造）
- ✅ 个人借阅记录查看

### 管理员功能
- ✅ 图书信息管理（增删改查）
- ✅ 分类管理（增删改查，删除时校验关联图书）
- ✅ 图书封面上传
- ✅ 所有借阅记录管理
- ✅ 系统数据统计仪表盘（图书总数、注册用户、借阅中数量、分类借阅统计图表）

### 安全特性
- ✅ JWT 拦截器统一鉴权，未登录请求返回 401
- ✅ 管理员操作接口级别权限校验，越权返回 403
- ✅ 借阅/归还接口从 token 提取用户身份，防止伪造
- ✅ 文件上传/下载路径穿越防护（normalize + startsWith 校验）
- ✅ 普通用户仪表盘不返回注册用户数量

## 🛠 技术栈

### 后端技术
| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.18 | 后端框架 |
| MyBatis | 2.3.1 | ORM框架 |
| MySQL | 8.0 | 数据库 |
| JWT (auth0) | 4.4.0 | 身份认证 |
| BCrypt | - | 密码加密 |
| Maven | - | 项目构建 |

### 前端技术
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue.js | 3.3.8 | 前端框架 |
| Vue Router | 4.2.5 | 路由管理 |
| Element Plus | 2.4.2 | UI组件库 |
| ECharts | 5.4.3 | 图表库 |
| Axios | 1.6.0 | HTTP客户端 |

### 部署技术
| 技术 | 说明 |
|------|------|
| Docker | 容器化 |
| Docker Compose | 容器编排 |
| Nginx | 前端服务器/反向代理 |

## 📁 项目结构

```
├── backend/                          # 后端项目
│   ├── src/main/java/com/library/
│   │   ├── controller/               # 控制器层
│   │   │   ├── AuthController        # 认证（登录/注册）
│   │   │   ├── BookController        # 图书管理
│   │   │   ├── BorrowController      # 借阅管理
│   │   │   ├── CategoryController    # 分类管理
│   │   │   ├── DashboardController   # 仪表盘统计
│   │   │   └── FileController        # 文件上传
│   │   ├── service/                  # 服务层
│   │   ├── mapper/                   # 数据访问层
│   │   ├── entity/                   # 实体类
│   │   ├── dto/                      # 数据传输对象
│   │   ├── common/                   # 通用类（Result、PageResult、异常处理）
│   │   ├── config/                   # 配置类（WebConfig、JwtInterceptor、SecurityConfig）
│   │   └── util/                     # 工具类（JwtUtil）
│   └── src/main/resources/
│       ├── application.yml           # 应用配置
│       ├── mapper/                   # MyBatis XML映射
│       └── db/init.sql               # 数据库初始化脚本
│
├── frontend/                         # 前端项目
│   ├── src/
│   │   ├── views/              
│   │   │   ├── Login.vue             # 登录页
│   │   │   ├── Register.vue          # 注册页
│   │   │   ├── Home.vue              # 主布局（侧边栏+顶栏）
│   │   │   ├── Dashboard.vue         # 数据概览
│   │   │   ├── Books.vue             # 图书管理/列表
│   │   │   ├── Categories.vue        # 分类管理（管理员）
│   │   │   └── Borrow.vue            # 借阅记录
│   │   ├── api/                      # API接口封装
│   │   └── router/                   # 路由配置
│   └── nginx.conf                    # Nginx配置
│
├── docker-compose.yml                # Docker编排配置
└── README.md                         # 项目说明文档
```

## 📊 数据库设计

### 用户表 (sys_user)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 用户ID（主键） |
| username | VARCHAR(50) | 用户名（唯一） |
| password | VARCHAR(100) | 密码（BCrypt加密） |
| nickname | VARCHAR(50) | 昵称 |
| role | TINYINT | 角色：0-普通用户，1-管理员 |
| status | TINYINT | 状态：0-禁用，1-启用 |

### 图书表 (book)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 图书ID（主键） |
| isbn | VARCHAR(20) | ISBN编号 |
| title | VARCHAR(200) | 书名 |
| author | VARCHAR(100) | 作者 |
| category | VARCHAR(50) | 分类 |
| price | DECIMAL(10,2) | 价格 |
| stock | INT | 库存数量 |
| cover_image | VARCHAR(255) | 封面图片URL |
| status | TINYINT | 状态：0-下架，1-上架 |

### 分类表 (category)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 分类ID（主键） |
| name | VARCHAR(50) | 分类名称（唯一） |
| description | VARCHAR(200) | 分类描述 |
| sort | INT | 排序号 |

### 借阅记录表 (borrow_record)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 记录ID（主键） |
| user_id | BIGINT | 用户ID |
| book_id | BIGINT | 图书ID |
| borrow_date | DATETIME | 借阅日期 |
| due_date | DATETIME | 应还日期 |
| return_date | DATETIME | 实际归还日期 |
| status | TINYINT | 状态：0-借阅中，1-已归还，2-已逾期 |

## 🔌 API接口

> 除认证接口和文件下载外，所有接口需在请求头携带 `Authorization: Bearer <token>`

### 认证接口
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | /api/auth/login | 用户登录 | 公开 |
| POST | /api/auth/register | 用户注册 | 公开 |

### 图书接口
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/books | 分页查询图书列表 | 登录用户 |
| GET | /api/books/{id} | 获取图书详情 | 登录用户 |
| GET | /api/books/categories | 获取所有分类（从图书表） | 登录用户 |
| GET | /api/books/latest | 获取最新上架图书 | 登录用户 |
| POST | /api/books | 新增图书 | 管理员 |
| PUT | /api/books/{id} | 更新图书 | 管理员 |
| DELETE | /api/books/{id} | 删除图书 | 管理员 |

### 分类接口
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/categories | 获取所有分类 | 登录用户 |
| POST | /api/categories | 新增分类 | 管理员 |
| PUT | /api/categories/{id} | 更新分类 | 管理员 |
| DELETE | /api/categories/{id} | 删除分类 | 管理员 |

### 借阅接口
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | /api/borrow/borrow?bookId={id} | 借阅图书（userId从token提取） | 登录用户 |
| POST | /api/borrow/return?bookId={id} | 归还图书（userId从token提取） | 登录用户 |
| GET | /api/borrow/my | 获取当前用户借阅记录 | 登录用户 |
| GET | /api/borrow/all | 获取所有借阅记录 | 管理员 |
| GET | /api/borrow/stats/category | 获取分类借阅统计 | 登录用户 |

### 仪表盘接口
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/dashboard/stats | 获取统计数据（管理员额外返回用户数） | 登录用户 |

### 文件接口
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | /api/upload | 上传文件 | 管理员 |
| GET | /api/files/{filename} | 获取文件 | 公开 |

## 📝 运行单元测试

```bash
# 本地运行（无需安装 Maven）
cd backend
./mvnw test

# Docker运行
docker run --rm -v "$(pwd)/backend":/app -w /app maven:3.8-eclipse-temurin-11 mvn test -B
```

测试覆盖：
- JwtUtilTest（6个用例）：token 生成、验证、解析
- UserServiceTest（5个用例）：登录成功/失败、注册成功/重复
- BookServiceTest（7个用例）：增删改查、分类查询、删除校验

## 📄 许可证

MIT License
