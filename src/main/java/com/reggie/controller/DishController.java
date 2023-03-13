package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.dto.DishDto;
import com.reggie.pojo.Category;
import com.reggie.pojo.Dish;
import com.reggie.pojo.DishFlavor;
import com.reggie.service.CategoryService;
import com.reggie.service.DishFlavorService;
import com.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @PostMapping
    public R<Object> addDish(@RequestBody DishDto dishDto) {
        dishService.addDishWithFlavor(dishDto);
        return R.success(null);
    }

    @GetMapping("/page")
    public R<Page> getDishes(int page, int pageSize, @RequestParam(required = false) String name) {
        // 根据page,pageSize和name查询dish
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, Dish::getName, name).orderByDesc(Dish::getUpdateTime);
        Page<Dish> dishPage = dishService.page(new Page<>(page, pageSize), wrapper);
        // 构造包含categoryName的dishDto对象
        List<DishDto> dishDtos = dishPage.getRecords().stream().map(dish -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);
            Category category = categoryService.getById(dishDto.getCategoryId());
            dishDto.setCategoryName(category == null ? "未分类" : category.getName());
            return dishDto;
        }).collect(Collectors.toList());
        // 封装新的Page<DishDto>对象
        Page<DishDto> dishDtoPage = new Page<>();
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");
        dishDtoPage.setRecords(dishDtos);
        return R.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public R<DishDto> getDishById(@PathVariable("id") long dishId) {
        // 查询dish信息
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dishService.getById(dishId), dishDto);
        // 查询flavor信息
        QueryWrapper<DishFlavor> wrapper = new QueryWrapper<>();
        wrapper.eq("dish_id", dishId);
        dishDto.setFlavors(dishFlavorService.list(wrapper));
        return R.success(dishDto);
    }

    @PutMapping
    public R<Object> updateDish(@RequestBody DishDto dishDto) {
        dishService.updateDishWithFlavor(dishDto);
        return R.success(null);
    }

    @DeleteMapping
    public R<Object> delDishByIds(String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        dishService.deleteDishesByIds(idList);
        return R.success(null);
    }

    @PostMapping("/status/{status}")
    public R<Object> changeDishStatus(@PathVariable("status") int status, String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        List<Dish> dishList = new ArrayList<>();
        idList.forEach(id -> {
            Dish dish = new Dish();
            dish.setId(id);
            dish.setStatus(status);
            dishList.add(dish);
        });
        dishService.updateBatchById(dishList);
        return R.success(null);
    }

    @GetMapping("/list")
    public R<List<DishDto>> getDishesByCategory(long categoryId) {
        QueryWrapper<Dish> wrapper = new QueryWrapper<>();
        wrapper.eq("category_id", categoryId)
                .eq("status", 1)
                .orderByAsc("sort")
                .orderByDesc("update_time");
        List<Dish> dishList = dishService.list(wrapper);
        LambdaQueryWrapper<DishFlavor> dishFlavorWrapper = new LambdaQueryWrapper<>();
        List<DishDto> dishDtos = dishList.stream().map(dish -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);
            dishFlavorWrapper.clear();
            dishFlavorWrapper.eq(DishFlavor::getDishId, dishDto.getId());
            dishDto.setFlavors(dishFlavorService.list(dishFlavorWrapper));
            return dishDto;
        }).collect(Collectors.toList());
        return R.success(dishDtos);
    }
}
