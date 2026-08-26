# Référentiel RAILWAY — step_type / event_type / incident_type

Source : `Resume_Centre_Controle_Port_MCD_JEE.docx` §9-10 (issu de l'analyse du fichier Excel Railway réel). Codes utilisés tels quels comme seed Flyway (`V3__rail_domain.sql`).

## step_type (domaine RAILWAY, séquence indicative)

| code | name |
|---|---|
| ARRIVAL | Arrivée du train |
| DECOUPLING | Désattelage |
| SAFETY_INSPECTION_ARRIVAL | Visite sécurité arrivée |
| LOCO_MANEUVER | Manœuvre / arrivée locomotive |
| WAGON_PLACEMENT | Placement des wagons |
| UNLOADING_WAIT | Attente déchargement |
| UNLOADING | Déchargement |
| FORMATION | Formation de la rame |
| SAFETY_INSPECTION_DEPARTURE | Visite sécurité départ |
| VEHICLE_CHECK | Relevé des véhicules |
| DECLARATION | Déclaration de la rame |
| CREW_ARRIVAL | Arrivée équipe de conduite (CMV/AMV) |
| BRAKE_TEST | Essais de freins |
| TRAIN_PRESENTATION | Présentation du train |
| DEPARTURE | Départ |

## incident_type (domaine RAILWAY, catégorie `category`)

| code | category | name |
|---|---|---|
| LOCOMOTIVE_FAILURE | materiel_roulant | Panne locomotive |
| PERSONNEL_UNAVAILABLE | humain | Absence/indisponibilité personnel |
| TRACK_OCCUPIED | voie | Voie occupée |
| EQUIPMENT_FAILURE | materiel_roulant | Panne équipement |
| BRAKE_PROBLEM | materiel_roulant | Problème de frein |
| TRACTION_PROBLEM | materiel_roulant | Problème de traction |
| DERAILMENT | voie | Déraillement |
| ACCIDENT | humain | Accident |
| WEATHER | autre | Intempéries |
| WAITING_AMV | humain | Attente AMV |
| WAITING_ADC | humain | Attente ADC |
| CMV_UNAVAILABLE | humain | CMV indisponible |
| OPERATIONAL_PRIORITY | autre | Priorité accordée à une autre opération |
| OTHER | autre | Autre cause (texte libre) |

## event_type (domaine RAILWAY — dérivés des incidents/étapes)

| code | category | default_severity |
|---|---|---|
| TRAIN_ARRIVAL | operation | info |
| TRAIN_DEPARTURE | operation | info |
| TRAIN_DELAY | operation | avertissement |
| TRAIN_FAILURE | materiel | critique |

## operation_type (domaine RAILWAY)

| code | name |
|---|---|
| TRAIN_ROTATION | Rotation ferroviaire |
| TRACK_MAINTENANCE | Maintenance de voie (domaine MAINTENANCE, utilisable côté rail) |

Statut : à valider avec les équipes ferroviaires avant utilisation en production.
