# Универсальный Dockerfile для сборки и запуска микросервисов на Java с использованием Gradle

# Этап сборки Gradle-проекта
FROM gradle:8.13-jdk23 AS builder

WORKDIR /builder

COPY settings.gradle build.gradle ./

COPY blocklist-producer/src/main ./blocklist-producer/src/main
COPY blocklist-producer/build.gradle ./blocklist-producer

COPY censure-producer/src/main ./censure-producer/src/main
COPY censure-producer/build.gradle ./censure-producer

COPY filter-processor/src/main ./filter-processor/src/main
COPY filter-processor/build.gradle ./filter-processor

COPY message-producer/src/main ./message-producer/src/main
COPY message-producer/build.gradle ./message-producer

# Сборка всех модулей
RUN gradle --parallel --no-daemon clean build -x test

# Этап исполнения
FROM eclipse-temurin:23-jre

WORKDIR /app
ARG SERVICE_NAME

# Копируем скомпилированный JAR из builder-этапа
COPY --from=builder /builder/$SERVICE_NAME/build/libs/*.jar app.jar

# Запуск сервиса
CMD ["java", "-jar", "app.jar"]