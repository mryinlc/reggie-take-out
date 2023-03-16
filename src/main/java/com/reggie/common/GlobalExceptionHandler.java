package com.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice(annotations = {RestController.class, Controller.class})
@Slf4j
public class GlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<Object> duplicateEntryExceptionHandler(SQLIntegrityConstraintViolationException ex) {
        String errMsg = ex.getMessage();
        if (errMsg.contains("Duplicate entry")) {
            return R.error(errMsg.split(" ")[2] + "已存在");
        }
        return R.error("未知错误");
    }

    @ResponseBody
    @ExceptionHandler(CustomException.class)
    public R<Object> customExceptionHandler(CustomException ex) {
        return R.error(ex.getMessage());
    }
}
