package com.payMyBuddy.payMyBuddy.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This aspect intercepts all requests and responses provided from HTTP endpoints,
 * with the aim of logging them.
 */
@Aspect
@Component
public class EndpointLoggingAspect {
    private static final Logger logger = LogManager.getLogger(EndpointLoggingAspect.class);
    private final ObjectWriter writer = new ObjectMapper().writerWithDefaultPrettyPrinter();

    @Pointcut("execution(* com.payMyBuddy.payMyBuddy.controller.*.*(..))")
    public void endpointMethods() {}

    @Before("execution(* com.payMyBuddy.payMyBuddy.controller.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String url = request.getRequestURL().toString();
        String method = request.getMethod();
        String controller = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String params = getRequestParams(request);

        logger.info("Calling endpoint - URL: {}, Method: {}, Controller: {}, MethodName: {}, Params: {}", url, method, controller, methodName, params);
    }

    @AfterReturning(pointcut = "endpointMethods() && @annotation(org.springframework.web.bind.annotation.GetMapping)", returning = "response")
    public void logSuccessGet( Object response) throws JsonProcessingException {
        if (response == null) {
            logger.warn("Please verify request method or provided params");
        } else {
            String jsonResponse = writer.writeValueAsString(response);
            logger.info("Endpoint success. Response : {}", jsonResponse);
        }
    }

    @AfterReturning(pointcut = "endpointMethods() && !@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void logSuccess() {
        logger.info("Endpoint success");
    }

    @AfterThrowing(pointcut = "endpointMethods()", throwing = "exception")
    public void logError(Exception exception) {
        logger.error("Endpoint error:", exception);
    }

    private String getRequestParams(HttpServletRequest request) {
        Map<String, String[]> requestParams = request.getParameterMap();
        return requestParams.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + String.join(",", entry.getValue()))
                .collect(Collectors.joining(", "));
    }
}
