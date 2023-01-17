<h1 align="center">
  Campsite
  <br>
</h1>
<h4 align="center">This is a microservice intended to handle campsite's reservations.</h4>
<p align="center">
  <a href="#key-features">Key Features</a> •
  <a href="#dependencies">Dependencies</a> •
  <a href="#how-to-use">How To Use</a> •
  <a href="#documentation">Documentation</a> •
  <a href="#faq">FAQ</a>
</p>

## Key Features

* Provide information of the availability of the campsite for a given date range with the default being 1 month.
* Create a reservation.
* Update a reservation.
* Cancel a reservation.

#### Business Rules
* To streamline the reservations a few constraints need to be in place:
    * The campsite will be free for all.
    * The campsite can be reserved for max 3 days.
    * The campsite can be reserved minimum 1 day(s) ahead of arrival and up to 1 month in advance.
    * Reservations can be cancelled anytime.
    * For sake of simplicity assume the check-in & check-out time is 12:00 AM  

## Dependencies
* [MySQL](https://dev.mysql.com/)
* [Java 17+ (optional through SDK man)](https://sdkman.io/jdks)
* [Docker](https://docs.docker.com/install/)

## How To Use

### Build the image

```shell
sh script-build.sh
```
### Running the project

```shell
sh script-run.sh
```
### Running the tests

In order to run the project tests you need to execute the following command:

```
sh script-test.sh
```

## Documentation

* **Swagger**: http://localhost:8080/campsite/swagger-ui/index.html#/reservation-controller
* **OpenAPI**: http://localhost:8080/campsite/api-docs
* **Postman**: **Campsite-API.postman_collection.json**

## FAQ

* If you want to add new features to this project please [see the contribution guide](.github/CONTRIBUTING.md)
* Questions?, <a href="mailto:luis.carbonel1991@gmail.com?Subject=Question about Project" target="_blank">write here</a>
