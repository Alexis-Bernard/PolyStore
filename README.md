# PolyStore

PolyStore is a small project that allows you to take orders from a store and checkout them. This project is made with six microservices :

- `Discovery server` : Eureka server. It allows to register all microservices and to discover them.
- `Gateway` : Simple gateway that allows to redirect requests to the right microservice. It also allows to aggregate data from several microservices.
- `Catalog` : Manage products and their name.
- `Inventory` : Manage the products quantities and their price.
- `Cart` : Manage the cart of a customer.
- `Order` : Manage the orders of a customer.

## Getting started

### Prerequisites

- Java 11
- Maven

### Running

First, you need to clone the project :

```bash
git clone git@github.com:Alexis-Bernard/PolyStore.git
```

Move to the project directory :

```bash
cd {project_name}
```

Then, you need to build the project :

```bash
mvn package
```

Finally, you can run the project :

```bash
java -jar ./target/{project_name}-0.0.1-SNAPSHOT.jar
```

> :warning: **You need to run the discovery server first !**: All microservices need to register to the discovery server. If you don't run the discovery server, the microservices will not be able to register and cannot work.

## Project structure

The project use the SAGA pattern with the choreography approach only for the checkout process. The other processes are simple CRUD operations.

Here is the sequence diagram of the checkout process :

[![](https://mermaid.ink/img/pako:eNqFUk1LAzEQ_StDru2CiKccCqWKehBF60X2Mk1mt6HZpOajUkr_u8nuui2s2hxCMvPmvZfJHJiwkhhnnj4jGUG3CmuHTWkgrS26oITaognw7smNo_cY6Av348QincfRR7MjE6z7peDZyR-Bbs-CxWw26SU4vGaLPkCwINYkNjb2Cj0ig7Msh4fl8mUEBzFYyqgiwYuBu624vro5yye2wS6HRa8IDXmPNcFOISSBSF0J6gDzHSqNK6VV2IPyUFsru2xeA1lWnrTPvUxL2tOI19jwH3fRNWHuN1BZBwjOar1CsekpjTxZvnMuQawQ0dEZX-uu5TprwV-EIwMX9dmUNeQaVDJN3iGHSxbW1FDJeDpKqjDqULLSHBMUY7BveyMYDy7SlMWtTN_WDyrjFaYeTVkaog9rT3eSKvl56qZbWFOpmh2_AZ_6-UQ?type=png)](https://mermaid.live/edit#pako:eNqFUk1LAzEQ_StDru2CiKccCqWKehBF60X2Mk1mt6HZpOajUkr_u8nuui2s2hxCMvPmvZfJHJiwkhhnnj4jGUG3CmuHTWkgrS26oITaognw7smNo_cY6Av348QincfRR7MjE6z7peDZyR-Bbs-CxWw26SU4vGaLPkCwINYkNjb2Cj0ig7Msh4fl8mUEBzFYyqgiwYuBu624vro5yye2wS6HRa8IDXmPNcFOISSBSF0J6gDzHSqNK6VV2IPyUFsru2xeA1lWnrTPvUxL2tOI19jwH3fRNWHuN1BZBwjOar1CsekpjTxZvnMuQawQ0dEZX-uu5TprwV-EIwMX9dmUNeQaVDJN3iGHSxbW1FDJeDpKqjDqULLSHBMUY7BveyMYDy7SlMWtTN_WDyrjFaYeTVkaog9rT3eSKvl56qZbWFOpmh2_AZ_6-UQ)

As you can see, each microservice is responsible for a specific task, and if en error occurs, it will send an event to the a queue for his previous microservice. For example, if the inventory service fails to update the quantity of a product, it will send an event to the cart service to cancel the cart edition.
