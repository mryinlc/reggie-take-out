package com.reggie.config;

import com.reggie.common.JacksonObjectMapper;
import com.reggie.filter.CheckLoginFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import java.util.Arrays;
import java.util.List;


@Slf4j
@Configuration
public class ReggieMvcConfigurer implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("redirect:/backend/page/login/login.html");
        registry.addViewController("/u").setViewName("redirect:/front/page/login.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("静态资源处理器已添加...");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }

    @Bean
    public FilterRegistrationBean<Filter> checkLoginFilter() {
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new CheckLoginFilter());
        bean.setUrlPatterns(Arrays.asList("/*"));
        return bean;
    }

    // 为防止id过长，导致前端js解析时损失精度，需要自行添加MessageConverter来在将返回对象转换为JSON数据时，将对象中的long类型数据转换为字符串
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(new JacksonObjectMapper());
        converters.add(0, converter);
    }
}
