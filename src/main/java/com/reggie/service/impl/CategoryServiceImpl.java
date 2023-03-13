package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.CustomException;
import com.reggie.mapper.CategoryMapper;
import com.reggie.pojo.Category;
import com.reggie.pojo.Dish;
import com.reggie.pojo.Setmeal;
import com.reggie.service.CategoryService;
import com.reggie.service.DishService;
import com.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public void removeCategoryById(long catId) {
        // 查询是否存在与该类型相关的菜品
        LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();
        dishWrapper.eq(Dish::getCategoryId, catId);
        int count = dishService.count(dishWrapper);
        if (count > 0) {
            // 抛出自定义异常
            throw new CustomException("当前类型有相关联的菜品，无法删除");
        }
        // 查询是否存在与该类型相关的套餐
        LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();
        setmealWrapper.eq(Setmeal::getCategoryId, catId);
        count = setmealService.count(setmealWrapper);
        if (count > 0) {
            // 抛出自定义异常
            throw new CustomException("当前类型有相关联的套餐，无法删除");
        }
        // 删除类型
        super.removeById(catId);
    }
}
