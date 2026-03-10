# Multi-stage build for cross-platform support (ARM and X86)
FROM --platform=$BUILDPLATFORM maven:3.8-eclipse-temurin-11 AS builder

WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# Runtime stage - using eclipse-temurin for cross-platform support
FROM eclipse-temurin:11-jre

WORKDIR /app

# Set timezone and encoding
ENV TZ=Asia/Shanghai
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8
ENV JAVA_OPTS="-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8"

# Create uploads directory
RUN mkdir -p /app/uploads

# Copy jar from builder stage
COPY --from=builder /app/target/library-backend-1.0.0.jar app.jar

EXPOSE 8416

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
