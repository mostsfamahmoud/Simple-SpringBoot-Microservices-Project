# Microservices Docker Stack

This repository uses:
- One `Dockerfile` per service (`api-gateway`, `product-service`, `inventory-service`, `order-service`)
- One root `docker-compose.yml` to run the full stack with infra

## Start The Stack

```bash
docker compose up --build
```

## Stop The Stack

```bash
docker compose down
```

To remove persisted volumes too:

```bash
docker compose down -v
```

## Local Endpoints

- API Gateway: `http://localhost:9000`
- Product Service: `http://localhost:8081`
- Inventory Service: `http://localhost:8084`
- Order Service: `http://localhost:8085`
- Keycloak: `http://localhost:8181`
- MySQL: `localhost:3306`
- MongoDB: `localhost:27017`

## Default Credentials

- Keycloak admin: `admin` / `admin`
- MySQL root password: `mysql` (or `MYSQL_ROOT_PASSWORD` if provided)
- Service DB users are initialized by `infra/mysql/init.sql`:
  - `inventory_user` / `inventory_pass`
  - `order_user` / `order_pass`
- MongoDB root: `root` / `password`

## Quick Verification

After startup:
- Gateway docs UI: `http://localhost:9000/swagger-ui.html`
- Product docs via gateway: `http://localhost:9000/aggregate/product-service/v3/api-docs`
- Inventory docs via gateway: `http://localhost:9000/aggregate/inventory-service/v3/api-docs`
- Order docs via gateway: `http://localhost:9000/aggregate/order-service/v3/api-docs`
