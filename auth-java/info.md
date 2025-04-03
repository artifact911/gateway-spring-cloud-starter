1. поднять posgresql
    - docker run -d --name authdb -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=authdb postgres
2. поднять redis
    - docker run -d --name redisdb -p 6379:6379 redis redis-server --requirepass "redispassword"