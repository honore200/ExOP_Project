# Centre de Contrôle OMP — Dossier de Conception Fullstack

**Projet :** Centralisation des données OMP (activités portuaires et ferroviaires)
**Date :** 25 août 2026
**Hébergement cible :** Serveur local / on-premise (site du port)

---

## 1. Objectifs et périmètre

Le site doit servir de **centre de contrôle unique** qui centralise tous les événements liés :

- aux **activités portuaires** (navires, quais, manutention, douane, incidents, maintenance des équipements) ;
- aux **activités ferroviaires** (trains, wagons, voies, chargement/déchargement, incidents, maintenance).

Toutes les données transitent par une **base de données MySQL unique**, et sont consultables via des **tableaux de bord (dashboards) dédiés** à chaque catégorie d'événement, avec vue globale et vues filtrées.

Hypothèse retenue (à ajuster) : hébergement **on-premise**, ce qui oriente vers une architecture **conteneurisée (Docker)** pour rester portable si l'infrastructure évolue vers le cloud plus tard.

**Stack imposée : Java EE (Jakarta EE).** L'ensemble du document ci-dessous a été mis à jour en conséquence (backend Jakarta EE, serveur d'applications, JPA/Hibernate pour MySQL).

---

## 2. Architecture globale

```
┌─────────────────────────────────────────────────────────────────┐
│                         POSTE UTILISATEUR                        │
│                  (navigateur — agents port / rail)               │
└───────────────────────────────┬───────────────────────────────┘
                                  │ HTTPS (réseau local)
┌───────────────────────────────▼───────────────────────────────┐
│                     SERVEUR ON-PREMISE (port)                    │
│                                                                    │
│  ┌───────────────┐   ┌────────────────────────────────────────┐ │
│  │  Reverse Proxy │──▶│  Serveur d'applications Jakarta EE      │ │
│  │  Nginx / Apache│   │  (WildFly ou Payara)                    │ │
│  │  (TLS, routing)│   │  ┌──────────────┐  ┌──────────────────┐│ │
│  │                │   │  │ Frontend web  │  │ Backend           ││ │
│  │                │   │  │ JSF+PrimeFaces│  │ JAX-RS (API REST) ││ │
│  │                │   │  │ (.war)        │  │ EJB / CDI          ││ │
│  │                │   │  └──────────────┘  │ JPA (Hibernate)    ││ │
│  │                │   │                     └─────────┬─────────┘│ │
│  └───────────────┘   └───────────────────────────────┼──────────┘ │
│                                                          │          │
│                                                  ┌───────▼──────┐   │
│                                                  │  MySQL 8.x     │   │
│                                                  │  (données)     │   │
│                                                  └────────────────┘   │
│                                                                    │
│  ┌───────────────────────┐   ┌───────────────────┐              │
│  │  Quartz / EJB Timer     │   │  WebSocket JSR 356  │              │
│  │  (tâches planifiées :   │   │  (push temps réel   │              │
│  │  rapports, purge)       │   │  vers les dashboards)│              │
│  └───────────────────────┘   └───────────────────┘              │
│                                                                    │
│  Sauvegardes automatiques MySQL (cron → stockage local + copie   │
│  externe/USB ou NAS)                                              │
└────────────────────────────────────────────────────────────────┘
```

Points clés :

- Le **reverse proxy Nginx** gère le TLS interne et route les requêtes vers le serveur d'applications Jakarta EE (WildFly/Payara), qui héberge à la fois le frontend (`.war` JSF) et l'API (`JAX-RS`).
- **WebSocket (JSR 356, natif Jakarta EE)** pour pousser les événements en temps réel vers les tableaux de bord (ex. arrivée d'un navire, alerte incident) sans dépendance externe.
- **EJB Timer / Quartz** pour les tâches planifiées (rapports périodiques, purge des vieux événements, sauvegardes).
- Déploiement possible en **conteneur Docker** (image WildFly/Payara + MySQL) via `docker-compose`, pour rester reproductible même en on-premise.

---

## 3. Stack technique retenue — Java EE (Jakarta EE)

| Couche | Choix recommandé | Justification |
|---|---|---|
| Langage / plateforme | Java 17+ / Jakarta EE 10 | Stack imposée, robuste et éprouvée pour les systèmes d'entreprise on-premise |
| Serveur d'applications | WildFly (ou Payara Server) | Implémentations Jakarta EE complètes, gratuites, bon support communautaire, faciles à conteneuriser |
| API REST | JAX-RS (Jersey / RESTEasy, inclus dans le serveur) | Standard Jakarta EE pour exposer les endpoints consommés par les dashboards |
| Logique métier | EJB (Stateless Session Beans) + CDI | Gestion transactionnelle native, injection de dépendances standard |
| Persistance | JPA + Hibernate (implémentation par défaut du serveur) | Mapping objet-relationnel vers MySQL, migrations via Flyway ou Liquibase |
| Frontend (vue par défaut) | JSF (Jakarta Faces) + PrimeFaces | Composants riches prêts à l'emploi (tableaux, graphiques `p:chart`, dashboards `p:dashboard`), tout en Java, pas de build JS séparé à maintenir on-premise |
| Frontend (alternative SPA) | Angular ou React consommant l'API JAX-RS | À envisager si l'équipe souhaite une interface plus dynamique côté client ; l'API REST reste la même dans les deux cas |
| Temps réel | WebSocket Jakarta (JSR 356) + CDI Events | Push des nouveaux événements/alertes vers les dashboards sans dépendance externe |
| Base de données | MySQL 8.x + connecteur `mysql-connector-j` | Imposé par le besoin exprimé |
| Sécurité | Jakarta Security (JASPIC/JACC) ou JWT via un filtre `Servlet Filter` | RBAC par rôle (admin, superviseur, agent, lecteur) |
| Build / dépendances | Maven (multi-modules) | Standard de l'écosystème Java EE, gestion claire des dépendances |
| Conteneurisation | Docker (image WildFly/Payara + MySQL) + docker-compose | Déploiement on-premise reproductible |
| Reverse proxy | Nginx ou Apache HTTPD | TLS, compression, routage |
| Tests | JUnit 5 + Mockito, Arquillian pour les tests d'intégration EJB/JPA | Fiabilité avant mise en production interne |

*Recommandation : partir sur **JSF + PrimeFaces** pour les dashboards — cela évite de maintenir une chaîne de build JavaScript séparée et PrimeFaces fournit déjà des composants de tableau de bord, graphiques et tableaux temps réel. Si l'équipe préfère à terme une interface type SPA (Angular/React), l'API JAX-RS ci-dessous fonctionne à l'identique pour les deux approches.*

---

## 4. Modélisation de la base de données MySQL

### 4.1 Principe de modélisation

Un socle commun (`events`, `users`, `sites`, `alerts`) + deux domaines métier (`port_*`, `rail_*`) reliés à la table pivot `events`. Cela permet un flux d'événements unifié pour les dashboards, tout en gardant les détails spécifiques à chaque domaine dans des tables dédiées.

Ce schéma SQL est directement mappable en **entités JPA** (`@Entity`, `@Table`, `@OneToMany`/`@ManyToOne` pour les relations vers `events`). Les migrations de schéma sont gérées avec **Flyway** ou **Liquibase** (scripts SQL versionnés, exécutés au démarrage du serveur d'applications), plutôt que par génération automatique Hibernate (`hbm2ddl`), pour garder un contrôle strict sur la structure en production.

### 4.2 Tables centrales (socle commun)

```sql
-- Utilisateurs et rôles
CREATE TABLE users (
  id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  full_name     VARCHAR(150) NOT NULL,
  email         VARCHAR(150) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  role          ENUM('admin','superviseur_port','superviseur_rail','agent_port','agent_rail','lecteur') NOT NULL,
  is_active     BOOLEAN DEFAULT TRUE,
  created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Sites / infrastructures (quais, gares, voies)
CREATE TABLE sites (
  id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  domaine     ENUM('port','rail') NOT NULL,
  code        VARCHAR(30) NOT NULL UNIQUE,     -- ex: QUAI-A3, GARE-OWENDO
  nom         VARCHAR(150) NOT NULL,
  meta        JSON NULL                         -- coordonnées GPS, capacité, etc.
);

-- Table pivot : TOUS les événements passent par ici
CREATE TABLE events (
  id             BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  domaine        ENUM('port','rail') NOT NULL,
  type_evenement VARCHAR(60) NOT NULL,          -- ex: arrivee_navire, depart_train, incident
  site_id        BIGINT UNSIGNED NULL REFERENCES sites(id),
  statut         ENUM('planifie','en_cours','termine','annule','incident') DEFAULT 'planifie',
  priorite       ENUM('normale','elevee','critique') DEFAULT 'normale',
  titre          VARCHAR(200) NOT NULL,
  description    TEXT NULL,
  date_debut     DATETIME NOT NULL,
  date_fin       DATETIME NULL,
  cree_par       BIGINT UNSIGNED REFERENCES users(id),
  created_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_domaine_statut (domaine, statut),
  INDEX idx_date_debut (date_debut)
);

-- Pièces jointes (photos, documents, bordereaux)
CREATE TABLE event_attachments (
  id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  event_id   BIGINT UNSIGNED NOT NULL REFERENCES events(id) ON DELETE CASCADE,
  chemin     VARCHAR(255) NOT NULL,
  type_mime  VARCHAR(100),
  uploaded_by BIGINT UNSIGNED REFERENCES users(id),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Journal d'audit (traçabilité, obligatoire pour un centre de contrôle)
CREATE TABLE audit_log (
  id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  user_id    BIGINT UNSIGNED REFERENCES users(id),
  action     VARCHAR(100) NOT NULL,
  entite     VARCHAR(60) NOT NULL,
  entite_id  BIGINT UNSIGNED,
  details    JSON NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Alertes actives (dérivées des événements critiques)
CREATE TABLE alerts (
  id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  event_id    BIGINT UNSIGNED NOT NULL REFERENCES events(id),
  niveau      ENUM('info','avertissement','critique') NOT NULL,
  message     VARCHAR(255) NOT NULL,
  acquittee   BOOLEAN DEFAULT FALSE,
  acquittee_par BIGINT UNSIGNED NULL REFERENCES users(id),
  created_at  DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### 4.3 Domaine PORT (détails spécifiques)

```sql
CREATE TABLE navires (
  id           BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nom          VARCHAR(150) NOT NULL,
  imo          VARCHAR(20) UNIQUE,             -- numéro IMO du navire
  pavillon     VARCHAR(60),
  type_navire  VARCHAR(60),                     -- vraquier, porte-conteneurs, pétrolier...
  longueur_m   DECIMAL(6,2),
  jauge_brute  DECIMAL(10,2)
);

CREATE TABLE port_escales (
  id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  event_id      BIGINT UNSIGNED NOT NULL REFERENCES events(id) ON DELETE CASCADE,
  navire_id     BIGINT UNSIGNED NOT NULL REFERENCES navires(id),
  quai_id       BIGINT UNSIGNED REFERENCES sites(id),
  eta           DATETIME,                       -- heure d'arrivée prévue
  ata           DATETIME,                       -- heure d'arrivée réelle
  etd           DATETIME,                       -- heure de départ prévue
  atd           DATETIME,                        -- heure de départ réelle
  motif_escale  ENUM('chargement','dechargement','ravitaillement','maintenance','autre')
);

CREATE TABLE port_manutention (
  id             BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  escale_id      BIGINT UNSIGNED NOT NULL REFERENCES port_escales(id) ON DELETE CASCADE,
  type_marchandise VARCHAR(100),                -- conteneurs, vrac, bois, hydrocarbures...
  tonnage        DECIMAL(12,2),
  nb_conteneurs  INT NULL,
  sens           ENUM('chargement','dechargement') NOT NULL,
  debut          DATETIME,
  fin            DATETIME
);

CREATE TABLE port_incidents (
  id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  event_id    BIGINT UNSIGNED NOT NULL REFERENCES events(id) ON DELETE CASCADE,
  categorie   ENUM('securite','environnement','materiel','humain','autre'),
  gravite     ENUM('mineure','majeure','critique'),
  mesures_prises TEXT
);
```

### 4.4 Domaine RAIL (détails spécifiques)

```sql
CREATE TABLE trains (
  id           BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  numero_train VARCHAR(30) NOT NULL UNIQUE,
  type_train   ENUM('marchandises','voyageurs','maintenance'),
  operateur    VARCHAR(100)                      -- ex: SETRAG
);

CREATE TABLE rail_mouvements (
  id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  event_id      BIGINT UNSIGNED NOT NULL REFERENCES events(id) ON DELETE CASCADE,
  train_id      BIGINT UNSIGNED NOT NULL REFERENCES trains(id),
  gare_depart_id BIGINT UNSIGNED REFERENCES sites(id),
  gare_arrivee_id BIGINT UNSIGNED REFERENCES sites(id),
  heure_prevue_depart  DATETIME,
  heure_reelle_depart  DATETIME,
  heure_prevue_arrivee DATETIME,
  heure_reelle_arrivee DATETIME,
  vitesse_moyenne_kmh  DECIMAL(6,2) NULL
);

CREATE TABLE rail_chargement (
  id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  mouvement_id  BIGINT UNSIGNED NOT NULL REFERENCES rail_mouvements(id) ON DELETE CASCADE,
  type_marchandise VARCHAR(100),                 -- minerai (manganèse), bois, marchandises générales
  tonnage       DECIMAL(12,2),
  nb_wagons     INT
);

CREATE TABLE rail_incidents (
  id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  event_id    BIGINT UNSIGNED NOT NULL REFERENCES events(id) ON DELETE CASCADE,
  categorie   ENUM('voie','materiel_roulant','signalisation','humain','autre'),
  gravite     ENUM('mineure','majeure','critique'),
  pk_localisation VARCHAR(30),                   -- point kilométrique
  mesures_prises TEXT
);

CREATE TABLE rail_maintenance (
  id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  event_id    BIGINT UNSIGNED NOT NULL REFERENCES events(id) ON DELETE CASCADE,
  type_intervention VARCHAR(100),
  tronçon     VARCHAR(150),
  duree_prevue_h DECIMAL(5,2)
);
```

Cette structure permet d'ajouter facilement un nouveau type d'événement (ex. `port_douane`, `rail_signalisation`) sans casser le socle commun `events`, qui reste la source unique pour construire les tableaux de bord.

---

## 5. Structure du projet (arborescence Maven multi-modules)

```
centre-controle-omp/                     # POM parent (packaging: pom)
├── pom.xml
│
├── omp-common/                          # module: entités JPA, DTO, constantes partagées
│   └── src/main/java/com/omp/common/
│       ├── entity/
│       │   ├── Event.java
│       │   ├── User.java
│       │   ├── Site.java
│       │   └── Alert.java
│       ├── dto/
│       └── enums/
│
├── omp-port/                            # module métier PORT
│   └── src/main/java/com/omp/port/
│       ├── entity/                      # Navire, PortEscale, PortManutention, PortIncident
│       ├── service/                     # EJB (règles métier)
│       └── repository/                  # DAO / Repository JPA
│
├── omp-rail/                            # module métier RAIL
│   └── src/main/java/com/omp/rail/
│       ├── entity/                      # Train, RailMouvement, RailChargement, RailIncident
│       ├── service/
│       └── repository/
│
├── omp-api/                             # module: API REST (JAX-RS), packaging WAR
│   └── src/main/java/com/omp/api/
│       ├── config/
│       │   └── JaxRsApplication.java    # @ApplicationPath("/api")
│       ├── resource/                    # endpoints REST
│       │   ├── AuthResource.java
│       │   ├── EventResource.java
│       │   ├── PortResource.java
│       │   ├── RailResource.java
│       │   ├── AlertResource.java
│       │   └── DashboardResource.java
│       ├── security/                    # filtre JWT / Jakarta Security
│       ├── websocket/                   # endpoints WebSocket (JSR 356)
│       └── mapper/                      # entité ↔ DTO
│   └── src/main/webapp/WEB-INF/
│       ├── beans.xml                    # activation CDI
│       └── web.xml
│
├── omp-web/                             # module: interface JSF + PrimeFaces, packaging WAR
│   └── src/main/
│       ├── java/com/omp/web/
│       │   ├── bean/                    # Managed Beans (@Named @ViewScoped)
│       │   │   ├── DashboardGlobalBean.java
│       │   │   ├── DashboardPortBean.java
│       │   │   ├── DashboardRailBean.java
│       │   │   └── AlertesBean.java
│       │   └── security/
│       └── webapp/
│           ├── WEB-INF/
│           │   ├── faces-config.xml
│           │   ├── web.xml
│           │   └── beans.xml
│           ├── templates/               # layout PrimeFaces (menu, header)
│           └── pages/
│               ├── login.xhtml
│               ├── dashboard-global.xhtml
│               ├── dashboard-port.xhtml
│               ├── dashboard-rail.xhtml
│               ├── alertes.xhtml
│               └── administration.xhtml
│
├── omp-ear/                             # module: assemblage EAR (regroupe omp-api + omp-web)
│   └── src/main/application/
│       └── META-INF/application.xml
│
├── db/
│   ├── migration/                       # scripts Flyway (V1__init.sql, V2__...)
│   └── seed/                            # données de test
│
├── docker/
│   ├── Dockerfile.wildfly
│   ├── docker-compose.yml
│   └── mysql/init/
│
├── docs/
│   └── (ce document, schémas, diagrammes)
└── README.md
```

Deux modules WAR (`omp-api` pour le backend REST, `omp-web` pour l'interface JSF) assemblés dans un **EAR** unique déployé sur WildFly/Payara — approche Jakarta EE classique qui garde la logique métier (`omp-common`, `omp-port`, `omp-rail`) mutualisée entre les deux.

---

## 6. API REST — endpoints principaux

| Domaine | Endpoint | Description |
|---|---|---|
| Auth | `POST /api/auth/login` | Connexion, retourne JWT |
| Événements | `GET /api/events?domaine=port&statut=en_cours` | Liste filtrable (pagination, tri) |
| Événements | `POST /api/events` | Création d'un événement |
| Événements | `PATCH /api/events/:id` | Mise à jour statut/infos |
| Port | `GET /api/port/escales?statut=en_cours` | Escales en cours |
| Port | `POST /api/port/escales` | Déclarer une nouvelle escale |
| Rail | `GET /api/rail/mouvements?date=today` | Mouvements du jour |
| Rail | `POST /api/rail/mouvements` | Déclarer un mouvement |
| Alertes | `GET /api/alerts?acquittee=false` | Alertes actives |
| Alertes | `POST /api/alerts/:id/acquitter` | Acquitter une alerte |
| Dashboard | `GET /api/dashboard/summary` | KPIs agrégés (vue globale) |
| Temps réel | `WS /events/stream` | Flux d'événements en direct (WebSocket JSR 356, poussé via CDI Events depuis les EJB) |

Tous les endpoints sont exposés par le module `omp-api` via JAX-RS (`@Path`, `@GET`, `@POST`, `@PathParam`...), avec `Jackson` (inclus dans le serveur) pour la sérialisation JSON. Le module `omp-web` (JSF) peut soit appeler ces mêmes services métier directement en interne (EJB local, pas d'appel HTTP), soit consommer l'API REST si l'on préfère découpler complètement les deux WAR.

---

## 7. Tableaux de bord (fenêtres par type d'événement)

Prévoir une **fenêtre par contexte métier**, toutes construites sur le même flux `events` mais filtrées :

1. **Dashboard global (vue de contrôle)** — KPIs consolidés port + rail : nombre d'événements en cours, alertes actives, taux d'occupation des quais/voies, timeline unifiée des événements du jour.
2. **Dashboard Port** — navires actuellement à quai, escales prévues (ETA/ETD), tonnage traité (jour/semaine), incidents portuaires en cours.
3. **Dashboard Rail** — trains en circulation, mouvements prévus vs réalisés, tonnage transporté, incidents voie/matériel.
4. **Dashboard Alertes/Incidents** — vue transversale port + rail, triée par gravité, avec acquittement et historique.
5. **Dashboard Administration** — gestion utilisateurs/rôles, sites, journal d'audit, paramétrage des types d'événements.
6. **Rapports** — export périodique (PDF/Excel) des statistiques par domaine, filtrable par période.

Chaque dashboard est une page JSF (`dashboard-*.xhtml`) associée à un Managed Bean `@ViewScoped`, utilisant les composants **PrimeFaces** (`p:dataTable`, `p:chart`, `p:panel`, `p:dashboard`) et le composant **`p:socket`/`f:websocket`** pour recevoir les mises à jour en temps réel poussées par le backend, sans rechargement de page.

---

## 8. Utilisateurs et rôles (RBAC)

| Rôle | Droits |
|---|---|
| `admin` | Accès total, gestion utilisateurs et paramétrage |
| `superviseur_port` | Lecture/écriture domaine port, validation des incidents |
| `superviseur_rail` | Lecture/écriture domaine rail, validation des incidents |
| `agent_port` / `agent_rail` | Saisie des événements de leur domaine |
| `lecteur` | Consultation des dashboards uniquement (ex. direction) |

---

## 9. Sécurité et fiabilité

- Authentification via **Jakarta Security** (module `JASPIC`/`JACC` du serveur) pour l'interface JSF, ou **JWT** vérifié par un `ContainerRequestFilter` JAX-RS pour l'API REST. Mots de passe hashés (bcrypt ou PBKDF2 via `Jakarta Security` `IdentityStore`).
- Contrôle d'accès par rôle via annotations `@RolesAllowed` sur les EJB et ressources JAX-RS (RBAC natif Jakarta EE).
- Validation stricte des entrées avec **Bean Validation** (`jakarta.validation`, annotations `@NotNull`, `@Size`, etc. sur les DTO/entités).
- `audit_log` systématique sur les créations/modifications/suppressions (intercepteur CDI `@Interceptor` générique).
- Sauvegardes MySQL automatisées (`mysqldump` quotidien via cron, rétention 30 jours, copie sur support externe).
- Accès HTTPS même en réseau local (certificat interne configuré sur le connecteur HTTPS de WildFly/Payara ou sur le reverse proxy).
- Journalisation applicative via **JBoss Logging** (WildFly) ou **java.util.logging**/SLF4J, centralisée dans les logs du serveur.

---

## 10. Déploiement on-premise

- Build Maven : `mvn clean package` génère les WAR (`omp-api.war`, `omp-web.war`) assemblés dans `omp-ear.ear`.
- Déploiement de l'EAR sur **WildFly** (ou Payara Server) : dépôt dans `standalone/deployments/` ou via la console d'administration/CLI (`jboss-cli.sh`).
- **Datasource MySQL** déclarée au niveau du serveur d'applications (module JDBC `mysql-connector-j` + `<datasource>` dans `standalone.xml`, pool de connexions géré par WildFly).
- Option conteneurisée : image Docker basée sur `quay.io/wildfly/wildfly` (ou Payara officielle), avec l'EAR copié dans `deployments/`, orchestrée avec MySQL via `docker-compose.yml` — pratique pour figer l'environnement on-premise et faciliter les mises à jour.
- Volumes (Docker ou dossiers dédiés si déploiement sans conteneur) persistants pour les données MySQL et les fichiers uploadés.
- Script de sauvegarde planifié (cron sur l'hôte).
- Documentation de restauration en cas de panne serveur (procédure testée en amont).
- Prévoir un onduleur/UPS si le serveur tourne en continu sur le site portuaire (contrainte électrique locale).

---

## 11. Feuille de route (phases de développement)

1. **Phase 0 — Cadrage** : valider stack technique définitive, lister précisément les types d'événements port/rail avec les équipes terrain, définir les rôles utilisateurs.
2. **Phase 1 — Socle** : mise en place du projet (backend, frontend, MySQL, Docker), authentification, gestion utilisateurs/sites.
3. **Phase 2 — Module Événements** : CRUD `events` générique + WebSocket temps réel.
4. **Phase 3 — Module Port** : navires, escales, manutention, incidents port + dashboard Port.
5. **Phase 4 — Module Rail** : trains, mouvements, chargement, incidents rail + dashboard Rail.
6. **Phase 5 — Dashboard global + Alertes** : agrégation KPIs, vue transversale, acquittement d'alertes.
7. **Phase 6 — Rapports et exports** : génération PDF/Excel périodiques.
8. **Phase 7 — Durcissement** : tests, audit sécurité, formation des utilisateurs, mise en production interne.

---

*Document de conception — à affiner une fois la liste exacte des événements port/rail et le choix définitif de stack confirmés.*
