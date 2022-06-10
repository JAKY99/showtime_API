FROM openjdk:18-jdk-alpine 
EXPOSE 8080 
ARG JAR_FILE=target/demo-app-1.0.0.jar 
ADD ${JAR_FILE} 
ENTRYPOINT ["java","-jar","/app.jar"]
# CMD ["java", "Main"]