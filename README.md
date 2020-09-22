# d2d backend service

## Overview

This service provides API for the functions as below.
 
* Vehicle Registration: Vehicles are registered to system through API
* Vehicle Location Update: Location of vehicles are provided to service and stored
* Vehicle Deregistration: Vehicles are removed from system and their location updates do not take effect

API for frontend
* Vehicle Query: Query vehicles inside an area regarding their locations 

API for testing purposes
* Table Truncation: In order to start test from scratch this function provided to truncate all application tables 

Vehicle and location data is expected to feed this service continuously. 
Stored data is provided to frontend clients to visualize vehicle location.

For data analytics purposes, all vehicle and location data are recorded in separate log tables.
Those are not affected by deregistration.
 
## Main Technologies 

* Java 11
* Spring Boot 2.1.8
* Hibernate 5.3.7
* PostgreSQL 12.4
* Maven

Additional Libraries
* junit for testing
* h2database for testing
* spring-boot-starter-test for testing
* apache lucene-spatial for spatial calculations
* jackson for json processing

## APIs

### Vehicle Registration
`POST /vehicles`
Vehicle is recorded with UUID.
One row inserted in VEHICLE table.
One row inserted in VEHICLE_LOG table.

Possible response HTTP codes
* 204 : success 
* 400 : bad request
    - parameter validation error,
    - vehicle with 'id' already exists
* 500 : interval server error 

Request example:
```json
{ "id": "720a7414-0276-40d3-9b28-47970f86dd09" }
```

Response example : HTTP 204 / No content
Response example : HTTP 400 / Bad request
```json
{ "errorDescription" : "id : size must be between 1 and 36" } 
```
```json
{ "errorDescription" : "id : must not be empty , id : size must be between 1 and 36" }
```
```json
{ "errorDescription" : "Vehicle with id: 5cfab4f3-84b4-4b0a-938e-f2cf091a3fef already exists" }
```
Response example : HTTP 500 / Internal server error
```json
{ "errorDescription" : "some error message" }
```

* Since abc123 used as an example on challenge page there is no validation for UUID format

### Vehicle Location Update
`POST /vehicles/:id/locations`
Vehicle location is received and recorded
One row inserted in LOCATION table.
One row inserted in LOCATION_LOG table.

Possible response HTTP codes
* 204 : success 
* 400 : bad request
    - parameter validation error
    - vehicle not registered with this id
* 500 : unexpected error 

Request example:
```json
{ "lat": 10.0, "lng": 20.0, "at": "2017-09-01T12:00:00Z" }
```

Response example : HTTP 204 / No content
Response example : HTTP 400 / Bad request
```json
{ "errorDescription" : "at : must not be null" } 
```
```json
{ "errorDescription" : "No vehicle exists for id: e56cb52d-31a3-4c0e-a7be-2ee1b79308f5" }
```
Response example : HTTP 500 / Internal server error
```json
{ "errorDescription" : "error message will be here" }
```

### Vehicle Deregistration

`DELETE /vehicles/:id`
Vehicle and its locations are deleted
One row is deleted from VEHICLE table.
Zero or more rows are deleted from LOCATION table.

Possible response HTTP codes
* 204 : success 
* 400 : bad request
    - parameter validation error
    - vehicle not registered with this id
* 500 : unexpected error 

Response example : HTTP 204 / No content
Response example : HTTP 400 / Bad request
```json
{ "errorDescription" : "No vehicle exists for id: 88fc8d6c-52d9-42ca-afd1-b0d8f192f369" }
```
Response example : HTTP 500 / Internal server error
```json
{ "errorDescription" : "error message will be here" }
```

### Vehicle Query

`GET /vehicles/{northEastLat}/{northEastLng}/{southWestLat}/{southWestLng}`
Retrieves registered vehicles inside this area and not away from 3.5km to the center(lat: 52.53, lng: 13.403)

Possible response HTTP codes
* 200 : success 
* 500 : unexpected error 

Response example : HTTP 200
```json
{
  "vehicleList": [
    {
      "locations": [
        {
          "lat": 52.53,
          "lng": 13.4
        }
      ],
      "uuid": "60b0207a-2611-479a-9d1a-e1debdfdc6ed"
    },
    {
      "locations": [
        {
          "lat": 52.53,
          "lng": 13.41
        },
        {
          "lat": 52.53,
          "lng": 13.4
        }
      ],
      "uuid": "720a7414-0276-40d3-9b28-47970f86dd09"
    }
  ]
}
```
Response example : HTTP 500 / Internal server error
```json
{ "errorDescription" : "error message will be here" }
```

### Vehicle Query

`GET /reset`
For testing purposes it truncates all tables so that a fresh test can begin.

## Installation

### Heroku
The service is installed on Heroku.
PostgreSQL DB is installed on Heroku.

url: https://d2d-backend-gungor.herokuapp.com 

### Docker

Using the commands below, app can run in your local container.
Application can be reached from this url: http://localhost:8080
```bash
cd d2d-challenge-service
docker build -t d2d/backend-gungor .
docker run -p 8080:8080 -e "SPRING_PROFILES_ACTIVE=test" -t  d2d/backend-gungor
```

PostgreSQL DB installed on Heroku can be used. 
Alternatively you can your own PostgreSQL DB by providing connection to the run command below

* If posgtresql hostname (<postgre-host>) contains 'localhost' or '127.0.0.1' replace it with 'host.docker.internal'

```bash
docker run -p 8080:8080 -e "SPRING_PROFILES_ACTIVE=test" -e "JAVA_OPTS=-Dspring.datasource.url=jdbc:postgresql://<postgre-host>:<postgre-port>/<postgre-databasename> -Dspring.datasource.username=<postgre-user> -Dspring.datasource.password=<postgre-password> -Dspring.jpa.properties.hibernate.default_schema=<postgre-schema>" -t  d2d/backend-gungor
```

## Test
Ensure that JAVA_HOME environment variable set to min JDK 11
```bash
mvn clean test
```
