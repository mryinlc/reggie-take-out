package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.dto.SetmealDto;
import com.reggie.pojo.Category;
import com.reggie.pojo.Setmeal;
import com.reggie.pojo.SetmealDish;
import com.reggie.service.CategoryService;
import com.reggie.service.SetmealDishService;
import com.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    @CacheEvict(value = "setmealCache", key = "'setmeal_' + #setmealDto.categoryId")
    @PostMapping
    public R<Object> addSetmeal(@RequestBody SetmealDto setmealDto) {
        setmealService.addSetmealWithDish(setmealDto);
        return R.success(null);
    }

    @GetMapping("/page")
    public R<Page> getSetmeals(int page, int pageSize, String name) {
        // 未加@RequestParam注解时，其功能相当于添加了@RequestParam(value = "name", required = false)注解，即未传name参数时也不报错
        Page<Setmeal> setmealPage = setmealService.page(new Page<>(page, pageSize),
                new QueryWrapper<Setmeal>()
                        .like(name != null, "name", name)
                        .orderByDesc("update_time"));
        Page<SetmealDto> setmealDtoPage = new Page<>();
        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");
        setmealDtoPage.setRecords(setmealPage.getRecords().stream().map(setmeal -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal, setmealDto);
            Category category = categoryService.getById(setmeal.getCategoryId());
            setmealDto.setCategoryName(category != null ? category.getName() : "未分类");
            return setmealDto;
        }).collect(Collectors.toList()));
        return R.success(setmealDtoPage);
    }

    @PostMapping("/status/{status}")
    public R<Object> changeSetmealStatus(@PathVariable("status") int status, @RequestParam("ids") List<Long> ids) {
        List<Setmeal> setmeals = new ArrayList<>();
        ids.forEach(id -> {
            Setmeal setmeal = new Setmeal();
            setmeal.setId(id);
            setmeal.setStatus(status);
            setmeals.add(setmeal);
        });
        setmealService.updateBatchById(setmeals);
        return R.success(null);
    }

    @CacheEvict(value = "setmealCache", allEntries = true)
    @DeleteMapping
    public R<Object> delSetmeals(@RequestParam("ids") List<Long> ids) {
        setmealService.delSetmealsByIds(ids);
        return R.success(null);
    }

    @Cacheable(value = "setmealCache", key = "'setmeal_' + #categoryId")
    @GetMapping("/list")
    public R<List<SetmealDto>> getSetmealsByCategoryId(Long categoryId, Integer status) {
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(categoryId != null, Setmeal::getCategoryId, categoryId)
                .eq(status != null, Setmeal::getStatus, status)
                .orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmealList = setmealService.list(wrapper);
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        List<SetmealDto> setmealDtoList = setmealList.stream().map(setmeal -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal, setmealDto);
            queryWrapper.clear();
            queryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
            setmealDto.setSetmealDishes(setmealDishService.list(queryWrapper));
            return setmealDto;
        }).collect(Collectors.toList());
        return R.success(setmealDtoList);
    }
}

