package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.BaseContext;
import com.reggie.common.R;
import com.reggie.pojo.Category;
import com.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<Object> addCategory(@RequestBody Category category) {
        categoryService.save(category);
        return R.success(null);
    }

    @GetMapping("/page")
    public R<Page> getCategories(int page, int pageSize) {
        Page<Category> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSort);
        return R.success(categoryService.page(pageInfo, wrapper));
    }

    @DeleteMapping
    public R<Object> delCategory(@RequestParam("ids") long catId) {
        categoryService.removeCategoryById(catId);
        log.info("用户{}删除了分类: {}", BaseContext.getUserId(), catId);
        return R.success(null);
    }

    @PutMapping
    public R<Object> updateCategory(@RequestBody Category category) {
        categoryService.updateById(category);
        log.info("用户{}修改了分类: {}", BaseContext.getUserId(), category.getId());
        return R.success(null);
    }

    @GetMapping("/list")
    public R<List<Category>> getCategoryList(@RequestParam(value = "type", required = false) Integer type) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(type != null, Category::getType, type)
                .orderByAsc(Category::getSort)
                .orderByDesc(Category::getUpdateTime);
        return R.success(categoryService.list(wrapper));
    }
}
