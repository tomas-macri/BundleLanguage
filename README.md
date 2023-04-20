# Gestión de idiomas en Android App Bundles

## ¿Qué es Android App Bundle (AAB)?

Android App Bundle (AAB) se lanzó en 2018 y es un formato de publicación para Android compatible con Google Play y otras tiendas de aplicaciones, además de herramientas de compilación como Android Studio.

## ¿Qué diferencia hay entre AAB y APK?

Los paquetes de aplicaciones solo se pueden publicar y no se pueden instalar en dispositivos Android. El paquete de Android (APK) es el formato instalable y ejecutable de Android para apps. Un distribuidor debe procesar los paquetes de aplicaciones y convertirlos en APK para que se puedan instalar en los dispositivos.


## AAB Deploy en Android emulator

### 1. Seleccionar la build variant a release
![SelectBuildVariant1.png](img/SelectBuildVariant1.png)
![SelectBuildVariant2.png](img/SelectBuildVariant2.png)

### 2. Configurar release build variant

En Project Structure, selecciona modules y configura una signing configuration para release. 
![CrearConfFirma.png](img/CrearConfFirma.png)

En Build Variants, selecciona release y elige la signing configuration.
![AsignarConfFirmaARelease.png](img/AsignarConfFirmaARelease.png)

Si no se configura una signing configuration, se obtiene el siguiente error:
![ErrorAABNoFirmado.png](img/ErrorAABNoFirmado.png)

### 3. Instalar AAB en Android emulator

![EditLaunchConf1.png](img/EditLaunchConf1.png)
![EditLaunchConf2.png](img/EditLaunchConf2.png)

## Code Snippets

#### Obtener los idiomas instalados en el dispositivo

```kotlin
val installedLocales: MutableList<Locale> = mutableListOf()
for (i in 0 until resources.configuration.locales.size()) {
    installedLocales.add(resources.configuration.locales.get(i))
}
```
resources se llama desde un contexto, por ejemplo, desde un Activity. En este caso,

resources.configuration.locales es de tipo LocaleList, que es una clase que hereda de LocaleListBase, que a su vez hereda de LocaleListInterface. LocaleListInterface es una interfaz que define los métodos get(index: Int) y size().

#### Obtener los idiomas disponibles en el dispositivo

```kotlin
val allLocales : MutableList<String> = mutableListOf()
allLocales.addAll(Locale.getAvailableLocales().map { it.displayName })
```

Locale.getAvailableLocales() devuelve un array de Locale.


## Solución al problema - Forma no eficiente
Una forma de resolver esta incidencia sería insertar este trozo de código en la división de android {} dentro del gradle de la app.

```kotlin
bundle {
        language {
            // Specifies that the app bundle should not support
            // configuration APKs for language resources. These
            // resources are instead packaged with each base and
            // dynamic feature APK.
            enableSplit = false
        }
    }
```
 Sin embargo, esta solución no sería eficiente ya que descagaría todos los recursos de lenguaje existentes al momento de instalar la aplicación en cualquier dispositivo, lo cual va en contra del formato AAB en el que se prioriza la optimización del espacio.

## Otra posible solución
Este método se puede probar únicamente con las aplicaciones que están en la PlayStore, por lo que no tenemos la certeza de que vaya a funcionar.

En este caso, implementando la dependencia de Google Play Core y con el siguiente código, cuando el usuario selecciona un idioma que no estaba instalado en la aplicación, se puede realizar la descarga automática del fichero strings.xml del nuevo idioma.

Dependencia que va en el módulo gradle de la app:
```kotlin
implementation 'com.google.android.play:core:1.10.3'
```

Código a ejecutar cuando se selecciona un idioma nuevo:
```kotlin
    // Creates a request to download and install additional language resources.
    // Creates a request to download and install additional language resources.
    val request =
        SplitInstallRequest.newBuilder() // Uses the addLanguage() method to include French language resources in the request.
            // Note that country codes are ignored. That is, if your app
            // includes resources for “fr-FR” and “fr-CA”, resources for both
            // country codes are downloaded when requesting resources for "fr".
            .addLanguage(Locale.forLanguageTag("fr"))
            .build()
    // Submits the request to install the additional language resources.
    // Submits the request to install the additional language resources.
    val splitInstall = SplitInstallManagerFactory.create(this)
    splitInstall.startInstall(request)
```


## Referencias

* [Acerca de Android App Bundles - developer.android.com](https://developer.android.com/guide/app-bundle?hl=es-419)
* [Como localizar tu app - developer.android.com](https://developer.android.com/guide/topics/resources/localization?hl=es-419)
* [Cómo habilitar o inhabilitar tipos de APK de configuración - developer.android.com](https://developer.android.com/guide/app-bundle/configure-base?hl=es-419#disable_config_apks)
* [Instalar idioma desde el Play Store - developer.android.com](https://developer.android.com/reference/com/google/android/play/core/splitinstall/SplitInstallManager#startInstall(com.google.android.play.core.splitinstall.SplitInstallRequest))