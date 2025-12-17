# Dockerfile for Spring Boot WhatsApp Service
# Uses pre-built executable from build/libs/
ARG TARGETPLATFORM
FROM --platform=${TARGETPLATFORM:-linux/amd64} eclipse-temurin:21-jre-jammy

# Install necessary packages
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Create app user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Set working directory
WORKDIR /app

# Copy the pre-built executable JAR from build/libs/
# Note: The JAR file should be built using: ./gradlew build
# This copies the executable JAR (not the -plain.jar)
COPY build/libs/whatsapp-[0-9]*.jar app.jar

# Change ownership to app user
RUN chown -R appuser:appuser /app
USER appuser

# Expose port
EXPOSE 9001

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:9001/actuator/health || exit 1

# JVM options for containerized environment
ENV JAVA_OPTS="-Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Default Spring profile
ENV SPRING_PROFILES_ACTIVE=aws-dev

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
