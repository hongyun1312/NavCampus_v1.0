package com.hongyun.accounting.controller;

import com.hongyun.accounting.entity.Category;
import com.hongyun.accounting.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器。
 * 提供分类的查询、创建、更新与删除接口。
 */
@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 查询全部分类。
     */
    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }
    
    /**
     * 按类型查询分类。
     */
    @GetMapping("/type/{type}")
    public List<Category> getCategoriesByType(@PathVariable Category.CategoryType type) {
        return categoryService.getCategoriesByType(type);
    }

    /**
     * 创建分类。
     */
    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        return categoryService.createCategory(category);
    }
    
    /**
     * 更新分类。
     */
    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable Long id, @RequestBody Category category) {
        return categoryService.updateCategory(id, category);
    }
    
    /**
     * 删除分类。
     */
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}
