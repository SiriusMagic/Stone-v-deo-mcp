# Cambios de Stone Video

## 0.4.0 (versionCode 4)
- Los tokens nuevos del Modo MCP empiezan por **`stv_`** (antes `stn_`, el de Stone): Gateway Hub reconoce cada app por
  su prefijo y los confundía. Los tokens ya creados siguen funcionando; para vincular con el hub, crea uno nuevo.

## 0.3.0 (versionCode 3)
- La pestaña «MV» pasa a llamarse **VM** (videos musicales) en español.
- **Fuera Playlists:** la pestaña y las opciones «Añadir a playlist» del reproductor, del menú del video y de la
  selección. Pestañas: ♪ Música | Videos · Shorts · Carpetas · VM · Géneros.

## 0.2.0 (versionCode 2)
- Pestañas: **♪ Música** primero, con un separador, y luego Videos · Shorts · Carpetas · Playlists · MV · **Géneros**.
- **Géneros** ahora agrupa videos por su género (terror, romance…): el que pongas en «Editar información de video» o
  el que Android ya conozca. Los videos sin género van juntos al final.
- Fuera Artistas y Álbumes (y tocar el artista en el reproductor ya no lleva a ellos).
- Fuera de Ajustes: Reproducción, Niveles de audio y Temporizador para dormir (también del menú del reproductor y de los
  accesos rápidos). Las herramientas MCP para cambiar el modo y los niveles de audio también se quitaron.
- **Siempre en modo Cine:** volumen de 0 a 300 % y aceleración completa, como el modo Cine de Stone.

## 0.1.0 (versionCode 1)
- Nace como copia de Stone 1.7.23, con id propio (`com.zirakusa.stone.video`) y nombre «Stone Video».
- Abre en **Videos**. Pestañas nuevas **Shorts** (cuadrícula de tarjetas verticales) y **MV**; **Carpetas** ahora
  muestra carpetas de videos.
- Pestañas con letra grande y barra de acento bajo la activa.
- El escaneo guarda ancho y alto de cada video (con su rotación) para reconocer los verticales.
- El servidor MCP se presenta como `stone-video`.
