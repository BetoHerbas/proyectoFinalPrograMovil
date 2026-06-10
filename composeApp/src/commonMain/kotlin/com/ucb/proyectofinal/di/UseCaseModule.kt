package com.ucb.proyectofinal.di

import com.ucb.proyectofinal.auth.domain.usecase.GetCurrentUserUseCase
import com.ucb.proyectofinal.auth.domain.usecase.LoginUseCase
import com.ucb.proyectofinal.auth.domain.usecase.LogoutUseCase
import com.ucb.proyectofinal.auth.domain.usecase.RegisterUseCase
import com.ucb.proyectofinal.lists.domain.usecase.AddItemToListUseCase
import com.ucb.proyectofinal.lists.domain.usecase.CreateContentListUseCase
import com.ucb.proyectofinal.lists.domain.usecase.DeleteItemUseCase
import com.ucb.proyectofinal.lists.domain.usecase.DeleteListUseCase
import com.ucb.proyectofinal.lists.domain.usecase.GetContentListsUseCase
import com.ucb.proyectofinal.lists.domain.usecase.GetListItemsUseCase
import com.ucb.proyectofinal.lists.domain.usecase.RateItemUseCase
import com.ucb.proyectofinal.lists.domain.usecase.SearchCatalogUseCase
import com.ucb.proyectofinal.lists.domain.usecase.ToggleItemSeenUseCase
import com.ucb.proyectofinal.lists.domain.usecase.UpdateListUseCase
import com.ucb.proyectofinal.lists.domain.usecase.GetPublicListsUseCase
import com.ucb.proyectofinal.lists.domain.usecase.GetFavoritesUseCase
import com.ucb.proyectofinal.lists.domain.usecase.AddFavoriteUseCase
import com.ucb.proyectofinal.lists.domain.usecase.RemoveFavoriteUseCase
import com.ucb.proyectofinal.settings.domain.usecase.ChangeLanguageUseCase
import com.ucb.proyectofinal.settings.domain.usecase.GetLanguageUseCase
import com.ucb.proyectofinal.settings.domain.usecase.GetThemeUseCase
import com.ucb.proyectofinal.settings.domain.usecase.SaveThemeUseCase
import com.ucb.proyectofinal.maintenance.domain.usecase.ObserveMaintenanceUseCase
import com.ucb.proyectofinal.onboarding.domain.usecase.GetOnboardingSlidesUseCase
import com.ucb.proyectofinal.onboarding.domain.usecase.CompleteOnboardingUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }
    factory { GetContentListsUseCase(get()) }
    factory { CreateContentListUseCase(get()) }
    factory { GetListItemsUseCase(get()) }
    factory { AddItemToListUseCase(get()) }
    factory { SearchCatalogUseCase(get()) }
    factory { ToggleItemSeenUseCase(get()) }
    factory { RateItemUseCase(get()) }
    factory { DeleteListUseCase(get()) }
    factory { DeleteItemUseCase(get()) }
    factory { UpdateListUseCase(get()) }
    factory { GetPublicListsUseCase(get()) }
    factory { GetFavoritesUseCase(get()) }
    factory { AddFavoriteUseCase(get()) }
    factory { RemoveFavoriteUseCase(get()) }
    // Settings
    factory { GetThemeUseCase(get()) }
    factory { SaveThemeUseCase(get()) }
    factory { GetLanguageUseCase(get()) }
    factory { ChangeLanguageUseCase(get()) }
    
    // Maintenance
    factory { ObserveMaintenanceUseCase(get()) }

    // Onboarding
    factory { GetOnboardingSlidesUseCase(get()) }
    factory { CompleteOnboardingUseCase(get()) }
}

