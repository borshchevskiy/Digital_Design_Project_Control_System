mvn clean package -DskipTests=true assembly:single
docker-compose build
docker-compose up