package com.ucb.proyectofinal.di

import com.ucb.proyectofinal.feature.notes.data.repository.NoteRepositoryImpl
import com.ucb.proyectofinal.feature.notes.domain.repository.NoteRepository
import com.ucb.proyectofinal.feature.notes.domain.usecase.CreateNoteUseCase
import com.ucb.proyectofinal.feature.notes.domain.usecase.GetAllNotesUseCase
import com.ucb.proyectofinal.feature.notes.presentation.CreateNoteViewModel
import com.ucb.proyectofinal.feature.notes.presentation.NoteListViewModel
import com.ucb.proyectofinal.remoteconfig.MaintenanceViewModel
import com.ucb.proyectofinal.remoteconfig.RemoteConfigRepository
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Firebase Remote Config
    single { RemoteConfigRepository() }
    viewModel { MaintenanceViewModel(get()) }
}

val notesModule = module {
    // Repository
    single<NoteRepository> { NoteRepositoryImpl(get()) }

    // Use Cases
    factory { CreateNoteUseCase(get(), getOrNull()) }
    factory { GetAllNotesUseCase(get()) }

    // ViewModels
    viewModel { NoteListViewModel(get()) }
    viewModel { CreateNoteViewModel(get()) }
}

