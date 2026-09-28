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