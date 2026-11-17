# Prime

Panel demonstracyjny Spring Boot 4.1.2, Hibernate ORM 7.4.16.Final i Java 25 LTS. Dostępne warianty: WAR dla WildFly z PostgreSQL, kontener Docker z embedded Tomcatem, PrimeFaces i SQLite oraz wykonywalny WAR Spring Boot z PostgreSQL. Konto demo: `prime` / `prime123`.

## WildFly + PostgreSQL

```sh
docker compose up -d postgres
mvn -Pwildfly clean package
cp target/prime-0.0.1.war "$WILDFLY_HOME/standalone/deployments/"
```

Aplikacja: `http://localhost:8080/prime-0.0.1/`. Ustaw `WILDFLY_HOME` na katalog WildFly. Konfigurację bazy można nadpisać przez `DB_URL`, `DB_USERNAME` i `DB_PASSWORD`.

## Docker + SQLite

Kontener zawiera Spring Boot, embedded Tomcat, JSF/PrimeFaces i SQLite. Dane SQLite są przechowywane w wolumenie `prime-sqlite-data`.

```sh
docker compose up -d --build prime
docker compose logs -f prime
```

Aplikacja: `http://localhost:8888/`. Zatrzymanie kontenera bez usuwania danych: `docker compose stop prime`. `docker compose down -v` usuwa wolumeny.

## Spring Boot + PostgreSQL

```sh
docker compose up -d postgres
mvn -Pspringboot clean package
java -jar target/prime-0.0.1.war --spring.profiles.active=springboot
```

Aplikacja: `http://localhost:8080/`. Ten wariant i WildFly używają portu 8080.

## Pobieranie

W wydaniu `0.0.1` dostępny jest WAR dla WildFly oraz obrazy Docker dla `linux/amd64` i `linux/arm64`.

```sh
docker load --input prime-0.0.1-docker-linux-amd64.tar
docker run -d --name prime -p 8888:8888 \
  -v prime-sqlite-data:/data \
  -e 'DB_URL=jdbc:sqlite:/data/prime.db?foreign_keys=on&date_string_format=yyyy-MM-dd' \
  prime:0.0.1-linux-amd64
```

Dla ARM64 pobierz archiwum `prime-0.0.1-docker-linux-arm64.tar` i użyj obrazu `prime:0.0.1-linux-arm64`.
