package com.hongyun.accounting.repository;

import com.hongyun.accounting.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * 分类仓库。
 * 支持按用户与类型查询分类。
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserId(Long userId);
    List<Category> findByUserIdAndType(Long userId, Category.CategoryType type);
}
