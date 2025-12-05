#!/usr/bin/env bash
set -euo pipefail

# Ensure we are always operating from repo root
GIT_ROOT=$(git rev-parse --show-toplevel)
cd "$GIT_ROOT"
echo "[build] Using repo root: $GIT_ROOT"

# -------------------------
# Build using Gradle
# -------------------------
if [ -f "${GIT_ROOT}/gradlew" ]; then
  echo "[build] Using gradlew"
  chmod +x "${GIT_ROOT}/gradlew"
  "${GIT_ROOT}/gradlew" clean build -x test --no-daemon
else
  echo "[build] gradlew missing; checking system gradle"
  if ! command -v gradle >/dev/null 2>&1; then
    echo "[build] Gradle missing"; exit 1
  fi
  gradle clean build -x test
fi

# -------------------------
# Verify artifact exists
# -------------------------
echo "[build] Checking JAR artifact in build/libs"

JAR_FILE=$(find "${GIT_ROOT}/build/libs" -maxdepth 1 -type f -name "*.jar" | head -1)

if [ -z "$JAR_FILE" ]; then
    echo "[build][error] No JAR produced in build/libs/"
    exit 1
else
    echo "[build] Found artifact: $JAR_FILE"
fi

echo "[build] Build completed"
