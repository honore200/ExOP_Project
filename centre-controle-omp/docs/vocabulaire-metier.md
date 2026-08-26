# Vocabulaire métier — Centre de Contrôle OMP

Statut : base de travail générée à partir du MCD (`Resume_Centre_Controle_Port_MCD_JEE.docx`) — **à valider avec les équipes terrain avant mise en production**.

## Concepts centraux

- **Domain** — domaine fonctionnel de haut niveau : `RAILWAY`, `PORT`, `MAINTENANCE`.
- **Operation** — unité métier centrale. Toute activité tracée (rotation de train, escale navire, intervention de maintenance) est une `Operation`.
- **OperationStep** — étape séquencée d'une opération (ex. arrivée, désattelage, déchargement, départ pour le rail ; accostage, déchargement, chargement, appareillage pour le port).
- **Event** — fait horodaté rattaché à une opération (et éventuellement à une étape précise, un actif, une localisation).
- **Incident** — perturbation ayant un début/fin, une gravité, une cause catégorisée.
- **Alert** — notification actionnable dérivée d'un événement, avec cycle de vie (créée → acquittée → résolue).
- **Asset** — actif physique persistant (locomotive, grue, navire, convoyeur...).
- **Resource** — ressource opérationnelle mobilisée (personne, équipe, véhicule).
- **KPI / KpiValue** — indicateur défini une fois, calculé périodiquement par opération.

## Spécialisations de domaine

- **RailwayRotation** — spécialisation Railway d'une `Operation` (1:0..1).
- **PortCall** — spécialisation Port d'une `Operation` (1:0..1), proposée par analogie avec `RailwayRotation` (cf `docs/proposition-port-call.md`).

## Règle de modélisation à respecter dans tout le code

Ne jamais ajouter de colonne domaine-spécifique sur les tables génériques (`operation`, `operation_step`, `event`, `incident`). Toute donnée propre à un domaine va dans sa table de spécialisation (`railway_rotation`, `port_call`, `port_cargo_operation`) ou est portée par un référentiel (`operation_type`, `step_type`, `event_type`, `incident_type`, `cargo_type`).
