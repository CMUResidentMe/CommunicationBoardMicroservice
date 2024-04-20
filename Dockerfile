# # Start with a base image containing Java runtime (adoptopenjdk is open source and free to use)
# # FROM adoptopenjdk:11-jre-hotspot as builder
# FROM openjdk:17-jdk-slim as build

# # The application's jar file
# ARG JAR_FILE=target/*.jar

# # Add the application's jar to the container
# ADD ${JAR_FILE} app.jar

# # Unpack the jar file
# RUN java -Djarmode=layertools -jar app.jar extract

# # Start with a new base image to create a smaller image
# FROM adoptopenjdk:11-jre-hotspot

# # Copy the extracted layers
# COPY --from=builder dependencies/ ./
# COPY --from=builder spring-boot-loader/ ./
# COPY --from=builder snapshot-dependencies/ ./
# COPY --from=builder application/ ./

# # Expose port 
# EXPOSE 5000

# # Run the application
# ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
# Start with a base image containing Java runtime
FROM openjdk:17-jdk-slim as build

EXPOSE 6000

# The application's jar file
ARG JAR_FILE=target/*.jar

# Add the application's jar to the container
ADD ${JAR_FILE} app.jar

# Run the jar file
ENTRYPOINT ["java","-jar","/app.jar"]
