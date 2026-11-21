# Stone Video: reglas para agentes

- Copia de Stone 1.7.23 dedicada a video. Lee `ARQUITECTURA.md`, luego `android-native/MAP.md`, y abre solo lo que necesites.
- Pruebas: `cd android-native && ./gradlew :core:test :app:testDebugUnitTest :app:lintDebug :app:assemblePreview`
  (debe pasar todo). Nunca desactives ni saltes una prueba. Lint sin avisos nuevos.
- Cada versión: sube `versionCode` y `versionName`, copia el APK a `android-native/StoneVideo-<versión>.apk`
  (borra el anterior) y anótalo en `CHANGELOG.md`.
- Id propio `com.zirakusa.stone.video`: se instala junto a Stone sin tocar sus datos. No lo cambies.
- Reglas de Shorts y MV: `core/.../VideoKind.kt` (puras, con pruebas).
- Firma: la de depuración del entorno, igual que Stone, Luma y Gateway Hub.
