package me.exrates.scheduleservice.aspects;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import static me.exrates.scheduleservice.utils.LoggingUtil.doBaseProfiling;

@Log4j2(topic = "Dao_layer_log")
@Aspect
@Component
public class DaoLayerLogAspect {

    @Around(" (execution(* me.exrates.scheduleservice.repositories.impl..*(..)))")
    public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
        return doBaseProfiling(pjp, log);
    }
}