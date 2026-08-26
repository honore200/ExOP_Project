-- Phase 4 - Specialisation PORT (cf plan section 1.3 / docs/proposition-port-call.md - brouillon
-- de travail a valider avec les equipes portuaires, meme statut que le reste du MCD).

-- Referentiels generiques transverses (asset_type/resource_type ne sont pas scopes par domaine,
-- cf V2) : places ici plutot que dans V2/V3 deja appliques, pour ne jamais modifier une migration
-- Flyway existante (le checksum casserait). VESSEL est requis par PortCallService (resolution/
-- creation du navire comme Asset).
INSERT INTO asset_type (code, name) VALUES
    ('VESSEL', 'Navire'),
    ('LOCOMOTIVE', 'Locomotive'),
    ('WAGON', 'Wagon'),
    ('CRANE', 'Grue / portique'),
    ('CONVEYOR', 'Convoyeur');

INSERT INTO resource_type (code, name) VALUES
    ('PERSON', 'Personne'),
    ('TEAM', 'Equipe'),
    ('EQUIPMENT', 'Equipement');

CREATE TABLE vessel_detail (
    asset_id      BIGINT UNSIGNED PRIMARY KEY,
    imo_number    VARCHAR(20) NULL,
    flag          VARCHAR(60) NULL,
    vessel_type   VARCHAR(60) NULL,
    length_m      DECIMAL(6,2) NULL,
    gross_tonnage DECIMAL(10,2) NULL,
    CONSTRAINT uq_vessel_detail_imo UNIQUE (imo_number),
    CONSTRAINT fk_vessel_detail_asset FOREIGN KEY (asset_id) REFERENCES asset (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE port_call (
    id               BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    operation_id     BIGINT UNSIGNED NOT NULL,
    vessel_asset_id  BIGINT UNSIGNED NOT NULL,
    quay_location_id BIGINT UNSIGNED NULL,
    eta_datetime     DATETIME NULL,
    ata_datetime     DATETIME NULL,
    etd_datetime     DATETIME NULL,
    atd_datetime     DATETIME NULL,
    declared_tonnage DECIMAL(12,2) NULL,
    status_id        BIGINT UNSIGNED NULL,
    CONSTRAINT uq_port_call_operation UNIQUE (operation_id),
    CONSTRAINT fk_port_call_operation FOREIGN KEY (operation_id) REFERENCES operation (id) ON DELETE CASCADE,
    CONSTRAINT fk_port_call_vessel FOREIGN KEY (vessel_asset_id) REFERENCES asset (id) ON DELETE RESTRICT,
    CONSTRAINT fk_port_call_quay FOREIGN KEY (quay_location_id) REFERENCES location (id) ON DELETE SET NULL,
    CONSTRAINT fk_port_call_status FOREIGN KEY (status_id) REFERENCES status (id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE cargo_type (
    id     BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code   VARCHAR(40)  NOT NULL,
    name   VARCHAR(100) NOT NULL,
    active BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_cargo_type_code UNIQUE (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE port_cargo_operation (
    id                BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    port_call_id      BIGINT UNSIGNED NOT NULL,
    operation_step_id BIGINT UNSIGNED NULL,
    cargo_type_id     BIGINT UNSIGNED NOT NULL,
    direction         VARCHAR(20) NOT NULL,
    tonnage           DECIMAL(12,2) NULL,
    container_count   INT NULL,
    start_datetime    DATETIME NULL,
    end_datetime      DATETIME NULL,
    CONSTRAINT fk_port_cargo_call FOREIGN KEY (port_call_id) REFERENCES port_call (id) ON DELETE CASCADE,
    CONSTRAINT fk_port_cargo_step FOREIGN KEY (operation_step_id) REFERENCES operation_step (id) ON DELETE SET NULL,
    CONSTRAINT fk_port_cargo_type FOREIGN KEY (cargo_type_id) REFERENCES cargo_type (id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Referentiel PORT (cf docs/proposition-port-call.md)
INSERT INTO operation_type (domain_id, code, name)
SELECT d.id, t.code, t.name FROM domain d
JOIN (
    SELECT 'SHIP_CALL_LOADING' AS code, 'Escale - chargement' AS name UNION ALL
    SELECT 'SHIP_CALL_UNLOADING', 'Escale - dechargement' UNION ALL
    SELECT 'SHIP_CALL_BUNKERING', 'Escale - ravitaillement' UNION ALL
    SELECT 'SHIP_CALL_MAINTENANCE', 'Escale - maintenance'
) t
WHERE d.code = 'PORT';

INSERT INTO step_type (domain_id, code, name)
SELECT d.id, t.code, t.name FROM domain d
JOIN (
    SELECT 'PILOT_BOARDING' AS code, 'Embarquement du pilote' AS name UNION ALL
    SELECT 'BERTHING', 'Accostage' UNION ALL
    SELECT 'CUSTOMS_CLEARANCE', 'Dedouanement' UNION ALL
    SELECT 'CARGO_OPERATIONS', 'Operations de chargement/dechargement' UNION ALL
    SELECT 'BUNKERING', 'Ravitaillement (soutage)' UNION ALL
    SELECT 'UNBERTHING', 'Appareillage'
) t
WHERE d.code = 'PORT';

INSERT INTO incident_type (code, name, category) VALUES
    ('CRANE_FAILURE', 'Panne de grue/portique', 'materiel'),
    ('BERTH_UNAVAILABLE', 'Quai indisponible', 'infrastructure'),
    ('CUSTOMS_DELAY', 'Retard douane', 'administratif'),
    ('CARGO_DAMAGE', 'Avarie marchandise', 'materiel'),
    ('ENVIRONMENTAL', 'Pollution / incident environnemental', 'environnement');
-- WEATHER/OTHER deja seedes en V3 (communs a tous les domaines)

INSERT INTO event_type (code, name, category, default_severity) VALUES
    ('SHIP_ARRIVAL', 'Arrivee navire', 'operation', 'INFO'),
    ('SHIP_DEPARTURE', 'Depart navire', 'operation', 'INFO'),
    ('SHIP_DELAY', 'Retard navire', 'operation', 'WARNING');

INSERT INTO cargo_type (code, name) VALUES
    ('CONTAINERS', 'Conteneurs'),
    ('BULK', 'Vrac'),
    ('WOOD', 'Bois'),
    ('HYDROCARBONS', 'Hydrocarbures'),
    ('GENERAL_CARGO', 'Marchandises generales');

-- Les statuts PORT_CALL (ANNOUNCED/AT_BERTH/DEPARTED) sont deja seedes en V1__socle.sql.
