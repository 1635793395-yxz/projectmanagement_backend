# 第一阶段：构建 (Build)
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
# 开始打包，跳过测试以加速构建
RUN mvn clean package -DskipTests

# 第二阶段：运行 (Run)
FROM openjdk:17-jdk-slim
WORKDIR /app
# 注意：这里假设你的 jar 包名字包含 snapshot，如果 pom.xml 改过名，这里要对应修改
# 下面这行命令会自动找到 target 目录下生成的 jar 包并重命名为 app.jar
COPY --from=build /app/target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java","-jar","app.jar"]