#!/usr/bin/env bash
set -euo pipefail

REGION="ap-south-1"
WAIT="false"

while [[ $# -gt 0 ]]; do
  case "$1" in
    --cluster) CLUSTER="$2"; shift 2;;
    --service) SERVICE="$2"; shift 2;;
    --region)  REGION="$2"; shift 2;;
    --wait)    WAIT="true"; shift;;
    *) echo "Unknown arg: $1"; exit 1;;
  esac
done

: "${CLUSTER:?cluster required}"
: "${SERVICE:?service required}"

aws ecs describe-services --cluster "${CLUSTER}" --services "${SERVICE}" --region "${REGION}" >/dev/null

aws ecs update-service \
  --cluster "${CLUSTER}" \
  --service "${SERVICE}" \
  --force-new-deployment \
  --region "${REGION}"

if [ "$WAIT" = "true" ]; then
  aws ecs wait services-stable \
    --cluster "${CLUSTER}" \
    --services "${SERVICE}" \
    --region "${REGION}"
fi

echo "[ecs-deploy] Deployment triggered"
