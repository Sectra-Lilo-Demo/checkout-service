# Runbook: Checkout Service — Payment Gateway 500s

**Last used:** November 14, 2025 (P1 incident — similar root cause)  
**Owner:** Platform SRE Team  
**Severity:** P1 when error rate >5%, P2 otherwise  

---

## Symptoms

- `checkout-service` returning HTTP 500 on all or most requests
- Errors spike immediately after a deploy
- Logs show `NullPointerException` or `initialization failed` in `PaymentGatewayClient`
- Payment gateway health check endpoint returns 200 (gateway itself is up)

## Quick Diagnosis

```bash
# 1. Check error rate (last 15 min)
kubectl logs -l app=checkout-service -n production --since=15m | grep -E "ERROR|Exception" | tail -50

# 2. Check recent deploys
kubectl rollout history deployment/checkout-service -n production

# 3. Confirm payment gateway is up
curl -s https://api.paygateway.io/health | jq .status

# 4. Check config in running pod
kubectl exec -it deploy/checkout-service -n production -- env | grep PAYMENT
```

## Decision Tree

```
500s after deploy?
  └── YES → Check PaymentGatewayClient logs
        └── NPE / null client?
              ├── YES → Missing config (see below) → Rollback or hotfix
              └── NO  → Check gateway status → If gateway down: wait/alert
```

## Recovery Steps

### Option A: Rollback (fastest — 5 min)

```bash
# Rollback to previous version
kubectl rollout undo deployment/checkout-service -n production

# Monitor recovery
kubectl rollout status deployment/checkout-service -n production
watch -n5 'kubectl logs -l app=checkout-service -n production --since=1m | grep -c ERROR'
```

Expected: error rate drops to 0 within 2-3 minutes of rollout completing.

### Option B: Hotfix Deploy (if rollback not possible)

1. Identify missing config key from logs
2. Add to `application.yml` and create hotfix branch
3. Get expedited review (SRE lead or on-call)
4. Deploy via standard pipeline with fast-track approval

## Common Root Causes

| Symptom | Root Cause | Fix |
|---|---|---|
| NPE in `PaymentGatewayClient.init()` | Missing required config key (e.g., `region`) | Add key to application.yml |
| `Connection refused` to gateway | Network policy change | Restore network policy |
| `401 Unauthorized` from gateway | Rotated API key not updated in secret | Update `PAYMENT_GATEWAY_API_KEY` secret |
| Timeout on every request | Gateway SDK timeout too low | Increase `payment.gateway.timeout-ms` |

## Post-Incident

- [ ] Update this runbook with new failure mode if applicable
- [ ] File Jira ticket for missing test coverage
- [ ] Add config validation to startup (`@PostConstruct` check)
- [ ] Add canary/smoke test to deployment pipeline for payment path

## Prior Incidents

| Date | Duration | Root Cause | Ticket |
|---|---|---|---|
| 2025-11-14 | 18 min | Missing `payment.gateway.timeout-ms` after config refactor | KAN-past-1 |
| 2026-02-20 | Active | Missing `payment.gateway.region` after SDK v3 migration | KAN-1 |

---

*This runbook is version-controlled in the `checkout-service` repo under `docs/runbooks/`.*
