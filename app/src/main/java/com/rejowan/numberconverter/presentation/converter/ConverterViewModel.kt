package com.rejowan.numberconverter.presentation.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejowan.numberconverter.domain.model.HistoryItem
import com.rejowan.numberconverter.domain.model.NumberBase
import com.rejowan.numberconverter.domain.usecase.converter.ConvertNumberUseCase
import com.rejowan.numberconverter.domain.usecase.converter.FormatOutputUseCase
import com.rejowan.numberconverter.domain.usecase.converter.ValidateInputUseCase
import com.rejowan.numberconverter.domain.usecase.history.DeleteHistoryUseCase
import com.rejowan.numberconverter.domain.usecase.history.GetHistoryUseCase
import com.rejowan.numberconverter.domain.usecase.history.SaveConversionUseCase
import com.rejowan.numberconverter.domain.usecase.history.ToggleBookmarkUseCase
import com.rejowan.numberconverter.presentation.converter.state.ConverterUiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class ConverterViewModel(
    private val convertNumberUseCase: ConvertNumberUseCase,
    private val validateInputUseCase: ValidateInputUseCase,
    private val formatOutputUseCase: FormatOutputUseCase,
    private val saveConversionUseCase: SaveConversionUseCase,
    private val converterRepository: com.rejowan.numberconverter.domain.repository.ConverterRepository,
    private val getHistoryUseCase: GetHistoryUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    private val deleteHistoryUseCase: DeleteHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConverterUiState())
    val uiState: StateFlow<ConverterUiState> = _uiState.asStateFlow()

    // History state
    val historyItems: StateFlow<List<HistoryItem>> = getHistoryUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val bookmarkedItems: StateFlow<List<HistoryItem>> = getHistoryUseCase.getBookmarked()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _inputFlow = MutableStateFlow("")

    init {
        // Observe input changes with debouncing
        viewModelScope.launch {
            _inputFlow
                .debounce(300) // Wait 300ms after user stops typing
                .collect { input ->
                    if (input.isNotEmpty()) {
                        convertNumber()
                    } else {
                        _uiState.update { it.copy(output = "", errorMessage = null, validationError = null, explanation = null) }
                    }
                }
        }
    }

    fun onInputChanged(input: String) {
        _uiState.update { it.copy(input = input, validationError = null, explanation = null) }
        _inputFlow.value = input
    }

    fun onFromBaseChanged(base: NumberBase) {
        _uiState.update { it.copy(fromBase = base) }
        if (_uiState.value.input.isNotEmpty()) {
            convertNumber()
        }
    }

    fun onToBaseChanged(base: NumberBase) {
        _uiState.update { it.copy(toBase = base) }
        if (_uiState.value.input.isNotEmpty()) {
            convertNumber()
        }
    }

    fun swapBases() {
        val currentFrom = _uiState.value.fromBase
        val currentTo = _uiState.value.toBase
        val currentOutput = _uiState.value.output

        _uiState.update {
            it.copy(
                fromBase = currentTo,
                toBase = currentFrom,
                input = currentOutput,
                output = _uiState.value.input,
                explanation = null // Clear old explanation
            )
        }

        // Trigger new conversion with swapped values
        _inputFlow.value = currentOutput
    }

    fun clearInput() {
        _uiState.update {
            it.copy(
                input = "",
                output = "",
                errorMessage = null,
                validationError = null,
                explanation = null
            )
        }
        _inputFlow.value = ""
    }

    private fun convertNumber() {
        val currentState = _uiState.value

        // Validate input first
        val validationResult = validateInputUseCase(currentState.input, currentState.fromBase)
        if (!validationResult.isValid) {
            _uiState.update {
                it.copy(
                    validationError = validationResult.errorMessage,
                    output = "",
                    errorMessage = null
                )
            }
            return
        }

        // Perform conversion
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, validationError = null) }

            convertNumberUseCase(
                input = currentState.input,
                fromBase = currentState.fromBase,
                toBase = currentState.toBase
            ).fold(
                onSuccess = { result ->
                    val formattedOutput = formatOutputUseCase(result.output)
                    _uiState.update {
                        it.copy(
                            output = formattedOutput,
                            isLoading = false,
                            errorMessage = null
                        )
                    }

                    // Fetch explanation
                    viewModelScope.launch {
                        converterRepository.explain(
                            input = currentState.input,
                            fromBase = currentState.fromBase,
                            toBase = currentState.toBase
                        ).fold(
                            onSuccess = { explanation ->
                                _uiState.update { it.copy(explanation = explanation) }
                            },
                            onFailure = { /* Silently fail, explanation is optional */ }
                        )
                    }

                    // Save to history
                    viewModelScope.launch {
                        saveConversionUseCase(
                            HistoryItem(
                                input = currentState.input,
                                output = formattedOutput,
                                fromBase = currentState.fromBase,
                                toBase = currentState.toBase,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            errorMessage = error.message ?: "Conversion failed",
                            output = "",
                            isLoading = false
                        )
                    }
                }
            )
        }
    }

    // History actions
    fun restoreFromHistory(item: HistoryItem) {
        _uiState.update {
            it.copy(
                input = item.input,
                output = item.output,
                fromBase = item.fromBase,
                toBase = item.toBase
            )
        }
        _inputFlow.value = item.input
    }

    fun toggleBookmark(id: Long) {
        viewModelScope.launch {
            toggleBookmarkUseCase(id)
        }
    }

    fun deleteHistoryItem(item: HistoryItem) {
        viewModelScope.launch {
            deleteHistoryUseCase(item)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            deleteHistoryUseCase.deleteAll()
        }
    }
}
