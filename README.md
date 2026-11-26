# Hackathon Demo App

Small Spring Boot application that the CI/CD Reliability AI Agent's Tekton pipeline builds, tests and deploys
(Clone → Build → Unit Test → Build and Push → Deploy → Rollout Status). It has no database: the product catalogue is
held in memory.

Spring Boot 4.1.2 · Java 21 · Gradle · Spring Web · Bean Validation · Actuator

## Endpoints

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/hello` | Application name, message and version |
| `GET` | `/api/products` | All products |
| `GET` | `/api/products/{id}` | One product; 404 if it does not exist |
| `POST` | `/api/orders/quote` | Total of an order (`BigDecimal`, rounded to 2 decimals, INR); 400 for an empty order, a quantity below 1 or above 1000, or an unknown product |
| `GET` | `/actuator/health`, `/actuator/health/liveness`, `/actuator/health/readiness`, `/actuator/info` | Health and probes for Kubernetes |

Products: 1 Wireless Mouse (399.99), 2 Mechanical Keyboard (699.99), 3 USB-C Hub (1299.00), 4 27-inch Monitor (15999.00).

Every error has the same body:

```json
{
  "timestamp": "2026-11-26T20:56:43.746Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Request validation failed",
  "path": "/api/orders/quote",
  "fieldErrors": [{ "field": "items[0].quantity", "message": "must be greater than 0" }]
}
```

## Try it

```bash
curl http://localhost:8080/api/hello
curl http://localhost:8080/api/products
curl http://localhost:8080/api/products/1
curl -i http://localhost:8080/api/products/99          # 404

# 2 x 399.99 + 1 x 699.99 = {"itemCount":3,"totalAmount":1499.97,"currency":"INR"}
curl -X POST http://localhost:8080/api/orders/quote -H 'Content-Type: application/json' \
  -d '{"items":[{"productId":1,"quantity":2},{"productId":2,"quantity":1}]}'

curl -i -X POST http://localhost:8080/api/orders/quote -H 'Content-Type: application/json' \
  -d '{"items":[{"productId":1,"quantity":0}]}'           # 400: quantity
curl -i -X POST http://localhost:8080/api/orders/quote -H 'Content-Type: application/json' \
  -d '{"items":[{"productId":99,"quantity":1}]}'          # 400: unknown product
curl -i -X POST http://localhost:8080/api/orders/quote -H 'Content-Type: application/json' \
  -d '{"items":[]}'                                        # 400: empty order
```

## Build, test and run

```bash
./gradlew clean assemble   # compile and package
./gradlew test             # unit and controller tests, no network or database needed
./gradlew bootRun          # http://localhost:8080
docker build -t hackathon-ai-demo-app .
```

Tests: service unit tests (`ProductServiceTest`, `OrderQuoteServiceTest`) and controller tests through the complete
application context with MockMvc (`ApiControllerTest`).
