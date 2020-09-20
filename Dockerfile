FROM adoptopenjdk/openjdk11-openj9:alpine-slim
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT  java $JAVA_OPTS -jar /app.jar


