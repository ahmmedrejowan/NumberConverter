package com.rejowan.numberconverter.di

import com.rejowan.numberconverter.data.local.database.AppDatabase
import com.rejowan.numberconverter.data.local.datastore.PreferencesManager
import com.rejowan.numberconverter.data.repository.ConverterRepositoryImpl
import com.rejowan.numberconverter.data.repository.HistoryRepositoryImpl
import com.rejowan.numberconverter.domain.repository.ConverterRepository
import com.rejowan.numberconverter.domain.repository.HistoryRepository
import com.rejowan.numberconverter.domain.usecase.converter.ConvertNumberUseCase
import com.rejowan.numberconverter.domain.usecase.converter.FormatOutputUseCase
import com.rejowan.numberconverter.domain.usecase.converter.ValidateInputUseCase
import com.rejowan.numberconverter.domain.usecase.history.DeleteHistoryUseCase
import com.rejowan.numberconverter.domain.usecase.history.GetHistoryUseCase
import com.rejowan.numberconverter.domain.usecase.history.SaveConversionUseCase
import com.rejowan.numberconverter.domain.usecase.history.ToggleBookmarkUseCase
import com.rejowan.numberconverter.domain.usecase.settings.GetSettingsUseCase
import com.rejowan.numberconverter.domain.usecase.settings.UpdateSettingUseCase
import com.rejowan.numberconverter.presentation.converter.ConverterViewModel
import com.rejowan.numberconverter.presentation.home.HomeViewModel
import com.rejowan.numberconverter.presentation.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // DataStore
    single { PreferencesManager(androidContext()) }

    // Database
    single { AppDatabase.getInstance(androidContext()) }
    single { get<AppDatabase>().historyDao() }

    // Repositories
    single<ConverterRepository> { ConverterRepositoryImpl(get()) }
    single<HistoryRepository> { HistoryRepositoryImpl(get()) }

    // Use Cases - Converter
    factory { ConvertNumberUseCase(get()) }
    factory { ValidateInputUseCase() }
    factory { FormatOutputUseCase() }

    // Use Cases - History
    factory { SaveConversionUseCase(get()) }
    factory { GetHistoryUseCase(get()) }
    factory { DeleteHistoryUseCase(get()) }
    factory { ToggleBookmarkUseCase(get()) }

    // Use Cases - Settings
    factory { GetSettingsUseCase(get()) }
    factory { UpdateSettingUseCase(get()) }

    // ViewModels
    viewModel { ConverterViewModel(get(), get(), get(), get()) }
    viewModel { HomeViewModel() }
    viewModel { SettingsViewModel(get(), get(), get()) }
    // viewModel { LearnViewModel(get(), get()) }
    // viewModel { LessonViewModel(get(), get()) }
    // viewModel { PracticeViewModel(get(), get()) }
}
