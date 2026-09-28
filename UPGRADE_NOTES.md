# Mejoras incluidas

## Experiencia

- Se reemplazó la pantalla básica por una experiencia oscura alineada con la
  estética BlueTrack web.
- Se agregaron jerarquía visual, estados de carga, error y botones con estados
  claros.
- Se creó un dashboard con métricas de activos, unidades en ruta y alertas.
- Se agregaron tarjetas de vehículos con placa, estado, ubicación, velocidad,
  batería y última actualización.
- Se agregó actualización manual de la flota y cierre de sesión.

## Arquitectura

- El login ahora expone un `LoginState` reactivo.
- La flota tiene `FleetViewModel` y `FleetViewModelFactory`.
- Los datos de prueba están separados en `MockAuthRepository` y
  `MockVehicleRepository`.
- La capa de presentación no depende de detalles del repositorio.

## Pendiente para producción

- Sustituir repositorios mock por clientes HTTP autenticados.
- Agregar persistencia de sesión segura.
- Integrar mapa, alertas, instalación GPS y reportes.
- Completar pruebas unitarias y de UI.
- Compilar en un entorno con Android SDK 34.

## Compatibilidad Termux

La configuración de compilación fue alineada con el entorno original del
proyecto: JDK 21, SDK en `~/android-sdk`, `aapt2` en `$PREFIX/bin/aapt2` y
Gradle Wrapper 9.6.0. `termux-build.sh` prepara `local.properties` antes de
compilar.