docker run --name postgres-container -e POSTGRES_USER=user1 -e POSTGRES_PASSWORD=password -e POSTGRES_DB=user_service_db -p 5432:5432 -d postgres

# connect into it
docker exec -it postgres-container bash
# connect to db
psql -U user1 -d user_service_db -W
password
# create new db
create database xxxx