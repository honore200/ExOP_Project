Centre de Contrôle OMP — Statut du projet
Vue d'ensemble
Centre de contrôle opérationnel unique centralisant les activités portuaires et ferroviaires de l'OMP. Stack Jakarta EE 10 (WildFly/Payara, JSF+PrimeFaces, JAX-RS, JPA/Hibernate), base MySQL 8, déploiement Docker on-premise.

Architecture
Backend : EJB/CDI + JAX-RS (API REST), JPA/Hibernate, migrations Flyway
Frontend : JSF + PrimeFaces (dashboards natifs, pas de build JS séparé)
Temps réel : WebSocket (JSR 356) pour pousser les événements vers les dashboards
Auth : JWT côté API, session container-managed côté web
Modèle métier : socle générique (Operation, OperationStep, Event, Incident, Alert, Asset, Resource) + spécialisations par domaine (RailwayRotation, PortCall proposé)
RBAC : 7 rôles (ADMIN, CONTROL_ROOM, RAILWAY, PORT, MAINTENANCE, MANAGER, VIEWER)
Modules Maven
Module	Rôle	Fichiers Java
omp-common	Entités, repos, services partagés	78
omp-api	API REST, sécurité JWT, WebSocket	14
omp-web	Frontend JSF/PrimeFaces	4
omp-rail	Domaine ferroviaire	3
omp-port	Domaine portuaire	0 (vide)
omp-ear	Packaging EAR	0 (vide)
Avancement par phase (~40% du projet global)
Phase	Contenu	État
0 — Cadrage	Stack, vocabulaire métier, rôles	✅ 100%
1 — Socle	Docker, MySQL, auth JWT, users/sites	✅ ~85%
2 — Événements	CRUD events + WebSocket temps réel	✅ ~80%
3 — Module Port	Navires, quais, douane	❌ 0%
4 — Module Rail	Trains, rotations	🟡 ~40-45%
5 — Dashboard global + Alertes	KPI, vue transversale	🟡 ~20%
6 — Rapports	Export PDF/Excel	❌ 0%
7 — Durcissement	Tests, sécurité, mise en prod	❌ 0%
Prochaine étape recommandée
Compléter RailResource avant de démarrer le Port :

Update/delete de rotation
Endpoints de transition d'étape (OperationStep)
Câblage des incidents domaine RAILWAY


