package com.school.aspect;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * AOP aspect for logging controller method execution.
 * <p>
 * Automatically logs entry, exit (with execution time and HTTP status), and failures
 * for all methods in {@code com.school.controller} without modifying controller code.
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * Pointcut matching all methods in controller classes.
     */
    @Pointcut("execution(* com.school.controller.*.*(..))")
    public void controllerMethods() {
    }

    /**
     * Around advice that logs method entry, exit with timing, and exceptions.
     *
     * @param joinPoint the join point representing the intercepted method
     * @return the method's return value
     * @throws Throwable if the intercepted method throws an exception
     */
    @Around("controllerMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String args = Arrays.toString(joinPoint.getArgs());

        log.info("Started {}.{} with args: {}", className, methodName, args);

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - start;

            if (result instanceof ResponseEntity<?> response) {
                log.info("Finished {}.{} in {} ms with result: {}", className, methodName, elapsed, response.getStatusCode());
            } else {
                log.info("Finished {}.{} in {} ms", className, methodName, elapsed);
            }

            return result;
        } catch (Throwable ex) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("Failed {}.{} in {} ms with exception: {}", className, methodName, elapsed, ex.getMessage());
            throw ex;
        }
    }
}
