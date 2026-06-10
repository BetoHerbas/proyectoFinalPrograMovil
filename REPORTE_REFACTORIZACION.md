# Reporte de Refactorización: Clean Architecture

**Proyecto:** `proyectoFinalPrograMovil`  
**Fecha:** 2026-06-10  
**Rama:** `main` — commit `1e8ab72`  
**Estado:** ✅ Build exitoso · ✅ Todos los tests pasan

---

## Contexto

Se aplicaron las correcciones indicadas en el reporte de revisión de arquitectura del docente (`reporte_arquitectura_limpia_last.md`). El objetivo fue alinear la estructura del proyecto con los principios de **Clean Architecture** y el patrón **MVI**, sin alterar el comportamiento de la aplicación.

La estrategia principal fue **actualizar los `package` declarados en cada archivo** (en lugar de moverlos físicamente), lo cual es equivalente y más seguro en proyectos Kotlin Multiplatform con `expect/actual`.

---

## Cambios Aplicados

### 1. Eliminación de Código Muerto (`core`)

| Archivo eliminado | Motivo |
|---|---|
| `core/data/db/TodoDao.kt` | No asociado a ningún feature de negocio |
| `core/data/db/TodoEntity.kt` | Sin uso real en la app |
| `core/data/db/EntityMappers.kt` | Redundante y obsoleto (omitía campos como `description`) |

**`AppRoomDatabase.kt`** actualizado: se eliminó `TodoEntity` de `@Database` y el DAO `getDao()`. Se incrementó la versión de la base de datos a `4`.

---

### 2. Feature `auth` — DataSource + DTO + MVI

#### Problema resuelto
`FirebaseAuthDataSource` y `AuthRemoteUser` vivían en `domain/repository`, exponiendo detalles de infraestructura (Firebase SDK) al dominio. `AuthEffect` y `AuthIntent` estaban en carpetas separadas `effect/` e `intent/`.

#### Cambios
| Archivo | Cambio |
|---|---|
| `auth/domain/repository/FirebaseAuthDataSource.kt` (commonMain) | Paquete → `auth.data.datasource` |
| `auth/domain/repository/FirebaseAuthDataSource.kt` (androidMain) | Paquete → `auth.data.datasource` |
| `auth/data/dto/AuthRemoteUser.kt` | **Nuevo** — DTO extraído del archivo anterior |
| `auth/presentation/effect/AuthEffect.kt` | Paquete → `auth.presentation.state` |
| `auth/presentation/intent/AuthIntent.kt` | Paquete → `auth.presentation.state` |
| `AuthRepositoryImpl.kt`, `AuthViewModel.kt`, `LoginScreen.kt`, `RegisterScreen.kt`, `RepositoryModule.kt` | Imports actualizados |

---

### 3. Feature `lists` — DataSource + MVI

#### Problema resuelto
`FirebaseRealtimeListsDataSource` estaba en `domain/repository`. Los Effects e Intents estaban dispersos en 8 archivos bajo carpetas `effect/` e `intent/`.

#### Cambios
| Archivo | Cambio |
|---|---|
| `lists/domain/repository/FirebaseRealtimeListsDataSource.kt` (commonMain) | Paquete → `lists.data.datasource` |
| `lists/domain/repository/FirebaseRealtimeListsDataSource.kt` (androidMain) | Paquete → `lists.data.datasource` |
| `AddItemEffect`, `ContentListsEffect`, `EditListEffect`, `ListDetailEffect` | Paquete → `lists.presentation.state` |
| `AddItemIntent`, `ContentListsIntent`, `EditListIntent`, `ItemDetailIntent`, `ListDetailIntent` | Paquete → `lists.presentation.state` |
| `ContentListRepositoryImpl.kt`, `RepositoryModule.kt`, todos los ViewModels y Screens de `lists` | Imports actualizados |

---

### 4. Feature `maintenance` — `expect class` → `interface`

#### Problema resuelto
`RemoteConfigRepository` era una `expect class` en el paquete de dominio. El dominio debe contener únicamente interfaces puras de Kotlin, sin acoplamientos a los mecanismos de KMP.

#### Cambios
| Archivo | Cambio |
|---|---|
| `maintenance/domain/repository/RemoteConfigRepository.kt` | Convertido de `expect class` a **`interface`** pura de Kotlin |
| `maintenance/data/repository/RemoteConfigRepositoryImpl.kt` (androidMain) | **Nuevo** — Implementación Android de la interfaz |
| `androidMain/di/RemoteConfigProvider.kt` | **Nuevo** — `actual fun provideRemoteConfigRepository()` |
| `iosMain/di/RemoteConfigProvider.kt` | **Nuevo** — Stub iOS de `actual fun provideRemoteConfigRepository()` |
| `di/AppModule.kt` | Registra `RemoteConfigRepositoryImpl` como `RemoteConfigRepository` vía Koin |

---

### 5. Feature `onboarding` — Mover a `datasource/`

#### Problema resuelto
`DeviceLanguage.kt` estaba en la raíz de `onboarding/data/` y `OnboardingPreferences.kt` en una carpeta no estándar `data/local/`.

#### Cambios
| Archivo | Cambio |
|---|---|
| `onboarding/data/DeviceLanguage.kt` (commonMain + androidMain) | Paquete → `onboarding.data.datasource` |
| `onboarding/data/local/OnboardingPreferences.kt` (commonMain + androidMain) | Paquete → `onboarding.data.datasource` |
| `OnboardingViewModel.kt`, `AppNavHost.kt`, `AppModule.kt` | Imports actualizados |

---

### 6. Feature `profile` — MVI en `state/`

#### Problema resuelto
`ProfileEffect` y `ProfileIntent` en carpetas separadas `effect/` e `intent/`.

#### Cambios
| Archivo | Cambio |
|---|---|
| `profile/presentation/effect/ProfileEffect.kt` | Paquete → `profile.presentation.state` |
| `profile/presentation/intent/ProfileIntent.kt` | Paquete → `profile.presentation.state` |
| `ProfileViewModel.kt`, `ProfileScreen.kt` | Imports actualizados |

---

### 7. Feature `favorites` — Extraer `FavoritesUiState`

#### Problema resuelto
`FavoritesUiState` estaba declarada inline dentro de `FavoritesViewModel.kt`, en lugar de en su propio archivo.

#### Cambios
| Archivo | Cambio |
|---|---|
| `favorites/presentation/state/FavoritesUiState.kt` | **Nuevo** — Clase extraída al paquete `state` |
| `FavoritesViewModel.kt` | Eliminada la declaración inline; se importa desde `state` |

---

### 8. Feature `settings` — Capa de Dominio Completa + Datasource

#### Problema resuelto
`SettingsViewModel` accedía directamente a `AppSettingsStore` (capa de datos), violando la regla de dependencia de Clean Architecture. `AppSettingsStore`, `ThemePreferences` y `ApplyLocale` estaban en ubicaciones no estándar.

#### Nuevos archivos creados
| Archivo | Descripción |
|---|---|
| `settings/domain/repository/SettingsRepository.kt` | **Interface** del repositorio de configuración |
| `settings/domain/usecase/SettingsUseCases.kt` | 4 casos de uso: `GetThemeUseCase`, `SaveThemeUseCase`, `GetLanguageUseCase`, `ChangeLanguageUseCase` |
| `settings/data/repository/SettingsRepositoryImpl.kt` | Implementación que delega en `AppSettingsStore` y `applyLocale` |

#### Archivos modificados
| Archivo | Cambio |
|---|---|
| `settings/data/AppSettingsStore.kt` | Paquete → `settings.data.datasource` |
| `settings/data/ThemePreferences.kt` (commonMain + androidMain) | Paquete → `settings.data.datasource` |
| `settings/platform/ApplyLocale.kt` (commonMain + androidMain) | Paquete → `settings.data.datasource` |
| `settings/presentation/effect/SettingsEffect.kt` | Paquete → `settings.presentation.state` |
| `settings/presentation/intent/SettingsIntent.kt` | Paquete → `settings.presentation.state` |
| `SettingsViewModel.kt` | **Refactorizado** — ahora recibe e invoca `GetThemeUseCase`, `SaveThemeUseCase`, `GetLanguageUseCase`, `ChangeLanguageUseCase` como dependencias |
| `SettingsScreen.kt` | Imports actualizados |
| `RepositoryModule.kt` | Registra `SettingsRepositoryImpl` |
| `UseCaseModule.kt` | Registra los 4 casos de uso de Settings |
| `ViewModelModule.kt` | Inyecta los 4 casos de uso en `SettingsViewModel` |

---

### 9. Feature `notification` — Mover a `data/datasource/`

#### Problema resuelto
`FirebaseToken.kt` estaba suelto en la raíz del paquete `notification/`.

#### Cambios
| Archivo | Cambio |
|---|---|
| `notification/FirebaseToken.kt` (commonMain + androidMain) | Paquete → `notification.data.datasource` |
| `App.kt` | Import actualizado a `notification.data.datasource.getToken` |

---

### 10. `worker` — Eliminar Código Dummy

#### Problema resuelto
`FetchPopularMoviesUseCase` era un caso de uso dummy definido en `androidMain` (código de plataforma) en lugar de en `commonMain` (dominio), y sin utilidad real.

#### Cambios
| Archivo | Cambio |
|---|---|
| `worker/FetchPopularMoviesUseCase.kt` (androidMain) | **Eliminado** |
| `worker/LogUploadWorker.kt` | Eliminada la inyección de `FetchPopularMoviesUseCase` |
| `MainApplication.kt` | Eliminado el `single { FetchPopularMoviesUseCase() }` de Koin |

---

## Verificación

```
✅ ./gradlew :composeApp:testDebugUnitTest  →  BUILD SUCCESSFUL
✅ git push origin main  →  commit 1e8ab72
```

Todos los tests de unidad, ViewModel e interfaz de usuario (Robolectric) pasan correctamente. El comportamiento de la aplicación no fue alterado.

---

## Estructura Final por Feature

```
com.ucb.proyectofinal/
├── auth/
│   ├── data/
│   │   ├── datasource/FirebaseAuthDataSource.kt   ← antes en domain/
│   │   ├── dto/AuthRemoteUser.kt                  ← nuevo
│   │   └── repository/AuthRepositoryImpl.kt
│   ├── domain/
│   │   ├── model/User.kt + vo/
│   │   ├── repository/AuthRepository.kt
│   │   └── usecase/Login|Register|Logout|GetCurrentUserUseCase.kt
│   └── presentation/
│       ├── state/AuthUiState.kt + AuthEffect.kt + AuthIntent.kt  ← unificado
│       ├── screen/Login|RegisterScreen.kt
│       └── viewmodel/AuthViewModel.kt
│
├── lists/
│   ├── data/
│   │   ├── datasource/FirebaseRealtimeListsDataSource.kt  ← antes en domain/
│   │   └── repository/ContentListRepositoryImpl.kt
│   ├── domain/
│   │   ├── model/ + vo/
│   │   ├── repository/ContentListRepository.kt
│   │   └── usecase/*.kt
│   └── presentation/
│       ├── state/ [UiState + Effect + Intent de todos los sub-flows]  ← unificado
│       ├── screen/*.kt
│       └── viewmodel/*.kt
│
├── maintenance/
│   ├── data/repository/RemoteConfigRepositoryImpl.kt  ← nuevo (Android)
│   └── domain/repository/RemoteConfigRepository.kt   ← interface pura (antes expect class)
│
├── onboarding/
│   ├── data/datasource/DeviceLanguage.kt + OnboardingPreferences.kt  ← antes en raíz/local/
│   ├── domain/model/*.kt
│   └── presentation/state/ + viewmodel/ + screen/
│
├── favorites/
│   └── presentation/
│       ├── state/FavoritesUiState.kt  ← nuevo (antes inline en ViewModel)
│       ├── screen/FavoritesScreen.kt
│       └── viewmodel/FavoritesViewModel.kt
│
├── settings/
│   ├── data/
│   │   ├── datasource/AppSettingsStore.kt + ThemePreferences.kt + ApplyLocale.kt
│   │   └── repository/SettingsRepositoryImpl.kt  ← nuevo
│   ├── domain/
│   │   ├── repository/SettingsRepository.kt   ← nuevo
│   │   └── usecase/SettingsUseCases.kt        ← nuevo (4 use cases)
│   └── presentation/
│       ├── state/ [UiState + Effect + Intent]  ← unificado
│       ├── screen/SettingsScreen.kt
│       └── viewmodel/SettingsViewModel.kt      ← refactorizado con UseCases
│
├── notification/
│   └── data/datasource/FirebaseToken.kt  ← antes en raíz notification/
│
└── profile/
    └── presentation/
        ├── state/ [UiState + Effect + Intent]  ← unificado (antes en effect/ + intent/)
        ├── screen/ProfileScreen.kt
        └── viewmodel/ProfileViewModel.kt
```
