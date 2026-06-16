# AppKotlinApi3

Aplicacion Android simple en Kotlin para consumir una API externa y mostrar un listado de productos de e-commerce.

## Funcionalidad

- Consume la URL `https://jsonkeeper.com/b/MX0A`.
- Muestra un listado simple con nombre y precio de cada producto.
- Al tocar un producto, abre una pantalla de detalle con descripcion, precio y disponibilidad.

## Requisitos

- JDK 17.
- Android SDK con `platforms;android-35` y `build-tools;35.0.0`.
- Android Studio o Gradle Wrapper.

## Como ejecutar

1. Abrir el proyecto en Android Studio.
2. Esperar la sincronizacion de Gradle.
3. Ejecutar la app en un emulador o dispositivo Android.

Tambien se puede compilar desde terminal con:

```powershell
.\gradlew.bat assembleDebug
```
