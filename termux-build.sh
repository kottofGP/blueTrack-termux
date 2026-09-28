#!/data/data/com.termux/files/usr/bin/bash

set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${PROJECT_DIR}"

bash "${PROJECT_DIR}/termux-setup.sh"
exec ./gradlew assembleDebug --no-daemon "$@"