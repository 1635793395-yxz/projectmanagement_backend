# 第一阶段：构建 (Build)
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 第二阶段：运行 (Run)
FROM eclipse-temurin:21-jdk
WORKDIR /app

# 复制构建好的 jar 包
COPY --from=build /app/target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java","-jar","app.jar"]
