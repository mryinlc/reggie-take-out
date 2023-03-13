package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.pojo.Category;

public interface CategoryService extends IService<Category> {
    void removeCategoryById(long catId);
}
