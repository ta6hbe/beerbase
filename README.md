# BEERBASE REST API

## Info:

A fully REST-full API based on SpringBoot, Spring Data JPA and Spring HATEOAS. 

Exposes a set of endpoints to make basic REST-full crud operations to a Beers repository, 
currently implemented in an in-memory H2 database.

The service is launched via an embedded Tomcat server.

--------------------------------------------------------------------------------------------

## How to Run:

This project uses Gradle builds and contains Gradle Wrapper. To build, first make sure you can
successfully build and run tests.

From the project root issue:
```
./gradlew clean build
```

### Run with Gradle:

If you run with Gradle, you will need your local port 8080 free.

Now issue:
```
./gradlew bootRun
```

### Run with Docker-Compose

If you run with docker-compose:
- You will need docker installed in your local environment.
- You will need local port 9090 to be free, as this will map the host port 8080 by default
  to your local port 9090 via the `.env` variables file included.
- Running with docker-compose though, this can be altered to any local port of your choice, by issuing:

```
SERVER_PORT=<Enter Your PORT Number here> dockoer-compose up --build
```

--------------------------------------------------------------------------------------------

