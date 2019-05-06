package me.exrates.scheduleservice.aspects;

import lombok.extern.log4j.Log4j2;
import me.exrates.ProcessIDManager;
import me.exrates.scheduleservice.events.ApplicationEventWithProcessId;
import me.exrates.scheduleservice.models.logging.MethodsLog;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static me.exrates.scheduleservice.utils.LoggingUtil.doBaseProfiling;
import static me.exrates.scheduleservice.utils.LoggingUtil.doBaseProfilingWithRegisterAndUnregister;
import static me.exrates.scheduleservice.utils.LoggingUtil.getAuthenticatedUser;
import static me.exrates.scheduleservice.utils.LoggingUtil.getExecutionTime;
import static me.exrates.scheduleservice.utils.LoggingUtil.getMethodName;

@Log4j2(topic = "Service_layer_log")
@Aspect
@Component
public class ServiceLayerLogAspect {

    @Around(" execution(* me.exrates.scheduleservice.services..*(..)) " +
            "&& (@annotation(org.springframework.transaction.event.TransactionalEventListener) " +
            "|| @annotation(org.springframework.context.event.EventListener)) " +
            "&& @annotation(org.springframework.scheduling.annotation.Async) ")
    public Object doBasicProfilingOfHandlers(ProceedingJoinPoint pjp) throws Throwable {
        String method = getMethodName(pjp);
        String args = Arrays.toString(pjp.getArgs());
        long start = System.currentTimeMillis();
        String user = getAuthenticatedUser();
        AtomicReference<Optional<String>> id = new AtomicReference<>();
        Arrays.stream(pjp.getArgs()).forEach(p -> {
            if (p instanceof ApplicationEventWithProcessId) {
                ApplicationEventWithProcessId event = (ApplicationEventWithProcessId) p;
                id.set(event.getProcessId());
            }
        });
        ProcessIDManager.registerNewThreadForParentProcessId(getClass(), id.get());
        try {
            Object result = pjp.proceed();
            log.debug(new MethodsLog(method, args, result, user, getExecutionTime(start), StringUtils.EMPTY));
            return result;
        } catch (Throwable ex) {
            log.debug(new MethodsLog(method, args, StringUtils.EMPTY, user, getExecutionTime(start), ex.getCause() + " " + ex.getMessage()));
            throw ex;
        } finally {
            ProcessIDManager.unregisterProcessId(getClass());
        }
    }

    @Around(" (execution(* me.exrates.scheduleservice.services.impl..*(..)) " +
            " || execution(* me.exrates.scheduleservice.api..*(..))) " +
            "&& !@annotation(org.springframework.scheduling.annotation.Async) " +
            "&& !@annotation(org.springframework.scheduling.annotation.Scheduled) ")
    public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
        return doBaseProfiling(pjp, log);
    }

    @Around(" execution(* me.exrates.scheduleservice.services..*(..)) " +
            "&& @annotation(org.springframework.scheduling.annotation.Async) ")
    public Object doBasicProfilingOfAsync(ProceedingJoinPoint pjp) throws Throwable {
        return doBaseProfilingWithRegisterAndUnregister(pjp, getClass(), log);
    }
}