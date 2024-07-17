# TinyX

## Table des matières
1. [Description](#description)
2. [Architecture](#architecture)
   - [Vue d'ensemble](#vue-densemble)
   - [Services](#services)
3. [Fonctionnement](#fonctionnement)
   - [Flux de données](#flux-de-données)
   - [Gestion des posts](#gestion-des-posts)
   - [Interactions sociales](#interactions-sociales)
   - [Recherche](#recherche)
   - [Timeline](#timeline)
   - [Médias](#médias)
4. [Technologies utilisées](#technologies-utilisées)
5. [Configuration et déploiement](#configuration-et-déploiement)
6. [API Endpoints](#api-endpoints)
7. [Tests](#tests)
8. [Contribution](#contribution)
9. [Licence](#licence)

## Description

TinyX est une application de micro-blogging distribuée, conçue pour offrir une expérience utilisateur fluide et performante. L'application est composée de plusieurs services indépendants qui travaillent ensemble pour fournir des fonctionnalités de publication de posts, d'interactions sociales, de recherche, de timeline, de gestion des médias, de sécurité, de métriques, de journalisation, de cache et de nettoyage des données.

## Architecture

### Vue d'ensemble

TinyX utilise une architecture de microservices, où chaque composant est développé et déployé indépendamment. Cette approche permet une meilleure scalabilité, une maintenance plus aisée et une évolution plus rapide de l'application.

### Services

1. **Models**: Définit les structures de données de base utilisées dans l'application.
   - `Social.java`: Modèle pour les interactions sociales.
   - `PostEntity.java`: Modèle pour les posts.

2. **Base**: Contient les interfaces de base pour les services principaux.
   - `SocialService.java`: Interface pour les interactions sociales.
   - `TimelineRepository.java`: Interface pour la gestion des timelines.
   - `SearchRepository.java`: Interface pour la recherche.

3. **Common**: Fournit des classes utilitaires et des DTO (Data Transfer Objects) communs.
   - `PostDTO.java`: DTO pour les posts.
   - `SocialDTO.java`: DTO pour les interactions sociales.
   - `ApiResponse.java`: Utilitaire pour créer des réponses API standardisées.
   - `DtoConverter.java`: Convertisseur entre entités et DTO.

4. **Post**: Gère la création, la récupération et la suppression des posts.
   - `PostResource.java`: Endpoints API pour les opérations sur les posts.
   - `PostRepository.java`: Interactions avec la base de données MongoDB pour les posts.
   - `PostService.java`: Logique métier pour les posts.

5. **Social**: Gère les interactions sociales entre utilisateurs.
   - `SocialService.java`: Interface pour les interactions sociales spécifiques.
   - `SocialServiceImpl.java`: Implémentation utilisant Neo4j.
   - `SocialResource.java`: Endpoints API pour les interactions sociales.

6. **Search**: Fournit des fonctionnalités de recherche.
   - `SearchResource.java`: Endpoints API pour la recherche.
   - `SearchService.java`: Logique de recherche.
   - `SearchRepositoryImpl.java`: Implémentation utilisant Elasticsearch.

7. **Timeline**: Gère les timelines des utilisateurs.
   - `UserTimelineService.java`: Logique pour les timelines utilisateur.
   - `TimelineRepositoryImpl.java`: Implémentation utilisant Redis.
   - `UserTimelineResource.java`: Endpoints API pour les timelines utilisateur.
   - `HomeTimelineResource.java`: Endpoints API pour les timelines générales.
   - `HomeTimelineService.java`: Logique pour les timelines générales.

8. **Media**: Gère le téléchargement et la récupération des médias.
   - `MediaService.java`: Logique pour la gestion des médias.
   - `MediaResource.java`: Endpoints API pour les opérations sur les médias.

9. **Security**: Gère l'authentification et l'autorisation.
   - `SecurityService.java`: Logique de sécurité avec méthodes protégées par rôles.

10. **Metrics**: Collecte et expose des métriques sur l'application.
   - `MetricsService.java`: Logique pour la collecte des métriques.
   - `MetricsResource.java`: Endpoints API pour exposer les métriques.

11. **Logging**: Gère la journalisation des événements de l'application.
   - `LoggingResource.java`: Endpoints API pour la journalisation.
   - `LoggingService.java`: Logique de journalisation.

12. **Cache**: Implémente la mise en cache pour améliorer les performances.
   - `CacheService.java`: Logique de mise en cache utilisant Redis.

13. **Cleanup**: Gère le nettoyage périodique des données anciennes.
   - `DataCleanupService.java`: Logique pour le nettoyage des données.

14. **Util**: Fournit des utilitaires et des configurations communes.
   - `ApiVersioningFilter.java`: Filtre pour la gestion des versions d'API.
   - `Neo4jDriverProducer.java`: Configuration du driver Neo4j.
   - `MongoClientProvider.java`: Configuration du client MongoDB.
   - `RateLimiter.java`: Implémentation d'un limiteur de taux simple.
   - `RedisClientProvider.java`: Configuration du client Redis.
   - `ElasticSearchClientProvider.java`: Configuration du client Elasticsearch.

## Fonctionnement

### Flux de données

1. Les requêtes entrantes sont d'abord traitées par le service approprié (Post, Social, Search, etc.).
2. Les services interagissent avec leurs bases de données respectives (MongoDB, Neo4j, Elasticsearch, Redis) pour récupérer ou stocker des données.
3. Les événements importants (création de post, like, follow, etc.) sont publiés et consommés par les services concernés pour maintenir la cohérence des données.
4. Les réponses sont formatées et renvoyées au client.

### Gestion des posts

1. Création d'un post:
   - Le client envoie une requête POST à `/posts`.
   - `PostService` valide le contenu et crée le post dans MongoDB.
   - Le post est indexé dans Elasticsearch pour la recherche.
   - Les timelines utilisateur et home sont mises à jour dans Redis.
   - Un événement de création de post est publié.

2. Récupération d'un post:
   - Le client envoie une requête GET à `/posts/{id}`.
   - `PostService` récupère le post depuis MongoDB.

3. Suppression d'un post:
   - Le client envoie une requête DELETE à `/posts/{id}`.
   - `PostService` supprime le post de MongoDB.
   - Le post est retiré de l'index Elasticsearch.
   - Le post est supprimé des timelines dans Redis.
   - Un événement de suppression de post est publié.

### Interactions sociales

1. Follow/Unfollow:
   - Le client envoie une requête POST à `/social` avec l'action appropriée.
   - `SocialService` met à jour la relation dans Neo4j.
   - Un événement de follow/unfollow est publié pour mettre à jour les timelines.

2. Like/Unlike:
   - Le client envoie une requête POST à `/social` avec l'action appropriée.
   - `SocialService` met à jour la relation dans Neo4j.
   - Un événement de like/unlike est publié pour mettre à jour les timelines et les compteurs.

3. Block/Unblock:
   - Le client envoie une requête POST à `/social` avec l'action appropriée.
   - `SocialService` met à jour la relation dans Neo4j.
   - Un événement de block/unblock est publié pour ajuster les timelines et les permissions.

### Recherche

1. Le client envoie une requête GET à `/search/posts` ou `/search/users` avec un terme de recherche.
2. `SearchService` utilise Elasticsearch pour effectuer la recherche.
3. Les résultats sont retournés au client.

### Timeline

1. Timeline utilisateur:
   - Le client envoie une requête GET à `/timeline/user/{userId}`.
   - `UserTimelineService` récupère la timeline depuis Redis.

2. Timeline home:
   - Le client envoie une requête GET à `/timeline/home/{userId}`.
   - `HomeTimelineService` récupère la timeline depuis Redis.

3. Mise à jour des timelines:
   - Les événements de création de post, like, follow, etc. sont consommés par `TimelineUpdateListener`.
   - Les timelines appropriées sont mises à jour dans Redis.

### Médias

1. Upload de média:
   - Le client envoie une requête POST multipart à `/media/upload`.
   - `MediaService` stocke le fichier et enregistre les métadonnées dans MongoDB.

2. Récupération de média:
   - Le client envoie une requête GET à `/media/{id}`.
   - `MediaService` récupère le fichier et le renvoie au client.

## Technologies utilisées

- Java 11
- Quarkus
- MongoDB
- Neo4j
- Elasticsearch
- Redis
- Maven

## Configuration

1. Assurez-vous d'avoir Java 11 et Maven installés.
2. Clonez le dépôt: `git clone https://github.com/your-repo/tinyx.git`
3. Naviguez dans le répertoire du projet: `cd tinyx`
4. Compilez le projet: `mvn clean package`

## API Endpoints

- Posts: `/posts`
- Social: `/social`
- Search: `/search`
- Timeline: `/timeline`
- Media: `/media`
- Metrics: `/metrics`
- Logging: `/logging`

Pour plus de détails sur les endpoints spécifiques, référez-vous à la documentation API de chaque service.

## Tests

Des tests unitaires sont fournis pour chaque service. Pour exécuter les tests:

```
mvn test
```



