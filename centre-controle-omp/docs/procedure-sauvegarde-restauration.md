# Procédure de sauvegarde et restauration

## Sauvegarde automatique (MySQL)

Le conteneur `mysql` (cf `docker/docker-compose.yml`) stocke ses données dans le volume Docker `omp_mysql_data`. Deux mécanismes complémentaires :

1. **Dump logique quotidien** (recommandé pour la portabilité et la restauration sélective) :
   ```bash
   docker exec omp-mysql-1 sh -c \
     'mysqldump -u root -p"$MYSQL_ROOT_PASSWORD" --single-transaction --routines --triggers omp_ccontrole' \
     | gzip > /chemin/vers/backups/omp_ccontrole_$(date +%Y%m%d_%H%M).sql.gz
   ```
   À planifier via `cron` sur l'hôte (ex. tous les jours à 2h, avant la fenêtre de purge applicative à 3h — cf `PurgeTimerBean`). Rétention recommandée : **30 jours** en local + copie hors-site (NAS, support externe) hebdomadaire.

2. **Snapshot du volume Docker** (restauration plus rapide en cas de sinistre serveur complet) :
   ```bash
   docker run --rm -v omp_mysql_data:/data -v /chemin/vers/backups:/backup \
     alpine tar czf /backup/omp_mysql_data_$(date +%Y%m%d).tar.gz -C /data .
   ```

## Restauration

### À partir d'un dump logique

```bash
gunzip < omp_ccontrole_20260101_0200.sql.gz | \
  docker exec -i omp-mysql-1 mysql -u root -p"$MYSQL_ROOT_PASSWORD" omp_ccontrole
```

Flyway ne rejoue pas les migrations sur une base déjà peuplée (`flyway_schema_history` restaurée avec le dump) — vérifier après restauration que `SELECT * FROM flyway_schema_history` correspond à la version attendue.

### À partir d'un snapshot de volume

```bash
docker compose down
docker volume rm omp_mysql_data
docker volume create omp_mysql_data
docker run --rm -v omp_mysql_data:/data -v /chemin/vers/backups:/backup \
  alpine tar xzf /backup/omp_mysql_data_20260101.tar.gz -C /data
docker compose up -d
```

## Test de restauration

**À exécuter au moins une fois avant mise en production**, puis trimestriellement : restaurer un dump récent sur un environnement de test isolé (`docker compose -f docker-compose.yml -p omp-restore-test up`), vérifier que l'application démarre et que les données attendues sont présentes. Une sauvegarde jamais testée n'est pas une garantie de restauration.

## Fichiers uploadés (pièces jointes, rapports générés)

Phase 1-6 du projet ne mettent pas encore en place de stockage de fichiers uploadés persistant (hors scope des `Operation`/`Event`) — si une fonctionnalité de pièce jointe est ajoutée ultérieurement, prévoir un volume Docker dédié inclus dans la même politique de sauvegarde que `omp_mysql_data`.
