#!/usr/bin/env bash
set -euo pipefail

REGION="${1:?region required}"
ACCOUNT="${2:?account required}"
IMAGE_NAME="${3:?image name required}"
IMAGE_TAG="${4:?image tag required}"

ECR_REGISTRY="${ACCOUNT}.dkr.ecr.${REGION}.amazonaws.com"
IMAGE_FULL_SHA="${ECR_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}"
IMAGE_FULL_LATEST="${ECR_REGISTRY}/${IMAGE_NAME}:latest"

# ensure repo exists
if ! aws ecr describe-repositories --repository-names "${IMAGE_NAME}" --region "${REGION}" >/dev/null 2>&1; then
  aws ecr create-repository \
    --repository-name "${IMAGE_NAME}" \
    --image-scanning-configuration scanOnPush=true \
    --region "${REGION}" >/dev/null
fi

# login
aws ecr get-login-password --region "${REGION}" \
  | docker login --username AWS --password-stdin "${ECR_REGISTRY}"

# build
docker build --pull --progress=plain \
  -t "${IMAGE_FULL_SHA}" \
  -t "${IMAGE_FULL_LATEST}" \
  .

# push
docker push "${IMAGE_FULL_SHA}"
docker push "${IMAGE_FULL_LATEST}"

echo "[push-image] Done"
