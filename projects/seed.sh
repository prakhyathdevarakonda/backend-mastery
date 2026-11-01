#!/usr/bin/env bash
# Loads sample data through the real APIs, so every call also exercises DB writes and Kafka events.
# Needs: wallet-service on :8080 (screening-workflow-service on :8082 is optional) and jq.
# Usage: ./seed.sh
set -uo pipefail

WALLET=${WALLET_URL:-http://localhost:8080/api/wallets}
SCREENING=${SCREENING_URL:-http://localhost:8082/api/screenings}

post() { curl -s -X POST "$1" -H 'Content-Type: application/json' -d "$2"; }

echo "== Wallets (Postgres + Kafka topic wallet.transactions)"
asha=$(post "$WALLET" '{"ownerName":"Asha"}' | jq -r .id)
ravi=$(post "$WALLET" '{"ownerName":"Ravi"}' | jq -r .id)
echo "created wallets: Asha=$asha Ravi=$ravi"

post "$WALLET/$asha/deposits"            '{"amount":500.00}' | jq -c .
post "$WALLET/$asha/withdrawals"         '{"amount":120.50}' | jq -c .
post "$WALLET/$ravi/deposits"            '{"amount":1000.00}' | jq -c .
post "$WALLET/$ravi/withdrawals/locked"  '{"amount":250.00}' | jq -c .   # pessimistic (FOR UPDATE) path

echo "-- expected failures (read the ProblemDetail responses):"
post "$WALLET/$asha/withdrawals" '{"amount":99999}' | jq -c '{status, detail}'   # 409 insufficient balance
post "$WALLET/$asha/deposits"    '{"amount":0}'     | jq -c '{status, detail}'   # 400 validation

echo "-- Asha's transaction history:"
curl -s "$WALLET/$asha/transactions" | jq -c '.[]'

if ! curl -sf "${SCREENING%/api/screenings}/actuator/health" >/dev/null; then
  echo "== screening-workflow-service not running on :8082, skipping screenings"; exit 0
fi

echo "== Screenings (async: API -> Kafka -> AI worker -> Kafka -> status)"
create() { post "$SCREENING" "$1" | jq -r .screeningId; }
high=$(create '{"candidateName":"Meera","email":"meera@example.com","resumeText":"5 years building Spring Boot microservices with Kafka, JUnit testing, and distributed tracing in production."}')
mid=$(create  '{"candidateName":"Arjun","email":"arjun@example.com","resumeText":"Backend developer with Spring Boot REST APIs and PostgreSQL."}')
low=$(create  '{"candidateName":"Kiran","email":"kiran@example.com","resumeText":"Frontend developer focused on CSS animations and design systems."}')

sleep 3   # give the Kafka round trip a moment
for id in "$high" "$mid" "$low"; do curl -s "$SCREENING/$id" | jq -c '{screeningId, status, score}'; done

echo "-- recruiter approves the borderline one (HUMAN_REVIEW_REQUIRED):"
post "$SCREENING/$mid/decision" '{"decision":"APPROVE","reason":"Good API experience"}' | jq -c .
sleep 2
curl -s "$SCREENING/$mid" | jq -c '{screeningId, status, reason}'
