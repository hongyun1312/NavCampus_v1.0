package com.hongyun.navcampus.finance.controller;

import com.hongyun.navcampus.common.core.R;
import com.hongyun.navcampus.finance.converter.VoConverter;
import com.hongyun.navcampus.finance.entity.Category;
import com.hongyun.navcampus.finance.service.CategoryService;
import com.hongyun.navcampus.finance.vo.CategoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "分类管理", description = "收支分类CRUD")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    @Operation(summary = "查询当前用户全部分类")
    public R<List<CategoryVO>> list() {
        return R.ok(VoConverter.toCategoryVOList(categoryService.getAllCategories()));
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "按类型查询分类")
    public R<List<CategoryVO>> listByType(@PathVariable String type) {
        return R.ok(VoConverter.toCategoryVOList(
                categoryService.getCategoriesByType(Category.CategoryType.valueOf(type))));
    }

    @PostMapping
    @Operation(summary = "创建分类")
    public R<CategoryVO> create(@RequestBody Category category) {
        return R.ok(VoConverter.toCategoryVO(categoryService.createCategory(category)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新分类")
    public R<CategoryVO> update(@PathVariable Long id, @RequestBody Category category) {
        return R.ok(VoConverter.toCategoryVO(categoryService.updateCategory(id, category)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除分类")
    public R<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return R.ok();
    }
}
