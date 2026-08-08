# NavCampus 智慧校园综合导航平台

> 沈阳工业大学省级大学生创新创业训练计划项目  
> 智慧校园 3D 导航 · 自习室预约 · 课表管理 · 收支记录 · A\* 路径规划

## 项目简介

NavCampus 是一套基于 Spring Boot 3.2.1 + Vue 3 + Three.js 的智慧校园综合管理平台。项目前身为记账系统，现已扩展为集 3D 校园导航、自习室预约、课表管理、天气查询、通知系统、收支管理、**A\* 路径规划**于一体的综合平台。

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
| 3D 引擎 | Three.js | 0.160.0 |
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
└── navcampus-admin     启动模块 (NavCampusApplication, DataInitializer, StartupRunner, SpaController)
```

**依赖链 (单向无环)**: `admin -> campus/finance -> system -> framework -> common`

## 核心功能

### 校园 3D 导航
- Three.js 渲染沈阳工业大学完整 3D 校园模型（FBX 模型 + 纹理贴图）
- 41 个地标建筑（教学楼、宿舍、食堂、校门等），点击交互 + 信息面板
- 天气模拟（晴/多云/雨/雷电）、昼夜交替、云层动画
- CSS2DRenderer 标签渲染、建筑高亮选中
- 环境控制面板（时间滑块、天气切换）

### A\* 路径规划（工程化实现）
- **路网数据**: 130 个节点（41 地标 + 89 路径节点）、122 条边（路径节点 y=15 悬浮渲染，地标节点 y=6~11.5 对应建筑高度）
- **算法核心**: 双精度浮点 A\* 算法，欧几里得距离启发函数，优先队列实现
- **难度权重**: 边权重 = `距离 × (1 + difficulty × 0.1)`，支持地形通行难度
- **路径简化**: 自动移除共线冗余节点（偏转角 <5°），保留转向点
- **转向导航指令**: 8 种方向枚举（直行/稍转/左转/右转/急转/掉头/到达）+ 中文指令文本
- **预计步行时间**: 按 1.4 m/s 校园平均步速估算
- **最近节点查找**: 支持从任意坐标发起导航
- **路网内存缓存**: `@PostConstruct` 启动时预加载，避免每次请求查询数据库
- **前端可视化**: 路网节点/边渲染、路径高亮（TubeGeometry 管线）、起终点标记球

### 自习室预约
- 座位查询、预约、签到、取消
- 预约冲突检测（时间段重叠校验）
- 自动签到提醒、失约自动取消
- 管理员座位状态管理

### 课表与考试管理
- 按星期查询课程、添加/删除课程
- 今日课程 / 本周课程 / 考试安排管理
- 课程图层在 3D 地图上展示

### 用户系统
- JWT 认证（登录/注册/Token 刷新/登出吊销）
- Redis Token 存储（单设备登录）
- 邮箱/手机号验证码
- 个人资料、主题色定制

### 收支管理
- 账户 CRUD（现金/银行卡/微信/支付宝）
- 收支记录（收入/支出/转账）+ 余额联动
- 预算管理 + 80%/100% 阈值提醒
- 分类管理、Excel/CSV 导入
- 月度统计聚合（收支/分类饼图/趋势/结余折线）

### 启动运维
- 专属 ASCII 艺术 Logo 启动横幅
- 各模块 Bean 加载状态检查
- MySQL / Redis / 路网缓存连接状态检查
- Controller 数量统计、启动耗时报告
- DataInitializer 初始化日志（@Slf4j 规范化输出）

### 前端布局
- **顶部导航栏**: 品牌 Logo + NavCampus 名称 + 副标题，点击返回校园首页
- **用户下拉菜单**: 头像（用户名首字母）+ 用户名 + 下拉菜单（个人资料/系统设置/偏好设置/退出登录）
- **主题色切换**: Element Plus CSS 变量动态注入，支持自定义颜色 + 色阶生成
- **动态侧边栏**: 根据页面类型自动切换（场景工具箱/财务菜单/学习菜单），菜单项选中高亮
- **登录/注册页**: 本地背景图片 + 毛玻璃卡片 + 渐变遮罩 + 品牌 Logo
- **3D 导航面板**: 左上角悬浮面板（路网开关 + 起终点选择 + A* 规划 + 转向指令列表）
- **环境控制面板**: 右上角悬浮面板（时间滑块 + 天气切换）
- **全局样式**: style.css 基础重置 + ui.css 主题配色 + 滚动条美化 + 响应式适配

## API 文档

启动后端后访问 Swagger UI: `http://localhost:8080/swagger-ui.html`

| 模块 | 基础路径 | 说明 |
|------|---------|------|
| 认证 | `/api/auth` | 登录、注册、Token 刷新、登出、用户信息 |
| 导航 | `/api/navigation` | 路网数据、A\* 路径规划、最近节点查找、缓存刷新 |
| 自习室 | `/api/study-room` | 座位查询、预约、签到、反馈 |
| 课表 | `/api/timetable` | 课程与考试管理（今日/本周/按星期） |
| 通知 | `/api/notifications` | 通知查询与管理 |
| 收支 | `/api/records` | 记录 CRUD |
| 账户 | `/api/accounts` | 账户 CRUD |
| 预算 | `/api/budgets` | 预算创建与查询 |
| 分类 | `/api/categories` | 分类 CRUD |
| 统计 | `/api/stats` | 月度统计聚合 |
| 管理 | `/api/admin` | 用户管理、日志查看 (ADMIN) |
| 教师 | `/api/teachers` | 教师信息查询 |
| 导入 | `/api/import` | Excel/CSV 批量导入 |

### 导航 API 详细说明

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/navigation/network` | GET | 获取完整路网数据（节点+边），用于前端可视化 |
| `/api/navigation/path` | GET | A\* 最短路径规划，返回路径节点、总距离、预计时间、转向指令 |
| `/api/navigation/nearest-node` | GET | 根据 x/z 坐标查找最近路径节点 |
| `/api/navigation/cache/refresh` | POST | 手动刷新路网内存缓存 |

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.9+
- Node.js 18+ (LTS)
- MySQL 8.0+
- Redis 6+

### 后端启动

#### 1. 配置环境变量

`application.yml` 使用环境变量引用所有敏感信息，不包含硬编码密码：

```bash
# 必需的环境变量
export DB_HOST=your_mysql_host
export DB_PORT=3306
export DB_NAME=navcampus
export DB_USER=your_mysql_user
export DB_PASSWORD=your_mysql_password
export REDIS_HOST=your_redis_host
export REDIS_PORT=6379
export REDIS_PASSWORD=your_redis_password
export JWT_SECRET=your_jwt_secret_at_least_32_bytes
```

或使用本地配置文件（已排除 Git 跟踪）：

```bash
# 复制模板
cp backend/navcampus-admin/src/main/resources/application-local.yml.example \
   backend/navcampus-admin/src/main/resources/application-local.yml
# 编辑 application-local.yml 填入实际连接信息
```

#### 2. 编译与启动

```bash
cd backend
mvn clean compile                          # 编译
mvn spring-boot:run -pl navcampus-admin    # 启动（默认 dev profile）
```

#### 3. Profile 说明

| Profile | 配置文件 | 说明 |
|---------|---------|------|
| dev (默认) | `application-dev.yml` | DEBUG 日志、SQL 输出、Druid 连接池缩减 |
| prod | `application-prod.yml` | INFO 日志、关闭 SQL 输出 |
| local | `application-local.yml` | 本地开发，含真实密码（不提交 Git） |

切换 Profile: `--spring.profiles.active=prod` 或环境变量 `SPRING_PROFILES_ACTIVE=prod`

### 前端启动

```bash
cd frontend
npm install                # 安装依赖
npm run dev                # 开发模式 (http://localhost:5173)
npm run build              # 生产构建
```

### 数据库

- **原始结构**: `docs/navcampus.sql`（MySQL 5.7 语法，含整数类型显示宽度）
- **优化结构**: `docs/navcampus_optimized.sql`（MySQL 8.4，移除显示宽度、添加审计字段、COLLATE 规范）
- **路网数据**: `docs/campus_road_network.sql`（仅含表头注释与 ID 规划，实际 130 节点 + 122 边数据在运行库中）
- **连接配置**: `docs/数据库连接信息.md`（MySQL 外网 mysql.mbfsr.com:23306，Redis 内网 192.168.3.102:6379）
- 路网节点 ID 规划:
  - `1-41`: 地标节点（对应 campus_landmark，y=6~11.5 对应建筑高度）
  - `100-153`: 南区路径节点 (p01-p54，y=15 悬浮渲染)
  - `200-234`: 北区路径节点 (b01-b35，y=15 悬浮渲染)

## 代码规范

- **分层架构**: Controller -> Service -> Mapper，Controller 不写业务逻辑
- **VO/DTO 分层**: Controller 返回 VO，不直接返回 Entity
- **参数校验**: JSR-380 注解 (@NotBlank/@NotNull/@Size/@Positive)
- **统一返回**: `R<T>` 包装所有接口响应
- **全局异常**: `GlobalExceptionHandler` 统一捕获异常
- **权限控制**: `@PreAuthorize` 注解
- **API 文档**: `@Tag`/`@Operation` OpenAPI 注解
- **敏感信息**: 密码/密钥通过环境变量注入，禁止硬编码（`.gitignore` 排除本地配置）

## 项目文档

| 文档 | 说明 |
|------|------|
| `docs/AI-DEVELOPMENT-GUIDELINES.md` | AI 开发约束规范 |
| `docs/项目缺陷分析与优化方案.md` | 架构缺陷分析 + 重构路线图 |
| `docs/智慧校园深化方案-2027届秋招冲大厂-20260807.md` | 后续开发规划 |
| `docs/数据库连接信息.md` | MySQL / Redis 连接配置（PVE 服务器） |
| `docs/前端样式风格总结.md` | 前端 UI 设计规范与样式总结 |
| `docs/navcampus.sql` | 原始数据库表结构（MySQL 5.7） |
| `docs/navcampus_optimized.sql` | 优化后数据库表结构（MySQL 8.4，含审计字段） |
| `docs/campus_road_network.sql` | 路网数据 SQL（仅表头注释，实际数据在运行库中） |
| `docs/index.html` | 原始 3D 校园模型（含路径点坐标与邻居连接） |

## License

本项目为沈阳工业大学大学生创新创业训练计划项目。
