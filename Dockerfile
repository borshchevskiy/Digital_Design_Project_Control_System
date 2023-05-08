FROM eclipse-temurin:18-jre-alpine
WORKDIR /opt/project-control-system
COPY app/target/*.jar *.jar
CMD ["java", "-jar", "*.jar" ]

