# Payment System — Understanding Spring by Removing It

This project is an experimental version of a Spring Boot payment system where Spring features are progressively removed and replaced with explicit Java code.

The goal is **not to rebuild Spring** or create a production-ready framework.

The goal is to understand what Spring is actually doing behind the scenes by removing one mechanism at a time and replacing its responsibilities with explicit Java code.

> **What happens if I remove this Spring feature? What was Spring doing for me, and what code is required to replace it?**

Each change is intentionally incremental so that the effect of each Spring mechanism can be observed and understood.

---

## 🎯 Purpose

The project started as a conventional Spring Boot application using mechanisms such as:

* `@Service`
* `@Component`
* `@Autowired`
* `@Value`
* Dependency Injection
* `ApplicationContext`
* `RestTemplate`
* `JdbcTemplate`

Instead of treating these mechanisms as abstractions that simply "make development easier", this project investigates what responsibilities they actually provide.

The application is progressively converted from:

```text
Spring-managed application
        │
        ├── Dependency Injection
        ├── RestTemplate
        ├── JdbcTemplate
        ├── Configuration
        └── Spring MVC / Security
```

towards explicit Java implementations:

```text
Explicit Java composition
        │
        ├── Manual dependency wiring
        ├── Apache HttpClient
        ├── JDBC
        ├── Explicit configuration
        └── Remaining Spring infrastructure
```

Spring is removed **one mechanism at a time**, rather than all at once.

---

## 🌿 Branch Strategy

The project is split into two main branches:

### `main`

Contains the original Spring-based version of the payment system.

This branch serves as the **baseline implementation** against which the Spring-removal experiments can be compared.

### `develop-no-spring`

Contains the ongoing experimental work where Spring mechanisms are progressively removed and replaced with explicit Java implementations.

Each Spring-removal experiment is performed on this branch while `main` remains unchanged.

Current progress on `develop-no-spring`:

```text
Dependency Injection
        ↓
     ✅ Removed

RestTemplate
        ↓
     ✅ Removed

JdbcTemplate
        ↓
     🔄 Next

@Value / Configuration
        ↓
     ⏳ Planned

Spring MVC
        ↓
     ⏳ Planned

Spring Security
        ↓
     ⏳ Planned
```
---

## 🔬 Current Status

The project has currently completed two major experiments:

### ✅ 1. Dependency Injection → Manual Composition

Spring's dependency injection has been removed from the application's service/integration/persistence object graph.

Objects are now explicitly constructed and wired through custom containers.

For example:

```java
return new PaymentService(
        stripe,
        paymentsDBAccess,
        fraudService
);
```

The dependency graph is therefore visible directly in the code.

The current composition is approximately:

```text
ApplicationContainer
        │
        ├── StripeSystemContainer
        │       └── StripeIntegration
        │
        ├── FraudSystemContainer
        │       └── FraudService
        │
        └── PaymentSystemContainer
                └── PaymentService
```

The custom containers are **not intended to replace Spring**.

They exist only to make object creation, ownership and dependencies explicit for the experiment.

---

### ✅ 2. `RestTemplate` → Apache HttpClient 5

All uses of Spring's `RestTemplate` have been removed from the application.

HTTP communication is now performed explicitly using Apache HttpClient 5.

For example, Stripe requests are now constructed manually:

```java
HttpPost httpPost = new HttpPost(stripeInitUrl);

httpPost.setHeader(
        "Authorization",
        "Bearer " + stripeSecretKey
);

httpPost.setHeader(
        "Content-Type",
        ContentType.APPLICATION_FORM_URLENCODED.getMimeType()
);

httpPost.setEntity(
        new UrlEncodedFormEntity(params)
);
```

JSON-based APIs use `StringEntity` together with Jackson when appropriate.

This experiment demonstrates what `RestTemplate` was previously handling for the application, including:

* HTTP request creation
* HTTP headers
* request bodies
* serialization/deserialization
* execution of HTTP requests
* response handling

The project currently uses **Apache HttpClient 5** rather than Java's `HttpClient`, intentionally keeping the HTTP implementation compatible with concepts applicable to older Java versions as well.

---

## 🔄 Remaining Spring Usage

Spring has **not** been completely removed.

The project is intentionally being modified incrementally.

Spring is still responsible for infrastructure that has not yet been investigated, including areas such as:

* Spring MVC
* Spring Security
* application startup
* configuration/property resolution
* `JdbcTemplate`

Some Spring annotations and infrastructure therefore remain in the project.

This is intentional.

---

## 🗺️ Planned Experiments

The experiments are performed incrementally rather than following a strict requirement to remove every Spring feature.

### 1. Dependency Injection → Manual Composition

* Remove Spring-managed application dependencies.
* Create objects explicitly.
* Make the dependency graph visible.

**Status: ✅ Complete**

---

### 2. `RestTemplate` → Apache HttpClient 5

* Remove Spring's HTTP abstraction.
* Construct HTTP requests explicitly.
* Handle request bodies and responses manually.

**Status: ✅ Complete**

---

### 3. `JdbcTemplate` → JDBC

Next, the persistence layer will be investigated.

The goal is to understand what `JdbcTemplate` provides on top of JDBC and replace it with explicit JDBC code where appropriate.

The experiment will involve concepts such as:

* `Connection`
* `PreparedStatement`
* `ResultSet`
* parameter binding
* result mapping
* exception handling
* resource management

The existing database behaviour should remain unchanged while the abstraction is removed.

**Status: 🔄 Next**

---

### 4. `@Value` → Explicit Configuration

The next configuration experiment will investigate how Spring resolves properties and injects them into objects.

The goal is to replace:

```java
@Value("${stripe.secret.key}")
private String stripeSecretKey;
```

with explicit configuration loading and constructor-based passing of configuration values.

**Status: ⏳ Planned**

---

### 5. Spring MVC

After the lower-level mechanisms have been investigated, the project can examine what Spring MVC provides around HTTP endpoints.

Possible areas of investigation include:

* controller registration
* request mapping
* request deserialization
* response serialization
* exception handling
* HTTP status handling

**Status: ⏳ Planned**

---

### 6. Spring Security

The security layer can then be investigated separately to understand what Spring Security provides around:

* authentication
* authorization
* filters
* security context
* request processing

**Status: ⏳ Planned**

---

### 7. Spring Application Context / Bootstrap

Finally, the remaining Spring application lifecycle and infrastructure can be examined.

The goal is not necessarily to remove Spring completely.

Instead, the project should reach a point where it is clear:

> **Which parts of the application actually need Spring, and what responsibilities does Spring provide for each remaining part?**

**Status: ⏳ Planned**

---

## 🧪 Testing

Tests are updated together with each experiment.

The project currently demonstrates several testing approaches:

* JUnit 5 unit tests
* Mockito-based tests
* Spring MVC tests
* integration tests
* manually constructed objects
* explicit dependency composition

When a Spring abstraction is removed, the corresponding tests are also adapted to test the new implementation rather than preserving the old abstraction artificially.

For example, after replacing `RestTemplate`, HTTP integration tests use the Apache HttpClient abstraction instead.

The intention is to keep the behaviour of the application unchanged while changing the mechanism underneath it.

---

## 🗄️ Database

The application uses:

* MySQL
* Flyway
* JDBC infrastructure
* Spring `JdbcTemplate` — currently, and scheduled for removal

Database migrations are managed through Flyway.

The database is also used in the Docker-based development and CI environments.

---

## 🐳 Docker & CI

The project includes Docker-based infrastructure for:

* MySQL
* Flyway migrations
* the Python fraud-detection service

GitHub Actions is used for CI.

The CI pipeline currently performs:

```text
Checkout
   ↓
Fraud detection image
   ↓
Fraud detection container
   ↓
MySQL service
   ↓
Flyway migrations
   ↓
Database schema validation
   ↓
Maven build
   ↓
Tests
```

The goal is to keep the CI pipeline working throughout each Spring-removal experiment.

---

## 🛠️ Tech Stack

* Java 17
* Spring Boot 3.4.2
* Maven
* JUnit 5
* Mockito
* Apache HttpClient 5
* Jackson
* MySQL
* Flyway
* Docker
* GitHub Actions
* Stripe API
* Python-based fraud detection service

---

## 📌 Status

**Work in progress.**

Current progress:

```text
Dependency Injection
        ↓
     ✅ Removed

RestTemplate
        ↓
     ✅ Removed

JdbcTemplate
        ↓
     🔄 Next

@Value / Configuration
        ↓
     ⏳ Planned

Spring MVC
        ↓
     ⏳ Planned

Spring Security
        ↓
     ⏳ Planned

Application Context / Bootstrap
        ↓
     ⏳ Planned
```

The final result is less important than the process.

The purpose of the project is to understand **why the application works with Spring in the first place, what Spring provides at each layer, and what changes when those abstractions are removed.**