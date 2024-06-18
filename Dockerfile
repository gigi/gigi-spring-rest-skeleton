FROM azul/zulu-openjdk-alpine:21-jre

ADD build/libs/app-0.0.1.jar /app.jar

ENTRYPOINT java -jar /app.jar
