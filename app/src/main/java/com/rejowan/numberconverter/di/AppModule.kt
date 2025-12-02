package com.rejowan.numberconverter.di

import com.rejowan.numberconverter.data.local.datastore.PreferencesManager
import com.rejowan.numberconverter.data.repository.ConverterRepositoryImpl
import com.rejowan.numberconverter.domain.repository.ConverterRepository
import com.rejowan.numberconverter.domain.usecase.converter.ConvertNumberUseCase
import com.rejowan.numberconverter.domain.usecase.converter.FormatOutputUseCase
import com.rejowan.numberconverter.domain.usecase.converter.ValidateInputUseCase
import com.rejowan.numberconverter.presentation.converter.ConverterViewModel
import com.rejowan.numberconverter.presentation.home.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // DataStore
    single { PreferencesManager(androidContext()) }

    // Database
    // single { AppDatabase.getInstance(get()) }
    // single { get<AppDatabase>().historyDao() }
    // single { get<AppDatabase>().progressDao() }

    // Repositories
    single<ConverterRepository> { ConverterRepositoryImpl(get()) }
    // single<HistoryRepository> { HistoryRepositoryImpl(get()) }
    // single<LessonRepository> { LessonRepositoryImpl(get()) }
    // single<ProgressRepository> { ProgressRepositoryImpl(get()) }

    // Use Cases - Converter
    factory { ConvertNumberUseCase(get()) }
    factory { ValidateInputUseCase() }
    factory { FormatOutputUseCase() }

    // Use Cases - History
    // factory { SaveConversionUseCase(get()) }
    // factory { GetHistoryUseCase(get()) }

    // ViewModels
    viewModel { ConverterViewModel(get(), get(), get()) }
    viewModel { HomeViewModel() }
    // viewModel { LearnViewModel(get(), get()) }
    // viewModel { LessonViewModel(get(), get()) }
    // viewModel { PracticeViewModel(get(), get()) }
    // viewModel { SettingsViewModel(get()) }
}
