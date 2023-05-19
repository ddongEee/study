package com.study.orderapi.config;

import com.study.orderapi.logging.ApiCallCheckHandlerInterceptor;
import com.study.orderapi.logging.MDCHandlerInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private static final int ORDER_1 = 1;
    private static final int ORDER_2 = 2;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        WebMvcConfigurer.super.addInterceptors(registry);
        registry.addInterceptor(new MDCHandlerInterceptor())
                .excludePathPatterns("/css/**")
                .order(ORDER_1);

        registry.addInterceptor(new ApiCallCheckHandlerInterceptor())
                .excludePathPatterns("/public/**") // todo : public 도 logging 할지 optional?
                .order(ORDER_2);
    }
}
