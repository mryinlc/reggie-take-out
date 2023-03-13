package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.CustomException;
import com.reggie.dto.DishDto;
import com.reggie.mapper.DishMapper;
import com.reggie.pojo.Dish;
import com.reggie.pojo.DishFlavor;
import com.reggie.service.DishFlavorService;
import com.reggie.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Transactional
    @Override
    public void addDishWithFlavor(DishDto dishDto) {
        // 添加dish项，DishDto继承了Dish类
        this.save(dishDto);
        // 添加该dish相应的DishFlavor
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.forEach(item -> {
            item.setDishId(dishDto.getId());
        });
        dishFlavorService.saveBatch(flavors);
    }

    @Transactional
    @Override
    public void updateDishWithFlavor(DishDto dishDto) {
        // 修改dish
        this.updateById(dishDto);
        // 删除已有的flavors
        QueryWrapper<DishFlavor> wrapper = new QueryWrapper<>();
        wrapper.eq("dish_id", dishDto.getId());
        dishFlavorService.remove(wrapper);
        // 添加新的flavors
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.forEach(item -> {
            item.setId(null);
            item.setDishId(dishDto.getId());
        });
        dishFlavorService.saveBatch(flavors);
    }

    @Transactional
    @Override
    public void deleteDishesByIds(List<Long> idList) {
        // 如果有对应的Dish未处于停售状态，拒绝删除
        QueryWrapper<Dish> dishWrapper = new QueryWrapper<>();
        dishWrapper.in("id", idList).eq("status", 1);
        if (this.count(dishWrapper) > 0)
            throw new CustomException("菜品未处于停售状态，无法删除");
        // 执行删除
        this.removeByIds(idList);
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(DishFlavor::getDishId, idList);
        dishFlavorService.remove(wrapper);
    }
}
