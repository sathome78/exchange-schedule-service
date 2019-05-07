FROM java:8
VOLUME /tmp
ARG APP_PATH=/schedule
ARG ENVIRONMENT

RUN mkdir -p schedule-service
COPY ./target/schedule-service-0.0.1-SNAPSHOT.jar ${APP_PATH}/schedule.jar
COPY ./target/config/${ENVIRONMENT}/application.yml ${APP_PATH}/application.yml
COPY ./target/config/${ENVIRONMENT}/logs.properties ${APP_PATH}/logs.properties
COPY ./target/config/${ENVIRONMENT}/log4j2.xml ${APP_PATH}/log4j2.xml

ARG CONFIG_FILE_PATH="-Dspring.config.location="${ENVIRONMENT}"/application.yml"

WORKDIR ${APP_PATH}

EXPOSE 8080
CMD java -jar schedule.jar $CONFIG_FILE_PATH