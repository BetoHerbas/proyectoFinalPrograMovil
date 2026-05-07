package com.ucb.proyectofinal.di

import com.ucb.proyectofinal.auth.presentation.viewmodel.AuthViewModel
import com.ucb.proyectofinal.lists.presentation.viewmodel.ContentListsViewModel
import com.ucb.proyectofinal.lists.presentation.viewmodel.ListDetailViewModel
import com.ucb.proyectofinal.maintenance.presentation.viewmodel.MaintenanceViewModel
import com.ucb.proyectofinal.profile.presentation.viewmodel.ProfileViewModel
import com.ucb.proyectofinal.settings.presentation.viewmodel.SettingsViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { AuthViewModel(get(), get(), get(), get()) }
    viewModel { ContentListsViewModel(get(), get(), get()) }
    viewModel { ListDetailViewModel(get(), get(), get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get()) }
    viewModel { SettingsViewModel() }
    viewModel { MaintenanceViewModel(get()) }
}
