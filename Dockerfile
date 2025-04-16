# Step 1: Use the Java runtime image (base image with JDK)
FROM openjdk:17-jdk-alpine

# Step 2: Set the working directory inside the container
WORKDIR /app

# Step 3: Copy the pre-built JAR file from your local machine into the container
COPY target/NokiaDemo-0.0.1-SNAPSHOT.jar app.jar

# Step 4: Run the application
CMD ["java", "-jar", "app.jar"]
