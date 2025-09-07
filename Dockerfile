# Use OpenJDK 21 runtime
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the pre-built jar file
COPY target/DevicesAPI-0.0.1-SNAPSHOT.jar app.jar

# Create non-root user for security
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

# Expose port 8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]