package pro.thinhha.core.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import pro.thinhha.core.util.JsonUtil;

import java.util.Arrays;

/**
 * Aspect for logging method execution time and details.
 */
@Slf4j
@Aspect
@Component
public class LoggingAspect {

    /**
     * Log execution time for methods annotated with @LogExecutionTime.
     */
    @Around("@annotation(pro.thinhha.core.annotation.LogExecutionTime) || " +
            "@within(pro.thinhha.core.annotation.LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        log.debug("Executing {}.{}()", className, methodName);

        Object result;
        try {
            result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            log.info("{}.{} executed in {} ms", className, methodName, executionTime);
        } catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("{}.{} threw exception after {} ms", className, methodName, executionTime, throwable);
            throw throwable;
        }

        return result;
    }

    /**
     * Log method entry and exit for all controller methods.
     */
    @Around("execution(* pro.thinhha..controller..*.*(..))")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        Object[] args = joinPoint.getArgs();

        log.info(">> {}.{}() - Args: {}", className, methodName, Arrays.toString(args));

        Object result;
        try {
            result = joinPoint.proceed();
            log.info("<< {}.{}() - Result: {}", className, methodName, JsonUtil.toJson(result));
        } catch (Throwable throwable) {
            log.error("<< {}.{}() - Exception: {}", className, methodName, throwable.getMessage());
            throw throwable;
        }

        return result;
    }
}
