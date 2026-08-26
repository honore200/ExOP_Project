-- Phase 1 - Socle : utilisateurs, roles, referentiel domaine/statut, localisations.
-- Corrections appliquees par rapport aux documents de conception (cf plan section 1.4) :
--  - syntaxe FOREIGN KEY complete (jamais de REFERENCES inline, ignore silencieusement par MySQL)
--  - ENGINE=InnoDB + utf8mb4 explicites
--  - ON DELETE explicite sur chaque FK
--  - UNIQUE sur les colonnes "code"
--  - "user"/"role" renommes app_user/app_role (mots potentiellement reserves MySQL 8)

CREATE TABLE app_role (
    id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code       VARCHAR(40)  NOT NULL,
    name       VARCHAR(100) NOT NULL,
    active     BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_app_role_code UNIQUE (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE app_user (
    id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(80)  NOT NULL,
    full_name     VARCHAR(150) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    enabled       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_app_user_username UNIQUE (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE user_role (
    user_id BIGINT UNSIGNED NOT NULL,
    role_id BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES app_user (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES app_role (id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE domain (
    id     BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code   VARCHAR(40)  NOT NULL,
    name   VARCHAR(100) NOT NULL,
    active BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_domain_code UNIQUE (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Referentiel de statuts partage, discrimine par categorie (cf plan section 1.4 point 1 -
-- comble un trou du MCD ou operation.status_id/asset.status_id/... n'avaient aucune table cible).
CREATE TABLE status (
    id       BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    category VARCHAR(40)  NOT NULL,
    code     VARCHAR(40)  NOT NULL,
    name     VARCHAR(100) NOT NULL,
    CONSTRAINT uq_status_category_code UNIQUE (category, code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE location (
    id                 BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code               VARCHAR(40)  NOT NULL,
    name               VARCHAR(150) NOT NULL,
    location_type      VARCHAR(20)  NOT NULL, -- STATION|TRACK|QUAY|ZONE|STORAGE (enum Java, cf LocationType)
    parent_location_id BIGINT UNSIGNED NULL,
    created_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_location_code UNIQUE (code),
    CONSTRAINT fk_location_parent FOREIGN KEY (parent_location_id) REFERENCES location (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Referentiel de base
INSERT INTO app_role (code, name) VALUES
    ('ADMIN', 'Administrateur'),
    ('CONTROL_ROOM', 'Salle de controle'),
    ('RAILWAY', 'Agent Railway'),
    ('PORT', 'Agent Port'),
    ('MAINTENANCE', 'Agent Maintenance'),
    ('MANAGER', 'Direction'),
    ('VIEWER', 'Lecteur');

INSERT INTO domain (code, name) VALUES
    ('RAILWAY', 'Ferroviaire'),
    ('PORT', 'Portuaire'),
    ('MAINTENANCE', 'Maintenance');

INSERT INTO status (category, code, name) VALUES
    ('OPERATION', 'PLANNED', 'Planifiee'),
    ('OPERATION', 'IN_PROGRESS', 'En cours'),
    ('OPERATION', 'COMPLETED', 'Terminee'),
    ('OPERATION', 'CANCELLED', 'Annulee'),
    ('RAILWAY_ROTATION', 'ANNOUNCED', 'Annoncee'),
    ('RAILWAY_ROTATION', 'AT_YARD', 'En gare'),
    ('RAILWAY_ROTATION', 'DEPARTED', 'Partie'),
    ('PORT_CALL', 'ANNOUNCED', 'Annoncee'),
    ('PORT_CALL', 'AT_BERTH', 'A quai'),
    ('PORT_CALL', 'DEPARTED', 'Partie'),
    ('ASSET', 'AVAILABLE', 'Disponible'),
    ('ASSET', 'IN_USE', 'En service'),
    ('ASSET', 'UNDER_MAINTENANCE', 'En maintenance'),
    ('ASSET', 'OUT_OF_SERVICE', 'Hors service'),
    ('RESOURCE', 'AVAILABLE', 'Disponible'),
    ('RESOURCE', 'ASSIGNED', 'Affectee'),
    ('RESOURCE', 'UNAVAILABLE', 'Indisponible');
