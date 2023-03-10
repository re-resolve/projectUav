package com.example.projectUav.filter;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class corsFilter implements WebMvcConfigurer {
    /**
     * 解决跨域问题
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 匹配所有的路径
                .allowCredentials(true) // 设置允许凭证,是否发送Cookie信息
                .allowedHeaders("*")   // 设置请求头
                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS") // 设置允许的方式
                .allowedOriginPatterns("*");// 放行哪些原始域
    }
}
