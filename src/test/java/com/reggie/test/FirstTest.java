package com.reggie.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.reggie.pojo.User;
import com.reggie.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class FirstTest {
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private EmployeeService employeeService;

    @Test
    public void testSerializer() throws JsonProcessingException {
        // Employee e = employeeService.getById(1);
        // redisTemplate.opsForValue().set("1", e);
        // System.out.println(redisTemplate.opsForValue().get("1"));
        User user = new User();
        user.setId((long) 123);
        user.setPhone("12345678910");
        user.setName("test");
        List<User> users = Arrays.asList(user);
        redisTemplate.opsForValue().set("users", users);
        users = (List<User>) redisTemplate.opsForValue().get("users");
        users.forEach(System.out::println);
    }
}
