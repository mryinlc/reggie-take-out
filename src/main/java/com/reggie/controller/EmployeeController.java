package com.reggie.controller;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.pojo.Employee;
import com.reggie.service.EmployeeService;
import com.reggie.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee employee, HttpServletRequest request) {
        // 从数据库中查找相应用户名的记录
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee e = employeeService.getOne(queryWrapper);
        if (e == null)
            return R.error("用户名不存在");
        // 对传入的password进行处理
        String pwd = employee.getPassword();
        pwd = DigestUtils.md5DigestAsHex(pwd.getBytes());
        if (!pwd.equals(e.getPassword()))
            return R.error("密码错误");
        // 验证员工账户是否被禁用，status为0表示禁用，为1表示未禁用
        if (e.getStatus() == 0)
            return R.error("当前账户被禁用");
        request.getSession().setAttribute("employee", e);
        String jwtToken = JwtUtil.generateJwtToken(e.getId(), e.getName(), "employee");
        return R.success(e).add("jwt", jwtToken);
    }

    @PostMapping("/logout")
    public R<Object> logout(HttpSession session) {
        session.removeAttribute("employee");
        return R.success(null);
    }

    @PostMapping
    public R<Object> addEmployee(HttpSession session, @RequestBody Employee employee) {
        // 设置默认密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        // 以下公共信息使用mybatis-plus的自动填充功能进行填充
        // employee.setCreateTime(LocalDateTime.now());
        // employee.setUpdateTime(employee.getCreateTime());
        // long userId = ((Employee) session.getAttribute("employee")).getId();
        // employee.setCreateUser(userId);
        // employee.setUpdateUser(userId);
        employeeService.save(employee);
        log.info("成功添加用户: " + employee);
        return R.success(null);
    }

    @GetMapping("/page")
    public R<Page> getEmployees(@RequestParam("page") int page,
                                @RequestParam("pageSize") int pageSize,
                                @RequestParam(value = "name", required = false) String name) {
        // 创建分页构造器
        Page<Employee> pageInfo = new Page<>(page, pageSize);
        // 封装条件构造器
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(!StringUtils.isEmpty(name), "name", name);
        queryWrapper.orderByDesc("update_time");
        return R.success(employeeService.page(pageInfo, queryWrapper));
    }

    @PutMapping
    public R<Object> updateEmployee(HttpSession session, @RequestBody Employee employee) {
        // 以下公共信息使用mybatis-plus的自动填充功能进行填充
        // long userId = ((Employee) session.getAttribute("employee")).getId();
        // employee.setUpdateUser(userId);
        // employee.setUpdateTime(LocalDateTime.now());
        employeeService.updateById(employee);
        return R.success(null);
    }

    @GetMapping("/{id}")
    public R<Employee> getEmployeeById(@PathVariable("id") long id) {
        return R.success(employeeService.getById(id));
    }
}
