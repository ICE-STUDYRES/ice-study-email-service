FROM openjdk:21-jre-slim

WORKDIR /app

# 필요한 패키지 설치
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# 애플리케이션 사용자 생성
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

# JAR 파일 복사
COPY build/libs/*.jar app.jar

# JVM 옵션 설정
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# 포트 노출
EXPOSE 8080

# 헬스체크
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
