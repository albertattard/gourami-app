FROM container-registry.oracle.com/java/jdk-no-fee-term:21-oraclelinux8 AS base


FROM base AS buidler

WORKDIR /opt/app
COPY ./target/gourami-app.jar gourami-app.jar
RUN mkdir extracted \
    && java -Djarmode=layertools -jar gourami-app.jar extract --destination extracted


FROM base

WORKDIR /opt/app
COPY --from=buidler /opt/app/extracted/dependencies/ ./
COPY --from=buidler /opt/app/extracted/spring-boot-loader/ ./
COPY --from=buidler /opt/app/extracted/snapshot-dependencies/ ./
COPY --from=buidler /opt/app/extracted/application/ ./

EXPOSE 8080

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
