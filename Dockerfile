# 빌드 이미지로 OpenJDK 17 & Gradle을 지정
FROM gradle:7.6.1-jdk17 AS build

# 소스코드를 복사할 작업 디렉토리를 생성
WORKDIR /app

# 라이브러리 설치에 필요한 파일만 복사
COPY build.gradle settings.gradle ./

RUN gradle dependencies --no-daemon

# 호스트 머신의 소스코드를 작업 디렉토리로 복사
COPY . /app

# Gradle 빌드를 실행하여 JAR 파일 생성
RUN gradle build -x test

# 런타임 이미지로 OpenJDK 17 지정
FROM openjdk:17.0.1

# 애플리케이션을 실행할 작업 디렉토리를 생성
WORKDIR /app

# 빌드 이미지에서 생성된 JAR 파일을 런타임 이미지로 복사
COPY --from=build /app/build/libs/*.jar /app/seunghyo-backend.jar

EXPOSE 8999
ENTRYPOINT ["java"]
CMD ["-jar", "seunghyo-backend.jar","--spring.profiles.active=dev"]