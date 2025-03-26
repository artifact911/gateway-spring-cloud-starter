1. поднять posgresql
    - docker run -d --name authdb -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=authdb postgres