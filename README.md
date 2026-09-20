# Payment System — Understanding Spring by Removing It

This project is an experimental version of a Spring Boot payment system where Spring features are progressively removed and replaced with explicit Java code.

The goal is **not to rebuild Spring** or create a production-ready framework. The goal is to understand what Spring is actually doing behind the scenes by removing one mechanism at a time and manually reproducing its responsibilities.

## 🎯 Purpose

Instead of only using Spring features such as:

* `@Service`
* `@Component`
* `@Autowired`
* `@Value`
* Dependency Injection
* Application Context

this project asks:

> **What happens if I remove this feature? What was Spring doing for me, and what code is required to replace it?**

Each change is intentionally incremental so that the effect of each Spring mechanism can be observed and understood.

---

## 🧪 Current Experiment

The project is currently focused on **Dependency Injection and manual object composition**.

Originally, dependencies were managed by Spring:

```text
Spring ApplicationContext
        │
        ├── PaymentService
        ├── FraudService
        ├── StripeIntegration
        └── ...
```

The experiment progressively replaces this with explicit Java composition:

```text
ApplicationContainer
        │
        ├── StripeSystemContainer
        │
        ├── FraudSystemContainer
        │
        └── PaymentSystemContainer
```

For example, instead of Spring discovering and creating `PaymentService`, the application explicitly creates it:

```java
return new PaymentService(
        stripe,
        paymentsDBAccess,
        fraudService
);
```

This makes the dependency graph visible in the code.

---

## 🔬 What Has Been Removed

### `@Service`

Spring-managed services such as:

```java
@Service
public class PaymentService {
    ...
}
```

are being converted into regular Java classes.

Their dependencies are now created and wired explicitly by custom containers.

### Dependency Injection

Instead of relying entirely on Spring to resolve dependencies, the project uses explicit composition:

```text
PaymentSystemContainer
        │
        ├── StripeSystemContainer
        ├── PaymentsDBAccess
        └── FraudSystemContainer
```

Each container is responsible for constructing the objects belonging to its part of the system.

### Controller → Service Dependency

The controller no longer directly knows about all the lower-level dependencies.

Instead:

```text
PaymentController
        │
        ▼
ApplicationContainer
        │
        ▼
PaymentService
        │
        ├── StripeIntegration
        ├── PaymentsDBAccess
        └── FraudService
```

This is intentionally explicit so the dependency graph can be studied.

---

## 🔄 Current Spring Usage

Spring has **not** been completely removed yet.

Some Spring functionality is intentionally kept because the project is being modified incrementally.

For example, `@Value` is currently still used for configuration:

```java
@Value("${stripe.secret.key}")
private String stripeSecretKey;
```

This is intentional.

The next stages will investigate how Spring resolves configuration properties and injects them into objects before removing this mechanism as well.

---

## 🧱 Custom Containers

The project currently uses a small hierarchy of manual containers:

```text
ApplicationContainer
       │
       ├── StripeSystemContainer
       │
       ├── FraudSystemContainer
       │
       └── PaymentSystemContainer
```

These classes are not intended to replace Spring as a framework.

They exist to make dependency creation and ownership explicit during the experiment.

---

## 🧪 Testing

The project also updates its tests as the dependency graph changes.

For example, after removing the controller's direct knowledge of Stripe and Fraud dependencies, the controller test no longer mocks those internal dependencies.

Instead, it tests the controller boundary:

```text
PaymentController
        │
        ▼
ApplicationContainer
        │
        ▼
PaymentService
```

Integration tests continue to use Spring where the experiment has not removed it yet.

This allows the project to demonstrate the difference between:

* Spring-managed integration tests
* manually constructed objects
* Mockito-based unit tests
* explicit dependency composition

---

## 🗺️ Planned Experiments

The project will progressively remove additional Spring mechanisms.

### 1. `@Service` → Manual Composition

* Remove service discovery.
* Create services explicitly.
* Understand what the Spring container normally does.

### 2. `@Component` → Manual Application Bootstrap

* Remove the need for `ApplicationContainer` to be a Spring bean.
* Move object composition outside the Spring ApplicationContext.

### 3. `@Value` → Explicit Configuration

* Remove property injection.
* Load configuration explicitly.
* Pass configuration values through constructors.

### 4. Further Dependency Injection

* Identify remaining Spring-managed dependencies.
* Replace implicit dependency resolution with explicit composition where appropriate.

### 5. Application Context

* Understand what responsibilities remain with Spring.
* Identify which parts of the application actually require the framework.

The project will stop removing Spring features once the experiment has provided enough understanding of the mechanisms being studied.

---

## 🛠️ Tech Stack

* Java 17
* Spring Boot 3.4.2
* Maven
* JUnit 5
* Mockito
* MySQL
* Flyway
* Docker
* GitHub Actions
* Stripe API
* Python-based fraud detection service

---

## 📌 Status

**Work in progress.**

This repository represents an ongoing learning experiment. The code will intentionally change as different Spring mechanisms are removed and replaced with explicit Java implementations.

The final result is less important than understanding **why the application works with Spring in the first place**.
