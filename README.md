# CorpReport Server

> The CorpReport Server provides the backend network functionality for the CorpReport Android and iOS mobile applications.
> The server manages the ingestion of company search data from the NASDAQ and NYSE stock exchanges, the fetching of company financial data from the XBRL US API, and user authentication/management.

## Getting Started

Make sure you have the latest Java compiler/IDE of your choice installed. 

### Requirements

- Java Compiler (We used [Intellij IDEA](https://www.jetbrains.com/idea/))
- [Gradle](https://gradle.org/)
- [PostgreSQL](https://www.postgresql.org/)

### Installing

- Install PostgreSQL
- Start the PostgreSQL database server
- Run __schema.sql__ to set up the database tables
- Configure the database details in __application.properties__
- Configure the SSL Certificate in __application.properties__ (Certificate must be in the __pkcs12__ format)
- Configure an email address in __application.properties__ (Used for the password-reset system)
- Clone the repository to your local machine.
- Import the project as a Gradle project (If you're using an IDE)

### Building

To build the server:

```java
./gradlew build
```

## Testing

This project utilises a combination of unit tests, integration tests, and code analysis tools.

To run all tests on your machine:

```java
./gradlew test
```

## Environment

The basic requirements for the server environment are:

- [Java Runtime](https://www.oracle.com/technetwork/java/javase/downloads/index.html) 
- A database (The current setup is for PostgreSQL, but the server is database-agnostic. )
- An SSL Certificate
- An __application.properties__ file to configure the server

### Important Note
A recent problem has been that the CorpReport server cannot be run on an AWS-based server.
It was determined that the NASDAQ website which we ingest search data from is blacklisting those server's IP addresses.