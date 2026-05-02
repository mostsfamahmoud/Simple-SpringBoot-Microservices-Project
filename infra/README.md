# Shared Local Infrastructure

This directory owns shared containers used by local services.

## Start MySQL

1. Copy `.env.example` to `.env` and adjust values if needed.
2. Run:

```bash
docker compose up -d
```

## Service credentials

- `order-service` uses `ORDER_DB_USER` / `ORDER_DB_PASSWORD` against `order_service`.
- `inventory-service` uses `INVENTORY_DB_USER` / `INVENTORY_DB_PASSWORD` against `inventory_service`.

The bootstrap script in `mysql/init.sql` creates both databases and users on first initialization.
