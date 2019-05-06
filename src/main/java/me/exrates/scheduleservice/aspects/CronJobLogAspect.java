package me.exrates.scheduleservice.aspects;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import static me.exrates.scheduleservice.utils.LoggingUtil.doBaseProfiling;
import static me.exrates.scheduleservice.utils.LoggingUtil.doBaseProfilingWithRegisterCronAndUnregister;

@Log4j2(topic = "Cron_job_layer_log")
@Aspect
@Component
public class CronJobLogAspect {

    @Around(" (execution(* me.exrates.scheduleservice.jobs..*(..))) " +
            "&& @annotation(org.springframework.scheduling.annotation.Scheduled)")
    public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
        return doBaseProfilingWithRegisterCronAndUnregister(pjp, getClass(), log);
    }
}