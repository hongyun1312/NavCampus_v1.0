package com.hongyun.navcampus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.mybatis.spring.annotation.MapperScan;

/**
 * 系统启动入口。
 * 使用 Spring Boot 自动配置，启动后提供后端 REST API 与安全认证能力。
 * 系统启动全过程步骤
 *
 * - 安装必要环境
 *   - 安装 Java
 *   - 安装 Maven（用于后端构建与运行）
 *   - 安装 Node.js（建议 LTS），安装完成后 node -v、npm -v
 * - 配置数据库
 *   - 后端 MySQL 配置详见 application.yml（已脱敏处理）
 *   - 默认管理员账号/密码详见 DataInitializer.java（已脱敏处理）
 *   - 首次启动将自动建表（spring.jpa.hibernate.ddl-auto=update）
 * - 初始化前端
 *   - 进入目录：frontend    指令： cd C:....frontend;   npm run dev
 *   - 安装依赖：npm install
 *   - 在 frontend 目录创建 .env 文件（建议）：
 *     - VITE_API_BASE= http://localhost:8080
 *   - 启动开发服务器：npm run dev
 *   - 打开浏览器访问开发地址（例如 http://localhost:5173 ）
 *
 */

@SpringBootApplication
@EnableScheduling
@MapperScan("com.hongyun.navcampus.**.mapper")
public class NavCampusApplication {

    /**
     * 应用主函数，启动 Spring 容器。
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(NavCampusApplication.class, args);
    }

}
