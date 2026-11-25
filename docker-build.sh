#!/bin/bash
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
GRADLE_CACHE_DIR="${GRADLE_DOCKER_CACHE:-$HOME/.gradle-docker}"
IMAGE="registry.gitlab.com/fdroid/fdroidserver:buildserver-trixie"
GRADLE_TASK="${1:-:app:assembleFossRelease}"

mkdir -p "$GRADLE_CACHE_DIR"

# Safely back up and restore local.properties even if the build fails
HAD_LOCAL_PROPERTIES=0
BACKUP_FILE="$(mktemp)"
if [ -f "$PROJECT_DIR/local.properties" ]; then
  HAD_LOCAL_PROPERTIES=1
  cp "$PROJECT_DIR/local.properties" "$BACKUP_FILE"
fi

cleanup() {
  if [ "$HAD_LOCAL_PROPERTIES" -eq 1 ]; then
    cp "$BACKUP_FILE" "$PROJECT_DIR/local.properties"
  else
    rm -f "$PROJECT_DIR/local.properties"
  fi
  rm -f "$BACKUP_FILE"
}
trap cleanup EXIT

# Point Gradle to the Android SDK inside the F-Droid container
printf 'sdk.dir=/opt/android-sdk\n' > "$PROJECT_DIR/local.properties"

# Handle Apple Silicon (arm64) Macs running linux/amd64 container images
# Using a string instead of an array avoids Bash 3.2 "unbound variable" with set -u
PLATFORM_FLAG=""
if [ "$(uname -s)" = "Darwin" ] && [ "$(uname -m)" = "arm64" ]; then
  PLATFORM_FLAG="--platform linux/amd64"
fi

# Only run chown on Linux hosts (not needed on macOS Docker Desktop)
CHOWN_CMD=""
if [ "$(uname -s)" = "Linux" ]; then
  CHOWN_CMD="chown -R $(id -u):$(id -g) /workspace /root/.gradle"
fi

echo "Building $GRADLE_TASK inside F-Droid container..."
docker run --rm \
  $PLATFORM_FLAG \
  -v "$PROJECT_DIR:/workspace" \
  -v "$GRADLE_CACHE_DIR:/root/.gradle" \
  -w /workspace \
  "$IMAGE" \
  bash -c "
    set -e
    if ! command -v javac >/dev/null 2>&1; then
      apt-get update -qq && apt-get install -y -q openjdk-21-jdk-headless >/dev/null
    fi
    JAVA_PATH=\$(dirname \$(dirname \$(readlink -f \$(command -v javac))))
    export JAVA_HOME=\"\$JAVA_PATH\"
    ./gradlew --no-daemon -Dorg.gradle.java.home=\"\$JAVA_PATH\" \
      --no-build-cache clean $GRADLE_TASK
    status=\$?
    $CHOWN_CMD
    exit \$status
  "

echo "Build complete. Output APKs:"
find "$PROJECT_DIR/app/build/outputs/apk" -name "*.apk" -exec ls -lh {} +
