# Use Amazon Corretto 21 as the base image
FROM openjdk:22-jdk

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the target directory to the container
COPY build/libs/TheArtGallery-0.0.1-SNAPSHOT.jar /app/TheArtGallery.jar

# Expose the port your Spring Boot app runs on (default is 8080)
EXPOSE 3333

# Command to run the JAR file
ENTRYPOINT ["java", "-jar", "/app/TheArtGallery.jar"]
