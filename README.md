# checkout-service

Handles order processing, cart management, and payment gateway integration for the Sectra platform.

## Configuration

See `src/main/resources/application.yml` for environment-specific config.

Required env vars:
- `PAYMENT_GATEWAY_API_KEY` — API key for payment gateway

## Running locally

```bash
./mvnw spring-boot:run
```

## Deployment

Managed via Kubernetes. See `k8s/` for manifests.

## Version history

- v2.4.1 — Migrate payment gateway to SDK v3 (⚠️ caused P1 incident 2026-02-20)
- v2.4.0 — Stable baseline
- v2.3.x — Legacy
