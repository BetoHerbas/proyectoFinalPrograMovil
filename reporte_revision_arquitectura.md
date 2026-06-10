# Reporte de Revisión Completa de Arquitectura (Clean Architecture)

Este reporte detalla el diagnóstico y análisis del proyecto **`proyectoFinalPrograMovil`** frente al estándar de **Clean Architecture por Feature** especificado en el archivo [Progra Móvil Arquitectura Limpia LAST.md](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACI%C3%93N_DE_DISPOSITIVOS_M%C3%93VILES/Code/proyectoFinalPrograMovil/Progra%20M%C3%B3vil%20Arquitectura%20Limpia%20LAST.md).

---

## 1. Estructura de Referencia (Esperada)

De acuerdo a la especificación, cada feature debe estructurarse estrictamente bajo el siguiente árbol de paquetes (pudiendo obviarse carpetas si la feature no las necesita):

*   **`presentation`**
    *   `screen/`: Pantallas principales (`@Composable fun SomeScreen`).
    *   `state/`: Clases de estado, eventos y efectos:
        *   `SomeUiState` (Data Class)
        *   `SomeEvent` (Interface/Sealed class)
        *   `SomeEffect` (Interface/Sealed class)
    *   `composable/`: Componentes Compose reutilizables a nivel de pantalla.
    *   `viewmodel/`: Clases `ViewModel` de la feature.
*   **`domain`**
    *   `model/`: Clases de datos de negocio puros (`Data Class`).
    *   `repository/`: Interfaces de repositorios (contratos).
    *   `usecase/`: Casos de uso específicos (lógica de negocio).
    *   `vo/`: Objetos de valor (`value class`).
*   **`data`**
    *   `repository/`: Implementación de los repositorios.
    *   `datasource/`: Interfaces de origen de datos (`SomeRemoteDatasource`, `SomeLocalDatasource`).
    *   `dto/`: Objetos de transferencia de datos de red (`Data Class` serializables).
    *   `entity/`: Objetos de persistencia de base de datos local.
    *   `mapper/`: Mapeadores entre DTO/Entity y modelos de dominio.
    *   `service/`: Clases de servicios específicos (como Ktor HTTP o implementaciones de DataSources).
    *   `dao/`: Interfaces DAO de Room local.

---

## 2. Inconsistencias Detectadas por Feature

A continuación se detallan las desviaciones de la estructura Clean Architecture encontradas en cada una de las carpetas del código común (`commonMain/kotlin/com/ucb/proyectofinal`):

### 🚫 A. Feature: `auth`
*   **Inconsistencia 1 (Violación de Capas):** La clase multiplataforma `FirebaseAuthDataSource` y sus platform actuals se definen en el paquete de dominio:
    *   [FirebaseAuthDataSource.kt (commonMain)](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACI%C3%93N_DE_DISPOSITIVOS_M%C3%93VILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/auth/domain/repository/FirebaseAuthDataSource.kt)
    *   `actual` en `androidMain/` e `iosMain/` en el mismo paquete.
    *   *Razón:* Las fuentes de datos de infraestructura (como Firebase) pertenecen estrictamente a la capa de **`data`**. El dominio no debe contener referencias a clases de infraestructura o plataformas específicas.
    *   *Corrección:* Mover `FirebaseAuthDataSource.kt` (expect/actual) a `auth/data/datasource/`.
*   **Inconsistencia 2 (Modelo en dominio incorrecto):** La clase `AuthRemoteUser` está declarada en el mismo archivo que `FirebaseAuthDataSource`.
    *   *Razón:* Es una entidad de transferencia (DTO) del servicio remoto.
    *   *Corrección:* Moverla a `auth/data/dto/AuthRemoteUser.kt`.
*   **Inconsistencia 3 (Ubicación de Eventos/Efectos):** Se crearon carpetas separadas `effect/` e `intent/` bajo `presentation/` para guardar los efectos y los intents:
    *   [AuthEffect.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACI%C3%93N_DE_DISPOSITIVOS_M%C3%93VILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/auth/presentation/effect/AuthEffect.kt)
    *   [AuthIntent.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACI%C3%93N_DE_DISPOSITIVOS_M%C3%93VILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/auth/presentation/intent/AuthIntent.kt)
    *   *Razón:* La guía establece que todos los objetos de estado, efectos y eventos deben agruparse en la carpeta **`presentation/state/`**. Además, la nomenclatura estándar de la guía usa `Event` en lugar de `Intent`.
    *   *Corrección:* Mover ambos archivos a `auth/presentation/state/` y renombrar `AuthIntent` a `AuthEvent`.
*   **Inconsistencia 4 (Mappers acoplados):** Los métodos de mapeo (`toDomainUser` y `toEntity`) están declarados de forma privada dentro de la clase de repositorio:
    *   [AuthRepositoryImpl.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACI%C3%93N_DE_DISPOSITIVOS_M%C3%93VILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/auth/data/repository/AuthRepositoryImpl.kt)
    *   *Razón:* Deben estructurarse fuera de la clase repositorio en la subcarpeta designada.
    *   *Corrección:* Extraer las extensiones de mapeo a un archivo `UserMapper.kt` bajo `auth/data/mapper/`.

---

### 🚫 B. Feature: `lists`
*   **Inconsistencia 1 (Violación de Capas - Datasource):** La clase multiplataforma `FirebaseRealtimeListsDataSource` está colocada en la capa de dominio:
    *   [FirebaseRealtimeListsDataSource.kt (commonMain)](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACI%C3%93N_DE_DISPOSITIVOS_M%C3%93VILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/lists/domain/repository/FirebaseRealtimeListsDataSource.kt)
    *   *Razón:* Es infraestructura de persistencia remota.
    *   *Corrección:* Mover el expect/actual a `lists/data/datasource/`.
*   **Inconsistencia 2 (Ubicación de Eventos/Efectos):** Se crearon carpetas separadas `effect/` e `intent/` bajo `presentation/` para las múltiples pantallas de listas (AddItem, ContentLists, EditList, ListDetail).
    *   *Razón:* Violación de la estructura unificada de `presentation/state/` y uso del término `Intent` en lugar de `Event`.
    *   *Corrección:* Mover los archivos de `lists/presentation/effect/` e `intent/` a `lists/presentation/state/` (ej. `AddItemEvent.kt`, `AddItemEffect.kt`, etc.).
*   **Inconsistencia 3 (Fuga de archivos de base de datos a `core`):** Las entidades de Room y DAOs específicos de listas están ubicados en la carpeta común `core/data/db/`:
    *   [ContentListDao.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACI%C3%93N_DE_DISPOSITIVOS_M%C3%93VILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/core/data/db/ContentListDao.kt)
    *   [ContentListEntity.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACI%C3%93N_DE_DISPOSITIVOS_M%C3%93VILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/core/data/db/ContentListEntity.kt)
    *   [ContentItemDao.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACI%C3%93N_DE_DISPOSITIVOS_M%C3%93VILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/core/data/db/ContentItemDao.kt)
    *   [ContentItemEntity.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACI%C3%93N_DE_DISPOSITIVOS_M%C3%93VILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/core/data/db/ContentItemEntity.kt)
    *   *Razón:* Cada feature debe auto-contener su lógica de persistencia. La base de datos general `AppDatabase` en `core` puede importarlas, pero las definiciones de DAO y Entity deben vivir dentro de la feature correspondiente.
    *   *Corrección:* Mover las entidades a `lists/data/entity/` y los DAOs a `lists/data/dao/`.
*   **Inconsistencia 4 (Mappers ubicados en `core`):** El archivo con las funciones de extensión de mapeo de listas está en la carpeta de base de datos de core:
    *   [EntityMappers.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACI%C3%93N_DE_DISPOSITIVOS_M%C3%93VILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/core/data/db/EntityMappers.kt)
    *   *Razón:* Deben residir en el paquete de mapeo de la feature.
    *   *Corrección:* Mover `EntityMappers.kt` a `lists/data/mapper/ListMappers.kt`.

---

### 🚫 C. Feature: `explore`
*   **Inconsistencia 1 (Salto de capa - Casos de Uso):** [ExploreViewModel.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACI%C3%93N_DE_DISPOSITIVOS_M%C3%93VILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/explore/presentation/viewmodel/ExploreViewModel.kt) depende directamente del repositorio `ContentListRepository` de la feature de listas.
    *   *Razón:* Los ViewModel deben orquestar la lógica a través de casos de uso (Domain UseCases).
    *   *Corrección:* Implementar y usar casos de uso como `GetPublicListsUseCase` y `ToggleFavoriteUseCase` dentro de la feature.
*   **Inconsistencia 2 (Ausencia de Estructura de Estados MVI/MVVM):** No existen archivos ni subcarpetas para Eventos y Efectos en la capa de presentación (carece de `ExploreEvent` y `ExploreEffect`).
    *   *Corrección:* Si la pantalla lo requiere en el futuro para eventos o errores asíncronos, crear el paquete `explore/presentation/state/` y declarar las interfaces correspondientes.

---

### 🚫 D. Feature: `favorites`
*   **Inconsistencia 1 (Ubicación de UI State):** El `FavoritesUiState` está declarado inline en el mismo archivo que el ViewModel:
    *   [FavoritesViewModel.kt (Líneas 15-19)](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACI%C3%93N_DE_DISPOSITIVOS_M%C3%93VILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/favorites/presentation/viewmodel/FavoritesViewModel.kt#L15-L19)
    *   *Razón:* Desvía la convención de subcarpetas obligatorias de estados.
    *   *Corrección:* Mover `FavoritesUiState` a `favorites/presentation/state/FavoritesUiState.kt`.
*   **Inconsistencia 2 (Salto de capa - Casos de Uso):** `FavoritesViewModel` inyecta directamente `ContentListRepository`.
    *   *Razón:* Bypassa la capa de UseCases.
    *   *Corrección:* Inyectar un caso de uso (ej. `GetFavoritesUseCase` o unificarlos).

---

### 🚫 E. Feature: `maintenance`
*   **Inconsistencia 1 (Falta de abstracción en Dominio):** La clase `RemoteConfigRepository` se declara directamente como `expect class` en dominio:
    *   [RemoteConfigRepository.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACI%C3%93N_DE_DISPOSITIVOS_M%C3%93VILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/maintenance/domain/repository/RemoteConfigRepository.kt)
    *   *Razón:* El dominio debe tener una `interface` limpia. El acoplamiento a Firebase Remote Config es un detalle de implementación (capa data).
    *   *Corrección:* Convertir `RemoteConfigRepository` a una `interface` en dominio. Definir el `expect class RemoteConfigDataSource` en `data/datasource/` y crear una implementación `RemoteConfigRepositoryImpl` en `data/repository/`.
*   **Inconsistencia 2 (Error de Mismatch de Paquetes en Múltiples Directorios):** Las implementaciones `actual class RemoteConfigRepository` en `androidMain/` y `iosMain/` están físicamente en carpetas `maintenance/data/repository/` pero declaran pertenecer al paquete `com.ucb.proyectofinal.maintenance.domain.repository`.
    *   *Razón:* Hay una discrepancia física-lógica de paquetes.
    *   *Corrección:* Una vez resuelto el punto anterior (usando interfaz en dominio e implementación en data), alinear la ubicación física en `data/repository` con la declaración del paquete Kotlin.
*   **Inconsistencia 3 (Ausencia de UseCases):** `MaintenanceViewModel` inyecta directamente el repositorio en lugar de usar UseCases.
    *   *Corrección:* Crear `ObserveMaintenanceUseCase` y `ObserveVideogameCategoryUseCase` en `maintenance/domain/usecase/`.

---

### 🚫 F. Feature: `onboarding`
*   **Inconsistencia 1 (Archivos directamente en data):** El archivo [DeviceLanguage.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACI%C3%93N_DE_DISPOSITIVOS_M%C3%93VILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/onboarding/data/DeviceLanguage.kt) (`expect fun getDeviceLanguage()`) está suelto en la carpeta raíz `data/`.
    *   *Razón:* Desvía la estructura de subcarpetas permitidas en la capa de datos.
    *   *Corrección:* Moverlo a `onboarding/data/datasource/` o `onboarding/data/service/`.
*   **Inconsistencia 2 (Ubicación de DataSources):** La clase `OnboardingPreferences` se ubica en un paquete personalizado `onboarding/data/local/`:
    *   [OnboardingPreferences.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACI%C3%93N_DE_DISPOSITIVOS_M%C3%93VILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/onboarding/data/local/OnboardingPreferences.kt)
    *   *Razón:* Debe estar dentro de `data/datasource/` como origen de datos local.
    *   *Corrección:* Mover a `onboarding/data/datasource/` (y opcionalmente renombrar a `OnboardingLocalDataSource`).
*   **Inconsistencia 3 (Ausencia de UseCases):** `OnboardingViewModel` utiliza directamente `OnboardingPreferences` y `RemoteConfigRepository` omitiendo casos de uso.
    *   *Corrección:* Crear `IsOnboardingCompletedUseCase` y `CompleteOnboardingUseCase`.

---

### 🚫 G. Feature: `profile`
*   **Inconsistencia 1 (Ubicación de Eventos/Efectos):** Posee carpetas separadas `effect/` y `intent/` bajo `profile/presentation/`:
    *   [ProfileEffect.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACI%C3%93N_DE_DISPOSITIVOS_M%C3%93VILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/profile/presentation/effect/ProfileEffect.kt)
    *   [ProfileIntent.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACI%C3%93N_DE_DISPOSITIVOS_M%C3%93VILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/profile/presentation/intent/ProfileIntent.kt)
    *   *Corrección:* Mover ambos a `profile/presentation/state/` (y renombrar `ProfileIntent` a `ProfileEvent`).

---

### 🚫 H. Feature: `settings`
*   **Inconsistencia 1 (Archivos directamente en data):** [AppSettingsStore.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACI%C3%93N_DE_DISPOSITIVOS_M%C3%93VILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/settings/data/AppSettingsStore.kt) está suelto en la carpeta raíz `data/`.
    *   *Corrección:* Mover a `settings/data/datasource/` o `settings/data/service/`.
*   **Inconsistencia 2 (Ubicación de código de plataforma):** La función `applyLocale` está en una carpeta no estándar llamada `platform/`:
    *   [ApplyLocale.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACI%C3%93N_DE_DISPOSITIVOS_M%C3%93VILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/settings/platform/ApplyLocale.kt)
    *   *Corrección:* Mover el expect/actual a `settings/data/service/` o `settings/data/datasource/`.
*   **Inconsistencia 3 (Ubicación de Eventos/Efectos):** Posee carpetas separadas `effect/` e `intent/` bajo `settings/presentation/`.
    *   *Corrección:* Mover a `settings/presentation/state/` (renombrando `SettingsIntent` a `SettingsEvent`).
*   **Inconsistencia 4 (Falta de abstracción de datos):** `SettingsViewModel` interactúa directamente con `AppSettingsStore` (datos) y llama a `applyLocale` directamente.
    *   *Corrección:* Definir un repositorio en el dominio y usar casos de uso como `ChangeLanguageUseCase` y `ToggleThemeUseCase`.

---

### 🚫 I. Feature: `notification`
*   **Inconsistencia 1 (Ausencia total de arquitectura por capas):** La feature solo tiene un archivo suelto a nivel de paquete raíz:
    *   [FirebaseToken.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACI%C3%93N_DE_DISPOSITIVOS_M%C3%93VILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/notification/FirebaseToken.kt)
    *   *Razón:* Falta estructurar según Clean Architecture.
    *   *Corrección:* Crear la estructura `notification/data/datasource/` y mover allí el expect/actual `getToken()`.

---

### 🚫 J. Lógica Compartida: `core/data/db/`
*   **Inconsistencia 1 (Fuga de archivos de base de datos):** Los archivos relacionados con el usuario están en core:
    *   [UserDao.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACI%C3%93N_DE_DISPOSITIVOS_M%C3%93VILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/core/data/db/UserDao.kt)
    *   [UserEntity.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACI%C3%93N_DE_DISPOSITIVOS_M%C3%93VILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/core/data/db/UserEntity.kt)
    *   *Razón:* Solo los consume la feature `auth` para persistir localmente el usuario activo.
    *   *Corrección:* Mover a `auth/data/dao/UserDao.kt` y `auth/data/entity/UserEntity.kt`.
*   **Inconsistencia 2 (Código Muerto / Incompleto):** Existen archivos de base de datos para una entidad de Tareas (Todo) que no se usa en el proyecto:
    *   [TodoDao.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACI%C3%93N_DE_DISPOSITIVOS_M%C3%93VILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/core/data/db/TodoDao.kt)
    *   [TodoEntity.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACI%C3%93N_DE_DISPOSITIVOS_M%C3%93VILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/core/data/db/TodoEntity.kt)
    *   *Corrección:* Eliminar estos archivos si la funcionalidad de "tareas" no es requerida, o encapsularlos en una feature `todo/` si se va a implementar.

---

### 🚫 K. Carpeta Huérfana: `feature`
*   **Inconsistencia 1:** Existe un directorio completamente vacío en el paquete raíz:
    *   Ruta: `com/ucb/proyectofinal/feature/firebase/...`
    *   *Razón:* Directorio huérfano sobrante que no contiene ningún archivo Kotlin.
    *   *Corrección:* Eliminar la carpeta `feature/`.

---

## 3. Resumen de Desviaciones Críticas y de Alta Prioridad

A continuación se agrupan los problemas por niveles de prioridad para orientar los esfuerzos de corrección de forma ordenada:

| Prioridad | Tipo de Problema | Descripción | Features Afectadas |
| :--- | :--- | :--- | :--- |
| 🔥 **Alta** | **Violación de Capas** | Fuentes de datos de plataforma (Firebase) y lógica de persistencia local puestas en capas de Dominio o Core. | `auth`, `lists`, `maintenance`, `core/data/db/` |
| 🟨 **Media** | **Salto de Capa (ViewModels)** | Los ViewModels inyectan y llaman repositorios o almacenes de datos directamente omitiendo los Casos de Uso. | `explore`, `favorites`, `maintenance`, `onboarding`, `settings` |
| 🟨 **Media** | **Falta de Estandarización** | Estructuras `intent/` y `effect/` separadas en lugar de estar unificadas dentro de `presentation/state/`. | `auth`, `lists`, `profile`, `settings` |
| 🟦 **Baja** | **Estructura Huérfana / Sucia** | Archivos sueltos en carpetas raíz de capas, carpetas vacías o código muerto. | `onboarding`, `settings`, `notification`, `feature/`, `core/data/db/` (Todo) |

---

## 4. Guía de Corrección Paso a Paso

Para reorganizar el proyecto de forma segura sin romper la compilación multiplataforma, se sugiere la siguiente secuencia:

1.  **Reubicar clases de datos locales/remotas a `data/`**: Mover `FirebaseAuthDataSource` y `FirebaseRealtimeListsDataSource` a `data/datasource/` y actualizar las importaciones en `RepositoryModule.kt`.
2.  **Migrar DAOs y Entities de `core` a sus Features**: Mover `UserDao/UserEntity` a `auth` y `ContentListDao/ContentListEntity/ContentItemDao/ContentItemEntity` a `lists`. Asegurarse de importar las entidades correctas en el archivo [AppRoomDatabase.kt](file:///c:/Users/Luz/Projects/9no_semestre/PROGRAMACI%C3%93N_DE_DISPOSITIVOS_M%C3%93VILES/Code/proyectoFinalPrograMovil/composeApp/src/commonMain/kotlin/com/ucb/proyectofinal/core/data/db/AppRoomDatabase.kt).
3.  **Mapear correctamente**: Extraer funciones de mapeo de `AuthRepositoryImpl` e `EntityMappers.kt` a carpetas `data/mapper/` de sus features.
4.  **Refactorizar los paquetes de Presentación**: Mover los contenidos de las carpetas `intent` y `effect` a la carpeta `state` de cada feature. Cambiar el sufijo `Intent` a `Event` y actualizar las llamadas y referencias en los `ViewModel` y las pantallas `Compose`.
5.  **Añadir Casos de Uso faltantes**: En features secundarias (`explore`, `favorites`, `settings`, `maintenance`), crear las clases de casos de uso requeridas y reemplazar la inyección directa de repositorios en los `ViewModel` mediante `UseCaseModule.kt`.
6.  **Limpiar Directorios**: Eliminar la carpeta `feature` y los archivos relacionados con `TodoEntity/TodoDao` si son obsoletos.
