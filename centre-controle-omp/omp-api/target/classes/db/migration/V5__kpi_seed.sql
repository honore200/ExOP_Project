-- Phase 5 - KPI (cf MCD doc2 section 11, calcules par KpiCalculationService).

CREATE TABLE kpi (
    id               BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code             VARCHAR(40)  NOT NULL,
    name             VARCHAR(150) NOT NULL,
    active           BOOLEAN      NOT NULL DEFAULT TRUE,
    unit             VARCHAR(20)  NULL,
    calculation_type VARCHAR(40)  NULL,
    CONSTRAINT uq_kpi_code UNIQUE (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE kpi_value (
    id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    kpi_id        BIGINT UNSIGNED NOT NULL,
    operation_id  BIGINT UNSIGNED NULL,
    value         DECIMAL(14,4) NOT NULL,
    period_start  DATETIME NULL,
    period_end    DATETIME NULL,
    calculated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_kpi_value_kpi FOREIGN KEY (kpi_id) REFERENCES kpi (id) ON DELETE RESTRICT,
    CONSTRAINT fk_kpi_value_operation FOREIGN KEY (operation_id) REFERENCES operation (id) ON DELETE CASCADE,
    INDEX idx_kpi_value_kpi_operation (kpi_id, operation_id, period_start)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO kpi (code, name, unit, calculation_type) VALUES
    ('TRAIN_TURNAROUND_TIME', 'Temps de retournement du train', 'h', 'DURATION'),
    ('ARRIVAL_TO_DEPARTURE', 'Duree totale entre arrivee et depart', 'h', 'DURATION'),
    ('UNLOADING_TIME', 'Temps de dechargement', 'h', 'DURATION'),
    ('TOTAL_WAITING_TIME', 'Temps total d''attente', 'h', 'SUM'),
    ('DEPARTURE_DELAY', 'Retard au depart', 'h', 'DIFF'),
    ('FORMATION_TIME', 'Temps de formation', 'h', 'DURATION'),
    ('BRAKE_TEST_TIME', 'Temps d''essais de freins', 'h', 'DURATION'),
    ('INCIDENT_COUNT', 'Nombre d''incidents', 'count', 'COUNT'),
    ('DOWNTIME', 'Temps d''indisponibilite', 'h', 'SUM');
