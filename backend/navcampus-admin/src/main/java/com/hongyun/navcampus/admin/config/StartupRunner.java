package com.hongyun.navcampus.admin.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.DatabaseMetaData;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 应用启动横幅与健康检查组件。
 * <p>
 * 在 Spring 容器完全启动后执行，输出以下信息：
 * <ul>
 *   <li>NavCampus 专属 ASCII 艺术 Logo</li>
 *   <li>各模块 Bean 加载状态检查</li>
 *   <li>MySQL 数据库连接状态与版本</li>
 *   <li>Redis 缓存连接状态</li>
 *   <li>API 接口统计（Controller 数量）</li>
 *   <li>路网缓存加载状态</li>
 *   <li>访问地址与 API 文档地址</li>
 *   <li>启动总耗时</li>
 * </ul>
 */
@Slf4j
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class StartupRunner implements CommandLineRunner {

    /** 应用启动时间戳（类加载时记录） */
    private static final long START_TIME = System.currentTimeMillis();

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Environment environment;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Override
    public void run(String... args) {
        long elapsed = System.currentTimeMillis() - START_TIME;

        // 1. 输出 Logo 横幅
        printLogo();

        // 2. 检查各模块加载状态
        Map<String, String> moduleStatus = checkModuleStatus();
        printModuleStatus(moduleStatus);

        // 3. 检查数据库连接
        String dbStatus = checkDatabase();
        log.info("[连接检查] MySQL  - {}", dbStatus);

        // 4. 检查 Redis 连接
        String redisStatus = checkRedis();
        log.info("[连接检查] Redis  - {}", redisStatus);

        // 5. 检查路网缓存
        String cacheStatus = checkNavigationCache();
        log.info("[连接检查] 路网缓存 - {}", cacheStatus);

        // 6. 统计 API 接口
        int controllerCount = countControllers();
        log.info("[接口统计] 共注册 {} 个 Controller", controllerCount);

        // 7. 输出启动完成信息
        String port = environment.getProperty("server.port", "8080");
        String profile = environment.getProperty("spring.profiles.active", "default");
        String contextPath = environment.getProperty("server.servlet.context-path", "");

        log.info("─────────────────────────────────────────────────");
        log.info("[启动完成] NavCampus v1.0.0 启动成功！耗时: {}ms", elapsed);
        log.info("[运行环境] Profile: {} | 端口: {}", profile, port);
        log.info("[访问地址] http://localhost:{}{}", port, contextPath);
        log.info("[API文档]  http://localhost:{}{}/swagger-ui.html", port, contextPath);
        log.info("─────────────────────────────────────────────────");
    }

    /**
     * 输出 NavCampus 专属 ASCII 艺术 Logo。
     */
    private void printLogo() {
        String logo = """

                ╔══════════════════════════════════════════════════════════════════╗
                ║                                                                  ║
                ║    ███╗   ██╗ █████╗ ██████╗ ██╗   ██╗███████╗███████╗██╗         ║
                ║    ████╗  ██║██╔══██╗██╔══██╗██║   ██║██╔════╝██╔════╝██║         ║
                ║    ██╔██╗ ██║███████║██████╔╝██║   ██║███████╗█████╗  ██║         ║
                ║    ██║╚██╗██║██╔══██║██╔══██╗██║   ██║╚════██║██╔══╝  ██║         ║
                ║    ██║ ╚████║██║  ██║██║  ██║╚██████╔╝███████║███████╗██║         ║
                ║    ╚═╝  ╚═══╝╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝ ╚══════╝╚══════╝╚═╝         ║
                ║                                                                  ║
                ║          智慧校园综合导航平台  v1.0.0                            ║
                ║          Spring Boot 3.2.1 · Java 17 · MyBatis-Plus 3.5.7      ║
                ╚══════════════════════════════════════════════════════════════════╝
                """;
        System.out.println(logo);
    }

    /**
     * 检查各模块 Bean 加载状态。
     * 通过检测各模块的标志性 Bean 是否存在来判断模块是否成功加载。
     */
    private Map<String, String> checkModuleStatus() {
        Map<String, String> status = new LinkedHashMap<>();

        // common 模块：检查 R 类（通过类名检测）
        status.put("navcampus-common", checkBeanExists("com.hongyun.navcampus.common.core.R")
                ? "通用工具层 (R, PageResult, BusinessException)" : "❌ 未加载");

        // framework 模块：检查 SecurityFilterChain Bean
        status.put("navcampus-framework", checkBeanExists("org.springframework.security.web.SecurityFilterChain")
                ? "安全框架层 (JWT, SecurityConfig, CORS)" : "❌ 未加载");

        // system 模块：检查 UserMapper
        status.put("navcampus-system", applicationContext.containsBean("userMapper")
                ? "系统管理 (User, Auth, Notification)" : "❌ 未加载");

        // campus 模块：检查 NavigationService
        status.put("navcampus-campus", applicationContext.containsBean("navigationService")
                ? "校园导航 (Navigation, StudyRoom, Timetable)" : "❌ 未加载");

        // finance 模块：检查任意 Finance Bean
        status.put("navcampus-finance", applicationContext.containsBean("accountMapper")
                ? "财务管理 (Account, Record, Budget)" : "❌ 未加载");

        return status;
    }

    /**
     * 通过类名检查 Bean 是否存在。
     */
    private boolean checkBeanExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * 打印各模块加载状态。
     */
    private void printModuleStatus(Map<String, String> status) {
        log.info("[模块加载] 共 {} 个模块", status.size());
        status.forEach((module, desc) -> {
            if (desc.startsWith("❌")) {
                log.warn("  ❌ {} - {}", module, desc);
            } else {
                log.info("  ✅ {} - {}", module, desc);
            }
        });
    }

    /**
     * 检查 MySQL 数据库连接状态。
     * 执行简单查询并获取数据库版本信息。
     *
     * @return 连接状态描述文本
     */
    private String checkDatabase() {
        try {
            String version = jdbcTemplate.queryForObject("SELECT VERSION()", String.class);
            Integer tableCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE()", Integer.class);

            // 从 DataSource URL 中提取主机信息
            String url = environment.getProperty("spring.datasource.url", "unknown");
            String host = extractHostFromUrl(url);

            return String.format("连接成功 | %s | MySQL %s | %d 张表", host, version, tableCount);
        } catch (Exception e) {
            return "连接失败 | " + e.getMessage();
        }
    }

    /**
     * 从 JDBC URL 中提取主机和端口信息。
     */
    private String extractHostFromUrl(String url) {
        try {
            // jdbc:mysql://host:port/database
            int start = url.indexOf("//") + 2;
            int end = url.indexOf("/", start);
            if (start > 1 && end > start) {
                return url.substring(start, end);
            }
        } catch (Exception ignored) {}
        return "unknown";
    }

    /**
     * 检查 Redis 连接状态。
     * 通过 RedisConnectionFactory 发送 PING 命令。
     *
     * @return 连接状态描述文本
     */
    private String checkRedis() {
        try {
            String host = environment.getProperty("spring.data.redis.host", "unknown");
            String port = environment.getProperty("spring.data.redis.port", "6379");
            String pong = redisConnectionFactory.getConnection().ping();
            return String.format("连接成功 | %s:%s | 响应: %s", host, port, pong);
        } catch (Exception e) {
            String host = environment.getProperty("spring.data.redis.host", "unknown");
            String port = environment.getProperty("spring.data.redis.port", "6379");
            return String.format("连接失败 | %s:%s | %s", host, port, e.getMessage());
        }
    }

    /**
     * 检查路网缓存加载状态。
     * 通过 NavigationService 的缓存方法获取节点和边数量。
     *
     * @return 缓存状态描述文本
     */
    private String checkNavigationCache() {
        try {
            Object navService = applicationContext.getBean("navigationService");
            if (navService != null) {
                // 通过反射调用 getRoadNetwork 获取缓存数据量
                java.lang.reflect.Method method = navService.getClass().getMethod("getRoadNetwork");
                Object network = method.invoke(navService);
                if (network != null) {
                    java.lang.reflect.Method getNodes = network.getClass().getMethod("getNodes");
                    java.lang.reflect.Method getEdges = network.getClass().getMethod("getEdges");
                    int nodeCount = ((java.util.List<?>) getNodes.invoke(network)).size();
                    int edgeCount = ((java.util.List<?>) getEdges.invoke(network)).size();
                    return String.format("已加载 | %d 节点, %d 边", nodeCount, edgeCount);
                }
            }
            return "未加载";
        } catch (Exception e) {
            return "检查失败 | " + e.getMessage();
        }
    }

    /**
     * 统计已注册的 Controller 数量。
     * 通过检测带有 @RestController 注解的 Bean 来计数。
     *
     * @return Controller 数量
     */
    private int countControllers() {
        String[] beanNames = applicationContext.getBeanNamesForAnnotation(
                org.springframework.web.bind.annotation.RestController.class);
        return beanNames.length;
    }
}
