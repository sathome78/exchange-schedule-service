package me.exrates.scheduleservice.utils;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.exrates.ProcessIDManager;
import me.exrates.scheduleservice.models.logging.MethodsLog;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class LoggingUtil {

    public static long getExecutionTime(long start) {
        return System.currentTimeMillis() - start;
    }

    public static Object doBaseProfiling(ProceedingJoinPoint pjp, org.apache.logging.log4j.Logger log) throws Throwable {
        String method = getMethodName(pjp);
        String args = Arrays.toString(pjp.getArgs());
        long start = System.currentTimeMillis();
        String user = getAuthenticatedUser();
        try {
            Object result = pjp.proceed();
            log.debug(new MethodsLog(method, args, result, user, getExecutionTime(start), StringUtils.EMPTY));
            return result;
        } catch (Throwable ex) {
            log.debug(new MethodsLog(method, args, StringUtils.EMPTY, user, getExecutionTime(start), formatException(ex)));
            throw ex;
        }
    }

    public static Object doBaseProfilingWithRegisterAndUnregister(ProceedingJoinPoint pjp, Class clazz, org.apache.logging.log4j.Logger log) throws Throwable {
        String method = getMethodName(pjp);
        String args = Arrays.toString(pjp.getArgs());
        long start = System.currentTimeMillis();
        String user = getAuthenticatedUser();
        ProcessIDManager.registerNewThreadForParentProcessId(clazz, Optional.empty());
        try {
            Object result = pjp.proceed();
            log.debug(new MethodsLog(method, args, result, user, getExecutionTime(start), StringUtils.EMPTY));
            return result;
        } catch (Throwable ex) {
            log.debug(new MethodsLog(method, args, StringUtils.EMPTY, user, getExecutionTime(start), ex.getCause() + " " + ex.getMessage()));
            throw ex;
        } finally {
            ProcessIDManager.unregisterProcessId(clazz);
        }
    }

    public static Object doBaseProfilingWithRegisterCronAndUnregister(ProceedingJoinPoint pjp, Class clazz, org.apache.logging.log4j.Logger log) throws Throwable {
        String method = getMethodName(pjp);
        String args = Arrays.toString(pjp.getArgs());
        long start = System.currentTimeMillis();
        String user = getAuthenticatedUser();
        ProcessIDManager.registerCronJobProcessId(clazz);
        try {
            Object result = pjp.proceed();
            log.debug(new MethodsLog(method, args, result, user, getExecutionTime(start), StringUtils.EMPTY));
            return result;
        } catch (Throwable ex) {
            log.debug(new MethodsLog(method, args, StringUtils.EMPTY, user, getExecutionTime(start), ex.getCause() + " " + ex.getMessage()));
            throw ex;
        } finally {
            ProcessIDManager.unregisterProcessId(clazz);
        }
    }

    public static String getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? null : authentication.getName();
    }

    public static String getMethodName(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String fullClassName = signature.getDeclaringTypeName();
        return getMethodName(fullClassName, signature.getMethod().getName());
    }

    public static String getMethodName(String fullClassName, String methodName) {
        return String.join("#", fullClassName.substring(fullClassName.lastIndexOf(".") + 1), methodName);
    }

    public static String formatException(Throwable throwable) {
        return String.join(" ", throwable.getClass().getName(), throwable.getMessage());
    }
}