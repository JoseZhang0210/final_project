# 第一階段：Maven 編譯與打包 (Build Stage)
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# 快取 Maven 依賴加速後續構建
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 複製原始碼並編譯打包（跳過單元測試）
COPY src ./src
RUN mvn clean package -DskipTests

# 第二階段：輕量 JRE 運行 (Runtime Stage)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 設定容器時區為台灣時間 (Asia/Taipei)
ENV TZ=Asia/Taipei
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Taipei /etc/localtime && \
    echo "Asia/Taipei" > /etc/timezone

# 複製打包完成的 jar 檔
COPY --from=builder /app/target/*.jar app.jar

# 建立上傳檔案的存放目錄
RUN mkdir -p /app/uploads /app/upload

EXPOSE 8081

# JVM 記憶體參數最佳化及設定時區
ENV JAVA_OPTS="-Duser.timezone=Asia/Taipei -Xms256m -Xmx512m -XX:+UseSerialGC"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

