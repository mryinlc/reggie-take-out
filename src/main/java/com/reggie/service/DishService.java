package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.dto.DishDto;
import com.reggie.pojo.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    void addDishWithFlavor(DishDto dishDto);

    void updateDishWithFlavor(DishDto dishDto);

    void deleteDishesByIds(List<Long> idList);
}
