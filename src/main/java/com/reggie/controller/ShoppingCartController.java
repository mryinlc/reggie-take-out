package com.reggie.controller;

import ch.qos.logback.core.hook.ShutdownHook;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.reggie.common.BaseContext;
import com.reggie.common.R;
import com.reggie.pojo.ShoppingCart;
import com.reggie.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    private final LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();

    @GetMapping("/list")
    public R<List<ShoppingCart>> getShoppingCartItems() {
        wrapper.clear();
        wrapper.eq(ShoppingCart::getUserId, BaseContext.getUserId())
                .orderByDesc(ShoppingCart::getCreateTime);
        return R.success(shoppingCartService.list(wrapper));
    }

    @PostMapping("/add")
    public R<ShoppingCart> addItem(@RequestBody ShoppingCart shoppingCart) {
        shoppingCart.setUserId(BaseContext.getUserId());
        // 根据当前userId和dishId或setmealId查询购物车项
        wrapper.clear();
        wrapper.eq(ShoppingCart::getUserId, shoppingCart.getUserId())
                .eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId())
                .eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        ShoppingCart item = shoppingCartService.getOne(wrapper);
        if (item != null) {
            // 当前菜品或套餐已存在
            item.setNumber(item.getNumber() + 1);
            shoppingCartService.updateById(item);
        } else {
            // 当前菜品或套餐不存在
            shoppingCart.setCreateTime(LocalDateTime.now());
            item = shoppingCart;
            shoppingCartService.save(shoppingCart);
        }
        return R.success(item);
    }

    @DeleteMapping("/clean")
    public R<Object> cleanShoppingCart() {
        wrapper.clear();
        wrapper.eq(ShoppingCart::getUserId, BaseContext.getUserId());
        shoppingCartService.remove(wrapper);
        return R.success(null);
    }

    @PostMapping("/sub")
    public R<Object> subNumInItem(@RequestBody ShoppingCart shoppingCart) {
        LambdaUpdateWrapper<ShoppingCart> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.setSql("number = number - 1")
                .eq(ShoppingCart::getUserId, BaseContext.getUserId());
        if (shoppingCart.getDishId() != null) {
            updateWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else {
            updateWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        shoppingCartService.update(updateWrapper);
        return R.success(null);
    }
}
