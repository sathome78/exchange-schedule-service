package me.exrates.scheduleservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.log4j.Log4j2;
import me.exrates.scheduleservice.configurations.CacheConfiguration;
import me.exrates.scheduleservice.configurations.DatabaseConfiguration;
import me.exrates.scheduleservice.configurations.SSMConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Log4j2
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableJpaRepositories(basePackages = {"me.exrates.scheduleservice.repositories"})
@Import({
        CacheConfiguration.class,
        DatabaseConfiguration.class,
        SSMConfiguration.class
})
public class ScheduleServiceConfiguration {

    public static final String JSON_MAPPER = "jsonMapper";

    @Bean(JSON_MAPPER)
    public ObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
