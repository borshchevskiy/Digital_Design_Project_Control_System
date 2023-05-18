mvn clean package -DskipTests=true assembly:single
docker-compose -f ./docker-compose/docker-compose.yml build
docker-compose -f ./docker-compose/docker-compose.yml up