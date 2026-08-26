# Proposition de spécialisation PORT — à valider avec les équipes portuaires

Aucun fichier Excel Port équivalent au fichier Railway n'a été fourni. Cette proposition reprend les informations du premier document de conception (`centre_controle_omp_conception_1.md` §4.3) et les adapte au MCD générique (`OPERATION`/`OPERATION_STEP`), en traitant le navire comme un `ASSET` (cf plan approuvé, section 1.3). **Statut : brouillon de travail, même niveau de confiance que le reste du MCD — à confirmer avant mise en production.**

## Tables de spécialisation

- `vessel_detail` — extension 1:1 de `asset` (asset_type = `VESSEL`) : `imo_number`, `flag`, `vessel_type`, `length_m`, `gross_tonnage`.
- `port_call` — extension 1:0..1 de `operation` : navire (`asset`), quai (`location`), ETA/ATA/ETD/ATD, statut.
- `cargo_type` — référentiel des types de marchandise.
- `port_cargo_operation` — 1:N par `port_call`, une escale peut charger et décharger plusieurs types de marchandise.

## step_type proposé (domaine PORT)

| code | name |
|---|---|
| PILOT_BOARDING | Embarquement du pilote |
| BERTHING | Accostage |
| CUSTOMS_CLEARANCE | Dédouanement |
| CARGO_OPERATIONS | Opérations de chargement/déchargement |
| BUNKERING | Ravitaillement (soutage) |
| UNBERTHING | Appareillage |

## incident_type proposé (domaine PORT)

| code | category | name |
|---|---|---|
| CRANE_FAILURE | materiel | Panne de grue/portique |
| BERTH_UNAVAILABLE | infrastructure | Quai indisponible |
| WEATHER | autre | Intempéries / conditions de mer |
| CUSTOMS_DELAY | administratif | Retard douane |
| CARGO_DAMAGE | materiel | Avarie marchandise |
| ENVIRONMENTAL | environnement | Pollution / incident environnemental |
| OTHER | autre | Autre cause (texte libre) |

## cargo_type proposé

| code | name |
|---|---|
| CONTAINERS | Conteneurs |
| BULK | Vrac |
| WOOD | Bois |
| HYDROCARBONS | Hydrocarbures |
| GENERAL_CARGO | Marchandises générales |

## operation_type proposé (domaine PORT)

| code | name |
|---|---|
| SHIP_CALL_LOADING | Escale — chargement |
| SHIP_CALL_UNLOADING | Escale — déchargement |
| SHIP_CALL_BUNKERING | Escale — ravitaillement |
| SHIP_CALL_MAINTENANCE | Escale — maintenance |
