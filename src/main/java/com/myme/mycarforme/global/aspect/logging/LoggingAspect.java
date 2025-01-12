package com.myme.mycarforme.global.aspect.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    private static final int MAX_REQUEST_CONTENT_LENGTH = 1000;
    private static final int MAX_RESPONSE_CONTENT_LENGTH = 1000;
    private static final int LONG_EXECUTION_TIME = 1000;

    // API 컨트롤러 메소드 로깅
    @Around("execution(* com.myme.mycarforme.domains..*.api.*.*(..))")
    public Object loggingApi(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 메소드 정보
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        // Request 정보
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String requestContent = getRequestContent(request);

        // 기본 로그
        log.info("[API] {}.{} Start - URI: {} {}",
                className, methodName, request.getMethod(), request.getRequestURI());

        // 요청 파라미터가 있는 경우만 로깅
        if (requestContent != null && !requestContent.isBlank()) {
            log.info("[API] Request Content: {}", truncateContent(requestContent, MAX_REQUEST_CONTENT_LENGTH));
        }

        try {
            // 실제 메소드 실행
            Object result = joinPoint.proceed();

            // 응답 로깅 (null이 아닌 경우만)
            if (result != null) {
                String responseContent = new ObjectMapper().writeValueAsString(result);
                log.info("[API] Response Content: {}", truncateContent(responseContent, MAX_RESPONSE_CONTENT_LENGTH));
            }

            // 실행 시간 로깅
            long executionTime = System.currentTimeMillis() - startTime;
            log.info("[API] {}.{} End - Execution Time: {}ms", className, methodName, executionTime);

            return result;

        } catch (Exception e) {
            // 예외 발생 시 로깅
            log.error("[API] {}.{} Exception - {} : {}",
                    className, methodName, e.getClass().getSimpleName(), e.getMessage());
            throw e;
        }
    }

    // 서비스 레이어 메소드 실행 시간 측정
    @Around("execution(* com.myme.mycarforme.domains..*.service.*.*(..))")
    public Object loggingServiceExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;
            String className = joinPoint.getTarget().getClass().getSimpleName();
            String methodName = joinPoint.getSignature().getName();

            // 실행 시간이 1초 이상인 경우에만 로깅
            if (executionTime > LONG_EXECUTION_TIME) {
                log.warn("[Service] Long execution time detected - {}.{} : {}ms",
                        className, methodName, executionTime);
            }

            return result;

        } catch (Exception e) {
            String className = joinPoint.getTarget().getClass().getSimpleName();
            String methodName = joinPoint.getSignature().getName();

            log.error("[Service] {}.{} Exception - {} : {}",
                    className, methodName, e.getClass().getSimpleName(), e.getMessage());
            throw e;
        }
    }

    private String getRequestContent(HttpServletRequest request) {
        try {
            // GET 메소드는 파라미터만 로깅
            if ("GET".equals(request.getMethod())) {
                return request.getQueryString();
            }

            // POST, PUT 등은 body 내용 로깅
            String contentType = request.getContentType();
            if (contentType != null && contentType.contains("application/json")) {
                return StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            log.warn("Failed to read request content", e);
        }
        return null;
    }

    private String truncateContent(String content, int maxLength) {
        if (content == null) return null;
        if (content.length() <= maxLength) return content;
        return content.substring(0, maxLength) + "...";
    }
}
