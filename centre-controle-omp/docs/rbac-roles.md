# RBAC — rôles applicatifs

Fusion du MCD (`Resume_Centre_Controle_Port_MCD_JEE.docx` §14) et du doc 1 (§8). Rôles stockés dans la table `role` (référentiel, pas un ENUM Java figé), affectés via `user_role`.

| code | description | droits |
|---|---|---|
| ADMIN | Administrateur | Accès total, gestion utilisateurs/rôles/référentiels |
| CONTROL_ROOM | Salle de contrôle | Lecture/écriture tous domaines, acquittement alertes tous domaines |
| RAILWAY | Agent Railway | Saisie/lecture des opérations du domaine RAILWAY |
| PORT | Agent Port | Saisie/lecture des opérations du domaine PORT |
| MAINTENANCE | Agent Maintenance | Saisie/lecture des opérations du domaine MAINTENANCE, gestion des `asset` |
| MANAGER | Direction | Lecture dashboards + rapports, pas de saisie |
| VIEWER | Lecteur | Consultation dashboards uniquement |

Mapping `@RolesAllowed` (JAX-RS/EJB) : les ressources d'écriture (`POST`/`PATCH`/`DELETE`) exigent `ADMIN`, `CONTROL_ROOM`, ou le rôle du domaine concerné (`RAILWAY`/`PORT`/`MAINTENANCE`) ; `GET` accessible à tous les rôles authentifiés. Détail par ressource à affiner en Phase 7 (audit sécurité).
