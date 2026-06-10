# Reporte de Revisión Completa de Arquitectura (Clean Architecture)

Este reporte detalla el diagnóstico y análisis exhaustivo del proyecto **`proyectoFinalPrograMovil`** frente a las directrices de **Clean Architecture por Feature** especificadas en el archivo [Progra Móvil Arquitectura Limpia LAST.md](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACIÓN_DE_DISPOSITIVOS_MÓVILES/Code/proyectoFinalPrograMovil/Progra%20Móvil%20Arquitectura%20Limpia%20LAST.md).

---

## 1. Resumen Ejecutivo de Hallazgos

El proyecto cuenta con una estructura general organizada por características (*features*) y implementa Koin para inyección de dependencias, lo cual es excelente. Sin embargo, persisten desviaciones críticas de Clean Architecture y buenas prácticas de desarrollo multiplataforma:

1. **Discrepancia Físico-Lógica Extensa (El mayor "code smell")**: Una refactorización previa actualizó los encabezados de `package` en los archivos Kotlin para reflejar una estructura correcta de Clean Architecture, pero **no movió físicamente los archivos en el disco**. Esto produce un desajuste total entre la ubicación física (`.../auth/domain/repository/...`) y el paquete lógico (`com.ucb.proyectofinal.auth.data.datasource`). Esto viola la directriz de estructuración estricta en carpetas físicas.
2. **Salto de la Capa de Dominio (ViewModels Directos a Repositorio)**: Múltiples ViewModels en las features `explore`, `favorites` y `maintenance` consumen interfaces de repositorio directamente, puenteando la capa de Casos de Uso.
3. **Lógica de Infraestructura y DTOs en el Dominio**: La feature `onboarding` declara clases DTO con anotaciones `@Serializable` de red en el dominio, y su ViewModel realiza decodificación y parseo manual de JSONs.
4. **Fat Repository (Acoplamiento de Responsabilidades)**: El repositorio `ContentListRepositoryImpl` (con más de 800 líneas) realiza la lógica de conexión HTTP con Ktor, lee configuraciones y tokens de API, y parsea manualmente las respuestas JSON para 5 servicios de catálogo externos diferentes.
5. **Mappers Inlined (Extensiones Privadas)**: Las funciones de mapeo entre base de datos, DTOs y modelos de dominio se implementan como extensiones privadas dentro de los repositorios en lugar de estar aisladas en la subcarpeta `data/mapper/`.
6. **Código Huérfano y Errores de KMP en iOS**: La clase `RemoteConfigRepository` en `iosMain` se declaró como una `actual class` que intenta implementar una clase `expect`, pero `RemoteConfigRepository` en `commonMain` fue modificada a una `interface` estándar. Esto causa un error de compilación multiplataforma insalvable para el target de iOS.

---

## 2. Análisis Detallado por Feature y Desviaciones Físicas

### 🔑 Feature: `auth`
*   **Discrepancia de Ruta Física:**
    *   `FirebaseAuthDataSource.kt` está físicamente en `auth/domain/repository/` pero declara `package com.ucb.proyectofinal.auth.data.datasource`.
    *   *Corrección:* Mover físicamente el archivo (y sus `actual` en `androidMain` e `iosMain`) a `auth/data/datasource/`.
*   **Ubicación Incorrecta de DAOs y Entities:**
    *   `UserDao.kt` y `UserEntity.kt` están físicamente en `auth/data/datasource/local/`.
    *   *Corrección:* Moverlos a `auth/data/dao/` y `auth/data/entity/` respectivamente.
*   **Value Objects Anidados:**
    *   Los value objects (`Email.kt`, `Password.kt`, `UserId.kt`) están físicamente en `auth/domain/model/vo/`.
    *   *Corrección:* Moverlos a `auth/domain/vo/` de acuerdo al diagrama de carpetas.
*   **Mappers Inlined:**
    *   Las funciones de extensión para convertir `AuthRemoteUser` y `UserEntity` a `User` están declaradas como privadas dentro de `AuthRepositoryImpl.kt`.
    *   *Corrección:* Extraerlas a `auth/data/mapper/AuthMappers.kt`.
*   **Estructura MVI Física:**
    *   `AuthEffect.kt` y `AuthIntent.kt` se encuentran físicamente en `auth/presentation/effect/` y `auth/presentation/intent/`.
    *   *Corrección:* Moverlos físicamente a `auth/presentation/state/`.

---

### 📂 Feature: `lists`
*   **Discrepancia de Ruta Física:**
    *   `FirebaseRealtimeListsDataSource.kt` está físicamente en `lists/domain/repository/` pero declara `package com.ucb.proyectofinal.lists.data.datasource`.
    *   *Corrección:* Moverlo a `lists/data/datasource/`.
*   **Ubicación de DAOs y Entities:**
    *   Las clases de base de datos (`ContentItemDao.kt`, `ContentItemEntity.kt`, `ContentListDao.kt`, `ContentListEntity.kt`) están físicamente en `lists/data/datasource/local/`.
    *   *Corrección:* Mover los DAOs a `lists/data/dao/` y las entidades a `lists/data/entity/`.
*   **Fat Repository (Violación grave de SRP y Clean Architecture):**
    *   `ContentListRepositoryImpl.kt` contiene la lógica para consultar APIs externas (TMDB, RAWG, iTunes, Google Books, TVMaze), inyecta `HttpClient` de Ktor de forma directa, obtiene tokens de API secretos y parsea respuestas JSON a mano.
    *   *Corrección:*
        1. Definir una interfaz `CatalogRemoteDataSource` en `lists/data/datasource/`.
        2. Implementar los servicios de red específicos (ej: `TmdbCatalogService`, `GoogleBooksCatalogService`, etc.) bajo la carpeta `lists/data/service/` donde se encapsule la llamada HTTP de Ktor y la decodificación JSON.
        3. Inyectar `CatalogRemoteDataSource` en `ContentListRepositoryImpl`.
*   **Mappers Inlined:**
    *   Los mapeadores de listas/ítems locales están declarados inline en `ContentListRepositoryImpl.kt`.
    *   *Corrección:* Moverlos a `lists/data/mapper/ContentListMapper.kt`.
*   **Estructura MVI Física:**
    *   Todos los Effect e Intent de AddItem, ContentLists, EditList y ListDetail están en carpetas físicas separadas `/effect` e `/intent`.
    *   *Corrección:* Trasladar todos a `lists/presentation/state/` y eliminar los directorios vacíos.

---

### 🚀 Feature: `onboarding`
*   **Discrepancia de Ruta Física:**
    *   `DeviceLanguage.kt` (expect/actual) está físicamente en la raíz de `onboarding/data/`.
    *   `OnboardingPreferences.kt` (expect/actual) está físicamente en `onboarding/data/local/`.
    *   *Corrección:* Mover ambos archivos a `onboarding/data/datasource/`.
*   **DTOs en el Dominio:**
    *   `OnboardingConfigResponse` y `OnboardingSlideConfig` están definidos en `onboarding/domain/model/OnboardingConfig.kt` con anotaciones de serialización (`@Serializable`, `@SerialName`).
    *   *Razón:* Son la representación del JSON de red, por ende son DTOs (capa de datos).
    *   *Corrección:* Moverlos a `onboarding/data/dto/OnboardingConfigResponse.kt`.
*   **Lógica de Negocio en la Presentación:**
    *   `OnboardingViewModel` realiza directamente el parseo de JSON (`json.decodeFromString`) y mapeo de idiomas del Onboarding, además de escribir directamente a preferencias (`onboardingPreferences.setOnboardingCompleted(true)`).
    *   *Corrección:*
        1. Crear una interfaz `OnboardingRepository` en el dominio y su implementación en `data`.
        2. Crear dos Casos de Uso: `GetOnboardingSlidesUseCase` (que encapsula el fetch de RemoteConfig, el parseo JSON y la selección del idioma del dispositivo) e `IsOnboardingCompletedUseCase`/`CompleteOnboardingUseCase` en `domain/usecase/`.
        3. Inyectar estos casos de uso en el ViewModel.

---

### ⚙️ Feature: `settings`
*   **Discrepancia de Ruta Física:**
    *   `AppSettingsStore.kt` y `ThemePreferences.kt` están físicamente en `settings/data/`.
    *   `ApplyLocale.kt` está físicamente en `settings/platform/`.
    *   `SettingsEffect.kt` y `SettingsIntent.kt` están físicamente en `settings/presentation/effect/` y `settings/presentation/intent/`.
    *   *Corrección:*
        - Mover los almacenes y preferencias a `settings/data/datasource/`.
        - Mover `ApplyLocale.kt` a `settings/data/datasource/`.
        - Mover los efectos e intents a `settings/presentation/state/`.

---

### 🔔 Feature: `notification`
*   **Discrepancia de Ruta Física:**
    *   `FirebaseToken.kt` (expect/actual) está físicamente suelto en la raíz de `notification/`.
    *   *Corrección:* Moverlo a `notification/data/datasource/FirebaseToken.kt`.

---

### 👤 Feature: `profile`
*   **Estructura MVI Física:**
    *   `ProfileEffect.kt` y `ProfileIntent.kt` están físicamente en `profile/presentation/effect/` y `profile/presentation/intent/`.
    *   *Corrección:* Moverlos físicamente a `profile/presentation/state/`.

---

### ⭐ Features: `explore` y `favorites`
*   **Bypass de Casos de Uso (Domain Bypass):**
    *   Tanto `ExploreViewModel` como `FavoritesViewModel` inyectan directamente la interfaz `ContentListRepository` de la feature de listas, saltándose la capa de dominio.
    *   *Corrección:*
        1. Crear los casos de uso correspondientes en la capa de dominio, por ejemplo: `GetPublicListsUseCase` para Explore, y `GetFavoritesUseCase`, `AddFavoriteUseCase`, `RemoveFavoriteUseCase` para Favorites.
        2. Inyectar y consumir estos Casos de Uso en los respectivos ViewModels.

---

### ⚙️ Feature: `maintenance`
*   **Bypass de Casos de Uso:**
    *   `MaintenanceViewModel` inyecta directamente `RemoteConfigRepository` de dominio en lugar de usar un caso de uso.
    *   *Corrección:* Crear el caso de uso `ObserveMaintenanceUseCase` en `maintenance/domain/usecase/` y consumirlo en el ViewModel.
*   **Código Roto/Muerto en iOS (Target Compilation Error):**
    *   En `composeApp/src/iosMain/kotlin/com/ucb/proyectofinal/maintenance/data/repository/RemoteConfigRepository.kt` se declara una `actual class RemoteConfigRepository`.
    *   *Razón:* En la última refactorización, `RemoteConfigRepository` se transformó de `expect class` a una `interface` en `commonMain` (lo cual es correcto). Sin embargo, se olvidó eliminar o actualizar esta clase específica de iOS. Al no haber un `expect class` correspondiente, la compilación de Kotlin Multiplatform para iOS falla.
    *   *Corrección:* Eliminar la clase obsoleta en iOS, ya que el stub de iOS se define correctamente mediante el proveedor de DI en [RemoteConfigProvider.kt (iosMain)](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACIÓN_DE_DISPOSITIVOS_MÓVILES/Code/proyectoFinalPrograMovil/composeApp/src/iosMain/kotlin/com/ucb/proyectofinal/di/RemoteConfigProvider.kt).

---

### 📂 Modulo: `designsystem`
*   El módulo `designsystem` (definido a nivel de raíz del proyecto) está modularizado de forma independiente y contiene componentes gráficos y temas unificados de Jetpack Compose (`components/`, `theme/`). Esta separación es **correcta** y recomendada, ya que no representa un feature de negocio y debe proveer elementos de UI de forma transversal, libre de la estructura de Clean Architecture tradicional.

---

### 📁 Carpeta Huérfana: `feature`
*   Existe el directorio físico vacío `composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/feature/firebase/` (con carpetas internas vacías `data/`, `domain/`, `presentation/`).
*   *Corrección:* Eliminar la carpeta `feature/` por completo para limpiar la estructura del proyecto.

---

## 3. Tabla Resumen de Inconsistencias Físicas vs. Lógicas

| Archivo / Clase | Ubicación Física Actual | Paquete Lógico Declarado | Ubicación Física Esperada | Diagnóstico |
| :--- | :--- | :--- | :--- | :--- |
| **`FirebaseAuthDataSource`** | `auth/domain/repository/` | `auth.data.datasource` | `auth/data/datasource/` | Mismatch físico-lógico (Datasource en Dominio) |
| **`UserDao`** / **`UserEntity`** | `auth/data/datasource/local/` | `auth.data.datasource` | `auth/data/dao/` y `/entity/` | Mismatches físicos y de paquetes. |
| **`Email`**, **`Password`**, **`UserId`** | `auth/domain/model/vo/` | `auth.domain.model.vo` | `auth/domain/vo/` | Value Objects anidados innecesariamente. |
| **`AuthEffect`** / **`AuthIntent`** | `auth/presentation/effect / intent/` | `auth.presentation.state` | `auth/presentation/state/` | Carpetas MVI redundantes físicas. |
| **`FirebaseRealtimeListsDataSource`**| `lists/domain/repository/` | `lists.data.datasource` | `lists/data/datasource/` | Mismatch físico-lógico (Datasource en Dominio) |
| **`ContentListDao`** / **`ContentItemDao`**| `lists/data/datasource/local/` | `lists.data.datasource` | `lists/data/dao/` | Mismatch físico-lógico. |
| **`ContentListEntity`** / **`ContentItemEntity`**| `lists/data/datasource/local/` | `lists.data.datasource` | `lists/data/entity/` | Mismatch físico-lógico. |
| MVI Effects/Intents de **`lists`** | `lists/presentation/effect / intent/`| `lists.presentation.state` | `lists/presentation/state/` | Carpetas MVI redundantes físicas. |
| Mappers en **`auth`** y **`lists`** | Inlined en repositorios | - | `.../data/mapper/` | Deben ser archivos independientes. |
| **`DeviceLanguage`** | `onboarding/data/` | `onboarding.data.datasource` | `onboarding/data/datasource/`| Mismatch físico-lógico. |
| **`OnboardingPreferences`** | `onboarding/data/local/` | `onboarding.data.datasource` | `onboarding/data/datasource/`| Mismatch físico-lógico. |
| **`OnboardingConfig`** (DTO) | `onboarding/domain/model/` | `onboarding.domain.model` | `onboarding/data/dto/` | DTO expuesto en el Dominio. |
| **`ProfileEffect`** / **`ProfileIntent`** | `profile/presentation/effect / intent/`| `profile.presentation.state`| `profile/presentation/state/`| Carpetas MVI redundantes físicas. |
| **`AppSettingsStore`** / **`ThemePreferences`** | `settings/data/` | `settings.data.datasource` | `settings/data/datasource/` | Mismatch físico-lógico. |
| **`ApplyLocale`** | `settings/platform/` | `settings.data.datasource` | `settings/data/datasource/` | Mismatch físico-lógico. |
| **`SettingsEffect`** / **`SettingsIntent`**| `settings/presentation/effect / intent/`| `settings.presentation.state`| `settings/presentation/state/`| Carpetas MVI redundantes físicas. |
| **`FirebaseToken`** | `notification/` | `notification.data.datasource`| `notification/data/datasource/`| Mismatch físico-lógico. |
| **`RemoteConfigRepository` (iOS)** | `maintenance/data/repository/` | `maintenance.domain.repository`| (Eliminar) | Código roto/obsoleto de KMP `actual class`. |
| Carpeta **`feature`** | `com/ucb/proyectofinal/feature/`| - | (Eliminar) | Esqueleto vacío. |

---

## 4. Plan de Acción Recomendado (Correcciones Paso a Paso)

Para llevar el proyecto a una implementación de Clean Architecture pura y físicamente correcta sin romper la compilación de Android y resolviendo iOS, se deben realizar los siguientes pasos de refactorización:

### Paso 1: Mover Físicamente las Clases de Datos e Infraestructura
Mover los archivos que están en carpetas incorrectas de Dominio o Core a sus destinos correspondientes y actualizar sus imports:
1.  Mover expect/actual `FirebaseAuthDataSource.kt` de `auth/domain/repository/` a `auth/data/datasource/`.
2.  Mover expect/actual `FirebaseRealtimeListsDataSource.kt` de `lists/domain/repository/` a `lists/data/datasource/`.
3.  Mover expect/actual `OnboardingPreferences.kt` de `onboarding/data/local/` a `onboarding/data/datasource/`.
4.  Mover expect/actual `DeviceLanguage.kt` de `onboarding/data/` a `onboarding/data/datasource/`.
5.  Mover `AppSettingsStore.kt` y expect/actual `ThemePreferences.kt` de `settings/data/` a `settings/data/datasource/`.
6.  Mover expect/actual `ApplyLocale.kt` de `settings/platform/` a `settings/data/datasource/`.
7.  Mover expect/actual `FirebaseToken.kt` de `notification/` a `notification/data/datasource/`.

### Paso 2: Alinear Entidades de Base de Datos y DAOs por Feature
1.  Mover `UserDao.kt` y `UserEntity.kt` de `auth/data/datasource/local/` a `auth/data/dao/` y `auth/data/entity/` respectivamente.
2.  Mover `ContentListDao.kt` y `ContentItemDao.kt` de `lists/data/datasource/local/` a `lists/data/dao/`.
3.  Mover `ContentListEntity.kt` y `ContentItemEntity.kt` de `lists/data/datasource/local/` a `lists/data/entity/`.
4.  Actualizar la declaración de paquetes de estos archivos para que coincidan con la ruta física y resolver los imports en `AppRoomDatabase.kt` y Koin.

### Paso 3: Organizar Lógica de MVI y Value Objects
1.  Mover los Value Objects de `auth/domain/model/vo/` a `auth/domain/vo/` y ajustar sus paquetes.
2.  En todas las features (`auth`, `lists`, `profile`, `settings`), mover los archivos de `effect/` e `intent/` a `state/`, actualizar su package a `presentation.state` y borrar las carpetas vacías de effect/ e intent/.

### Paso 4: Resolver el Desacoplamiento de Repositorios en la Presentación (UseCases)
1.  **En `explore`**: Crear `GetPublicListsUseCase.kt` en `lists/domain/usecase/` (o en una nueva capa dominio de explore) y consumirla desde `ExploreViewModel`.
2.  **En `favorites`**: Crear `GetFavoritesUseCase.kt` y `RemoveFavoriteUseCase.kt` en `lists/domain/usecase/` y consumirlas desde `FavoritesViewModel`.
3.  **En `maintenance`**: Crear `ObserveMaintenanceUseCase.kt` y inyectarlo en `MaintenanceViewModel`.
4.  **En `onboarding`**: 
    - Extraer la lógica de parseo JSON y mapeo de idioma de `OnboardingViewModel` a un Caso de Uso `GetOnboardingSlidesUseCase.kt`.
    - Mover `OnboardingConfigResponse` y `OnboardingSlideConfig` a `onboarding/data/dto/`.
    - Crear `CompleteOnboardingUseCase.kt` para abstraer la escritura de preferencias y inyectarlo en el ViewModel.

### Paso 5: Simplificar el Fat Repository (`ContentListRepositoryImpl`)
1.  Declarar `CatalogRemoteDataSource` en `lists/data/datasource/`.
2.  Crear `lists/data/service/CatalogServices.kt` (o clases individuales por API) que inyecten el `HttpClient` de Ktor y contengan la lógica de llamadas HTTP de red y deserialización JSON.
3.  Inyectar este DataSource en `ContentListRepositoryImpl`, simplificando la clase y eliminando la dependencia directa de Ktor HttpClient de la implementación del repositorio.

### Paso 6: Resolver Código Huérfano
1.  Eliminar la carpeta física `composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/feature/` por completo.
2.  Eliminar el archivo obsoleto `composeApp/src/iosMain/kotlin/com/ucb/proyectofinal/maintenance/data/repository/RemoteConfigRepository.kt` en iOS para corregir el error de compilación multiplataforma.
