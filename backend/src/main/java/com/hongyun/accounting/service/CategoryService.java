package com.hongyun.accounting.service;

import com.hongyun.accounting.entity.Category;
import com.hongyun.accounting.entity.User;
import com.hongyun.accounting.repository.CategoryRepository;
import com.hongyun.accounting.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类服务。
 * 提供分类的 CRUD 与按类型查询。
 */
@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 获取当前登录用户。
     */
    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
    }

    /**
     * 查询当前用户全部分类。
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findByUserId(getCurrentUser().getId());
    }
    
    /**
     * 按类型查询分类。
     */
    public List<Category> getCategoriesByType(Category.CategoryType type) {
        return categoryRepository.findByUserIdAndType(getCurrentUser().getId(), type);
    }

    /**
     * 创建分类。
     */
    public Category createCategory(Category category) {
        category.setUser(getCurrentUser());
        return categoryRepository.save(category);
    }
    
    /**
     * 更新分类（校验归属）。
     */
    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = categoryRepository.findById(id).orElseThrow();
        if (!category.getUser().getId().equals(getCurrentUser().getId())) {
            throw new RuntimeException("Unauthorized");
        }
        category.setName(categoryDetails.getName());
        category.setType(categoryDetails.getType());
        category.setIcon(categoryDetails.getIcon());
        category.setColor(categoryDetails.getColor());
        return categoryRepository.save(category);
    }
    
    /**
     * 删除分类（校验归属）。
     */
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow();
        if (!category.getUser().getId().equals(getCurrentUser().getId())) {
            throw new RuntimeException("Unauthorized");
        }
        categoryRepository.delete(category);
    }
}
