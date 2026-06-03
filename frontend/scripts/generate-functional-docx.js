import { Document, Packer, Paragraph, HeadingLevel, TextRun } from "docx"
import fs from "fs"
import path from "path"

const title = "创新记账系统 功能文档"
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

        h("一、产品概述"),
        bullet("支持个人记账全流程：分类、账户、记录、预算、统计、导入与通知"),
        bullet("前后端分离，后端提供 REST API 与安全认证，前端提供页面与交互"),

        h("二、用户角色与权限"),
        bullet("普通用户：登录后访问个人数据（记录/分类/账户/预算/通知）"),
        bullet("未登录仅可访问登录与注册页"),
        bullet("接口通过 JWT 鉴权，401 时前端尝试刷新令牌并重试一次"),

        h("三、功能清单（前端）"),
        bullet("导航与主题：顶部导航与主题色切换，偏好本地持久化"),
        bullet("登录与注册：登录保存 token 与用户信息；注册后跳转登录"),
        bullet("记账：列表/筛选（到分钟），新增收入/支出/转账，删除记录，备注文本域"),
        bullet("分类：列表与增删改，类型为收入/支出，含“去记账”入口"),
        bullet("账户：列表与增删改，类型（现金/银行卡/微信/支付宝/其他）、余额与图标"),
        bullet("预算：查询周期预算（yyyy-MM），新增整体或分类预算，提醒通过通知展示"),
        bullet("仪表盘：本月收入/支出/结余；分类饼图、日趋势柱状图、结余折线图；导出 PNG/PDF"),
        bullet("导入：Excel/CSV 上传并导入，展示导入成功条数"),
        bullet("通知：展示预算提醒与站内通知"),
        bullet("偏好：主题颜色与图表显示开关"),

        h("四、功能清单（后端接口）"),
        bullet("认证：POST /api/auth/signin、/api/auth/signup、/api/auth/refresh"),
        bullet("记录：GET /api/records、GET /api/records/range、POST /api/records、DELETE /api/records/{id}"),
        bullet("分类：GET/POST/PUT/DELETE /api/categories"),
        bullet("账户：GET/POST/PUT/DELETE /api/accounts"),
        bullet("预算：GET /api/budgets?period=yyyy-MM、POST /api/budgets"),
        bullet("导入：POST /api/import/excel、POST /api/import/csv"),
        bullet("通知：GET /api/notifications"),

        h("五、数据模型（核心字段）"),
        bullet("记录提交体：amount、type（INCOME/EXPENSE/TRANSFER）、time（yyyy-MM-ddTHH:mm:ss）、categoryId、accountId、targetAccountId（转账）、remark"),
        bullet("账户：name、type（CASH/BANK_CARD/WECHAT/ALIPAY/OTHER）、balance、icon、user、createdAt"),
        bullet("通知：title、content、type（SITE/EMAIL/SMS）、readFlag、createdAt"),
        bullet("认证响应：token、type、id、username、email"),

        h("六、主要业务流程"),
        bullet("登录：输入用户名密码 → 后端认证 → 返回 JWT → 前端持久化并跳转仪表盘"),
        bullet("新增记录：填写信息 → 校验（金额/时间/账户/转账目标）→ 提交 → 列表刷新"),
        bullet("预算提醒：设置预算（TOTAL/CATEGORY）→ 后端统计生成通知 → 通知页展示"),
        bullet("批量导入：上传 Excel/CSV → 后端解析入库 → 返回导入条数并提示"),

        h("七、校验与交互约束"),
        bullet("必填：type、amount>0、time、accountId；TRANSFER 时需 targetAccountId"),
        bullet("时间：使用本地时间字符串（yyyy-MM-ddTHH:mm:ss），范围查询同格式"),
        bullet("交互：所有操作提供 loading 与成功/失败提示；删除前二次确认"),

        h("八、错误处理与重试"),
        bullet("401：自动刷新令牌并重试一次，失败则清空认证状态"),
        bullet("前端编译：保证标签闭合（如 el-dialog），避免 Vite 构建错误"),
        bullet("后端编译：确保 JDK17 与 Lombok1.18.32，启用注解处理"),

        h("九、配置与环境"),
        bullet("后端：端口8080；MySQL 数据源；JPA ddl-auto=update；JWT secret/expiration"),
        bullet("前端：VITE_API_BASE 默认 http://localhost:8080；路由使用 Hash 模式"),

        h("十、非功能性要求"),
        bullet("可维护性：组件职责清晰，统一 http 封装与错误提示，后端分层清晰"),
        bullet("安全性：密码加密存储，接口鉴权，不泄露敏感配置"),

        h("十一、验收标准"),
        bullet("登录成功后页面可访问且数据加载正常"),
        bullet("新增/删除记录正确影响列表与统计"),
        bullet("分类/账户管理后可在记账页选择"),
        bullet("仪表盘三类图表渲染正确并可导出"),
        bullet("导入显示正确条数，通知页能显示提醒"),
      ],
    },
  ],
})

const outPath = path.resolve(process.cwd(), "../功能文档.docx")
Packer.toBuffer(doc).then((buffer) => {
  fs.writeFileSync(outPath, buffer)
  console.log("功能文档已生成：", outPath)
})

