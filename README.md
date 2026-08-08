# NavCampus 智慧校园综合导航平台

> 沈阳工业大学省级大学生创新创业训练计划项目  
> 智慧校园 3D 导航 · 自习室预约 · 课表管理 · 收支记录 · 路径规划

## 项目简介

NavCampus 是一套基于 Spring Boot 3.2.1 + Vue 3 + Three.js 的智慧校园综合管理平台。项目前身 为记账系统，现已扩展为集 3D 校园导航、自习室预约、课表管理、天气查询、通知系统、收支管理、**A\* 路径规划**于一体的综合平台。

## 技术栈

| 层次 | 技术 | 版本 |
|------|------|------|
| JDK | Java | 17 |
| 后端框架 | Spring Boot | 3.2.1 |
| 安全框架 | Spring Security | 6.x |
| ORM | MyBatis-Plus | 3.5.7 |
| 连接池 | Druid | 1.2.20 |
| 缓存 | Redis (Lettuce) | 7.x |
| 数据库 | MySQL | 8.4 |
| API 文档 | SpringDoc OpenAPI | 2.3.0 |
| 前端框架 | Vue | 3.4.21 |
| UI 组件 | Element Plus | 2.7.8 |
| 3D 引擎 | Three.js | - |
| 图表 | ECharts | 5.5.0 |
| 构建工具 | Maven (后端) / Vite (前端) | 3.9+ / 5.0+ |

## 多模块架构

```
navcampus (parent pom)
├── navcampus-common    通用工具 (R<T>, PageResult, BaseEntity, BusinessException)
├── navcampus-framework 核心框架 (Security, JWT, Redis, MyBatis-Plus, 异常处理)
├── navcampus-system    系统管理 (User, Notification, SystemLog, VerificationCode, Auth)
├── navcampus-campus    校园业务 (Seat, Reservation, Course, Exam, Navigation, Teacher)
├── navcampus-finance   财务管理 (Account, Record, Budget, Category, Import, Stats)
└── navcampus-admin     启动模块 (NavCampusApplication, DataInitializer, SpaController)
```

**依赖链 (单向无环)**: `admin → campus/finance → system → framework → common`

## 核心功能

### 校园 3D 导航
- Three.js 渲染沈阳工业大学完整 3D 校园模型
- 41 个地标建筑（教学楼、宿舍、食堂、校门等）
- 建筑点击交互、信息面板、标签显示
- 天气模拟、昼夜交替、云层动画

### A\* 路径规划
- **路网数据**: 130 个节点（41 地标 + 89 路径节点）、130 条边
- **A\* 算法**: 基于欧几里得距离启发函数的最短路径算法
- **前端可视化**: 路网节点/边渲染、路径高亮、起终点标记
- **API 接口**: `GET /api/navigation/network` (路网数据), `GET /api/navigation/path` (路径规划)
- 路网数据来源于 3D 模型 HTML 中的路径点坐标，已转换为数据库存储

### 自习室预约
- 座位查询、预约、签到、取消
- 预约冲突检测（时间段重叠校验）
- 自动签到提醒、失约自动取消
- 管理员座位状态管理

### 课表与考试管理
- 按星期查询课程、添加/删除课程
- 考试安排管理
- 课程图层在 3D 地图上展示

### 用户系统
- JWT 认证（登录/注册/Token 刷新/吊销）
- Redis Token 存储（单设备登录）
- 邮箱/手机号验证码
- 个人资料、主题色定制

### 收支管理
- 账户 CRUD（现金/银行卡/微信/支付宝）
- 收支记录（收入/支出/转账）+ 余额联动
- 预算管理 + 80%/100% 阈值提醒
- 分类管理、Excel/CSV 导入
- 月度统计聚合（收支/分类饼图/趋势/结余折线）

## API 文档

启动后端后访问 Swagger UI: `http://localhost:8080/swagger-ui.html`

| 模块 | 基础路径 | 说明 |
|------|---------|------|
| 认证 | `/api/auth` | 登录、注册、Token 刷新、用户信息 |
| 导航 | `/api/navigation` | 路网数据、A* 路径规划 |
| 自习室 | `/api/study-room` | 座位查询、预约、签到、反馈 |
| 课表 | `/api/timetable` | 课程与考试管理 |
| 通知 | `/api/notifications` | 通知查询与管理 |
| 收支 | `/api/records` | 记录 CRUD |
| 账户 | `/api/accounts` | 账户 CRUD |
| 预算 | `/api/budgets` | 预算创建与查询 |
| 分类 | `/api/categories` | 分类 CRUD |
| 统计 | `/api/stats` | 月度统计聚合 |
| 管理 | `/api/admin` | 用户管理、日志查看 (ADMIN) |
| 教师 | `/api/teachers` | 教师信息查询 |
| 导入 | `/api/import` | Excel/CSV 批量导入 |

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.9+
- Node.js 18+ (LTS)
- MySQL 8.0+
- Redis 6+

### 后端启动
```bash
cd backend
mvn clean compile          # 编译
mvn spring-boot:run -pl navcampus-admin  # 启动
```

配置文件: `backend/navcampus-admin/src/main/resources/application.yml`
- 开发环境: `application-dev.yml` (DEBUG 日志, SQL 输出)
- 生产环境: `application-prod.yml` (INFO 日志, 关闭 SQL 输出)
- 环境切换: `SPRING_PROFILES_ACTIVE=dev|prod`

### 前端启动
```bash
cd frontend
npm install
npm run dev                # 开发模式
npm run build              # 生产构建
```

### 数据库
- SQL 文件: `docs/navcampus_optimized.sql` (表结构), `docs/campus_road_network.sql` (路网数据)
- 路网节点 ID 规划:
  - `1-41`: 地标节点（对应 campus_landmark）
  - `100-153`: 南区路径节点 (p01-p54)
  - `200-234`: 北区路径节点 (b01-b35)

## 代码规范

- **分层架构**: Controller → Service → Mapper，Controller 不写业务逻辑
- **VO/DTO 分层**: Controller 返回 VO，不直接返回 Entity
- **参数校验**: JSR-380 注解 (@NotBlank/@NotNull/@Size/@Positive)
- **统一返回**: `R<T>` 包装所有接口响应
- **全局异常**: `GlobalExceptionHandler` 统一捕获异常
- **权限控制**: `@PreAuthorize` 注解
- **API 文档**: `@Tag`/`@Operation` OpenAPI 注解

## 项目文档

| 文档 | 说明 |
|------|------|
| `docs/AI-DEVELOPMENT-GUIDELINES.md` | AI 开发约束规范 |
| `docs/项目缺陷分析与优化方案.md` | 架构缺陷分析 + 重构路线图 |
| `docs/智慧校园深化方案-2027届秋招冲大厂-20260807.md` | 后续开发规划 |
| `docs/数据库连接信息.md` | MySQL/Redis 连接信息 |
| `docs/navcampus_optimized.sql` | 优化后数据库表结构 |
| `docs/campus_road_network.sql` | 路网数据 SQL |
| `docs/index.html` | 原始 3D 校园模型 (含路径点坐标) |

## License

本项目为沈阳工业大学大学生创新创业训练计划项目。
