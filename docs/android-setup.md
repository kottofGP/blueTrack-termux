# Configuración Android

## Requisitos en Android Studio

- Android Studio Ladybug o posterior.
- Android SDK 34.
- JDK 21 o una versión compatible con la configuración de Gradle.
- Android Emulator API 26 o superior, o un dispositivo físico.

## Configuración local en Android Studio

1. Copia `local.properties.example` como `local.properties`.
2. Cambia `sdk.dir` por la ruta de tu Android SDK.
3. Abre el proyecto en Android Studio.
4. Sincroniza Gradle.
5. Ejecuta la variante `app`.

Desde terminal:

```bash
./gradlew assembleDebug
```

El APK se genera en `app/build/outputs/apk/debug/`.

## Compilar en Termux

La configuración original que compilaba este proyecto usaba:

- SDK: `/data/data/com.termux/files/home/android-sdk`
- `aapt2`: `/data/data/com.termux/files/usr/bin/aapt2`
- Java: JDK 21
- Gradle Wrapper: 9.6.0

Desde la raíz del proyecto ejecuta:

```bash
bash termux-build.sh
```

El script valida `aapt2`, detecta `JAVA_HOME`, genera `local.properties` con
la ruta del SDK de Termux y ejecuta `assembleDebug`. También puedes preparar
solo la configuración con:

```bash
bash termux-setup.sh
./gradlew assembleDebug --no-daemon
```

`local.properties` se genera localmente y permanece excluido del ZIP portable.

## Datos actuales

La aplicación usa `MockAuthRepository` y `MockVehicleRepository`. Cualquier
login con usuario y contraseña no vacíos permite probar la navegación local.
Sustituye estos repositorios por implementaciones HTTP cuando el API de
systemStar esté disponible.

## Estética

La UI usa:

- Fondo `#0F1115`
- Tarjetas `#181C20`
- Bordes `#22282F`
- Azul principal `#007CFF`
- Texto claro `#EFF1F3`
- Texto secundario `#858D98`

Estado actual de desarrollo

Funcionalidades implementadas y validadas

La versión actual de BlueTrack tiene implementada y probada en un dispositivo Android la siguiente navegación:

Login
  ↓
Dashboard
  ↓
FleetScreen
  ↓
VehicleDetailScreen
  ↓
Volver al Dashboard

Componentes principales

- "LoginScreen"
- "DashboardScreen"
- "FleetScreen"
- "VehicleDetailScreen"
- "LoginViewModel"
- "FleetViewModel"
- "LoginViewModelFactory"
- "FleetViewModelFactory"

Modelos de dominio

- "User"
- "Session"
- "Vehicle"
- "Position"

Interfaces de repositorio

- "AuthRepository"
- "VehicleRepository"
- "PositionRepository"

Fuentes de datos actuales

- "MockAuthRepository"
- "MockVehicleRepository"

Los datos utilizados durante esta etapa son simulados y no representan un contrato real de "systemStar".

Validación en dispositivo

La aplicación fue compilada e instalada correctamente mediante:

./gradlew :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk

Pruebas funcionales realizadas

1. Login visible — PASS
2. Login funcional — PASS
3. Dashboard visible — PASS
4. Métricas visibles — PASS
5. Vehículos visibles — PASS
6. Actualización de vehículos — PASS
7. Logout y re-login — PASS
8. Selección de vehículo — PASS
9. Detalle del vehículo — PASS
10. Regreso al Dashboard — PASS

Estado de integración con systemStar

La integración real con "systemStar" todavía no ha comenzado.

No se han inventado endpoints REST, modelos JSON, credenciales, eventos WebSocket ni otros contratos de comunicación.

La arquitectura mantiene las interfaces de dominio desacopladas de las implementaciones concretas para permitir posteriormente sustituir los mocks por las implementaciones oficiales de "systemStar".

Próximo objetivo

El siguiente incremento funcional es Position.

Objetivo:

Vehicle
  ↓
PositionRepository
  ↓
MockPositionRepository
  ↓
VehicleDetailScreen

Posteriormente, la posición podrá alimentar la abstracción del proveedor de mapas.

La integración real con "systemStar" queda pendiente hasta disponer y verificar su contrato oficial.
