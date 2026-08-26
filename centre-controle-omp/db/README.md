# Emplacement reel des scripts

Les scripts Flyway executes par l'application (`FlywayMigrationStartup`, `@Singleton @Startup` dans `omp-api`) vivent dans **`omp-api/src/main/resources/db/migration/`** (chargement classpath, empaquetes dans le WAR). C'est la source de verite.

Ce dossier `db/` a la racine reste disponible pour :
- `db/migration/` — usage `mvn flyway:migrate` manuel en dev local (pointer `-Dflyway.locations=filesystem:db/migration` si besoin d'iterer sans rebuild du WAR) ; garder son contenu synchronise avec `omp-api/src/main/resources/db/migration/` si utilise.
- `db/seed/` — jeux de donnees de test/demo (hors Flyway, charges manuellement en dev).
