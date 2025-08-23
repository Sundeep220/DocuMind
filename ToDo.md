Great question — “Kafka-only” workers still need real security. Here’s a pragmatic, production-grade checklist tailored to your service (Kafka consumer + vectorizer + tiny FastAPI):

# 1) Lock down Kafka itself (authn, authz, transport)

* **TLS everywhere**: enable SSL/TLS on brokers; use `security_protocol="SASL_SSL"` or `"SSL"` so creds never go in cleartext.
* **Client auth (service accounts)**:

  * **SASL/SCRAM** (simple & common) → app uses a Kafka username/password.
  * or **mTLS** (client certs) → strongest machine-to-machine trust without per-message tokens.
  * or **SASL/OAUTHBEARER** (JWT issued by your IdP) if your platform already does OAuth.
* **Least-privilege ACLs**: give this worker only:

  * `READ` on the documents topic.
  * `WRITE` on the status topic.
  * `READ` on its **own** consumer group (if your Kafka supports group ACLs).
* **Topic hygiene**: use separate **DLQ** (dead-letter) and **retry** topics and grant only what’s needed.

### Python config example (SASL/SCRAM)

```python
common = dict(
    bootstrap_servers=os.getenv("KAFKA_BOOTSTRAP_SERVERS"),
    security_protocol="SASL_SSL",
    sasl_mechanism="SCRAM-SHA-512",
    sasl_plain_username=os.getenv("KAFKA_USERNAME"),
    sasl_plain_password=os.getenv("KAFKA_PASSWORD"),
    ssl_cafile=os.getenv("KAFKA_CA_CERT"),  # path to CA bundle
)
KafkaConsumer(KAFKA_TOPIC, group_id=GROUP_ID, **common, ...)
KafkaProducer(**common, ...)
```

# 2) Decide: per-message auth vs transport-level auth

Since you **don’t expose public APIs** and only Spring services publish to Kafka, you have two good options:

* **Recommended (simpler):** rely on **broker-level auth** (mTLS or SASL) + **ACLs** for trust. No JWT in messages. This is enough for most internal buses.
* **If you need app-level authorization:** have producers attach a **JWT in Kafka headers**. Consumer verifies it before processing.

### Verifying a JWT from Kafka headers (optional)

```python
import jwt, time, requests

def verify_jwt(token: str, jwks, issuer, audience):
    header = jwt.get_unverified_header(token)
    key = next(k for k in jwks["keys"] if k["kid"] == header["kid"])
    return jwt.decode(
        token,
        jwt.algorithms.RSAAlgorithm.from_jwk(key),
        algorithms=["RS256"],
        issuer=issuer,
        audience=audience,
    )

for message in consumer:
    jwt_bytes = message.headers and dict(message.headers).get(b"auth")
    if not jwt_bytes:
        # drop or send to DLQ if you require JWT
        continue
    token = jwt_bytes.decode()
    claims = verify_jwt(token, jwks_cache(), ISSUER, AUDIENCE)
    # proceed only if claims (like roles/service name) are allowed
```

> Tip: cache JWKS for \~15m, enforce **short token TTL** (≤5–15 min), and validate critical claims.

# 3) Secure your tiny FastAPI surface

Even if it’s just `/health` & `/status`, protect them in non-dev:

* **Network fence**: expose only inside cluster (no public LB); or restrict by IP allowlist / service mesh policy.
* **JWT on HTTP** (if exposed): add a dependency that checks a service-to-service token or mTLS at the gateway.

```python
from fastapi import Depends, HTTPException, Request

def require_internal(request: Request):
    # Option A: rely on mTLS at ingress (x-forwarded-client-cert), or
    # Option B: check Authorization: Bearer <token>
    auth = request.headers.get("authorization", "")
    if not auth.startswith("Bearer "):
        raise HTTPException(401, "Unauthorized")
    # verify token here...
    return True

@app.get("/status", dependencies=[Depends(require_internal)])
def status(): ...
```

# 4) Secrets management

* **Never** hardcode secrets. Read from **env vars** or a secret manager (K8s Secrets, AWS Secrets Manager, Azure Key Vault, HashiCorp Vault).
* Rotate regularly. Keep **two** sets during rotation to avoid downtime.

# 5) Input hardening & safety (very relevant for your vector indexing)

* **Validate schema** of incoming events (use Pydantic) to avoid malformed payloads and path tricks.
* **Path safety**: resolve under a fixed base and forbid traversal.

```python
BASE = Path("/data/storage").resolve()

def safe_path(rel_path: str) -> Path:
    p = (BASE / rel_path.lstrip("/")).resolve()
    if not str(p).startswith(str(BASE)):
        raise ValueError("Path traversal detected")
    return p
```

* **Allowed extensions only** (`.pdf`, `.docx`, `.txt`). Reject others.
* **Resource limits**: enforce max file size, max pages, and chunk count to prevent memory abuse.
* **Idempotency**: if the same `doc_id` arrives again, detect and short-circuit (or reindex safely).

# 6) Data at rest & filesystem

* Store FAISS indexes under a dedicated dir with restricted permissions (e.g., `0700`).
* If using ephemeral nodes/containers, consider **encrypting the volume** or switch to a managed vector store that supports auth & encryption.

# 7) Observability with security in mind

* **Structured logs** (no secrets, no PII); redact paths/user IDs if sensitive.
* Emit **audit logs**: who produced (principal), what doc\_id, action, result (INDEXED/FAILED), reason.
* Metrics: successes, failures, retries, DLQ count, processing latency.

# 8) Reliability patterns that also reduce risk

* **Retries + backoff** for transient failures; after N attempts → **DLQ** with error details.
* **Poison-pill protection**: cap processing time per message; use `max_poll_interval_ms` and `max_poll_records` sanely.
* **Schema registry** (if using Avro/Protobuf/JSON-Schema) to enforce contracts between producers/consumers.

# 9) Container & runtime hardening

* Run as **non-root**, read-only root filesystem, drop Linux capabilities.
* Pin dependency versions; enable vulnerability scanning.
* Set CPU/memory limits; liveness & readiness probes.
* In Kubernetes: use **NetworkPolicies** to allow only Kafka + necessary storage, and **PodSecurity** standards.

# 10) Sample: Pydantic validation + safe join + allowed types

```python
from pydantic import BaseModel, Field
from typing import Literal

class DocEvent(BaseModel):
    id: int
    storagePath: str = Field(min_length=1)
    userId: int
    fileName: str
    # optional: checksum, contentType

ALLOWED = (".pdf", ".docx", ".txt")

def handle_event(event: dict):
    e = DocEvent.model_validate(event)
    ext = Path(e.fileName).suffix.lower()
    if ext not in ALLOWED:
        raise ValueError(f"Unsupported type: {ext}")
    full_path = safe_path(e.storagePath)
    process_and_embed_doc(e.id, str(full_path), e.fileName, e.userId)
```

# 11) (Nice to have) Message-level integrity

If you don’t go with JWT in headers, you can still protect integrity by adding a **detached HMAC signature** over the payload in headers (`X-Sig`) and verifying it with a shared secret from your secret manager.

---

## Quick “minimum secure” setup you can implement now

1. **Enable SASL\_SSL + SCRAM** for Kafka; create service account with read/write ACLs.
2. **Private HTTP**: keep your FastAPI service internal-only (cluster-local) and/or require a bearer token on `/status`.
3. **Validate events** with Pydantic; **safe path resolution**; limit file types & sizes.
4. **Secrets via env/secret manager**; no secrets in code.
5. **DLQ + retries**; redact logs; basic metrics.

If you want, I can:

* Patch your `consumer.py` to enforce Pydantic validation, safe paths, retries, and optional JWT-from-Kafka-headers.
* Add SASL\_SSL config toggled by env vars.
* Add a `/metrics` endpoint (Prometheus) gated behind internal network.
