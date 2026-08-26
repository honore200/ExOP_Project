# Plan de formation — agents terrain (Railway / Port / Maintenance)

Statut : trame de formation à dérouler avant mise en production, en complément de `docs/manuel-utilisateur.md`. Durée indicative : 1h30 par groupe de domaine.

## Objectifs pédagogiques

À l'issue de la session, chaque agent doit être capable de :
1. Se connecter et retrouver son dashboard de domaine sans assistance.
2. Comprendre le vocabulaire commun (`docs/vocabulaire-metier.md`) : Operation, OperationStep, Event, Incident, Alert.
3. Saisir un événement/incident depuis son domaine et vérifier son apparition en temps réel sur le dashboard général (autre poste, pour démontrer le WebSocket).
4. Acquitter et résoudre une alerte.
5. Savoir qui contacter en cas de blocage (support technique + référent métier ADMIN).

## Déroulé suggéré

1. **Contexte (15 min)** — pourquoi ce centre de contrôle, qu'est-ce qui change par rapport aux outils/tableurs actuels, ce qui NE change PAS (le vocabulaire terrain reste le même, seule la saisie est centralisée).
2. **Démonstration (20 min)** — parcours complet : connexion → saisie d'une opération de son domaine → apparition dans le dashboard général → génération d'une alerte simulée → acquittement.
3. **Pratique guidée (40 min)** — chaque agent réalise le parcours sur un jeu de données de test (`db/seed`, environnement de formation isolé — ne jamais former sur la base de production).
4. **Questions / cas particuliers (15 min)** — recueillir les écarts entre le référentiel actuel (`docs/referentiel-rail.md` / `docs/proposition-port-call.md`) et la réalité terrain : **ces retours alimentent la validation Phase 0**, à ne pas traiter comme de simples questions support.

## Points d'attention identifiés (à valider Phase 0)

- La proposition `PORT_CALL` (`docs/proposition-port-call.md`) est un brouillon non encore confronté aux équipes portuaires — la session de formation Port est aussi la première occasion de validation réelle.
- Les référentiels `step_type`/`incident_type`/`cargo_type` sont extensibles sans migration (tables de référence) — toute étape/cause manquante signalée en formation peut être ajoutée rapidement par un ADMIN, pas besoin d'attendre une nouvelle version applicative.

## Support post-formation

Une période d'accompagnement renforcé (astreinte technique + référent métier disponible) est recommandée sur les 2 premières semaines suivant la mise en production, le temps que les habitudes de saisie se stabilisent.
