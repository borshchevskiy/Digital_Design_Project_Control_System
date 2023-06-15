FROM eclipse-temurin:18-jre-alpine
WORKDIR /opt/project-control-system
COPY project-control-system-app/target/*.jar *.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "*.jar" ]

