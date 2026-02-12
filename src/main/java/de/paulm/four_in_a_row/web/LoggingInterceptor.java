package de.paulm.four_in_a_row.web;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String CYAN = "\u001B[36m";

    @SuppressWarnings("null")
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        request.setAttribute("startTime", System.currentTimeMillis());
        log.info("\n{}>> Incoming Request:{} {}{} {}{}{}",
                CYAN, RESET, YELLOW, request.getMethod(), GREEN, request.getRequestURI(), RESET);
        return true;
    }

    @SuppressWarnings("null")
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        Long startTime = (Long) request.getAttribute("startTime");
        Long duration = System.currentTimeMillis() - startTime;
        log.info("\n{}Completed request:{} {} {}, Status: {}, Duration: {}ms", CYAN, RESET, request.getMethod(),
                request.getRequestURI(),
                response.getStatus(), duration);
    }

}