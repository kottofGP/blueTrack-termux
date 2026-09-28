# BlueTrack Android

Cliente Android de BlueStar Technology para consultar la operación de flotas.

## Estado actual

MVP funcional con:

- Login reactivo con estados de carga y error.
- Dashboard oscuro con la estética BlueTrack.
- Métricas de flota.
- Lista de vehículos con estado, ubicación, velocidad y batería.
- Actualización manual de la flota.
- Cierre de sesión.
- Repositorios mock listos para sustituirse por el API de systemStar.

## Arquitectura

```text
UI → Presentation → Domain → Data
```

`systemStar` debe ser la autoridad central. BlueTrack no implementa protocolos
GPS, TCP ni acceso directo a la base de datos.

## Ejecutar en Android Studio

1. Abre la carpeta en Android Studio.
2. Configura el SDK local; no subas `local.properties`.
3. Ejecuta `./gradlew assembleDebug`.
4. Instala el APK debug en un emulador o dispositivo Android API 26+.

El repositorio usa datos simulados en `MockAuthRepository` y
`MockVehicleRepository`. Para conectar el backend, crea implementaciones reales
de `AuthRepository` y `VehicleRepository` sin cambiar la capa de presentación.

## Ejecutar en Termux

El proyecto conserva la configuración de Termux usada por la versión original:
JDK 21, Android SDK en `~/android-sdk`, `aapt2` de `$PREFIX/bin` y Gradle
Wrapper 9.6.0.

Desde la carpeta raíz:

```bash
bash termux-build.sh
```

El script crea `local.properties` automáticamente y genera el APK en:

```text
app/build/outputs/apk/debug/app-debug.apk
```

También acepta una ubicación diferente del SDK o de `aapt2`:

```bash
ANDROID_HOME=/ruta/al/sdk AAPT2_PATH=/ruta/a/aapt2 bash termux-build.sh
```
