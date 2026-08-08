package com.hongyun.navcampus.admin.config;

import com.hongyun.navcampus.system.entity.User;
import com.hongyun.navcampus.system.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Fix database schema issues for notifications
            jdbcTemplate.execute("ALTER TABLE notifications MODIFY user_id BIGINT NULL");
            jdbcTemplate.execute("ALTER TABLE notifications MODIFY type VARCHAR(50)");
            // Try to drop temporary columns if they exist (ignore errors if they don't)
            try { jdbcTemplate.execute("ALTER TABLE notifications DROP COLUMN category"); } catch (Exception e) {}
            try { jdbcTemplate.execute("ALTER TABLE notifications DROP COLUMN recipient_id"); } catch (Exception e) {}
        } catch (Exception e) {
            log.warn("数据库表结构更新提醒: {}", e.getMessage());
        }

        // 替换为实际的默认管理员用户名
        String defaultUsername = "【默认管理员用户名】";
        // 替换为实际的默认管理员密码（将使用 PasswordEncoder 加密存储）
        String defaultPassword = "【默认管理员密码】";

        if (!userMapper.existsByUsername(defaultUsername)) {
            User user = new User();
            user.setUsername(defaultUsername);
            user.setPassword(passwordEncoder.encode(defaultPassword));
            user.setEmail("【管理员邮箱】");
            user.setPhone("【管理员手机号】");
            user.setRole(User.Role.ADMIN);
            userMapper.save(user);
            log.info("默认管理员用户已初始化: {} (ADMIN)", defaultUsername);
        } else {
            // Ensure default user is ADMIN
            userMapper.findByUsername(defaultUsername).ifPresent(user -> {
                if (user.getRole() != User.Role.ADMIN) {
                    user.setRole(User.Role.ADMIN);
                    userMapper.save(user);
                    log.info("用户 {} 角色已更新为 ADMIN", defaultUsername);
                }
            });
        }
    }
}
