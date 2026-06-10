# Reporte de Revisión de Arquitectura: Clean Architecture en proyectoFinalPrograMovil

Este reporte detalla los hallazgos de la revisión del proyecto `proyectoFinalPrograMovil` con base en las directrices y estructura esperada descrita en [Progra Móvil Arquitectura Limpia LAST.md](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACIÓN_DE_DISPOSITIVOS_MÓVILES/Code/proyectoFinalPrograMovil/Progra%20Móvil%20Arquitectura%20Limpia%20LAST.md).

---

## 1. Resumen de Hallazgos Principales

El proyecto tiene una buena base modularizada por características (*feature*), pero presenta desviaciones importantes en los principios de Clean Architecture y la organización física de archivos:
1. **Fugas de detalles de infraestructura al Dominio**: Varios orígenes de datos (*Data Sources*) de Firebase están declarados en paquetes de `domain/repository`. El dominio debe ser agnóstico a implementaciones técnicas y SDKs de terceros.
2. **Abstracciones de Repositorio incorrectas**: Se usan `expect class` de Kotlin Multiplatform en la capa de dominio en lugar de `interface` tradicionales de Kotlin, acoplando el dominio a la mecánica interna de KMP.
3. **Mecanismo MVI disperso en carpetas**: En lugar de agrupar `UiState`, `Event/Intent` y `Effect` dentro del paquete `presentation/state` (como especifica el estándar), se crearon subcarpetas redundantes `presentation/intent/` y `presentation/effect/`.
4. **Acoplamiento en Settings**: La característica `settings` se salta completamente la capa `domain`, permitiendo que el ViewModel interactúe directamente con objetos de persistencia/datos (`AppSettingsStore`), además de instanciar clases directamente sin usar inyección de dependencias (Koin).
5. **Repositorios con demasiadas responsabilidades (Fat Repository)**: El `ContentListRepositoryImpl` maneja directamente el cliente HTTP (Ktor), las claves de API, las URLs y la lógica de parseo JSON manual para múltiples servicios de catálogo (TMDB, RAWG, Google Books, TVMaze).
6. **Código Muerto / Boilerplate sin uso**: Persisten entidades de base de datos y DAOs para un feature inexistente (`TodoEntity`/`TodoDao`) y mapeadores redundantes que no están siendo importados.

---

## 2. Análisis Detallado y Correcciones por Feature

### 🔑 Autenticación (`auth`)
* **Ubicación de Data Sources**:
  * **Problema**: [FirebaseAuthDataSource.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACIÓN_DE_DISPOSITIVOS_MÓVILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/auth/domain/repository/FirebaseAuthDataSource.kt) está en `domain/repository`.
  * **Explicación**: Los Data Sources manejan la interacción con el SDK de Firebase (detalle de datos/infraestructura) y pertenecen a la capa `data/datasource`.
  * **Corrección**: Mover a `auth/data/datasource/FirebaseAuthDataSource.kt` y actualizar imports en `AuthRepositoryImpl` y `RepositoryModule`.
* **Ubicación de Modelos de Datos de Red (DTO)**:
  * **Problema**: `AuthRemoteUser` está declarado dentro de `FirebaseAuthDataSource.kt` en el paquete de dominio.
  * **Corrección**: Moverlo a un archivo DTO propio en `auth/data/dto/AuthRemoteUser.kt`.
* **Definición de Mappers**:
  * **Problema**: Los mappers entre el DTO de red (`AuthRemoteUser`), la entidad local (`UserEntity`) y el modelo de dominio (`User`) se definen como extensiones privadas dentro de [AuthRepositoryImpl.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACIÓN_DE_DISPOSITIVOS_MÓVILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/auth/data/repository/AuthRepositoryImpl.kt).
  * **Corrección**: Mover estas funciones a un archivo dedicado `auth/data/mapper/AuthMappers.kt`.
* **Carpetas del patrón MVI (State, Effect, Intent)**:
  * **Problema**: `AuthEffect.kt` y `AuthIntent.kt` están bajo los paquetes `auth/presentation/effect/` y `auth/presentation/intent/`.
  * **Corrección**: Moverlos dentro de `auth/presentation/state/` y eliminar los directorios `effect/` e `intent/` para respetar la estructura:
    ```
    - presentation
        - state
            - AuthUiState.kt
            - AuthEffect.kt
            - AuthIntent.kt (o AuthEvent.kt)
    ```

---

### 📂 Gestión de Listas (`lists`)
* **Ubicación de Data Sources**:
  * **Problema**: [FirebaseRealtimeListsDataSource.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACIÓN_DE_DISPOSITIVOS_MÓVILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/lists/domain/repository/FirebaseRealtimeListsDataSource.kt) está bajo `domain/repository`.
  * **Explicación**: Interactúa con Firebase Realtime Database. Es un detalle de datos y debe estar en la capa de datos.
  * **Corrección**: Mover a `lists/data/datasource/FirebaseRealtimeListsDataSource.kt`.
* **Lógica de APIs y Cliente HTTP dentro del Repositorio**:
  * **Problema**: [ContentListRepositoryImpl.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACIÓN_DE_DISPOSITIVOS_MÓVILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/lists/data/repository/ContentListRepositoryImpl.kt) (con más de 800 líneas) maneja directamente llamadas de red con `HttpClient` de Ktor y parsea respuestas complejas en JSON para múltiples catálogos.
  * **Explicación**: Violar la separación de responsabilidades hace que el repositorio sea difícil de mantener y probar. El repositorio no debería saber cómo hacer peticiones HTTP ni conocer los JSONs crudos de las APIs externas.
  * **Corrección**:
    1. Crear interfaces de datasources remotos en `lists/data/datasource/`, por ejemplo: `CatalogRemoteDataSource`.
    2. Implementar estos datasources en `lists/data/service/` (ej: `TmdbCatalogService`, `GoogleBooksCatalogService`, etc.) donde se configure el cliente HTTP de Ktor y se realice el parseo JSON.
    3. Inyectar estos datasources en `ContentListRepositoryImpl` para simplificar su lógica.
* **Mapeadores duplicados e inlinados**:
  * **Problema**: Los métodos `toEntity()` y `toDomain()` para listas e ítems se implementan de forma privada dentro de `ContentListRepositoryImpl.kt`. Adicionalmente, existe un archivo redundante y desactualizado en `core` llamado [EntityMappers.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACIÓN_DE_DISPOSITIVOS_MÓVILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/core/data/db/EntityMappers.kt) que no se utiliza y descarta campos como `description` y `coverImageUrl`.
  * **Corrección**:
    1. Eliminar el archivo de mappers huérfano `core/data/db/EntityMappers.kt`.
    2. Crear `lists/data/mapper/ContentListMapper.kt` y mover allí los mappers del repositorio.
* **Carpetas del patrón MVI**:
  * **Problema**: Múltiples efectos e intents están estructurados en subcarpetas `lists/presentation/effect/` y `lists/presentation/intent/`.
  * **Corrección**: Agrupar todos los efectos e intents en `lists/presentation/state/` y eliminar los directorios `effect/` e `intent/`.

---

### ⚙️ Modo de Mantenimiento (`maintenance`)
* **Abstracción del Repositorio**:
  * **Problema**: [RemoteConfigRepository.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACIÓN_DE_DISPOSITIVOS_MÓVILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/maintenance/domain/repository/RemoteConfigRepository.kt) se declara en el dominio como un `expect class`.
  * **Explicación**: El dominio debe contener únicamente contratos e interfaces puras de Kotlin. La mecánica de KMP `expect/actual` no debería acoplar el dominio a la plataforma.
  * **Corrección**:
    1. Definir una `interface RemoteConfigRepository` estándar de Kotlin en `maintenance/domain/repository/`.
    2. Colocar la implementación específica de la plataforma en la capa de datos. Para ello, se puede declarar un `expect class RemoteConfigDataSource` en `maintenance/data/datasource/` e implementar el repositorio como `class RemoteConfigRepositoryImpl(private val dataSource: RemoteConfigDataSource): RemoteConfigRepository` en la capa `data/repository`.
* **Inconsistencia de Paquete/Carpeta**:
  * **Problema**: Los archivos actuals de [RemoteConfigRepository.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACIÓN_DE_DISPOSITIVOS_MÓVILES/Code/proyectoFinalPrograMovil/composeApp/src/androidMain/kotlin/com/ucb/proyectofinal/maintenance/data/repository/RemoteConfigRepository.kt) en `androidMain` e `iosMain` están ubicados físicamente en la carpeta `data/repository/` pero declaran el paquete `com.ucb.proyectofinal.maintenance.domain.repository`. Esto rompe la coherencia entre estructura de carpetas y paquetes de Kotlin.
  * **Corrección**: Una vez aplicada la corrección anterior, la interfaz de dominio y la implementación física de datos estarán alineadas en sus carpetas correctas.

---

### 🚀 Onboarding (`onboarding`)
* **Ubicación de archivos utilitarios**:
  * **Problema**: [DeviceLanguage.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACIÓN_DE_DISPOSITIVOS_MÓVILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/onboarding/data/DeviceLanguage.kt) (`expect fun getDeviceLanguage`) está directamente en la raíz de `onboarding/data/`.
  * **Corrección**: Mover a `onboarding/data/datasource/` o extraerlo a las utilidades generales de plataforma en `core`.
* **Carpetas no estándar**:
  * **Problema**: [OnboardingPreferences.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACIÓN_DE_DISPOSITIVOS_MÓVILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/onboarding/data/local/OnboardingPreferences.kt) está en una carpeta personalizada `data/local/`.
  * **Corrección**: Mover el archivo a `onboarding/data/datasource/OnboardingPreferences.kt` (actúa como el data source local para preferencias).

---

### 👤 Perfil (`profile`)
* **Estructura MVI**:
  * **Problema**: `ProfileEffect.kt` y `ProfileIntent.kt` están bajo subcarpetas `profile/presentation/effect/` y `profile/presentation/intent/`.
  * **Corrección**: Mover ambos archivos a `profile/presentation/state/` y eliminar las carpetas vacías sobrantes.

---

### ⭐ Favoritos (`favorites`)
* **UI State Inline**:
  * **Problema**: La clase `FavoritesUiState` está declarada inline directamente dentro de [FavoritesViewModel.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACIÓN_DE_DISPOSITIVOS_MÓVILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/favorites/presentation/viewmodel/FavoritesViewModel.kt).
  * **Corrección**: Crear el archivo `favorites/presentation/state/FavoritesUiState.kt` y trasladar la declaración de la clase allí.

---

### 🛠️ Configuración de la App (`settings`)
* **Acoplamiento Directo y Falta de Capa de Dominio**:
  * **Problema**: [SettingsViewModel.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACIÓN_DE_DISPOSITIVOS_MÓVILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/settings/presentation/viewmodel/SettingsViewModel.kt) accede e interactúa directamente con `AppSettingsStore` en la capa de datos.
  * **Explicación**: Esto viola la regla de dependencia de Clean Architecture: la capa de presentación no debe depender directamente de los datos, sino a través de abstracciones del dominio (casos de uso/repositorios).
  * **Corrección**:
    1. Definir la interfaz `SettingsRepository` en `settings/domain/repository/`.
    2. Implementarla en la capa de datos como `SettingsRepositoryImpl` delegando en `AppSettingsStore` y `ThemePreferences`.
    3. Crear Casos de Uso en `settings/domain/usecase/` (ej. `GetThemeUseCase`, `SaveThemeUseCase`, `ChangeLanguageUseCase`).
    4. Modificar `SettingsViewModel` para que interactúe únicamente con los casos de uso.
* **Instanciación Directa (Sin Inyección de Dependencias)**:
  * **Problema**: [AppSettingsStore.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACIÓN_DE_DISPOSITIVOS_MÓVILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/settings/data/AppSettingsStore.kt) crea de manera estática y manual la instancia de `ThemePreferences` (`private val themePreferences = ThemePreferences()`).
  * **Corrección**: Registrar `ThemePreferences` y `AppSettingsStore` en el módulo Koin correspondiente e inyectarlos.
* **Carpetas no estándar / Estructuración**:
  * **Problema**: `AppSettingsStore.kt` y `ThemePreferences.kt` están sueltos en `settings/data/`.
  * **Corrección**: Moverlos a `settings/data/datasource/`.
  * **Problema**: [ApplyLocale.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACIÓN_DE_DISPOSITIVOS_MÓVILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/settings/platform/ApplyLocale.kt) usa la carpeta `settings/platform/`.
  * **Corrección**: Clasificarlo bajo `settings/data/datasource/` o centralizarlo en un paquete de utilidades de plataforma de `core`.
  * **Problema**: Los archivos de MVI se encuentran en `effect/` e `intent/` en vez de `state/`.
  * **Corrección**: Moverlos a `settings/presentation/state/`.

---

### 🔔 Notificaciones (`notification`)
* **Falta de Estructura de Capas**:
  * **Problema**: [FirebaseToken.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACIÓN_DE_DISPOSITIVOS_MÓVILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/notification/FirebaseToken.kt) está suelto en la raíz de `notification/`.
  * **Corrección**: Al ser un origen de datos de plataforma (obtiene el token FCM), debe moverse al paquete de base de datos/infraestructura en `notification/data/datasource/FirebaseTokenDataSource.kt`.

---

## 3. Código Muerto / Inconsistencias en Android/Platform

* **WorkManager / Workers en androidMain**:
  * **Problema**: [FetchPopularMoviesUseCase.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACIÓN_DE_DISPOSITIVOS_MÓVILES/Code/proyectoFinalPrograMovil/composeApp/src/androidMain/kotlin/com/ucb/proyectofinal/worker/FetchPopularMoviesUseCase.kt) es un caso de uso *dummy* que está definido dentro del código de Android (`androidMain`) en lugar de en `commonMain`.
  * **Explicación**: Todos los casos de uso deben ser puramente de Kotlin y residir en `commonMain` dentro de la capa de dominio. Colocarlo en el código de plataforma viola la regla de portabilidad del negocio.
  * **Corrección**: Eliminar el caso de uso si no tiene utilidad real, o migrarlo a la capa `domain/usecase` del feature correspondiente en `commonMain`.
* **Entidades y DAOs Huérfanos**:
  * **Problema**: [TodoDao.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACIÓN_DE_DISPOSITIVOS_MÓVILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/core/data/db/TodoDao.kt) y `TodoEntity.kt` están definidos e integrados en `AppRoomDatabase.kt` pero no se asocian a ningún feature de negocio ni están registrados en Koin.
  * **Corrección**: Eliminar ambos archivos de base de datos y removerlos de la configuración de `AppRoomDatabase.kt`.
* **Carpeta de Feature Vacía**:
  * **Problema**: La carpeta `composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/feature/firebase` contiene carpetas vacías de Clean Architecture.
  * **Corrección**: Eliminar toda la ruta de carpetas `feature/firebase/` si no se planea utilizar a corto plazo.

---

## 4. Tabla Comparativa de Violaciones por Feature

| Feature / Módulo | Archivo Afectado | Ubicación Actual | Ubicación Correcta | Tipo de Inconsistencia |
| :--- | :--- | :--- | :--- | :--- |
| **`auth`** | `FirebaseAuthDataSource.kt` | `domain/repository` | `data/datasource` | Origen de datos en Dominio |
| | `AuthRemoteUser` (DTO) | `domain/repository` | `data/dto` | Modelo de red en Dominio |
| | `AuthEffect.kt` / `AuthIntent.kt` | `presentation/effect / intent` | `presentation/state` | Organización del patrón MVI |
| **`lists`** | `FirebaseRealtimeListsDataSource.kt` | `domain/repository` | `data/datasource` | Origen de datos en Dominio |
| | `ContentListRepositoryImpl.kt` | `data/repository` | `data/repository` | Violación de responsabilidades (cliente HTTP/JSON en Repo) y Mappers inlined |
| | Varios Effects/Intents | `presentation/effect / intent` | `presentation/state` | Organización del patrón MVI |
| **`maintenance`**| `RemoteConfigRepository.kt` (expect) | `domain/repository` | `domain/repository` (como interface) | Clase concreta en lugar de interfaz en Dominio |
| | `RemoteConfigRepository.kt` (actual) | `data/repository` (físicamente) | `data/repository` | Desajuste entre directorio y paquete declarado |
| **`onboarding`** | `DeviceLanguage.kt` | `data/` (raíz) | `data/datasource` | Organización de capa |
| | `OnboardingPreferences.kt` | `data/local/` | `data/datasource/` | Carpeta de capa no estándar |
| **`favorites`** | `FavoritesUiState` | Inline en ViewModel | `presentation/state/` | Falta de estructura de estados |
| **`profile`** | `ProfileEffect.kt` / `ProfileIntent.kt` | `presentation/effect / intent` | `presentation/state` | Organización del patrón MVI |
| **`settings`** | `SettingsViewModel.kt` | `presentation/viewmodel` | `presentation/viewmodel` | Salto directo a capa de datos (sin domain) |
| | `AppSettingsStore.kt` | `data/` (raíz) | `data/datasource/` | Organización de capa y falta de DI |
| | `ThemePreferences.kt` | `data/` (raíz) | `data/datasource/` | Organización de capa |
| | `ApplyLocale.kt` | `platform/` | `data/datasource/` (o `core`) | Carpeta de capa no estándar |
| | `SettingsEffect.kt` / `SettingsIntent.kt` | `presentation/effect / intent` | `presentation/state` | Organización del patrón MVI |
| **`notification`**| `FirebaseToken.kt` | `notification/` (raíz) | `notification/data/datasource/`| Falta de estructura de capas |
| **`worker`** (Android)| `FetchPopularMoviesUseCase.kt` | `androidMain/../worker` | `commonMain/../domain/usecase`| Lógica de negocio en plataforma |
| **`core`** | `EntityMappers.kt` | `core/data/db` | (Eliminar) | Mapper redundante y obsoleto |
| | `TodoDao.kt` / `TodoEntity.kt` | `core/data/db` | (Eliminar) | Código muerto |
