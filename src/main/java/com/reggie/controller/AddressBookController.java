package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.reggie.common.BaseContext;
import com.reggie.common.R;
import com.reggie.pojo.AddressBook;
import com.reggie.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/addressBook")
@Api(tags = "地址操作相关接口")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    private LambdaQueryWrapper<AddressBook> lambdaQueryWrapper = new LambdaQueryWrapper<>();

    @ApiOperation("获取用户地址列表接口")
    @GetMapping("/list")
    public R<List<AddressBook>> getAddressList() {
        lambdaQueryWrapper.clear();
        lambdaQueryWrapper.eq(AddressBook::getUserId, BaseContext.getUserId())
                .orderByDesc(AddressBook::getUpdateTime);
        return R.success(addressBookService.list(lambdaQueryWrapper));
    }

    @GetMapping("/{id}")
    @ApiOperation("通过地址id获取用户地址接口")
    @ApiImplicitParams({
            // 对于路径中的参数，需要指定type为path，type值为query，且此时的name应为路径中指定的参数名，而不是方法中的参数名
            @ApiImplicitParam(name = "id", value = "地址id", required = true, type = "path")
    })
    public R<AddressBook> getAddressById(@PathVariable("id") long addressId) {
        AddressBook addressBook = addressBookService.getById(addressId);
        return addressBook != null ? R.success(addressBook) : R.error("指定地址不存在");
    }

    @PostMapping
    public R<Object> addAddress(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getUserId());
        addressBookService.save(addressBook);
        return R.success(null);
    }

    @PutMapping
    public R<Object> updateAddress(@RequestBody AddressBook addressBook) {
        addressBookService.updateById(addressBook);
        return R.success(null);
    }

    @DeleteMapping
    public R<Object> delAddress(Long ids) {
        addressBookService.removeById(ids);
        return R.success(null);
    }

    @GetMapping("/default")
    public R<AddressBook> getDefaultAddress() {
        lambdaQueryWrapper.clear();
        lambdaQueryWrapper.eq(AddressBook::getUserId, BaseContext.getUserId())
                .eq(AddressBook::getIsDefault, 1);
        AddressBook addressBook = addressBookService.getOne(lambdaQueryWrapper);
        return addressBook != null ? R.success(addressBook) : R.error("无默认地址");
    }

    @PutMapping("/default")
    public R<Object> setDefaultAddress(@RequestBody AddressBook addressBook) {
        // 把已有的默认地址的isDefault设为0
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(AddressBook::getIsDefault, 0)
                .eq(AddressBook::getUserId, BaseContext.getUserId())
                .eq(AddressBook::getIsDefault, 1);
        addressBookService.update(updateWrapper);
        // 把当前地址的isDefault设为1
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success(null);
    }
}
