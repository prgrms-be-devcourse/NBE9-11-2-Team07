FROM container-registry.oracle.com/graalvm/jdk:25
WORKDIR /app

# 캐시 방지용 주석 (아무 글자나 넣으세요) - v1.0.1
COPY build/libs/mozu-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]