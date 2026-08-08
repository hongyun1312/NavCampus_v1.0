package com.hongyun.navcampus.finance.service;

import com.hongyun.navcampus.finance.entity.Budget;
import com.hongyun.navcampus.finance.entity.Category;
import com.hongyun.navcampus.finance.entity.Record;
import com.hongyun.navcampus.system.entity.User;
import com.hongyun.navcampus.finance.mapper.BudgetMapper;
import com.hongyun.navcampus.finance.mapper.RecordMapper;
import com.hongyun.navcampus.finance.mapper.CategoryMapper;
import com.hongyun.navcampus.system.mapper.UserMapper;
import com.hongyun.navcampus.system.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 预算服务。
 * 提供预算创建、查询与支出阈值检测并生成站内提醒。
 */
@Service
public class BudgetService {
    @Autowired
    private BudgetMapper budgetMapper;
    @Autowired
    private RecordMapper recordMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NotificationService notificationService;

    private User currentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userMapper.findByUsername(userDetails.getUsername()).orElseThrow();
    }

    /**
     * 创建预算。
     */
    public Budget create(Budget b) {
        b.setUser(currentUser());
        return budgetMapper.save(b);
    }

    /**
     * 按周期查询预算列表。
     */
    public List<Budget> list(String period) {
        return budgetMapper.findByUserIdAndPeriod(currentUser().getId(), period);
    }

    /**
     * 对支出记录进行预算阈值检查，达到 80%/100% 时生成通知。
     */
    public void checkAndNotify(Record record) {
        String period = record.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        User tempUser = record.getUser();
        if (tempUser == null && record.getUserId() != null) {
            tempUser = userMapper.selectById(record.getUserId());
        }
        final User user = tempUser;
        if (record.getType() != Record.RecordType.EXPENSE) return;
        BigDecimal spentTotal = sumExpense(user, period, null);
        budgetMapper.findByUserIdAndPeriodAndType(user.getId(), period, Budget.BudgetType.TOTAL)
                .ifPresent(b -> notifyIfThreshold(user, "整体预算", spentTotal, b.getAmount(), period));
        if (record.getCategoryId() != null) {
            Category recordCat = categoryMapper.selectById(record.getCategoryId());
            BigDecimal spentCat = sumExpense(user, period, recordCat);
            List<Budget> budgets = budgetMapper.findByUserId(user.getId());
            budgets.stream().filter(b -> b.getType() == Budget.BudgetType.CATEGORY
                    && b.getCategoryId() != null
                    && b.getCategoryId().equals(record.getCategoryId())
                    && b.getPeriod().equals(period))
                    .findFirst()
                    .ifPresent(b -> {
            Category cat = b.getCategoryId() != null ? categoryMapper.selectById(b.getCategoryId()) : null;
            notifyIfThreshold(user, "分类预算:" + (cat != null ? cat.getName() : "未知"), spentCat, b.getAmount(), period);
        });
        }
    }

    /**
     * 计算周期内的支出总额或指定分类的支出。
     */
    private BigDecimal sumExpense(User user, String period, Category category) {
        LocalDate first = LocalDate.parse(period + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDateTime start = first.atStartOfDay();
        LocalDateTime end = first.plusMonths(1).minusDays(1).atTime(23, 59, 59);
        List<Record> records = recordMapper.findByUserIdAndDateRange(user.getId(), start, end);
        return records.stream()
                .filter(r -> r.getType() == Record.RecordType.EXPENSE)
                .filter(r -> category == null || r.getCategoryId() != null && r.getCategoryId().equals(category.getId()))
                .map(Record::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 达到阈值时发送站内通知。
     */
    private void notifyIfThreshold(User user, String title, BigDecimal spent, BigDecimal budgetAmount, String period) {
        if (budgetAmount == null || budgetAmount.compareTo(BigDecimal.ZERO) <= 0) return;
        BigDecimal ratio = spent.multiply(BigDecimal.valueOf(100)).divide(budgetAmount, 2, java.math.RoundingMode.HALF_UP);
        if (ratio.compareTo(BigDecimal.valueOf(80)) >= 0 && ratio.compareTo(BigDecimal.valueOf(100)) < 0) {
            notificationService.notifySite(user, title, "已达80%阈值，周期:" + period + "，已用:" + spent + "/" + budgetAmount);
        } else if (ratio.compareTo(BigDecimal.valueOf(100)) >= 0) {
            notificationService.notifySite(user, title, "已达100%阈值，周期:" + period + "，已用:" + spent + "/" + budgetAmount);
        }
    }
}
