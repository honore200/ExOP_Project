# Manuel utilisateur — Centre de Contrôle OMP

## Connexion

Ouvrir `https://<serveur>/omp/pages/login.xhtml`, saisir l'identifiant et le mot de passe fournis par l'administrateur. En cas d'oubli, contacter un utilisateur ayant le rôle `ADMIN` (cf `docs/rbac-roles.md`).

## Navigation

Le menu principal (haut de page) donne accès à :

| Page | Contenu | Rôles typiques |
|---|---|---|
| **Vue générale** | KPIs consolidés tous domaines, opérations du jour, alertes/incidents actifs | Tous |
| **Railway Control** | Rotations ferroviaires en cours et récentes, tonnage, wagons, statut | RAILWAY, CONTROL_ROOM |
| **Port Operations** | Escales navires (ETA/ATA/ATD), quai, tonnage déclaré | PORT, CONTROL_ROOM |
| **Maintenance** | État des actifs (disponible/en maintenance/hors service), interventions | MAINTENANCE, CONTROL_ROOM |
| **Incidents / Alertes** | Alertes actives (acquittement/résolution), incidents ouverts (résolution) | CONTROL_ROOM, tous domaines |
| **Rapports** | Export PDF/Excel par domaine et période | MANAGER, ADMIN |
| **Administration** | Gestion utilisateurs et localisations | ADMIN |

## Opérations courantes

### Déclarer une rotation ferroviaire (agent RAILWAY)

Actuellement réalisé via l'API REST (`POST /api/rail/rotations`) — l'écran de saisie dédié n'est pas encore construit (prévu à la suite de la Phase 4, cf plan). En attendant, un agent ADMIN/RAILWAY peut utiliser un client REST (ou Postman) avec le token obtenu via `POST /api/auth/login`.

### Déclarer une escale navire (agent PORT)

Idem, via `POST /api/port/calls`.

### Acquitter une alerte

Page **Incidents / Alertes** → bouton "Acquitter" sur la ligne concernée. L'alerte passe en statut `ACKNOWLEDGED` et vous êtes enregistré comme responsable (`assigned_to`). Le bouton "Résoudre" la clôture définitivement.

### Générer un rapport

Page **Rapports** → choisir le domaine (Railway/Port/Maintenance) et la période → "Télécharger PDF" ou "Télécharger Excel".

## Support

En cas d'anomalie applicative, noter l'heure précise et l'action effectuée, puis contacter l'équipe technique — le journal d'audit (`audit_log`, consultable par un ADMIN) trace qui a fait quoi et quand.
