# AI 企业级项目开发约束规范（AI-DEVELOPMENT-GUIDELINES）

> **文件目的**：本文件是 AI 代码助手在参与本项目开发时必须严格遵守的最高准则。
> 所有 AI 生成的代码、配置、SQL、文档必须符合本规范，否则视为不合格产出。
> **适用范围**：后端（Java/Spring Boot）、前端（Vue）、数据库（MySQL/TDengine）、运维（Docker/脚本）全链路。

---

## 第一章：AI 行为总则（铁律·不可违反）

### 1.1 禁止事项

| 编号 | 禁止行为 | 说明 |
|------|---------|------|
| P0-01 | **禁止编造 API/类库** | 不得使用不存在的类、方法、注解；不确定时必须声明并查询 |
| P0-02 | **禁止硬编码敏感信息** | 密码、密钥、Token、连接串禁止写入代码或提交到 Git |
| P0-03 | **禁止破坏现有架构** | 不得随意新增模块、改变分层结构、引入未经评估的依赖 |
| P0-04 | **禁止省略异常处理** | 所有外部调用（DB/HTTP/Redis/MQ）必须有 try-catch 或全局异常兜底 |
| P0-05 | **禁止使用 double/float 做精度计算** | 金额、坐标、测量值等必须使用 BigDecimal |
| P0-06 | **禁止删除或修改他人代码而不说明** | 任何对已有代码的修改必须在提交说明中标注原因 |
| P0-07 | **禁止生成不可运行的代码** | 所有代码必须完整、依赖明确、语法正确、可直接编译运行 |
| P0-08 | **禁止忽略线程安全** | 共享变量必须考虑并发安全，优先使用线程安全集合或加锁 |

### 1.2 必须事项

| 编号 | 必须行为 | 说明 |
|------|---------|------|
| P1-01 | **必须先理解再编码** | 编码前必须阅读相关已有代码，保持风格一致 |
| P1-02 | **必须写注释** | 类注释、方法注释、复杂逻辑注释，中文编写 |
| P1-03 | **必须分层** | Controller 不写业务逻辑，Service 不直接返回前端对象（需转换） |
| P1-04 | **必须校验入参** | Controller 层参数校验，Service 层业务校验，不可遗漏 |
| P1-05 | **必须考虑空值** | 所有对象使用前判空，集合使用前判空，避免 NPE |
| P1-06 | **必须遵循开闭原则** | 新增功能优先扩展而非修改已有代码 |
| P1-07 | **必须处理边界** | 空集合、单条数据、最大值、时间范围非法等边界场景 |
| P1-08 | **必须统一返回格式** | 所有接口返回统一的 AjaxResult / R<T> 结构 |

### 1.3 AI 输出规范

- 输出代码时必须标注语言（```java / ```sql / ```xml 等）
- 每段代码后附简要说明（做了什么、为什么这样做）
- 涉及多文件改动时，按「新建文件 -> 修改文件 -> 删除文件」顺序列出
- 涉及数据库变更时，必须同步输出 SQL 脚本并放入版本管理目录

---

## 第二章：项目架构规范

### 2.1 技术栈基线

| 层次 | 技术 | 版本要求 | 说明 |
|------|------|---------|------|
| JDK | Java | 11+ | 统一 JDK 11 |
| 后端框架 | Spring Boot | 2.5.x | 统一版本，禁止混用 |
| 安全框架 | Spring Security | 5.7.x | 与 Spring Boot 版本对齐 |
| ORM | MyBatis | 3.5.x | 禁止混用 JPA |
| 分页 | PageHelper | 1.4.x | 统一分页方案 |
| 连接池 | Druid | 1.2.x | 含监控能力 |
| 缓存 | Redis | 6.x+ | Lettuce 客户端 |
| 关系数据库 | MySQL | 8.0 | utf8mb4 字符集 |
| 时序数据库 | TDengine | 3.0+ | WebSocket 驱动 |
| 对象存储 | MinIO | - | 文件/图片存储 |
| 工作流 | Warm-Flow | 1.6.x | 轻量工作流引擎 |
| 前端框架 | Vue | 2.6.x | 统一 Vue 2 + Element-UI |
| 图表 | ECharts | 5.x | 数据可视化 |
| 构建工具 | Maven | 3.6+ | 多模块管理 |

### 2.2 Maven 多模块架构

```
项目根（pom）
├── xxx-admin       → 启动模块 + 业务聚合（Web 入口、定时任务、业务 Controller）
├── xxx-framework   → 核心框架（安全、AOP、数据源、WebSocket、异常处理）
├── xxx-system      → 系统管理（用户、角色、菜单、部门、字典、配置）
├── xxx-common      → 通用工具（工具类、注解、常量、异常、中间件封装）
├── xxx-port        → 设备/业务接入端口（按业务域拆分子包）
├── xxx-protocol    → 协议处理引擎（数据格式化、协议适配）
├── xxx-flow        → 工作流适配层
├── xxx-quartz      → 定时任务
├── xxx-generator   → 代码生成器
├── xxx-ui          → 前端工程
└── doc              → 文档与 SQL 版本脚本
    └── sql/         → 按日期命名的 DDL/DML 变更脚本
```

### 2.3 模块依赖规则（严格单向依赖）

```
依赖方向（上层依赖下层，禁止反向依赖）：

  xxx-admin  →  xxx-framework  →  xxx-system  →  xxx-common
  xxx-admin  →  xxx-port       →  xxx-common
  xxx-admin  →  xxx-protocol   →  xxx-common
  xxx-admin  →  xxx-flow       →  xxx-common
  xxx-admin  →  xxx-quartz     →  xxx-common
  xxx-admin  →  xxx-generator  →  xxx-common
```

**规则**：
- `xxx-common` 是最底层模块，**禁止依赖任何业务模块**
- `xxx-framework` 依赖 `xxx-common` + `xxx-system`，**禁止依赖业务模块**
- 业务模块之间**禁止互相依赖**（如 `xxx-port` 不能依赖 `xxx-protocol`）
- 只有 `xxx-admin`（启动模块）可以聚合依赖所有模块
- 所有模块版本统一使用 `${xxx.version}` 变量管理，禁止硬编码版本号

### 2.4 新增模块/子包的决策原则

| 场景 | 决策 |
|------|------|
| 新增一个独立业务域（如"车辆管理"） | 在 `xxx-port` 下新建子包，含 controller/domain/mapper/service 四层 |
| 新增一个通用工具类 | 放入 `xxx-common` 对应子包 |
| 新增一个框架级能力（如新的拦截器） | 放入 `xxx-framework` |
| 新增一个独立的、可复用的引擎 | 评估是否需要新建独立 Maven 模块，需团队评审 |
| 新增一个系统级功能（如"通知中心"） | 放入 `xxx-system` |

---

## 第三章：分层架构规范

### 3.1 Controller 层（控制层）

**职责**：接收请求、参数校验、调用 Service、组装返回结果。**禁止写业务逻辑**。

```java
@RestController
@RequestMapping("/port/slope")
public class AlarmSlopeController extends BaseController {

    @Autowired
    private IAlarmSlopeService alarmSlopeService;

    /**
     * 分页查询边坡告警列表
     */
    @PreAuthorize("@ss.hasPermi('port:slope:list')")
    @GetMapping("/list")
    public TableDataInfo list(AlarmSlope alarmSlope) {
        startPage();  // PageHelper 分页
        List<AlarmSlope> list = alarmSlopeService.selectAlarmSlopeList(alarmSlope);
        return getDataTable(list);
    }

    /**
     * 新增边坡告警
     */
    @PreAuthorize("@ss.hasPermi('port:slope:add')")
    @Log(title = "边坡告警", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody AlarmSlope alarmSlope) {
        return toAjax(alarmSlopeService.insertAlarmSlope(alarmSlope));
    }
}
```

**Controller 层规范**：
- 每个 Controller 必须标注 `@RequestMapping` 一级路径
- 查询用 `@GetMapping`，新增用 `@PostMapping`，修改用 `@PutMapping`，删除用 `@DeleteMapping`
- 必须使用 `@PreAuthorize` 做权限控制
- 写操作必须加 `@Log` 注解记录操作日志
- 参数对象必须使用 `@Validated` 触发校验
- 返回值统一使用 `AjaxResult`（单对象）或 `TableDataInfo`（分页列表）

### 3.2 Service 层（业务层）

**职责**：核心业务逻辑、事务管理、数据编排。

```java
public interface IAlarmSlopeService {
    List<AlarmSlope> selectAlarmSlopeList(AlarmSlope alarmSlope);
    int insertAlarmSlope(AlarmSlope alarmSlope);
    int updateAlarmSlope(AlarmSlope alarmSlope);
    int deleteAlarmSlopeByIds(Long[] ids);
}

@Service
public class AlarmSlopeServiceImpl implements IAlarmSlopeService {

    @Autowired
    private AlarmSlopeMapper alarmSlopeMapper;

    /**
     * 查询边坡告警列表
     * @param alarmSlope 查询条件
     * @return 告警列表
     */
    @Override
    public List<AlarmSlope> selectAlarmSlopeList(AlarmSlope alarmSlope) {
        return alarmSlopeMapper.selectAlarmSlopeList(alarmSlope);
    }

    /**
     * 新增边坡告警（含业务校验）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertAlarmSlope(AlarmSlope alarmSlope) {
        // 业务校验
        if (StringUtils.isEmpty(alarmSlope.getDeviceId())) {
            throw new ServiceException("设备编号不能为空");
        }
        return alarmSlopeMapper.insertAlarmSlope(alarmSlope);
    }
}
```

**Service 层规范**：
- 接口与实现分离，接口前缀 `I`，实现类后缀 `Impl`
- 写操作必须加 `@Transactional(rollbackFor = Exception.class)`
- 业务校验在 Service 层完成，抛出 `ServiceException`
- Service 返回 Domain 对象，**不直接返回 VO**（VO 组装在 Controller 或专门的 Converter 中）
- 一个方法只做一件事，方法行数建议不超过 80 行，超过则拆分

### 3.3 Mapper/DAO 层（数据访问层）

```java
public interface AlarmSlopeMapper {
    List<AlarmSlope> selectAlarmSlopeList(AlarmSlope alarmSlope);
    int insertAlarmSlope(AlarmSlope alarmSlope);
}
```

**Mapper 层规范**：
- Mapper 接口方法名遵循 `select/insert/update/delete` + `EntityName` + `By` + `条件` 命名
- SQL 写在 XML 文件中，**禁止使用注解写复杂 SQL**（简单 SQL 可用注解）
- XML 文件路径：`src/main/resources/mapper/{模块}/{EntityName}Mapper.xml`
- 查询条件使用 `<if>` 动态拼接，禁止拼接 SQL 字符串（防注入）
- 批量操作使用 `<foreach>` 标签

### 3.4 Domain/Entity 层（实体层）

```java
public class AlarmSlope extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 设备编号 */
    @Excel(name = "设备编号")
    private String deviceId;

    /** 告警等级（1低 2中 3高） */
    @Excel(name = "告警等级", readConverterExp = "1=低,2=中,3=高")
    private String alarmLevel;

    // getter/setter 或使用 Lombok
}
```

**Entity 规范**：
- 继承 `BaseEntity`（含 createBy/createTime/updateBy/updateTime/remark）
- 实现 `Serializable`，声明 `serialVersionUID`
- 字段使用 `@Excel` 注解支持导出
- 字段类型：ID 用 Long，金额/测量值用 BigDecimal，时间用 Date，状态用 String

### 3.5 VO/DTO 层（视图对象/数据传输对象）

```java
/**
 * 边坡变形曲线图返回VO（适配ECharts）
 */
public class DeformationCurveVO {
    /** X轴：变形阶段名称列表 */
    private List<String> stages;

    /** Y轴：变形速率数据 */
    private List<BigDecimal> velocityData;

    /** Y轴：切线角数据 */
    private List<BigDecimal> tangentAngleData;
}
```

**VO/DTO 规范**：
- VO 用于前端展示，字段语义清晰，JSON 序列化字段名规范
- DTO 用于服务间传输，与 Domain 解耦
- 禁止 Domain 直接透传给前端（敏感字段泄露风险）
- VO 转换使用专门的 Converter 类或 MapStruct，禁止在 Service 中手动 set

---

## 第四章：包结构规范

### 4.1 标准包结构

```
com.xxx
├── xxx.module                  → 业务模块根包
│   ├── controller/             → 控制器
│   ├── domain/                 → 实体类
│   │   └── vo/                 → 视图对象
│   ├── mapper/                 → MyBatis Mapper 接口
│   ├── service/                → Service 接口
│   │   └── impl/               → Service 实现
│   ├── task/                   → 定时任务（可选）
│   ├── utils/                  → 模块工具类（可选）
│   └── config/                 → 模块配置类（可选）
```

### 4.2 类命名规范

| 类型 | 命名规则 | 示例 |
|------|---------|------|
| Controller | `{业务名}Controller` | `AlarmSlopeController` |
| Service 接口 | `I{业务名}Service` | `IAlarmSlopeService` |
| Service 实现 | `{业务名}ServiceImpl` | `AlarmSlopeServiceImpl` |
| Mapper | `{业务名}Mapper` | `AlarmSlopeMapper` |
| Entity | `{业务名}`（无后缀） | `AlarmSlope` |
| VO | `{业务名}VO` | `DeformationCurveVO` |
| DTO | `{业务名}DTO` | `AlarmSlopeDTO` |
| Config | `{功能名}Config` | `RedisConfig` |
| Constants | `{模块名}Constants` | `CommonConstants` |
| Enum | `{业务名}Enum` | `AlarmLevelEnum` |
| Exception | `{业务名}Exception` | `ServiceException` |
| Utils | `{功能名}Utils` | `StringUtils` |

### 4.3 方法命名规范

| 操作 | 命名规则 | 示例 |
|------|---------|------|
| 查询列表 | `select{Entity}List` | `selectAlarmSlopeList` |
| 查询单条 | `select{Entity}ById` | `selectAlarmSlopeById` |
| 新增 | `insert{Entity}` | `insertAlarmSlope` |
| 修改 | `update{Entity}` | `updateAlarmSlope` |
| 删除 | `delete{Entity}ByIds` | `deleteAlarmSlopeByIds` |
| 批量新增 | `batchInsert{Entity}` | `batchInsertAlarmSlope` |
| 统计 | `count{Entity}` | `countAlarmSlope` |
| 校验 | `check{Field}` | `checkDeviceIdUnique` |
| 导出 | `export{Entity}List` | `exportAlarmSlopeList` |

---

## 第五章：代码规范

### 5.1 通用编码规范

1. **每行代码不超过 120 字符**，超过需换行
2. **方法行数建议不超过 80 行**，类行数建议不超过 500 行
3. **圈复杂度不超过 15**，超过需拆分
4. **一个文件只定义一个类**（内部类除外）
5. **import 严禁通配符** `import xxx.*`，必须明确导入
6. **equals 比较常量在前**：`"1".equals(str)` 而非 `str.equals("1")`
7. **集合判空用工具类**：`StringUtils.isEmpty(list)` 而非 `list.size() == 0`
8. **魔法值必须提取为常量**：禁止代码中出现 `if (status == 1)` 这样的硬编码

### 5.2 注释规范

```java
/**
 * 边坡告警服务实现类
 * <p>
 * 负责边坡监测告警的增删改查、告警等级判定、告警通知推送等核心业务。
 * </p>
 *
 * @author yourname
 * @version 1.0
 * @since 2026-08-07
 */
@Service
public class AlarmSlopeServiceImpl implements IAlarmSlopeService {

    /**
     * 根据设备编号查询最近24小时内的告警记录
     *
     * @param deviceId 设备编号，不可为空
     * @param hours    查询时长（小时），默认24
     * @return 告警列表，按告警时间倒序排列；无数据返回空集合
     * @throws ServiceException 当 deviceId 为空时抛出
     */
    public List<AlarmSlope> selectRecentAlarms(String deviceId, int hours) {
        // 1. 参数校验
        // 2. 构建查询条件
        // 3. 执行查询并返回
    }
}
```

**注释要求**：
- 类注释：作者、版本、创建日期、功能描述
- 方法注释：参数说明、返回值说明、异常说明
- 复杂逻辑：步骤编号注释（// 1. xxx // 2. xxx）
- TODO 注释必须带负责人和日期：`// TODO(zhangsan 2026-08-07): 需要增加缓存`

### 5.3 Lombok 使用规范

```java
// 推荐：实体类使用 @Data
@Data
public class AlarmSlope extends BaseEntity {
    private Long id;
    private String deviceId;
}

// 推荐：构建器模式
@Builder
@Data
public class AlarmSlopeVO {
    private String stage;
    private BigDecimal velocity;
}

// 禁止：在需要序列化的类上滥用 @AllArgsConstructor（可能引起反序列化问题）
```

**Lombok 规则**：
- 实体类优先使用 `@Data`
- 不可变对象使用 `@Builder` + `@Getter`
- 日志使用 `@Slf4j` 而非手动声明 logger
- 构造器注解谨慎使用，确保不影响框架注入

### 5.4 集合使用规范

```java
// 返回空集合而非 null
public List<AlarmSlope> selectList(AlarmSlope query) {
    List<AlarmSlope> list = mapper.selectList(query);
    return list != null ? list : Collections.emptyList();
}

// 遍历时删除使用 Iterator 或 removeIf
list.removeIf(item -> item.getStatus().equals("0"));

// 初始化集合指定容量
List<AlarmSlope> result = new ArrayList<>(list.size());
Map<Long, AlarmSlope> map = new HashMap<>(list.size() * 4 / 3 + 1);
```

---

## 第六章：设计模式使用规范

### 6.1 工厂模式（Factory）

**使用场景**：根据类型/配置创建不同实现，且未来可能扩展新类型。

```java
/**
 * 数据采集器工厂
 * 根据 config.getType() 创建对应协议的采集器实例
 */
public class DataCollectorFactory {
    public static DataCollector createCollector(CollectorConfig config) {
        String type = config.getType().toLowerCase();
        switch (type) {
            case "mqtt":  return new MqttDataCollector((MqttCollectorConfig) config);
            case "http":  return new HttpDataCollector((HttpCollectorConfig) config);
            case "socket": return new SocketDataCollector((SocketCollectorConfig) config);
            default:
                throw new DataCollectorException("不支持的采集器类型: " + type);
        }
    }
}
```

**适用场景**：
- 多协议/多渠道接入（数据采集器、消息推送渠道）
- 多种数据源创建
- 多种导出格式生成

### 6.2 策略模式（Strategy）

**使用场景**：同一行为有多种实现，需要根据条件动态切换，避免大量 if-else。

```java
// 策略接口
public interface QueryStrategy {
    String getGranularity();
    List<Map<String, Object>> query(String deviceId, LocalDateTime start, LocalDateTime end);
}

// 策略实现
@Component("minuteStrategy")
public class MinuteQueryStrategy implements QueryStrategy { ... }

@Component("hourStrategy")
public class HourQueryStrategy implements QueryStrategy { ... }

// 策略上下文（通过 Spring 容器自动注入所有策略）
@Component
public class QueryStrategyContext {
    @Autowired
    private Map<String, QueryStrategy> strategyMap;

    public QueryStrategy getStrategy(String granularity) {
        QueryStrategy strategy = strategyMap.get(granularity + "Strategy");
        if (strategy == null) {
            throw new ServiceException("不支持的时间粒度: " + granularity);
        }
        return strategy;
    }
}
```

**适用场景**：
- 时序数据多粒度查询（分钟/小时/天/周/月/年）
- 不同设备类型的数据处理
- 不同告警等级的通知策略

### 6.3 模板方法模式（Template Method）

**使用场景**：多个类有相同的执行流程，但各步骤实现不同。

```java
public abstract class AbstractDataCollector implements DataCollector {
    // 模板方法：定义采集流程骨架
    public final void collect() {
        initialize();
        start();
        processData();
        stop();
    }

    // 抽象方法：子类实现具体逻辑
    protected abstract void initialize();
    protected abstract void start();
    protected abstract void stop();

    // 通用方法
    protected void processData() { /* 通用数据处理 */ }
}
```

### 6.4 观察者模式 / 事件驱动

**使用场景**：一个状态变化需要通知多个下游处理者。

```java
// Spring 事件机制
@Service
public class AlarmEventPublisher {
    @Autowired
    private ApplicationEventPublisher publisher;

    public void publishAlarm(AlarmSlope alarm) {
        publisher.publishEvent(new AlarmEvent(alarm));
    }
}

// 监听者
@Component
public class AlarmEventListener {
    @EventListener
    @Async  // 异步处理，不阻塞主流程
    public void handleAlarm(AlarmEvent event) {
        // 发送短信、推送APP、记录日志等
    }
}
```

### 6.5 设计模式使用原则

| 原则 | 说明 |
|------|------|
| **不过度设计** | 只有 2 种情况且不会扩展时，直接 if-else 即可，不必上策略模式 |
| **优先组合而非继承** | 能用组合解决的不要用继承 |
| **面向接口编程** | 依赖接口而非具体实现 |
| **单一职责** | 一个类/方法只做一件事 |
| **开闭原则** | 对扩展开放，对修改关闭 |

---

## 第七章：数据库设计规范

### 7.1 MySQL 建表规范

```sql
-- 变形阶段配置表
CREATE TABLE `dock_deformation_stage` (
    `id`              BIGINT(20)   NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    `stage_code`      VARCHAR(20)  NOT NULL                 COMMENT '阶段编码（CM/CV/AC_T2~T5/CP）',
    `stage_name`      VARCHAR(50)  NOT NULL                 COMMENT '阶段名称',
    `tangent_angle_min`  DECIMAL(10,4) DEFAULT NULL         COMMENT '切线角下限（°），NULL表示无下限',
    `tangent_angle_max`  DECIMAL(10,4) DEFAULT NULL         COMMENT '切线角上限（°），NULL表示无上限',
    `velocity_min`    DECIMAL(10,4) DEFAULT NULL            COMMENT '变形速率下限（mm/d）',
    `velocity_max`    DECIMAL(10,4) DEFAULT NULL            COMMENT '变形速率上限（mm/d）',
    `status`          CHAR(1)      DEFAULT '0'              COMMENT '状态（0正常 1停用）',
    `create_by`       VARCHAR(64)  DEFAULT ''               COMMENT '创建者',
    `create_time`     DATETIME     DEFAULT NULL             COMMENT '创建时间',
    `update_by`       VARCHAR(64)  DEFAULT ''               COMMENT '更新者',
    `update_time`     DATETIME     DEFAULT NULL             COMMENT '更新时间',
    `remark`          VARCHAR(500) DEFAULT NULL             COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_stage_code` (`stage_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='变形阶段配置表';
```

**建表规范**：
- 表名前缀按模块：`sys_`（系统）、`dock_`（设备接入）、`alarm_`（告警）、`operation_`（运维）
- 字段名小写下划线：`device_id`、`create_time`
- 主键统一 `BIGINT(20)` 自增
- 状态字段统一 `CHAR(1)`，`'0'` 正常 `'1'` 停用
- 时间字段统一 `DATETIME`
- 金额/测量值统一 `DECIMAL`，禁止 `FLOAT/DOUBLE`
- 字符集统一 `utf8mb4`，引擎统一 `InnoDB`
- **每个字段必须有 COMMENT**
- **每张表必须有 create_by/create_time/update_by/update_time/remark 五个审计字段**

### 7.2 索引规范

| 索引类型 | 命名规则 | 使用原则 |
|---------|---------|---------|
| 主键 | `PRIMARY KEY` | 每表必有，BIGINT 自增 |
| 唯一索引 | `uk_{字段名}` | 业务唯一字段必须加（如 stage_code） |
| 普通索引 | `idx_{字段名}` | 高频查询条件字段加索引 |
| 联合索引 | `idx_{字段1}_{字段2}` | 遵循最左前缀原则 |

**索引原则**：
- 单表索引数量不超过 5 个
- 联合索引字段数不超过 4 个
- 区分度低的字段（如 status）不加索引
- WHERE、ORDER BY、GROUP BY、JOIN ON 字段考虑加索引
- 禁止在索引字段上使用函数：`WHERE DATE(create_time) = '2026-08-07'` -> 改用范围查询

### 7.3 SQL 编写规范

```sql
-- 禁止 SELECT *，必须明确字段
SELECT id, device_id, alarm_level, create_time
FROM alarm_slope
WHERE device_id = #{deviceId}
ORDER BY create_time DESC;

-- 批量插入使用 foreach
INSERT INTO alarm_slope (device_id, alarm_level) VALUES
<foreach collection="list" item="item" separator=",">
    (#{item.deviceId}, #{item.alarmLevel})
</foreach>

-- 分页查询使用 PageHelper，禁止手动 LIMIT
-- Java 代码中调用 startPage() 后紧跟查询即可
```

### 7.4 TDengine 时序数据库规范

```sql
-- 超级表设计：位移监测数据
CREATE STABLE IF NOT EXISTS displacement_data (
    ts            TIMESTAMP,    -- 时间戳（主键）
    displacement  DOUBLE,       -- 位移值
    velocity      DOUBLE        -- 变形速率
) TAGS (
    device_id     BINARY(32),   -- 设备编号
    device_type   BINARY(16),   -- 设备类型
    project_id    BINARY(32)    -- 项目编号
);

-- 子表自动创建（自动建表语法）
INSERT INTO d_dev001 USING displacement_data
TAGS ('DEV001', 'GNSS', 'PROJ001')
VALUES (NOW, 12.5, 0.03);
```

**TDengine 规范**：
- 每种设备类型设计一个超级表（STABLE）
- TAG 标签选择高基数查询字段（device_id、project_id）
- 时序数据通过子表写入，自动建表
- 查询优先按 TAG 过滤（走索引）
- 聚合查询使用 TDengine 内置窗口函数（INTERVAL、FILL）

### 7.5 SQL 脚本版本管理

```
doc/sql/
├── 20260801_init_base_table.sql          -> 初始化基础表
├── 20260805_add_alarm_contact_group.sql  -> 新增告警联系组表
├── 20260806_add_sms_send_job.sql         -> 新增短信发送任务表
└── 20260807_modify_slope_add_column.sql  -> 修改边坡表新增字段
```

**脚本管理规范**：
- 文件名格式：`{日期}_{简述}.sql`，如 `20260807_add_slope_column.sql`
- 每个脚本头部必须注释：变更目的、作者、日期
- DDL 必须可重复执行（使用 `IF NOT EXISTS` 或 `IF EXISTS`）
- DML 初始化数据使用 `INSERT ... ON DUPLICATE KEY UPDATE` 或先 DELETE 再 INSERT
- 脚本按时间顺序执行，不可修改已执行的历史脚本

---

## 第八章：API 接口设计规范

### 8.1 RESTful 接口规范

| 操作 | HTTP 方法 | URL | 示例 |
|------|----------|-----|------|
| 查询列表 | GET | `/port/slope/list` | 分页查询 |
| 查询详情 | GET | `/port/slope/{id}` | 按ID查 |
| 新增 | POST | `/port/slope` | 新增 |
| 修改 | PUT | `/port/slope` | 修改 |
| 删除 | DELETE | `/port/slope/{ids}` | 批量删除（逗号分隔） |
| 导出 | POST | `/port/slope/export` | Excel 导出 |
| 导入 | POST | `/port/slope/importData` | Excel 导入 |

### 8.2 统一返回格式

```java
// 单对象返回
{
    "code": 200,          // 200成功 500失败
    "msg": "操作成功",
    "data": { ... }       // 业务数据
}

// 分页列表返回
{
    "code": 200,
    "msg": "查询成功",
    "rows": [ ... ],      // 列表数据
    "total": 100          // 总记录数
}

// 表格数据返回（TableDataInfo）
{
    "code": 200,
    "msg": "查询成功",
    "rows": [ ... ],
    "total": 100
}
```

### 8.3 接口命名规范

```
/api/{模块}/{业务}/{操作}

示例：
  /port/slope/list           -> 边坡列表
  /port/slope/detail/{id}    -> 边坡详情
  /port/slope/add            -> 新增边坡（或直接POST /port/slope）
  /port/slope/edit           -> 修改边坡
  /port/slope/remove/{ids}   -> 删除边坡
  /port/slope/export         -> 导出
  /operation/warning/rule/list -> 预警规则列表
```

### 8.4 接口安全规范

- 所有接口必须经过认证（JWT Token），除登录/验证码等公开接口
- 所有写操作必须有权限注解 `@PreAuthorize`
- 接口路径在 `application.yml` 的 `xss.urlPatterns` 中配置 XSS 过滤
- 文件上传限制大小（单文件 2048MB，总 3072MB）
- 接口必须记录访问日志（通过 AOP 拦截）

---

## 第九章：安全开发规范

### 9.1 认证与授权

```java
// Spring Security 配置
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 白名单路径（无需认证）
    private static final String[] WHITE_LIST = {
        "/login", "/captchaImage", "/register",
        "/swagger-resources/**", "/webjars/**",
        "/druid/**", "/actuator/**"
    };

    // 所有其他请求必须认证
    http.authorizeRequests()
        .antMatchers(WHITE_LIST).permitAll()
        .anyRequest().authenticated();
}
```

**认证规范**：
- JWT Token 放在请求头 `Authorization`
- Token 有效期默认 30 分钟，支持刷新
- 密码必须 BCrypt 加密存储，禁止明文
- 密码错误 5 次锁定 10 分钟

### 9.2 权限控制

```java
// 权限标识格式：{模块}:{业务}:{操作}
@PreAuthorize("@ss.hasPermi('port:slope:list')")    // 查询权限
@PreAuthorize("@ss.hasPermi('port:slope:add')")     // 新增权限
@PreAuthorize("@ss.hasPermi('port:slope:edit')")    // 修改权限
@PreAuthorize("@ss.hasPermi('port:slope:remove')")  // 删除权限
@PreAuthorize("@ss.hasRole('admin')")               // 角色控制
```

### 9.3 数据安全

| 安全项 | 规范 |
|--------|------|
| **SQL 注入** | 禁止拼接 SQL，MyBatis 使用 `#{}` 而非 `${}`（除排序字段外） |
| **XSS 攻击** | 开启 XSS 过滤器，输入内容转义 HTML 特殊字符 |
| **CSRF** | 前后端分离架构下使用 JWT 天然防 CSRF |
| **敏感数据** | 密码、身份证、手机号脱敏展示 |
| **文件上传** | 校验文件类型、大小，禁止上传可执行文件 |
| **接口幂等** | 写操作支持幂等（唯一键/Token 机制） |
| **越权访问** | 查询数据时校验当前用户的数据权限（部门级别） |

### 9.4 敏感信息处理

```java
// 数据脱敏
public class UserVO {
    private String username;

    @Sensitive(strategy = SensitiveStrategy.PHONE)  // 手机号脱敏
    private String phone;

    @Sensitive(strategy = SensitiveStrategy.ID_CARD) // 身份证脱敏
    private String idCard;
}

// 配置文件中的密码必须加密或使用环境变量
// 禁止在代码中硬编码密码、密钥
spring:
  datasource:
    password: ${DB_PASSWORD:default}  # 从环境变量读取
```

---

## 第十章：异常处理规范

### 10.1 全局异常处理

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 业务异常 */
    @ExceptionHandler(ServiceException.class)
    public AjaxResult handleServiceException(ServiceException e) {
        log.error("业务异常: {}", e.getMessage(), e);
        return AjaxResult.error(e.getCode(), e.getMessage());
    }

    /** 参数校验异常 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public AjaxResult handleValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining("; "));
        return AjaxResult.error(message);
    }

    /** 权限异常 */
    @ExceptionHandler(AccessDeniedException.class)
    public AjaxResult handleAccessDeniedException(AccessDeniedException e) {
        log.error("权限不足: {}", e.getMessage());
        return AjaxResult.error(HttpStatus.FORBIDDEN, "没有权限，请联系管理员");
    }

    /** 兜底异常 */
    @ExceptionHandler(Exception.class)
    public AjaxResult handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return AjaxResult.error("系统繁忙，请稍后重试");
    }
}
```

### 10.2 异常分类与使用

| 异常类型 | 使用场景 | 处理方式 |
|---------|---------|---------|
| `ServiceException` | 业务逻辑校验失败 | 全局捕获，返回错误信息 |
| `BusinessException` | 业务规则不满足 | 全局捕获，返回错误信息 |
| `IllegalArgumentException` | 参数非法 | 全局捕获 |
| `RuntimeException` | 非预期异常 | 全局兜底捕获 |
| `IOException` | IO 操作失败 | try-catch 处理或抛出 |
| 自定义异常 | 特定业务异常 | 继承 RuntimeException |

**异常处理原则**：
- **禁止吞异常**：catch 后必须有日志或处理，禁止空 catch 块
- **禁止用异常控制流程**：异常是异常情况，不是正常逻辑分支
- **异常信息对用户友好**：返回前端的是中文提示，日志记录的是堆栈
- **事务回滚**：`@Transactional(rollbackFor = Exception.class)` 必须加 rollbackFor

### 10.3 参数校验规范

```java
// DTO 参数校验注解
public class AlarmSlopeDTO {
    @NotBlank(message = "设备编号不能为空")
    private String deviceId;

    @NotNull(message = "告警等级不能为空")
    @Range(min = 1, max = 3, message = "告警等级必须为1-3")
    private Integer alarmLevel;

    @DecimalMin(value = "0.0", message = "位移值不能为负")
    private BigDecimal displacement;
}

// Controller 中触发校验
@PostMapping
public AjaxResult add(@Validated @RequestBody AlarmSlopeDTO dto) { ... }
```

---

## 第十一章：日志规范

### 11.1 日志级别使用

| 级别 | 使用场景 | 示例 |
|------|---------|------|
| ERROR | 系统异常、业务错误、需要立即处理 | `log.error("设备健康度计算失败: {}", deviceId, e);` |
| WARN | 可预期的异常、业务预警 | `log.warn("设备[{}]数据不足2条，跳过计算", deviceId);` |
| INFO | 关键业务节点、系统启动信息 | `log.info("定时任务[{}]执行完成，处理{}条数据", taskName, count);` |
| DEBUG | 调试信息（开发环境） | `log.debug("查询参数: {}", queryParam);` |

### 11.2 日志使用规范

```java
// 使用 Slf4j（Lombok @Slf4j）
@Slf4j
@Service
public class AlarmSlopeServiceImpl {

    public void processAlarm(String deviceId) {
        log.info("开始处理设备[{}]的告警", deviceId);

        try {
            // 业务逻辑
            log.info("设备[{}]告警处理完成", deviceId);
        } catch (Exception e) {
            // 异常日志必须记录堆栈（e 作为最后一个参数）
            log.error("设备[{}]告警处理异常: {}", deviceId, e.getMessage(), e);
            throw new ServiceException("告警处理失败");
        }
    }
}
```

**日志规范**：
- 使用 `{}` 占位符，禁止字符串拼接（性能差）
- 异常日志必须传入异常对象 `e`（记录完整堆栈）
- 敏感信息禁止打印日志（密码、Token、身份证）
- 日志框架统一使用 SLF4J + Logback
- 日志按天滚动，保留 30 天，单文件最大 100MB
- ERROR 日志单独文件：`logs/error.{date}.log`
- 慢 SQL 日志：Druid 配置 `slow-sql-millis: 1000`

### 11.3 日志配置

```xml
<!-- logback-spring.xml 关键配置 -->
<appender name="file_info" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>${log.path}/info.log</file>
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
        <fileNamePattern>${log.path}/info.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
        <maxFileSize>100MB</maxFileSize>
        <maxHistory>30</maxHistory>
    </rollingPolicy>
</appender>
```

---

## 第十二章：配置管理规范

### 12.1 多环境配置

```
src/main/resources/
├── application.yml          -> 主配置（公共配置）
├── application-dev.yml      -> 开发环境
├── application-staging.yml  -> 测试环境
├── application-prod.yml     -> 生产环境
└── application-druid.yml    -> 数据源专用配置
```

**配置原则**：
- 公共配置放 `application.yml`，环境差异配置放 `application-{profile}.yml`
- 通过 `spring.profiles.active` 切换环境
- 环境变量优先级高于配置文件：`${DB_HOST:localhost}`

### 12.2 配置项规范

```yaml
# 配置项必须分组、有注释、有默认值
spring:
  datasource:                    # 数据源组
    type: com.alibaba.druid.pool.DruidDataSource
    druid:
      master:                    # 主库
        driverClassName: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://${DB_HOST:127.0.0.1}:3306/${DB_NAME:ims}?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8
        username: ${DB_USER:root}
        password: ${DB_PASSWORD:}   # 从环境变量读取，禁止硬编码
      slave:                     # 从库（TDengine）
        enabled: true
        driverClassName: com.taosdata.jdbc.ws.WebSocketDriver
        url: jdbc:TAOS-WS://${TDENGINE_HOST:127.0.0.1}:6041/ims
```

### 12.3 自定义配置项

```java
// 使用 @ConfigurationProperties 绑定配置
@Data
@Component
@ConfigurationProperties(prefix = "ai")
public class AiConfig {
    private String name;
    private String version;
    private String profile;        // 文件上传路径
    private Boolean addressEnabled; // IP 地址开关
    private String captchaType;     // 验证码类型
}
```

---

## 第十三章：测试规范

### 13.1 单元测试要求

```java
@SpringBootTest
class AlarmSlopeServiceImplTest {

    @Autowired
    private IAlarmSlopeService alarmSlopeService;

    @Test
    @DisplayName("新增边坡告警 - 正常场景")
    void testInsertAlarmSlope_Success() {
        AlarmSlope alarm = new AlarmSlope();
        alarm.setDeviceId("DEV001");
        alarm.setAlarmLevel("2");
        int result = alarmSlopeService.insertAlarmSlope(alarm);
        Assertions.assertTrue(result > 0);
    }

    @Test
    @DisplayName("新增边坡告警 - 设备编号为空应抛异常")
    void testInsertAlarmSlope_DeviceIdEmpty() {
        AlarmSlope alarm = new AlarmSlope();
        alarm.setAlarmLevel("2");
        Assertions.assertThrows(ServiceException.class, () -> {
            alarmSlopeService.insertAlarmSlope(alarm);
        });
    }
}
```

**测试规范**：
- 核心算法（变形计算、健康度计算、区间判定）**必须有单元测试**
- 测试类命名：`{ClassName}Test`，测试方法命名：`test{Method}_{Scenario}`
- 每个方法至少覆盖：正常场景 + 异常场景 + 边界场景
- 测试用例使用 `@DisplayName` 标注中文描述
- 断言使用 JUnit5 `Assertions`，禁止 `System.out.println` 验证
- 测试数据使用 `@BeforeEach` 初始化，`@AfterEach` 清理

### 13.2 必须测试的边界场景

| 场景 | 测试要点 |
|------|---------|
| 空集合/空对象 | 传入 null、空 List、空 Map，确保不 NPE |
| 单条数据 | 只有 1 条数据时算法是否正常 |
| 数据不足 | 计算需要 2 条数据但只有 1 条时的处理 |
| 最大值/最小值 | BigDecimal 边界值、时间范围边界 |
| 并发场景 | 多线程写入同一资源的安全性 |
| 精度边界 | BigDecimal 比较时 0.00 与 0.0 是否相等 |

### 13.3 测试覆盖率目标

| 模块 | 覆盖率要求 |
|------|-----------|
| 核心算法（operation 模块） | ≥ 80% |
| 工具类（common/utils） | ≥ 70% |
| Service 业务层 | ≥ 60% |
| Controller 层 | ≥ 50%（可集成测试） |
| Mapper 层 | 不强制（依赖数据库） |

---

## 第十四章：Git 版本管理规范

### 14.1 分支策略

```
main          -> 生产分支（保护分支，只接受 PR 合入）
develop       -> 开发主分支（日常集成）
feature/*     -> 功能分支（从 develop 拉出，开发完合回 develop）
hotfix/*      -> 紧急修复分支（从 main 拉出，修复后合回 main 和 develop）
release/*     -> 发布分支（预发布测试）
```

**分支命名规范**：
```
feature/{模块}-{简述}     -> feature/slope-alarm-rule
hotfix/{模块}-{简述}      -> hotfix/slope-null-pointer
```

### 14.2 提交信息规范

```bash
# 格式：<类型>(<模块>): <简述>
git commit -m "feat(slope): 新增边坡告警分级判定功能"
git commit -m "fix(tdengine): 修复GNSS设备健康度计算空指针异常"
git commit -m "refactor(common): 重构数据采集器工厂支持WebSocket协议"
git commit -m "docs(sql): 新增变形阶段配置表初始化脚本"
```

**提交类型**：

| 类型 | 说明 |
|------|------|
| `feat` | 新增功能 |
| `fix` | 修复 Bug |
| `refactor` | 重构（不改功能） |
| `perf` | 性能优化 |
| `docs` | 文档变更 |
| `style` | 代码格式调整（不改逻辑） |
| `test` | 测试相关 |
| `chore` | 构建/工具变更 |

### 14.3 代码审查规范

- 每个 PR/MR 至少 1 人审查通过才能合入
- PR 标题遵循提交信息规范
- PR 描述必须包含：变更目的、影响范围、测试方式
- 审查重点：架构一致性、安全风险、性能影响、代码规范
- 禁止直接 push 到 `main` 和 `develop` 分支

### 14.4 .gitignore 必须忽略的内容

```
# 编译产物
target/
*.class
*.jar
*.war

# IDE
.idea/
*.iml
.vscode/

# 日志
logs/
*.log

# 前端
node_modules/
dist/
.env.local

# 系统文件
.DS_Store
Thumbs.db

# 敏感配置（不提交真实密码的配置文件）
application-prod-secret.yml
```

---

## 第十五章：性能优化规范

### 15.1 数据库性能

| 场景 | 优化方案 |
|------|---------|
| 慢查询 | 开启 Druid 慢 SQL 监控（>1s 记录），定期分析优化 |
| 大数据量查询 | 必须分页，禁止全表扫描 |
| N+1 查询 | 使用 MyBatis 关联查询或批量查询替代循环查询 |
| 深度分页 | 超过 10w 条使用游标或子查询优化 |
| 时序数据聚合 | 优先在 TDengine 中聚合，不拉到 Java 层计算 |
| 批量写入 | 使用 MyBatis `<foreach>` 批量插入，单批不超过 500 条 |

### 15.2 缓存策略

```java
// 使用 Redis 缓存高频读、低频写的数据
@Service
public class SysDictTypeServiceImpl {

    @Cacheable(value = "sys_dict", key = "#dictType")  // 查询走缓存
    public List<SysDictData> selectDictDataByType(String dictType) {
        return mapper.selectDictDataByType(dictType);
    }

    @CacheEvict(value = "sys_dict", key = "#dictType")  // 修改时清除缓存
    public int updateDictData(SysDictData dictData) {
        return mapper.updateDictData(dictData);
    }
}
```

**缓存原则**：
- 字典数据、配置数据、组织架构等高频读数据使用缓存
- 缓存必须设置过期时间，防止内存泄漏
- 写操作必须清除或更新对应缓存
- 缓存穿透：空值也缓存，设置短过期时间
- 缓存雪崩：过期时间加随机偏移

### 15.3 并发与异步

```java
// 耗时操作使用异步执行（@Async）
@Async("asyncTaskExecutor")
public void sendAlarmNotification(AlarmSlope alarm) {
    // 发送短信、邮件、钉钉通知（不阻塞主流程）
}

// 线程池配置
@Configuration
public class AsyncConfig {
    @Bean("asyncTaskExecutor")
    public ThreadPoolTaskExecutor asyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(200);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("async-task-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
```

**并发原则**：
- 线程池参数根据 CPU 核心数和任务类型合理配置
- IO 密集型：线程数 = CPU 核心数 × 2
- CPU 密集型：线程数 = CPU 核心数 + 1
- 共享可变状态必须加锁或使用线程安全容器
- 优先使用 CompletableFuture 编排异步任务

### 15.4 前端性能

- 路由懒加载：`() => import('@/views/xxx/index')`
- 大列表虚拟滚动：使用 vxe-table 虚拟滚动
- 图片懒加载：`v-lazy` 指令
- 防抖节流：搜索框输入使用 debounce
- 生产构建压缩：开启 gzip 压缩

---

## 第十六章：前端开发规范

### 16.1 目录结构

```
src/
├── api/             -> 接口请求封装（按模块组织）
├── assets/          -> 静态资源（图片/样式/图标）
├── components/      -> 全局公共组件
├── directive/       -> 全局自定义指令
├── layout/          -> 布局组件
├── plugins/         -> 插件封装
├── router/          -> 路由配置
├── store/           -> Vuex 状态管理
├── utils/           -> 工具函数
├── views/           -> 页面视图（按业务模块组织）
├── App.vue          -> 根组件
├── main.js          -> 入口文件
├── permission.js    -> 路由权限控制
└── settings.js      -> 全局配置
```

### 16.2 API 请求封装规范

```javascript
// src/api/port/slope.js
import request from '@/utils/request'

// 查询边坡告警列表
export function listSlope(query) {
  return request({
    url: '/port/slope/list',
    method: 'get',
    params: query
  })
}

// 新增边坡告警
export function addSlope(data) {
  return request({
    url: '/port/slope',
    method: 'post',
    data: data
  })
}
```

### 16.3 Vue 组件规范

```vue
<template>
  <div class="slope-container">
    <!-- 查询表单 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true">
      <el-form-item label="设备编号" prop="deviceId">
        <el-input v-model="queryParams.deviceId" placeholder="请输入设备编号" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { listSlope } from '@/api/port/slope'

export default {
  name: 'Slope',  // 组件名必须多单词，避免与HTML标签冲突
  data() {
    return {
      loading: false,
      slopeList: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        deviceId: undefined
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询列表 */
    getList() {
      this.loading = true
      listSlope(this.queryParams).then(response => {
        this.slopeList = response.rows
        this.total = response.total
      }).finally(() => {
        this.loading = false
      })
    },
    /** 搜索按钮 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮 */
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    }
  }
}
</script>
```

**前端规范**：
- 组件名使用 PascalCase 且多单词（`SlopeIndex` 而非 `Slope`）
- data 必须是函数返回对象
- 方法命名：事件处理 `handle{Action}`，获取数据 `get{Data}`
- 必须处理 loading 状态，防止重复提交
- 表单必须有 `ref`，支持 `resetFields` 重置
- ECharts 图表必须在 `mounted` 中初始化，组件销毁时 `dispose`

---

## 第十七章：部署与运维规范

### 17.1 Docker 部署规范

```dockerfile
# 多阶段构建
FROM maven:3.8-openjdk-11 AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar
EXPOSE 9090
ENV JAVA_OPTS="-Xms512m -Xmx1024m -Dfile.encoding=UTF-8"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

**Docker 规范**：
- 使用多阶段构建减小镜像体积
- 使用 slim 基础镜像
- 不以 root 用户运行应用
- 健康检查：`HEALTHCHECK`
- 时区设置：`ENV TZ=Asia/Shanghai`

### 17.2 构建脚本规范

```bash
#!/bin/bash
# build.sh - 统一构建脚本

# 1. 后端构建
mvn clean package -DskipTests

# 2. 前端构建
cd ai-ui && npm install && npm run build:prod && cd ..

# 3. 复制前端到后端静态资源
cp -r ai-ui/dist/* ai-admin/src/main/resources/static/

# 4. 打包最终 jar
mvn clean package -DskipTests -pl ai-admin -am
```

### 17.3 日志与监控

| 监控项 | 工具/方式 | 告警阈值 |
|--------|----------|---------|
| 应用存活 | Spring Boot Actuator `/actuator/health` | 不可用即告警 |
| JVM 内存 | Actuator + Prometheus | 堆内存 > 85% |
| 慢 SQL | Druid 监控面板 | > 1s 记录，> 5s 告警 |
| 线程池 | Actuator metrics | 活跃线程 > 80% 最大值 |
| 磁盘空间 | 日志/上传目录 | 剩余空间 < 10% |
| 接口响应时间 | AOP 记录 | P99 > 3s |

### 17.4 环境变量清单

```bash
# 数据库
DB_HOST=127.0.0.1
DB_PORT=3306
DB_NAME=ims
DB_USER=root
DB_PASSWORD=********

# Redis
REDIS_HOST=127.0.0.1
REDIS_PORT=6379
REDIS_PASSWORD=********

# TDengine
TDENGINE_HOST=127.0.0.1
TDENGINE_PORT=6041

# MinIO
MINIO_ENDPOINT=http://127.0.0.1:9000
MINIO_ACCESS_KEY=********
MINIO_SECRET_KEY=********

# JWT
JWT_SECRET=********
JWT_EXPIRE_TIME=30
```

---

## 附录A：AI 开发自检清单

> AI 每次输出代码后，必须对照此清单自检，全部通过后方可提交。

### 代码质量自检

- [ ] 是否遵循分层架构（Controller 不写业务逻辑）？
- [ ] 是否有完整的类注释和方法注释？
- [ ] 是否处理了空值和边界场景？
- [ ] 是否使用了正确的类型（BigDecimal 替代 double）？
- [ ] 是否有魔法值（硬编码数字/字符串）？
- [ ] 是否遵循命名规范？
- [ ] 异常是否被正确捕获和处理（非吞异常）？
- [ ] 是否有线程安全问题？

### 安全自检

- [ ] 是否有 `@PreAuthorize` 权限注解？
- [ ] 是否有 `@Log` 操作日志注解（写操作）？
- [ ] SQL 是否使用 `#{}` 而非 `${}`？
- [ ] 是否有硬编码的密码/密钥？
- [ ] 文件上传是否校验类型和大小？
- [ ] 敏感数据是否脱敏？

### 数据库自检

- [ ] 建表是否有完整的审计字段（createBy/createTime/updateBy/updateTime）？
- [ ] 字段是否有 COMMENT 注释？
- [ ] 是否有合理的索引？
- [ ] SQL 脚本是否放入版本管理目录？
- [ ] DDL 是否可重复执行？

### 架构自检

- [ ] 新增代码是否放在正确的模块/包中？
- [ ] 模块依赖是否单向（无循环依赖）？
- [ ] 是否优先使用设计模式而非 if-else 堆砌？
- [ ] 是否复用已有工具类而非重复造轮子？
- [ ] 配置项是否使用 `@ConfigurationProperties` 绑定？

---

## 附录B：常见反模式（禁止出现）

| 反模式 | 问题 | 正确做法 |
|--------|------|---------|
| Controller 写业务逻辑 | 层级混乱，难以测试 | 业务逻辑下沉到 Service |
| Service 直接返回 Domain | 敏感字段泄露 | 转换为 VO 再返回 |
| SQL 拼接字符串 | SQL 注入风险 | 使用 MyBatis `#{}` |
| double 做金额计算 | 精度丢失 | 使用 BigDecimal |
| 空 catch 块 | 异常被吞 | 记录日志并处理 |
| 循环中查询数据库 | N+1 问题 | 批量查询 |
| 硬编码密码 | 安全风险 | 环境变量/配置中心 |
| if-else 超过 3 层嵌套 | 可读性差 | 策略模式/提前 return |
| 万能工具类 | 职责不清 | 按功能拆分工具类 |
| 注释与代码不一致 | 误导维护者 | 修改代码同步修改注释 |

---

## 附录C：技术选型决策矩阵

> 新增技术依赖时，按此矩阵评估，通过后方可引入。

| 评估维度 | 权重 | 说明 |
|---------|------|------|
| 社区活跃度 | 高 | GitHub Star、最近提交、Issue 响应速度 |
| 文档完善度 | 高 | 是否有详细文档和示例 |
| 生产可用性 | 高 | 是否有大型项目生产验证 |
| 维护成本 | 中 | 升级难度、与现有技术栈兼容性 |
| 安全性 | 高 | 是否有已知漏洞、CVE 记录 |
| 许可证 | 高 | 是否为开源友好许可证（Apache 2.0 / MIT） |
| 团队熟悉度 | 中 | 团队是否有能力维护 |

**引入流程**：
1. 技术调研 -> 2. 编写 POC 验证 -> 3. 团队评审 -> 4. 灰度引入 -> 5. 全量推广

---

> **本规范由项目团队维护，AI 助手在参与本项目开发时必须逐条遵守。**
> **规范版本：1.0 | 最后更新：2026-08-07**
> **如有疑问或建议，请提交至团队技术评审会议讨论。**
