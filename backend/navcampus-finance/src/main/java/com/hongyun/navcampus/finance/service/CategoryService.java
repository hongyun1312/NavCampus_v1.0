package com.hongyun.navcampus.finance.service;

import com.hongyun.navcampus.finance.entity.Category;
import com.hongyun.navcampus.system.entity.User;
import com.hongyun.navcampus.finance.mapper.CategoryMapper;
import com.hongyun.navcampus.system.mapper.UserMapper;
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
    private CategoryMapper categoryMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 获取当前登录用户。
     */
    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userMapper.findByUsername(userDetails.getUsername()).orElseThrow();
    }

    /**
     * 查询当前用户全部分类。
     */
    public List<Category> getAllCategories() {
        return categoryMapper.findByUserId(getCurrentUser().getId());
    }
    
    /**
     * 按类型查询分类。
     */
    public List<Category> getCategoriesByType(Category.CategoryType type) {
        return categoryMapper.findByUserIdAndType(getCurrentUser().getId(), type);
    }

    /**
     * 创建分类。
     */
    public Category createCategory(Category category) {
        category.setUser(getCurrentUser());
        return categoryMapper.save(category);
    }
    
    /**
     * 更新分类（校验归属）。
     */
    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = categoryMapper.findById(id).orElseThrow();
        if (!category.getUserId().equals(getCurrentUser().getId())) {
            throw new RuntimeException("Unauthorized");
        }
        category.setName(categoryDetails.getName());
        category.setType(categoryDetails.getType());
        category.setIcon(categoryDetails.getIcon());
        category.setColor(categoryDetails.getColor());
        return categoryMapper.save(category);
    }
    
    /**
     * 删除分类（校验归属）。
     */
    public void deleteCategory(Long id) {
        Category category = categoryMapper.findById(id).orElseThrow();
        if (!category.getUserId().equals(getCurrentUser().getId())) {
            throw new RuntimeException("Unauthorized");
        }
        categoryMapper.delete(category);
    }
}
