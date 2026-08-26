-- Phase 3 - Specialisation RAILWAY (cf plan section 1.2 et docs/referentiel-rail.md).

CREATE TABLE railway_rotation (
    id                            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    operation_id                  BIGINT UNSIGNED NOT NULL,
    rotation_number               VARCHAR(40) NOT NULL,
    client_id                     BIGINT UNSIGNED NULL,
    client_detail_id              BIGINT UNSIGNED NULL,
    train_arrival_number          VARCHAR(30) NULL,
    train_return_number           VARCHAR(30) NULL,
    train_code_gsez               VARCHAR(30) NULL,
    train_code_arise              VARCHAR(30) NULL,
    tonnage                       DECIMAL(12,2) NULL,
    declared_tonnage               DECIMAL(12,2) NULL,
    wagon_count                   INT NULL,
    arrival_datetime              DATETIME NULL,
    departure_datetime            DATETIME NULL,
    announced_departure_datetime  DATETIME NULL,
    status_id                     BIGINT UNSIGNED NULL,
    CONSTRAINT uq_railway_rotation_operation UNIQUE (operation_id),
    CONSTRAINT fk_railway_rotation_operation FOREIGN KEY (operation_id) REFERENCES operation (id) ON DELETE CASCADE,
    CONSTRAINT fk_railway_rotation_client FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE SET NULL,
    CONSTRAINT fk_railway_rotation_client_detail FOREIGN KEY (client_detail_id) REFERENCES client_detail (id) ON DELETE SET NULL,
    CONSTRAINT fk_railway_rotation_status FOREIGN KEY (status_id) REFERENCES status (id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Referentiel RAILWAY (cf docs/referentiel-rail.md - a valider avec les equipes terrain, Phase 0)
INSERT INTO operation_type (domain_id, code, name)
SELECT id, 'TRAIN_ROTATION', 'Rotation ferroviaire' FROM domain WHERE code = 'RAILWAY';

INSERT INTO step_type (domain_id, code, name)
SELECT d.id, t.code, t.name FROM domain d
JOIN (
    SELECT 'ARRIVAL' AS code, 'Arrivee du train' AS name UNION ALL
    SELECT 'DECOUPLING', 'Desattelage' UNION ALL
    SELECT 'SAFETY_INSPECTION_ARRIVAL', 'Visite securite arrivee' UNION ALL
    SELECT 'LOCO_MANEUVER', 'Manoeuvre / arrivee locomotive' UNION ALL
    SELECT 'WAGON_PLACEMENT', 'Placement des wagons' UNION ALL
    SELECT 'UNLOADING_WAIT', 'Attente dechargement' UNION ALL
    SELECT 'UNLOADING', 'Dechargement' UNION ALL
    SELECT 'FORMATION', 'Formation de la rame' UNION ALL
    SELECT 'SAFETY_INSPECTION_DEPARTURE', 'Visite securite depart' UNION ALL
    SELECT 'VEHICLE_CHECK', 'Releve des vehicules' UNION ALL
    SELECT 'DECLARATION', 'Declaration de la rame' UNION ALL
    SELECT 'CREW_ARRIVAL', 'Arrivee equipe de conduite' UNION ALL
    SELECT 'BRAKE_TEST', 'Essais de freins' UNION ALL
    SELECT 'TRAIN_PRESENTATION', 'Presentation du train' UNION ALL
    SELECT 'DEPARTURE', 'Depart'
) t
WHERE d.code = 'RAILWAY';

INSERT INTO incident_type (code, name, category) VALUES
    ('LOCOMOTIVE_FAILURE', 'Panne locomotive', 'materiel_roulant'),
    ('PERSONNEL_UNAVAILABLE', 'Absence/indisponibilite personnel', 'humain'),
    ('TRACK_OCCUPIED', 'Voie occupee', 'voie'),
    ('EQUIPMENT_FAILURE', 'Panne equipement', 'materiel_roulant'),
    ('BRAKE_PROBLEM', 'Probleme de frein', 'materiel_roulant'),
    ('TRACTION_PROBLEM', 'Probleme de traction', 'materiel_roulant'),
    ('DERAILMENT', 'Deraillement', 'voie'),
    ('ACCIDENT', 'Accident', 'humain'),
    ('WAITING_AMV', 'Attente AMV', 'humain'),
    ('WAITING_ADC', 'Attente ADC', 'humain'),
    ('CMV_UNAVAILABLE', 'CMV indisponible', 'humain'),
    ('OPERATIONAL_PRIORITY', 'Priorite accordee a une autre operation', 'autre');
-- 'WEATHER' et 'OTHER' seront communs a tous les domaines (cf V4, inseres une seule fois si absents)
INSERT INTO incident_type (code, name, category)
SELECT 'WEATHER', 'Intemperies', 'autre' WHERE NOT EXISTS (SELECT 1 FROM incident_type WHERE code = 'WEATHER');
INSERT INTO incident_type (code, name, category)
SELECT 'OTHER', 'Autre cause (texte libre)', 'autre' WHERE NOT EXISTS (SELECT 1 FROM incident_type WHERE code = 'OTHER');

INSERT INTO event_type (code, name, category, default_severity) VALUES
    ('TRAIN_ARRIVAL', 'Arrivee train', 'operation', 'INFO'),
    ('TRAIN_DEPARTURE', 'Depart train', 'operation', 'INFO'),
    ('TRAIN_DELAY', 'Retard train', 'operation', 'WARNING'),
    ('TRAIN_FAILURE', 'Panne train', 'materiel', 'CRITICAL');

-- Les statuts RAILWAY_ROTATION (ANNOUNCED/AT_YARD/DEPARTED) sont deja seedes en V1__socle.sql.
