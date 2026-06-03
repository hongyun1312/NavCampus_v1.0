package com.hongyun.accounting.repository;

import com.hongyun.accounting.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 * 预算仓库。
 * 支持按用户与周期查询，区分整体/分类预算。
 */
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUserId(Long userId);
    List<Budget> findByUserIdAndPeriod(Long userId, String period);
    Optional<Budget> findByUserIdAndPeriodAndType(Long userId, String period, Budget.BudgetType type);
}
