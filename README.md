# TinyX

## Table of Contents
1. [Description](#description)
2. [Architecture](#architecture)
   - [Overview](#overview)
   - [Architecture Diagram](#architecture-diagram)
   - [Services](#services)
3. [Operation](#operation)
4. [Technologies Used](#technologies-used)
5. [Tests](#tests)

## Description

TinyX is a distributed micro-blogging application designed to provide a smooth and performant user experience. The application uses a microservices architecture to deliver functionalities such as post publishing, social interactions, search, timelines, media management, security, metrics, logging, caching, and data cleanup.

## Architecture

### Overview

TinyX employs a microservices architecture, allowing for better scalability, easier maintenance, and rapid application evolution.

### Architecture Diagram

![TinyX Architecture](https://i.imgur.com/cYw9cZ7.png)

The diagram illustrates the complete architecture of TinyX:
- The client communicates with the API Gateway.
- The API Gateway directs requests to the various microservices.
- An Event Bus facilitates asynchronous communication between services.
- Each service is connected to its specific database (MongoDB, Neo4j, Elasticsearch, Redis).

### Services

1. **Post**: Manages CRUD operations on posts.
2. **Social**: Manages social interactions (follow, like, block).
3. **Search**: Provides search functionalities for posts and users.
4. **Timeline**: Manages user and home timelines.
5. **Media**: Handles media upload and retrieval.
6. **Security**: Manages authentication and authorization.
7. **Metrics**: Collects and exposes application metrics.
8. **Logging**: Manages event logging.
9. **Cache**: Implements caching to improve performance.
10. **Cleanup**: Handles periodic cleanup of old data.

## Operation

The system operates as follows:

1. Client requests are directed to the appropriate services via the API Gateway.
2. Services interact with their respective databases for CRUD operations.
3. Important events (post creation, like, follow) are published on the Event Bus.
4. Relevant services consume these events to maintain data consistency.
5. Timelines are updated in real-time thanks to Redis.
6. Search is optimized through Elasticsearch indexing.

## Technologies Used

- Java 11
- Quarkus 2.16.5.Final
- MongoDB 4.4
- Neo4j 4.2
- Elasticsearch 7.17
- Redis 6.2
- Maven 3.8

## Tests

Unit tests are provided for several services. To run the tests:

```bash
mvn test
```
