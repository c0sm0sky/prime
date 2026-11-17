# Instrukcje dla agenta

Przeczytaj `docs/PROJECT_HANDOFF.md` przed zmianami. Plik opisuje wymagania, wykonane prace, uruchomienie i otwarte kwestie.

## Wymagania i decyzje

- Java 25 LTS, najnowsze stabilne wydania bibliotek, bez RC, milestone i snapshotów. Zapisz datę sprawdzenia wersji przy aktualizacjach.
- Aplikacja ma trzy profile: `wildfly` (WAR dla WildFly 42 + PostgreSQL; JSF/CDI/EL z serwera), `docker` (embedded Tomcat + PrimeFaces + SQLite w obrazie Docker) oraz `springboot` (wykonywalny WAR z embedded Tomcatem + PostgreSQL). Profil `wildfly` wyklucza autokonfigurację Mojarra JoinFaces; nie pakuj w nim implementacji JSF, CDI ani EL.
- Widoki używają Jakarta Faces i PrimeFaces. Panel zawiera klientów, zamówienia oraz przychody; rekordy mają menu edycji/usuwania i formularze dodawania.
- Zamówienie ma obowiązkowego klienta przez FK. Przychód jest przypisany do zamówienia; historyczny przychód może pozostać bez powiązania. Usunięcie klienta kaskaduje na jego zamówienia i przychody; usunięcie zamówienia kaskaduje na jego przychody. Rekordy przychodów bez zamówienia pozostają.
- PostgreSQL działa przez Docker Compose. Flyway zarządza schematem w `src/main/resources/db/migration`, Hibernate ma `ddl-auto=validate`. Nie usuwaj wolumenu `prime-postgres-data` ani nie edytuj zastosowanych migracji; dodawaj kolejne pliki `Vn__opis.sql`. Nie uruchamiaj Liquibase; pozostałe tabele `databasechangelog*` są wyłącznie historyczne.
- Zachowuj warstwy: widok → serwis transakcyjny → repozytorium. Reguły biznesowe i walidacja należą do serwisów, nie do `DashboardView` ani XHTML.
- Używaj Lomboka: `@RequiredArgsConstructor` dla wstrzykiwania zależności, klasowego `@Getter`/`@Setter` tam, gdzie jest to właściwe. Nie dopisuj ręcznie getterów/setterów generowanych przez Lombok.
- Locale, format daty i waluty są konfigurowane przez `prime.format.*` w `application.properties` oraz `FormatProperties` / `DisplayFormatters`.
- Formatuj zmieniane XML, XHTML, POM i SQL. Zostawiaj wszystkie teksty UI po polsku.
- Nie dodawaj ani nie uruchamiaj testów automatycznych, chyba że użytkownik o nie poprosi. Kompilacja WAR i kontrola wdrożenia są osobną weryfikacją.

## Szybkie komendy

```sh
docker compose up -d postgres
mvn -Pwildfly clean package
```

WAR: `target/prime-0.0.1.war`. Szczegóły oraz otwarte zadania: `docs/PROJECT_HANDOFF.md`.
