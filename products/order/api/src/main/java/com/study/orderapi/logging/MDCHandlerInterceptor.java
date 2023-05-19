package com.study.orderapi.logging;

import com.study.orderapi.security.TokenAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import java.util.Optional;

@Slf4j
public class MDCHandlerInterceptor implements HandlerInterceptor {
    public static final String MDC_KEY_USER_NAME = "username"; // todo : 공통 constant 따로 빼기?

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Optional<String> jwtFromRequest = TokenAuthenticationFilter.getJwtFromRequest(request);
        JwtPayloadExtractor.Payload payload = JwtPayloadExtractor.extract(jwtFromRequest);
        MDC.put(MDC_KEY_USER_NAME, payload.getName());
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
        MDC.clear();
    }
}
