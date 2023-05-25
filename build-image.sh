#mvn clean package -DskipTests=true assembly:single
mvn clean package -DskipTests=true
docker-compose -f ./docker-compose/docker-compose.yml up