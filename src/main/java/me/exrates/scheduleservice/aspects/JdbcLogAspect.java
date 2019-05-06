package me.exrates.scheduleservice.aspects;


import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import static me.exrates.scheduleservice.utils.LoggingUtil.doBaseProfiling;

@Log4j2(topic = "Jdbc_query_layer_log")
@Aspect
@Component
public class JdbcLogAspect {

    @Around("execution(* org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations..*(..)) || " +
            "(execution(* org.springframework.jdbc.core.JdbcOperations..*(..))))")
    public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
        return doBaseProfiling(pjp, log);
    }
}