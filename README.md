# Introduction
This is a recipe REST API written in Java and Spring Boot, supporting all CRUD operations as well as complex search. The [full API definition](src/main/resources/recipe-api-1.0.0.yaml) in OpenAPI-3.0 format helps understanding the full functionality. The application is tested in h2 (driver not included in build) and mariaDB.

# Running
## In Eclipse
In Eclipse, the gradle tasks *eclipse* and *openApiGenerate* need to be run in order to generate files for [mapstruct](https://mapstruct.org/) and the files from the API specification.
## Local with java
Build the project with:

```
./gradlew build
./gradlew bootJar
```
Create an own `application-local.yml` similar to the provided `application-example.yml`. Run from `build/libs` with:

```
java -Dspring.profiles.active=local -jar recipe-api-1.0.0.jar
```
## Local with docker
Build the docker image with (with a local running docker daemon, for using a repository see [the JIB gradle plugin documentation](https://github.com/GoogleContainerTools/jib/tree/master/jib-gradle-plugin#configuration):

```
./gradlew build
./gradlew jibDockerBuild
```

Then run the built image with:

```
docker run -p 8080:8080 -p 8081:8081 --add-host=host.docker.internal:host-gateway --mount type=bind,source="$(pwd)"/application-docker.yml,target=/application-docker.yml recipe:1.0.0
```

Notes:
- the two ports are for the actual application (8080) and Spring Boot Actuator (8081)
- the `--add-host` is needed, when a local database not on docker is used
- the file in `--mount` needs to be present and needs to be similar to `application-example.yml`, replacing `localhost` by `host.docker.internal` as specified in `--add-host`

## In production
In production the image produced above can easily be incorporated into a Helm chart. A configuration similar to `application-example.yml` needs to be provided by usual means of Spring Boot.

# Architectural decisions
## API first
The application follows an API first approach. Developing an API against a contract, which allows to generate classes from the API specification, lowering chances of violating the contract.
## Separation of API and persistent data model
Separating the API from the persistent data model allows to modify both sides independently without any serious impact on each other. While at this point, both definitions are still similar, this may change in the future. For example, switching to a many-to-many relation between recipes and ingredients would make both representations differ from each other.
## Docker
Today, most companies are used to using containerization, so there is no way around building a docker image with the JIB gradle plugin.
## Spring Cloud Sleuth
Spring Cloud Sleuth allows to trace requests easily by dealing with trace ids either specified by the consumer or by itself. Those trace ids get incorporated into the log statements, making it easy to follow requests despite high load.
## Prometheus
Using the Micrometer Prometheus library, Spring Actuator gets expanded with a Prometheus status page, which can be parsed automatically and used to fill Prometheus, thus making further performance monitoring through e.g. grafana possible.
## Liquibase
Working in a team, database versioning becomes as important as source code versioning. This is solved with Liquibase.
## Logbook
Logbook was selected for logging request.

# TODOs
## Security
Right now, security is missing. Using OAuth2 tokens would be an option for that.
## Helm file
A helm file for a complete deployment including an optional database could be added.
## JSON logging
Right now logs on docker are in plain text, which makes searching e.g. in Kibana harder. JSON logging could facilitate that.

# Example calls

## Creating a recipe
Request:

```http
POST localhost:8080/recipe

{
    "title": "Some recipe",
    "servings": 5,
    "vegetarian": false,
    "instructions": "Do this and that",
    "ingredients": [
        {
            "name": "meat",
            "unit": "kg",
            "number": 1.5
        },
        {
            "name": "chili",
            "number": 2
        }
    ]
}
```

Response:
```json
{
    "id": "bd0e5165-33ae-4029-ae3d-d99006e6df53",
    "title": "Some recipe",
    "servings": 5,
    "vegetarian": false,
    "instructions": "Do this and that",
    "ingredients": [
        {
            "name": "meat",
            "unit": "kg",
            "number": 1.5
        },
        {
            "name": "chili",
            "unit": null,
            "number": 2.0
        }
    ]
}
```

### Updating a recipe
Request:

```http
PUT localhost:8080/recipe/bd0e5165-33ae-4029-ae3d-d99006e6df53

{
    "title": "Some recipe",
    "servings": 5,
    "vegetarian": false,
    "instructions": "Do this and that",
    "ingredients": [
        {
            "name": "meat",
            "unit": "kg",
            "number": 1.5
        },
        {
            "name": "chili",
            "number": 2
        }
    ]
}
```

Response: see [Creating a recipe](#creating-a-recipe).

### Getting a recipe
Request:

```http
GET localhost:8080/recipe/bd0e5165-33ae-4029-ae3d-d99006e6df53
```

Response: see [Creating a recipe](#creating-a-recipe).

### Deleting a recipe

Request:

```http
DELETE localhost:8080/recipe/bd0e5165-33ae-4029-ae3d-d99006e6df53
```

Response: empty

### Searching

Request:

```http
GET localhost:8080/recipe?page=2&size=5&ingredientsExcluded=salt&ingredientsIncluded=pepper&text=cook&servings=2&vegetarian=true
```

- `page` and `size` are for pagination
- `ingredientsExcluded` and `ingredientsIncluded` specify, which ingredients must (not) be part of the recipe and can be duplicated
- `text` is plain text search in the recipe instructions

Response:

```json
{
    "content": [
        ...
    ],
    "pageMetadata": {
        "size": 5,
        "totalElements": 4,
        "totalPages": 1,
        "number": 0
    }
}
```

Content is omitted for brevity and a list of recipes similar to the response of [Creating a recipe](#creating-a-recipe).