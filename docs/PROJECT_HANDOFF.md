# Prime — przekazanie pracy

Stan na 2026-11-17. Zasady i wymagania agenta: [`AGENTS.md`](../AGENTS.md).

## Wymagania i wersje

- Java 25 LTS, stabilne wersje bibliotek, Spring Boot 4, Lombok i WAR dla WildFly.
- Profil `wildfly` (domyślny): JSF/PrimeFaces, PostgreSQL, Flyway; WAR na domyślnych portach serwera.
- Profil `docker`: JSF/PrimeFaces na embedded Tomcat, SQLite, port `8888`; uruchomienie w obrazie Docker.
- Profil `springboot`: JSF/PrimeFaces na embedded Tomcat, PostgreSQL, port `8080`; wykonywalny WAR uruchamiany przez `java -jar`. Zajmuje ten sam port co WildFly.
- Dane: klient → zamówienia → przychody; usuwanie kaskadowe. Dane PostgreSQL i SQLite utrwalane są w wolumenach Dockera.

Profil `wildfly` korzysta z JSF, CDI i EL dostarczanych przez WildFly. `application-wildfly.properties` wyklucza autokonfigurację Mojarra JoinFaces, a WAR nie może zawierać embedded EL/Tomcata.

| Biblioteka | Wersja |
| --- | ---: |
| Spring Boot | `4.1.2` |
| JoinFaces | `6.1.2` |
| PrimeFaces | `16.0.2` |
| Hibernate ORM | `7.4.16.Final` |
| Flyway | `13.12.2` |
| PostgreSQL JDBC | `42.7.14` |
| SQLite JDBC | `3.54.1.0` |
| Lombok | `1.18.48` |
| JUnit Jupiter | `6.1.3` |

## Budowanie i uruchamianie

```sh
docker compose up -d --build prime # docker: embedded Tomcat + PrimeFaces + SQLite
docker compose up -d postgres           # PostgreSQL dla WildFly lub Spring Boot
mvn -Pwildfly clean package             # WAR dla WildFly
mvn -Pspringboot clean package          # wykonywalny WAR dla Spring Boot
```

WAR: `target/prime-0.0.1.war`; wdrożyć do `$WILDFLY_HOME/standalone/deployments/`, URL `/prime-0.0.1/`. WildFly korzysta z PostgreSQL na porcie 5432.

Profil `docker` uruchamia się poleceniem `docker compose up -d --build prime`; URL `http://localhost:8888/`. Obraz zawiera aplikację Spring Boot, embedded Tomcat, JSF/PrimeFaces i sterownik SQLite. Plik bazy znajduje się w wolumenie `prime-sqlite-data`.

Profil `springboot` uruchamia się po starcie PostgreSQL poleceniem `java -jar target/prime-0.0.1.war --spring.profiles.active=springboot`; URL `http://localhost:8080/`. Nie może działać równocześnie z WildFly na tym samym porcie.

## Zrobione

- JSF `index.xhtml` jest stroną startową WildFly. Logowanie i panel aktualizują się przez Ajax pod tym samym URL.
- Panel ma zakładki klientów, zamówień i przychodów, formularze dodawania oraz menu edycji/usuwania.
- Wszystkie trzy warianty wykorzystują te same widoki JSF/PrimeFaces. Profil `docker` korzysta z SQLite, a profile `wildfly` i `springboot` z PostgreSQL.
- Serwisy zawierają reguły i granice transakcji; widoki używają modeli DTO. Lombok generuje wymagane konstruktory i akcesory.
- PostgreSQL: migracje `V1`–`V6`, relacje i kaskady, walidacja, unikalność e-maila; testy Testcontainers.
- SQLite: migracje `db/migration-sqlite`, przykładowe powiązane dane, `foreign_keys=on`; ustawiono `date_string_format=yyyy-MM-dd` dla xerial JDBC.
- Format waluty/dat/locale skonfigurowany centralnie przez `application.properties`, `FormatProperties` i `DisplayFormatters`.
- Compose udostępnia usługę `docker` na porcie 8888 oraz PostgreSQL na porcie 5432. Każda baza ma oddzielny wolumen.

## Do zrobienia / ograniczenia

1. Logowanie demonstracyjne (`prime` / `prime123`); przed produkcją dodać Spring Security, haszowane hasła, zewnętrzne sekrety i ochronę CSRF.
2. Testy obejmują serwisy i migracje/kaskady PostgreSQL, nie pełne przepływy UI. Testcontainers wymaga Dockera.
3. Nie zmieniać zapisanych migracji Flyway; schemat zmieniać nowymi migracjami.

## Dane demonstracyjne

- Konto: `prime` / `prime123`.
- PostgreSQL Compose: baza/użytkownik `prime`, hasło deweloperskie `prime_dev`, port `5432`.
- Nie używać `docker compose down -v`, jeśli wolumeny zawierają dane do zachowania.
