package com.hongyun.accounting.service;

import com.hongyun.accounting.entity.Budget;
import com.hongyun.accounting.entity.Category;
import com.hongyun.accounting.entity.Record;
import com.hongyun.accounting.entity.User;
import com.hongyun.accounting.repository.BudgetRepository;
import com.hongyun.accounting.repository.RecordRepository;
import com.hongyun.accounting.repository.UserRepository;
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
    private BudgetRepository budgetRepository;
    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationService notificationService;

    private User currentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
    }

    /**
     * 创建预算。
     */
    public Budget create(Budget b) {
        b.setUser(currentUser());
        return budgetRepository.save(b);
    }

    /**
     * 按周期查询预算列表。
     */
    public List<Budget> list(String period) {
        return budgetRepository.findByUserIdAndPeriod(currentUser().getId(), period);
    }

    /**
     * 对支出记录进行预算阈值检查，达到 80%/100% 时生成通知。
     */
    public void checkAndNotify(Record record) {
        String period = record.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        User user = record.getUser();
        if (record.getType() != Record.RecordType.EXPENSE) return;
        BigDecimal spentTotal = sumExpense(user, period, null);
        budgetRepository.findByUserIdAndPeriodAndType(user.getId(), period, Budget.BudgetType.TOTAL)
                .ifPresent(b -> notifyIfThreshold(user, "整体预算", spentTotal, b.getAmount(), period));
        if (record.getCategory() != null) {
            BigDecimal spentCat = sumExpense(user, period, record.getCategory());
            List<Budget> budgets = budgetRepository.findByUserId(user.getId());
            budgets.stream().filter(b -> b.getType() == Budget.BudgetType.CATEGORY
                    && b.getCategory() != null
                    && b.getCategory().getId().equals(record.getCategory().getId())
                    && b.getPeriod().equals(period))
                    .findFirst()
                    .ifPresent(b -> notifyIfThreshold(user, "分类预算:" + b.getCategory().getName(), spentCat, b.getAmount(), period));
        }
    }

    /**
     * 计算周期内的支出总额或指定分类的支出。
     */
    private BigDecimal sumExpense(User user, String period, Category category) {
        LocalDate first = LocalDate.parse(period + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDateTime start = first.atStartOfDay();
        LocalDateTime end = first.plusMonths(1).minusDays(1).atTime(23, 59, 59);
        List<Record> records = recordRepository.findByUserIdAndDateRange(user.getId(), start, end);
        return records.stream()
                .filter(r -> r.getType() == Record.RecordType.EXPENSE)
                .filter(r -> category == null || (r.getCategory() != null && r.getCategory().getId().equals(category.getId())))
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
