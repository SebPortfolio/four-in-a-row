# four-in-a-row

Das Spiel 4-Gewinnt

## Highlights

### API Design:

- **Contract-First Ansatz:** Die API-Definition erfolgt zentral via OpenAPI-Spezifikation (YAML).
- **Code-Generation:** Controller-Interfaces und WDTOs werden automatisiert generiert, um Konsistenz zwischen Spezifikation und Implementierung zu garantieren.

### Security & Resilience:

- **Stateless Auth:** Stateless Authentication mittels JWT.
- **Rate Limiting:** Schutz vor Brute-Force-Angriffen und API-Missbrauch durch Implementierung des Token-Bucket-Algorithmus (Bucket4j).

### Architecture & Data:

- **Type-Safe Mapping:** Einsatz von MapStruct für performantes und typsicheres Mapping zwischen Datenbank-Entities und Web-DTOs.
- **Database Evolution:** Versionierte Datenbank-Migrationen mit Flyway für eine nachvollziehbare Schema-Historie. _(soon)_
- **Boilerplate Reduction:** Nutzung von Project Lombok zur Steigerung der Lesbarkeit und Wartbarkeit des Codes.

### Persistence

- **H2-Database:** Initiale Entwicklung erfolgt in anbetracht des Aufwands lokal.
- **PostgreSQL:** Einsatz einer relationalen Hochleistungs-Datenbank. _(soon)_
- **Dockerized:** Containerisierte Datenbank-Umgebung für einfache lokale Entwicklung. _(soon)_
