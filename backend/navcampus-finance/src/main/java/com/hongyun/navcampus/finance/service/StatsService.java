package com.hongyun.navcampus.finance.service;

import com.hongyun.navcampus.finance.entity.Record;
import com.hongyun.navcampus.system.entity.User;
import com.hongyun.navcampus.finance.mapper.RecordMapper;
import com.hongyun.navcampus.finance.mapper.CategoryMapper;
import com.hongyun.navcampus.system.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计服务。
 * 生成月度维度的收入/支出/结余、分类占比饼图数据、日趋势与结余折线。
 */
@Service
public class StatsService {
    @Autowired
    private RecordMapper recordMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private UserMapper userMapper;

    private User currentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userMapper.findByUsername(userDetails.getUsername()).orElseThrow();
    }

    /**
     * 统计聚合。
     * @param period 周期（yyyy-MM）
     * @return 聚合结果
     */
    public Map<String, Object> aggregate(String period) {
        LocalDate first = LocalDate.parse(period + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDateTime start = first.atStartOfDay();
        LocalDateTime end = first.plusMonths(1).minusDays(1).atTime(23, 59, 59);
        List<Record> records = recordMapper.findByUserIdAndDateRange(currentUser().getId(), start, end);
        BigDecimal income = sum(records, Record.RecordType.INCOME);
        BigDecimal expense = sum(records, Record.RecordType.EXPENSE);
        BigDecimal balance = income.subtract(expense);
        java.util.Map<Long, String> categoryNameMap = new java.util.HashMap<>();
        categoryMapper.findByUserId(currentUser().getId()).forEach(c -> categoryNameMap.put(c.getId(), c.getName()));
        Map<String, BigDecimal> categoryPie = records.stream()
                .filter(r -> r.getType() == Record.RecordType.EXPENSE && r.getCategory() != null)
                .collect(Collectors.groupingBy(r -> categoryNameMap.getOrDefault(r.getCategoryId(), "未分类"),
                        Collectors.mapping(Record::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("income", income);
        res.put("expense", expense);
        res.put("balance", balance);
        res.put("categoryPie", categoryPie);
        res.put("trend", buildTrend(records, first));
        res.put("balanceLine", buildBalanceLine(records, first));
        return res;
    }

    /**
     * 求和辅助函数。
     */
    private BigDecimal sum(List<Record> records, Record.RecordType type) {
        return records.stream().filter(r -> r.getType() == type).map(Record::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 构建每日趋势数据。
     */
    private List<Map<String, Object>> buildTrend(List<Record> records, LocalDate first) {
        int days = first.lengthOfMonth();
        List<Map<String, Object>> trend = new ArrayList<>();
        for (int i = 1; i <= days; i++) {
            LocalDate day = LocalDate.of(first.getYear(), first.getMonth(), i);
            LocalDateTime s = day.atStartOfDay();
            LocalDateTime e = day.atTime(23, 59, 59);
            BigDecimal income = sum(records.stream().filter(r -> r.getTime().isAfter(s.minusSeconds(1)) && r.getTime().isBefore(e.plusSeconds(1))).collect(Collectors.toList()), Record.RecordType.INCOME);
            BigDecimal expense = sum(records.stream().filter(r -> r.getTime().isAfter(s.minusSeconds(1)) && r.getTime().isBefore(e.plusSeconds(1))).collect(Collectors.toList()), Record.RecordType.EXPENSE);
            Map<String, Object> d = new LinkedHashMap<>();
            d.put("date", day.toString());
            d.put("income", income);
            d.put("expense", expense);
            trend.add(d);
        }
        return trend;
    }

    /**
     * 构建结余折线数据。
     */
    private List<Map<String, Object>> buildBalanceLine(List<Record> records, LocalDate first) {
        int days = first.lengthOfMonth();
        List<Map<String, Object>> line = new ArrayList<>();
        BigDecimal acc = BigDecimal.ZERO;
        for (int i = 1; i <= days; i++) {
            LocalDate day = LocalDate.of(first.getYear(), first.getMonth(), i);
            LocalDateTime s = day.atStartOfDay();
            LocalDateTime e = day.atTime(23, 59, 59);
            BigDecimal income = sum(records.stream().filter(r -> r.getTime().isAfter(s.minusSeconds(1)) && r.getTime().isBefore(e.plusSeconds(1))).collect(Collectors.toList()), Record.RecordType.INCOME);
            BigDecimal expense = sum(records.stream().filter(r -> r.getTime().isAfter(s.minusSeconds(1)) && r.getTime().isBefore(e.plusSeconds(1))).collect(Collectors.toList()), Record.RecordType.EXPENSE);
            acc = acc.add(income).subtract(expense);
            Map<String, Object> d = new LinkedHashMap<>();
            d.put("date", day.toString());
            d.put("balance", acc);
            line.add(d);
        }
        return line;
    }
}
