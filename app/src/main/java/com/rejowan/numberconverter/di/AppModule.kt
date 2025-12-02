package com.rejowan.numberconverter.di

import org.koin.dsl.module

val appModule = module {
    // DataStore
    // single { PreferencesManager(get()) }

    // Database
    // single { AppDatabase.getInstance(get()) }
    // single { get<AppDatabase>().historyDao() }
    // single { get<AppDatabase>().progressDao() }

    // Repositories
    // single<ConverterRepository> { ConverterRepositoryImpl() }
    // single<HistoryRepository> { HistoryRepositoryImpl(get()) }
    // single<LessonRepository> { LessonRepositoryImpl(get()) }
    // single<ProgressRepository> { ProgressRepositoryImpl(get()) }

    // Use Cases - Converter
    // factory { ConvertNumberUseCase(get()) }
    // factory { GenerateExplanationUseCase(get()) }
    // factory { ValidateInputUseCase() }

    // Use Cases - History
    // factory { SaveConversionUseCase(get()) }
    // factory { GetHistoryUseCase(get()) }

    // ViewModels
    // viewModel { ConverterViewModel(get(), get(), get(), get()) }
    // viewModel { LearnViewModel(get(), get()) }
    // viewModel { LessonViewModel(get(), get()) }
    // viewModel { PracticeViewModel(get(), get()) }
    // viewModel { SettingsViewModel(get()) }
    // viewModel { HomeViewModel() }
}
