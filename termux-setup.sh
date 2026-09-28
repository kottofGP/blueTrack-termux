#!/data/data/com.termux/files/usr/bin/bash

set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TERMUX_PREFIX="${PREFIX:-/data/data/com.termux/files/usr}"
TERMUX_HOME="${HOME:-/data/data/com.termux/files/home}"
SDK_DIR="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-${TERMUX_HOME}/android-sdk}}"
AAPT2_BIN="${AAPT2_PATH:-${TERMUX_PREFIX}/bin/aapt2}"

if [[ ! -x "${AAPT2_BIN}" ]]; then
  echo "No se encontró aapt2 ejecutable en: ${AAPT2_BIN}" >&2
  echo "Instala/configura aapt2 en Termux o define AAPT2_PATH." >&2
  exit 1
fi

if [[ ! -d "${SDK_DIR}" ]]; then
  echo "No se encontró el Android SDK en: ${SDK_DIR}" >&2
  echo "Define ANDROID_HOME o instala el SDK en ~/android-sdk." >&2
  exit 1
fi

JAVA_BIN="$(command -v java || true)"
if [[ -z "${JAVA_BIN}" ]]; then
  echo "No se encontró Java. Este proyecto usa JDK 21." >&2
  exit 1
fi

JAVA_HOME_DETECTED="$(dirname "$(dirname "$(readlink -f "${JAVA_BIN}")")")"
JAVA_VERSION="$("${JAVA_BIN}" -version 2>&1 | head -n 1)"
if ! "${JAVA_BIN}" -version 2>&1 | grep -qE '"2[1-9](\.|")|"(2[1-9])\.'; then
  echo "Se detectó ${JAVA_VERSION}; se requiere JDK 21 o superior." >&2
  exit 1
fi

printf 'sdk.dir=%s\n' "${SDK_DIR}" > "${PROJECT_DIR}/local.properties"

GRADLE_PROPERTIES="${PROJECT_DIR}/gradle.properties"
if grep -q '^android.aapt2FromMavenOverride=' "${GRADLE_PROPERTIES}"; then
  sed -i "s|^android.aapt2FromMavenOverride=.*|android.aapt2FromMavenOverride=${AAPT2_BIN}|" "${GRADLE_PROPERTIES}"
else
  printf 'android.aapt2FromMavenOverride=%s\n' "${AAPT2_BIN}" >> "${GRADLE_PROPERTIES}"
fi

export JAVA_HOME="${JAVA_HOME:-${JAVA_HOME_DETECTED}}"
export ANDROID_HOME="${SDK_DIR}"
export ANDROID_SDK_ROOT="${SDK_DIR}"

echo "Termux configurado:"
echo "  JAVA_HOME=${JAVA_HOME}"
echo "  ANDROID_HOME=${ANDROID_HOME}"
echo "  AAPT2=${AAPT2_BIN}"