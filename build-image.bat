call mvn clean package -DskipTests=true assembly:single
call docker-compose -f ./docker-compose/docker-compose.yml build
call docker-compose -f ./docker-compose/docker-compose.yml build