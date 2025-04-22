1.  posgresql для auth
    - docker run -d --name authdb -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=authdb postgres
2. redis для refreshToken
    - docker run -d --name redisdb -p 6379:6379 redis redis-server --requirepass "redispassword"
3. zipkin для трассировки
   - docker run -d -p 9411:9411 openzipkin/zipkin (http://localhost:9411/zipkin)
4. Kafka
   - docker run -d -p 9092:9092 --name kafkaBroker apache/kafka:latest