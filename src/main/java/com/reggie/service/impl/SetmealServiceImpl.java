package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.CustomException;
import com.reggie.dto.SetmealDto;
import com.reggie.mapper.SetmealMapper;
import com.reggie.pojo.Setmeal;
import com.reggie.pojo.SetmealDish;
import com.reggie.service.SetmealDishService;
import com.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Transactional
    @Override
    public void addSetmealWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);
        // 保存当前套餐中的dish
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealDto.getId());
        }
        setmealDishService.saveBatch(setmealDishes);
    }

    @Transactional
    @Override
    public void delSetmealsByIds(List<Long> ids) {
        // 如果有setmeal未处于停售状态，拒绝删除
        QueryWrapper<Setmeal> wrapper = new QueryWrapper<>();
        wrapper.in("id", ids).eq("status", 1);
        if (this.count(wrapper) > 0)
            throw new CustomException("套餐未处于停售状态，无法删除");
        // 执行删除
        this.removeByIds(ids);
        QueryWrapper<SetmealDish> setmealDishQueryWrapper = new QueryWrapper<>();
        setmealDishQueryWrapper.in("setmeal_id", ids);
        setmealDishService.remove(setmealDishQueryWrapper);
    }
}
