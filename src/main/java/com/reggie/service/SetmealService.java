package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.dto.SetmealDto;
import com.reggie.pojo.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    void addSetmealWithDish(SetmealDto setmealDto);

    void delSetmealsByIds(List<Long> ids);
}
