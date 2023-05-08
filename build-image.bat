call mvn clean package -DskipTests=true assembly:single
call docker-compose build
call docker-compose up