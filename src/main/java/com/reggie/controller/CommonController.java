package com.reggie.controller;

import com.reggie.common.CustomException;
import com.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${spring.profiles.active}")
    private String envName;

    @GetMapping("/env")
    public String getEnvName() {
        return envName;
    }

    @Value("${reggie.base-path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> uploadFile(@RequestPart("file") MultipartFile file) {
        // 读取上传的文件后缀
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf('.'));
        // 使用UUID生成文件名
        String fileName = UUID.randomUUID() + suffix;
        // 判断basePath中的目录是否存在
        File baseDir = new File(basePath);
        if (!baseDir.exists())
            if (!baseDir.mkdir())
                throw new CustomException("服务器文件目录不存在");
        try {
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            throw new CustomException("上传文件过程出错");
        }
        return R.success(fileName);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        try {
            FileInputStream inStream = new FileInputStream(new File(basePath + name));
            byte[] bytes = new byte[inStream.available()];
            inStream.read(bytes);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(bytes);
            outputStream.flush();
            outputStream.close();
            inStream.close();
        } catch (IOException e) {
            throw new CustomException("文件下载失败");
        }
    }

    @GetMapping("/download2")
    public ResponseEntity<byte[]> download(String name) {
        try {
            FileInputStream inStream = new FileInputStream(new File(basePath + name));
            byte[] bytes = new byte[inStream.available()];
            inStream.read(bytes);
            // 创建HttpHeaders对象设置响应头信息
            MultiValueMap<String, String> headers = new HttpHeaders();
            // 设置要下载方式以及下载文件的名字
            headers.add("Content-Disposition", "attachment;filename=" + name);
            // 设置响应状态码
            HttpStatus statusCode = HttpStatus.OK;
            // 创建ResponseEntity对象
            ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(bytes, headers, statusCode);
            // 关闭输入流
            inStream.close();
            return responseEntity;
        } catch (IOException e) {
            throw new CustomException("文件下载出错");
        }
    }
}
