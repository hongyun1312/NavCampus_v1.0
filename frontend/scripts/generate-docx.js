import { Document, Packer, Paragraph, HeadingLevel, TextRun } from "docx"
import fs from "fs"
import path from "path"

const title = "创新记账系统 开发文档"
const dateStr = new Date().toISOString().slice(0, 10)

function h(text, level = HeadingLevel.HEADING_2) {
  return new Paragraph({ text, heading: level })
}
function p(text) {
  return new Paragraph({ children: [new TextRun(text)] })
}
function bullet(text) {
  return new Paragraph({ text, bullet: { level: 0 } })
}

const doc = new Document({
  sections: [
    {
      properties: {},
      children: [
        h(title, HeadingLevel.TITLE),
        p(`版本：0.0.1    日期：${dateStr}`),

        h("一、项目概览"),
        p("本系统提供记账、分类、账户、预算、统计报表、批量导入与通知等功能，采用前后端分离架构。"),

        h("二、技术栈"),
        bullet("后端：Spring Boot 3.2.1、Spring Security、JPA、JWT、Lombok 1.18.32、MySQL"),
        bullet("前端：Vue 3 + Vite、Element Plus、ECharts、Axios、Vue Router（Hash）"),

        h("三、目录结构"),
        bullet("backend：后端工程，配置与 REST API"),
        bullet("frontend：前端工程，页面与路由"),

        h("四、运行与配置"),
        bullet("后端：进入 backend，执行 mvn spring-boot:run；Swagger：http://localhost:8080/swagger-ui/index.html"),
        bullet("数据库：application.yml 配置 MySQL 连接，首次启动自动建表"),
        bullet("前端：进入 frontend，执行 npm install、npm run dev，浏览器访问 http://localhost:5173"),
        bullet("前端环境：.env 中配置 VITE_API_BASE=http://localhost:8080"),

        h("五、后端模块"),
        bullet("SecurityConfig：安全配置与认证提供者"),
        bullet("JwtAuthenticationFilter：JWT 解析与上下文注入"),
        bullet("AuthController：登录/注册/刷新令牌"),
        bullet("RecordController：记录查询/新增/删除与时间范围接口"),
        bullet("AccountService、NotificationService：业务逻辑"),
        bullet("DTO：RecordRequest、JwtResponse"),
        bullet("配置：application.yml（端口、数据源、JPA 与 JWT）"),

        h("六、前端模块与页面"),
        bullet("App.vue：导航与主题色切换，按钮使用 router-link 保证可靠导航"),
        bullet("路由：Hash 模式，未登录跳转登录页"),
        bullet("Login、Register：认证与用户信息保存"),
        bullet("Records：记录查询与新增，支持收入/支出/转账、时间到分钟、备注文本域"),
        bullet("Categories：分类增删改，含“去记账”快速入口"),
        bullet("Accounts：账户管理，类型与余额维护"),
        bullet("Budgets：预算设置与查询，阈值提醒通过通知页展示"),
        bullet("Dashboard：饼图/柱状图/折线图统计，并支持导出 PNG/PDF"),
        bullet("Import：Excel/CSV 上传导入"),
        bullet("Notifications：站内通知列表"),
        bullet("Preferences：主题与图表显示偏好"),

        h("七、主要接口"),
        bullet("认证：POST /api/auth/signin、/api/auth/signup、/api/auth/refresh"),
        bullet("记录：GET /api/records、GET /api/records/range?start&end、POST /api/records、DELETE /api/records/{id}"),
        bullet("账户：GET/POST/PUT/DELETE /api/accounts"),
        bullet("分类：GET/POST/PUT/DELETE /api/categories"),
        bullet("预算：GET /api/budgets?period=yyyy-MM、POST /api/budgets"),
        bullet("导入：POST /api/import/excel、POST /api/import/csv"),
        bullet("通知：GET /api/notifications"),

        h("八、数据模型示例"),
        bullet("Account：id、name、type、balance、user、icon、createdAt"),
        bullet("Notification：id、user、title、content、type、readFlag、createdAt"),
        bullet("RecordRequest：amount、type、time、categoryId、accountId、targetAccountId、remark"),

        h("九、安全与认证"),
        bullet("登录后返回 JWT；请求携带 Authorization: Bearer <token>"),
        bullet("401 时尝试刷新令牌并重试一次"),

        h("十、常见问题与排查"),
        bullet("前端编译错误：元素未闭合（如 el-dialog），修复标签闭合"),
        bullet("后端编译错误：JDK 与 Lombok 兼容问题，使用 JDK17 与 Lombok 1.18.32，并启用注解处理"),
        bullet("记录页无法打开：确认路由为 Hash 模式，后端已启动，环境变量正确"),

        h("十一、开发约定"),
        bullet("前后端使用中文注释与清晰命名，避免引入未安装的第三方库"),
        bullet("所有接口错误时前端给出用户友好提示与加载状态"),
      ],
    },
  ],
})

const outPath = path.resolve(process.cwd(), "../开发文档.docx")
Packer.toBuffer(doc).then((buffer) => {
  fs.writeFileSync(outPath, buffer)
  console.log("文档已生成：", outPath)
})

