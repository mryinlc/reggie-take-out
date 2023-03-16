package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.reggie.common.R;
import com.reggie.pojo.User;
import com.reggie.service.UserService;
import com.reggie.utils.JwtUtil;
import com.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/code")
    public R<String> getCode(String phone) {
        log.info("给号码{}发送验证码", phone);
        String code = ValidateCodeUtils.generateValidateCode(4).toString();
        // session.setAttribute("code", code);
        // 将验证码存储到redis中
        redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);
        return R.success(code);
    }

    @PostMapping("/login")
    public R<String> login(@RequestBody Map map, HttpSession session) {
        // String saveCode = session.getAttribute("code").toString();
        String phone = (String) map.get("phone");
        if (phone == null)
            return R.error("电话号码出错了");
        String saveCode = (String) redisTemplate.opsForValue().get(phone);
        if (saveCode == null)
            return R.error("验证码已过期");
        if (!saveCode.equals(map.get("code").toString())) {
            return R.error("验证码错误");
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", phone);
        User user = userService.getOne(wrapper);
        if (user == null) {
            user = new User();
            user.setPhone(phone);
            userService.save(user);
        }
        session.setAttribute("user", user);
        // 删除缓存中的验证码
        redisTemplate.delete(phone);
        String jwtToken = JwtUtil.generateJwtToken(user.getId(), user.getName(), "user");
        return R.success(jwtToken);
    }

    @PostMapping("/loginout")
    public R<Object> logout(HttpSession session) {
        session.removeAttribute("user");
        return R.success(null);
    }
}
