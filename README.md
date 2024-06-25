# TinyX

## Description
TinyX est une application de micro-blogging distribuée. Cette application est composée de plusieurs services qui fonctionnent ensemble pour fournir des fonctionnalités de publication de posts, interactions sociales, recherche, timeline, gestion des médias, sécurité, métriques, journalisation, cache et nettoyage des données.

## Architecture
L'application est composée de plusieurs services indépendants :

### Services
1. **models**
   - `Social.java` : Classe modèle pour représenter les interactions sociales entre utilisateurs, avec des champs pour `userId`, `targetUserId` et `action`.
   - `PostEntity.java` : Classe modèle pour représenter les posts, avec des champs pour `id`, `userId`, `content`, `replyTo`, `repostOf`, et `createdAt`.

2. **base**
   - `SocialService.java` : Interface pour définir les méthodes liées aux interactions sociales, comme vérifier si un utilisateur est bloqué.
   - `TimelineRepository.java` : Interface pour définir les méthodes liées à la gestion des timelines des utilisateurs.
   - `SearchRepository.java` : Interface pour définir les méthodes liées à la recherche de posts et d'utilisateurs.

3. **common**
   - `PostDTO.java` : Data Transfer Object (DTO) pour transférer les données des posts.
   - `SocialDTO.java` : DTO pour transférer les données des interactions sociales.
   - `ApiResponse.java` : Classe utilitaire pour créer des réponses API standardisées.
   - `DtoConverter.java` : Classe utilitaire pour convertir entre les entités et les DTO.

4. **post**
   - `ServiceProducer.java` : Classe pour produire les instances de `SearchRepository`, `SocialService` et `TimelineRepository`.
   - `PostResource.java` : Classe ressource pour gérer les endpoints liés aux posts.
   - `PostRepository.java` : Classe pour interagir avec la base de données MongoDB pour les opérations liées aux posts.
   - `PostService.java` : Service pour gérer la logique métier liée aux posts.

5. **social**
   - `SocialService.java` : Interface pour définir les méthodes liées aux interactions sociales spécifiques à ce service.
   - `SocialServiceImpl.java` : Implémentation de `SocialService` utilisant Neo4j pour gérer les interactions sociales.
   - `SocialResource.java` : Classe ressource pour gérer les endpoints liés aux interactions sociales.

6. **search**
   - `SearchResource.java` : Classe ressource pour gérer les endpoints de recherche.
   - `SearchService.java` : Service pour la logique métier de recherche.
   - `SearchRepositoryImpl.java` : Implémentation de `SearchRepository` utilisant Elasticsearch pour la recherche.

7. **timeline**
   - `UserTimelineService.java` : Service pour gérer la logique métier des timelines des utilisateurs.
   - `TimelineRepositoryImpl.java` : Implémentation de `TimelineRepository` utilisant Redis pour gérer les timelines.
   - `UserTimelineResource.java` : Classe ressource pour gérer les endpoints de timeline utilisateur.
   - `HomeTimelineResource.java` : Classe ressource pour gérer les endpoints de timeline générale.
   - `HomeTimelineService.java` : Service pour la logique métier de la timeline générale.

8. **media**
   - `FileUploadForm.java` : Classe pour gérer les formulaires de téléchargement de fichiers.
   - `Media.java` : Classe modèle pour représenter les médias.
   - `MediaService.java` : Service pour gérer la logique métier liée aux médias.
   - `MediaResource.java` : Classe ressource pour gérer les endpoints liés aux médias.

9. **security**
   - `SecurityService.java` : Service pour gérer la sécurité, avec des méthodes protégées par des rôles.

10. **metrics**
   - `MetricsService.java` : Service pour gérer les métriques de l'application.
   - `MetricsResource.java` : Classe ressource pour exposer les métriques via des endpoints.
   - `Metrics.java` : Classe pour représenter les métriques collectées.

11. **logging**
   - `LoggingResource.java` : Classe ressource pour gérer les endpoints de journalisation.
   - `LoggingService.java` : Service pour gérer la logique de journalisation.

12. **cache**
   - `CacheService.java` : Service pour gérer la logique de mise en cache utilisant Redis.

13. **cleanup**
   - `DataCleanupService.java` : Service pour gérer le nettoyage des données anciennes.
   - `PostRepositoryProducer.java` : Producteur pour injecter `PostRepository` dans les services de nettoyage.

14. **util**
   - `ApiVersioningFilter.java` : Filtre pour gérer la version des API.
   - `Neo4jDriverProducer.java` : Producteur pour injecter le driver Neo4j.
   - `MongoClientProvider.java` : Fournisseur pour injecter le client MongoDB.
   - `RateLimiter.java` : Classe pour implémenter un rate limiter simple.
   - `RedisClientProvider.java` : Fournisseur pour injecter le client Redis.
   - `ElasticSearchClientProvider.java` : Fournisseur pour injecter le client Elasticsearch.

