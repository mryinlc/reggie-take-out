package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.reggie.common.R;
import com.reggie.pojo.User;
import com.reggie.service.UserService;
import com.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/code")
    public R<Integer> getCode(String phone, HttpSession session) {
        log.info("给号码{}发送验证码", phone);
        Integer code = ValidateCodeUtils.generateValidateCode(4);
        session.setAttribute("code", code);
        return R.success(code);
    }

    @PostMapping("/login")
    public R<Object> login(@RequestBody Map map, HttpSession session) {
        String saveCode = session.getAttribute("code").toString();
        if (!saveCode.equals(map.get("code").toString())) {
            return R.error("验证码错误");
        }
        String phone = map.get("phone").toString();
        if (phone == null)
            return R.error("电话号码出错了");
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", phone);
        User user = userService.getOne(wrapper);
        if (user == null) {
            user = new User();
            user.setPhone(phone);
            userService.save(user);
        }
        session.setAttribute("user", user);
        return R.success(null);
    }

    @PostMapping("/loginout")
    public R<Object> logout(HttpSession session) {
        session.removeAttribute("user");
        return R.success(null);
    }
}
