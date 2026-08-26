-- Phase 2 - Moteur generique Operations (cf plan section 1.1/1.4).
-- Regles de ON DELETE appliquees uniformement :
--  - enfants directs d'une operation (operation_id/event_id) -> CASCADE
--  - tables de reference/type (*_type, status) -> RESTRICT (jamais orpheliner l'historique,
--    desactiver via "active" plutot que supprimer, cf V1)
--  - master data nullable (client/location/asset/user) -> SET NULL
--  - colonnes NOT NULL vers master data -> RESTRICT (SET NULL impossible)

CREATE TABLE operation_type (
    id        BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    domain_id BIGINT UNSIGNED NOT NULL,
    code      VARCHAR(40)  NOT NULL,
    name      VARCHAR(100) NOT NULL,
    active    BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_operation_type_domain_code UNIQUE (domain_id, code),
    CONSTRAINT fk_operation_type_domain FOREIGN KEY (domain_id) REFERENCES domain (id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE step_type (
    id        BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    domain_id BIGINT UNSIGNED NOT NULL,
    code      VARCHAR(40)  NOT NULL,
    name      VARCHAR(100) NOT NULL,
    active    BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_step_type_domain_code UNIQUE (domain_id, code),
    CONSTRAINT fk_step_type_domain FOREIGN KEY (domain_id) REFERENCES domain (id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE asset_type (
    id     BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code   VARCHAR(40)  NOT NULL,
    name   VARCHAR(100) NOT NULL,
    active BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_asset_type_code UNIQUE (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE resource_type (
    id     BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code   VARCHAR(40)  NOT NULL,
    name   VARCHAR(100) NOT NULL,
    active BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_resource_type_code UNIQUE (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE event_type (
    id               BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code             VARCHAR(40)  NOT NULL,
    name             VARCHAR(100) NOT NULL,
    active           BOOLEAN      NOT NULL DEFAULT TRUE,
    category         VARCHAR(40)  NULL,
    default_severity VARCHAR(20)  NOT NULL DEFAULT 'INFO',
    CONSTRAINT uq_event_type_code UNIQUE (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE incident_type (
    id       BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code     VARCHAR(40)  NOT NULL,
    name     VARCHAR(100) NOT NULL,
    active   BOOLEAN      NOT NULL DEFAULT TRUE,
    category VARCHAR(40)  NULL,
    CONSTRAINT uq_incident_type_code UNIQUE (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE client (
    id   BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(40)  NOT NULL,
    name VARCHAR(150) NOT NULL,
    CONSTRAINT uq_client_code UNIQUE (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE client_detail (
    id        BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    client_id BIGINT UNSIGNED NOT NULL,
    code      VARCHAR(40)  NOT NULL,
    name      VARCHAR(150) NOT NULL,
    CONSTRAINT uq_client_detail_client_code UNIQUE (client_id, code),
    CONSTRAINT fk_client_detail_client FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE operation (
    id               BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    version          BIGINT UNSIGNED NOT NULL DEFAULT 0,
    operation_code   VARCHAR(60) NOT NULL,
    operation_type_id BIGINT UNSIGNED NOT NULL,
    domain_id        BIGINT UNSIGNED NOT NULL,
    client_id        BIGINT UNSIGNED NULL,
    status_id        BIGINT UNSIGNED NULL,
    location_id      BIGINT UNSIGNED NULL,
    start_datetime   DATETIME NOT NULL,
    end_datetime     DATETIME NULL,
    description      TEXT NULL,
    created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_operation_code UNIQUE (operation_code),
    CONSTRAINT fk_operation_type FOREIGN KEY (operation_type_id) REFERENCES operation_type (id) ON DELETE RESTRICT,
    CONSTRAINT fk_operation_domain FOREIGN KEY (domain_id) REFERENCES domain (id) ON DELETE RESTRICT,
    CONSTRAINT fk_operation_client FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE SET NULL,
    CONSTRAINT fk_operation_status FOREIGN KEY (status_id) REFERENCES status (id) ON DELETE RESTRICT,
    CONSTRAINT fk_operation_location FOREIGN KEY (location_id) REFERENCES location (id) ON DELETE SET NULL,
    INDEX idx_operation_domain_status (domain_id, status_id),
    INDEX idx_operation_start (start_datetime)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE operation_step (
    id             BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    version        BIGINT UNSIGNED NOT NULL DEFAULT 0,
    operation_id   BIGINT UNSIGNED NOT NULL,
    step_type_id   BIGINT UNSIGNED NOT NULL,
    sequence       INT NOT NULL,
    status_id      BIGINT UNSIGNED NULL,
    planned_start  DATETIME NULL,
    actual_start   DATETIME NULL,
    actual_end     DATETIME NULL,
    duration_hours DOUBLE NULL,
    waiting_hours  DOUBLE NULL,
    CONSTRAINT uq_operation_step_sequence UNIQUE (operation_id, sequence),
    CONSTRAINT fk_operation_step_operation FOREIGN KEY (operation_id) REFERENCES operation (id) ON DELETE CASCADE,
    CONSTRAINT fk_operation_step_type FOREIGN KEY (step_type_id) REFERENCES step_type (id) ON DELETE RESTRICT,
    CONSTRAINT fk_operation_step_status FOREIGN KEY (status_id) REFERENCES status (id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE asset (
    id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    asset_code    VARCHAR(60)  NOT NULL,
    asset_name    VARCHAR(150) NOT NULL,
    asset_type_id BIGINT UNSIGNED NOT NULL,
    location_id   BIGINT UNSIGNED NULL,
    status_id     BIGINT UNSIGNED NULL,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_asset_code UNIQUE (asset_code),
    CONSTRAINT fk_asset_type FOREIGN KEY (asset_type_id) REFERENCES asset_type (id) ON DELETE RESTRICT,
    CONSTRAINT fk_asset_location FOREIGN KEY (location_id) REFERENCES location (id) ON DELETE SET NULL,
    CONSTRAINT fk_asset_status FOREIGN KEY (status_id) REFERENCES status (id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE operation_asset (
    operation_id   BIGINT UNSIGNED NOT NULL,
    asset_id       BIGINT UNSIGNED NOT NULL,
    role           VARCHAR(40) NULL,
    start_datetime DATETIME NULL,
    end_datetime   DATETIME NULL,
    PRIMARY KEY (operation_id, asset_id),
    CONSTRAINT fk_operation_asset_operation FOREIGN KEY (operation_id) REFERENCES operation (id) ON DELETE CASCADE,
    CONSTRAINT fk_operation_asset_asset FOREIGN KEY (asset_id) REFERENCES asset (id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE resource (
    id               BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code             VARCHAR(60)  NOT NULL,
    name             VARCHAR(150) NOT NULL,
    resource_type_id BIGINT UNSIGNED NOT NULL,
    status_id        BIGINT UNSIGNED NULL,
    created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_resource_code UNIQUE (code),
    CONSTRAINT fk_resource_type FOREIGN KEY (resource_type_id) REFERENCES resource_type (id) ON DELETE RESTRICT,
    CONSTRAINT fk_resource_status FOREIGN KEY (status_id) REFERENCES status (id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE operation_resource (
    operation_id   BIGINT UNSIGNED NOT NULL,
    resource_id    BIGINT UNSIGNED NOT NULL,
    role           VARCHAR(40) NULL,
    start_datetime DATETIME NULL,
    end_datetime   DATETIME NULL,
    PRIMARY KEY (operation_id, resource_id),
    CONSTRAINT fk_operation_resource_operation FOREIGN KEY (operation_id) REFERENCES operation (id) ON DELETE CASCADE,
    CONSTRAINT fk_operation_resource_resource FOREIGN KEY (resource_id) REFERENCES resource (id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE event (
    id               BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    operation_id     BIGINT UNSIGNED NOT NULL,
    operation_step_id BIGINT UNSIGNED NULL,
    event_type_id    BIGINT UNSIGNED NOT NULL,
    asset_id         BIGINT UNSIGNED NULL,
    location_id      BIGINT UNSIGNED NULL,
    event_datetime   DATETIME NOT NULL,
    severity         VARCHAR(20) NOT NULL DEFAULT 'INFO',
    status           VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    description      TEXT NULL,
    created_by       BIGINT UNSIGNED NULL,
    created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_event_operation FOREIGN KEY (operation_id) REFERENCES operation (id) ON DELETE CASCADE,
    CONSTRAINT fk_event_step FOREIGN KEY (operation_step_id) REFERENCES operation_step (id) ON DELETE SET NULL,
    CONSTRAINT fk_event_type FOREIGN KEY (event_type_id) REFERENCES event_type (id) ON DELETE RESTRICT,
    CONSTRAINT fk_event_asset FOREIGN KEY (asset_id) REFERENCES asset (id) ON DELETE SET NULL,
    CONSTRAINT fk_event_location FOREIGN KEY (location_id) REFERENCES location (id) ON DELETE SET NULL,
    CONSTRAINT fk_event_created_by FOREIGN KEY (created_by) REFERENCES app_user (id) ON DELETE SET NULL,
    INDEX idx_event_operation_datetime (operation_id, event_datetime)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE incident (
    id                BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    operation_id      BIGINT UNSIGNED NOT NULL,
    operation_step_id BIGINT UNSIGNED NULL,
    incident_type_id  BIGINT UNSIGNED NOT NULL,
    start_datetime    DATETIME NOT NULL,
    end_datetime      DATETIME NULL,
    severity          VARCHAR(20) NOT NULL DEFAULT 'WARNING',
    description       TEXT NULL,
    resolved          BOOLEAN NOT NULL DEFAULT FALSE,
    created_by        BIGINT UNSIGNED NULL,
    created_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_incident_operation FOREIGN KEY (operation_id) REFERENCES operation (id) ON DELETE CASCADE,
    CONSTRAINT fk_incident_step FOREIGN KEY (operation_step_id) REFERENCES operation_step (id) ON DELETE SET NULL,
    CONSTRAINT fk_incident_type FOREIGN KEY (incident_type_id) REFERENCES incident_type (id) ON DELETE RESTRICT,
    CONSTRAINT fk_incident_created_by FOREIGN KEY (created_by) REFERENCES app_user (id) ON DELETE SET NULL,
    INDEX idx_incident_resolved_severity (resolved, severity)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE alert (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    event_id        BIGINT UNSIGNED NOT NULL,
    alert_type      VARCHAR(60) NOT NULL,
    severity        VARCHAR(20) NOT NULL DEFAULT 'WARNING',
    status          VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    acknowledged_at DATETIME NULL,
    resolved_at     DATETIME NULL,
    assigned_to     BIGINT UNSIGNED NULL,
    CONSTRAINT fk_alert_event FOREIGN KEY (event_id) REFERENCES event (id) ON DELETE CASCADE,
    CONSTRAINT fk_alert_assigned_to FOREIGN KEY (assigned_to) REFERENCES app_user (id) ON DELETE SET NULL,
    INDEX idx_alert_status_severity (status, severity)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE operation_observation (
    id           BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    operation_id BIGINT UNSIGNED NOT NULL,
    author_id    BIGINT UNSIGNED NOT NULL,
    content      TEXT NOT NULL,
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_observation_operation FOREIGN KEY (operation_id) REFERENCES operation (id) ON DELETE CASCADE,
    CONSTRAINT fk_observation_author FOREIGN KEY (author_id) REFERENCES app_user (id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE audit_log (
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT UNSIGNED NULL,
    action      VARCHAR(100) NOT NULL,
    entity_type VARCHAR(60) NOT NULL,
    entity_id   BIGINT UNSIGNED NULL,
    old_value   JSON NULL,
    new_value   JSON NULL,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_log_user FOREIGN KEY (user_id) REFERENCES app_user (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
