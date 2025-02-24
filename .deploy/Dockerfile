# 빌드 단계
FROM openjdk:17-jdk-slim as builder
WORKDIR /build
# Gradle, Maven 빌드 도구 설치 후 빌드 진행 (예시)
COPY ../.github .
RUN ./gradlew build -x test

# 실행 단계
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY ../.env .env
COPY --from=builder /build/build/libs/delivery-0.0.1-SNAPSHOT.jar delivery.jar
ENTRYPOINT ["java", "-jar", "delivery.jar"]