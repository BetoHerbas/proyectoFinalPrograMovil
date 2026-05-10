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
}
