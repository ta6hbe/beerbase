# BEERBASE REST API

## Info:

A fully REST-full API based on SpringBoot, Spring Data JPA and Spring HATEOAS. 

Exposes a set of endpoints to make basic REST-full crud operations to a Beers repository, 
currently implemented in an in-memory H2 database.

The service is launched via an embedded Tomcat server.

## Hypermedia As the Engine Of Application State (HATEOAS)
All records are returned wrapped inside {@code RepresentationModel<EntityModel>} or 
{@code RepresentationModel<CollectionModel<EntityModel>>}
These are Spring Framework HATEOAS classes, that can be used to add the HTTP links that represeent
each record.

## Batch Operations - POST data to the Service.
The service also exposes a set of HTTP - POST endpoints that can be used to perform "Batch"
input operations. These are:
- /beers/urlupload - Consumes JSON - Produces JSON
 - Endpoint listening for a URL string, in the incoming JSON, 
   which will be used to retrieve new records.
- /beers/fileupload - Consumes JSON - Produces JSON
 - Endpoint listening for a URL string, where a file can be downloaded from, in order to parse
   its contents into DB records. File has to have the correct data structure, containing a list 
   of {@code BeerDTO} data transfer objects.
- /beers/multipartUpload - Consumes multipart/form-data - Produces JSON
 - Endpoint listening for MultipartForm File Upload. The file is downloaded to local file store,
   from where it gets parsed into a list of {@code BeerDTO} objects, which can then be used to 
   add the records to our DB.
   
You can use the POSTMAN collection of calls I have included in the `./postman` folder, to test this 
functionality. I have tested locally both with:
- passing a post body containing the PUNKAPI beers url: https://api.punkapi.com/v2/beers.
- Passing a post body containing a multipart file upload of /resources/static/local_data,josn.

Make sure you modify the collection to point to the host port you are using to run the service locally.
   
### Batch operations do not duplicate records - CreateOrUpdate to the Rescue!!!

The database Entity model holds additional data from what is exchanged / exposed via the view layer
model, namely the {@code BeerDTO}. The {@code Beer} entity model contains additional fields:
- {@code String} externalId
- {@code String} dataSource
- {@code String} remotePath;

These fields combined can be used to filter data by datasource type and id and make sure we 
match incoming records to existing ones, if they are from the same Datasource, they contain the same
external id (the sourcess identifier) and the same data soure. In case of URLs it is the url,
in case of a file, the {@code CanonicalFileName}.

**This has 3 advantages:**
1. When we make a batch call, the data queried is segmented to those records that
   belong to the same datasource. I.e. smaller, faster queries, than having to process all the 
   records, in the DB.
2. Data that already exists in our DB is identified, so it gets *Updated* and not duplicated, by
   being inserted again. 
3. We do not need to have BatchInsert and BatchUpdate queries.

### Use of @Transactional
All batch operations use transactional queries. If an erroris raised while carrying out a DB
operation, the transaction is reverted, and the existing records are left in their prior state.

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

