package com.rejowan.numberconverter.presentation.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejowan.numberconverter.domain.model.HistoryItem
import com.rejowan.numberconverter.domain.model.NumberBase
import com.rejowan.numberconverter.domain.usecase.converter.ConvertNumberUseCase
import com.rejowan.numberconverter.domain.usecase.converter.FormatOutputUseCase
import com.rejowan.numberconverter.domain.usecase.converter.ValidateInputUseCase
import com.rejowan.numberconverter.domain.usecase.history.SaveConversionUseCase
import com.rejowan.numberconverter.presentation.converter.state.ConverterUiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class ConverterViewModel(
    private val convertNumberUseCase: ConvertNumberUseCase,
    private val validateInputUseCase: ValidateInputUseCase,
    private val formatOutputUseCase: FormatOutputUseCase,
    private val saveConversionUseCase: SaveConversionUseCase,
    private val converterRepository: com.rejowan.numberconverter.domain.repository.ConverterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConverterUiState())
    val uiState: StateFlow<ConverterUiState> = _uiState.asStateFlow()

    private val _inputFlow = MutableStateFlow("")
    private val _outputFlow = MutableStateFlow("")

    init {
        // Observe input changes with debouncing
        viewModelScope.launch {
            _inputFlow
                .debounce(300) // Wait 300ms after user stops typing
                .collect { input ->
                    if (input.isNotEmpty()) {
                        convertFromInput()
                    } else {
                        _uiState.update { it.copy(output = "", errorMessage = null, validationError = null) }
                    }
                }
        }

        // Observe output changes with debouncing (reverse conversion)
        viewModelScope.launch {
            _outputFlow
                .debounce(300)
                .collect { output ->
                    if (output.isNotEmpty()) {
                        convertFromOutput()
                    } else {
                        _uiState.update { it.copy(input = "", errorMessage = null, validationError = null) }
                    }
                }
        }
    }

    fun onInputChanged(input: String) {
        _uiState.update { it.copy(input = input, validationError = null) }
        _inputFlow.value = input
        _outputFlow.value = "" // Clear output flow to avoid conflict
    }

    fun onOutputChanged(output: String) {
        _uiState.update { it.copy(output = output, validationError = null) }
        _outputFlow.value = output
        _inputFlow.value = "" // Clear input flow to avoid conflict
    }

    fun onFromBaseChanged(base: NumberBase) {
        _uiState.update { it.copy(fromBase = base) }
        if (_uiState.value.input.isNotEmpty()) {
            convertFromInput()
        }
    }

    fun onToBaseChanged(base: NumberBase) {
        _uiState.update { it.copy(toBase = base) }
        if (_uiState.value.input.isNotEmpty()) {
            convertFromInput()
        } else if (_uiState.value.output.isNotEmpty()) {
            convertFromOutput()
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
                output = _uiState.value.input
            )
        }
    }

    fun clearInput() {
        _uiState.update {
            it.copy(
                input = "",
                output = "",
                errorMessage = null,
                validationError = null
            )
        }
        _inputFlow.value = ""
    }

    private fun convertFromInput() {
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

    private fun convertFromOutput() {
        val currentState = _uiState.value

        // Validate output first (treat it as input in toBase)
        val validationResult = validateInputUseCase(currentState.output, currentState.toBase)
        if (!validationResult.isValid) {
            _uiState.update {
                it.copy(
                    validationError = validationResult.errorMessage,
                    input = "",
                    errorMessage = null
                )
            }
            return
        }

        // Perform reverse conversion (from toBase to fromBase)
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, validationError = null) }

            convertNumberUseCase(
                input = currentState.output,
                fromBase = currentState.toBase,
                toBase = currentState.fromBase
            ).fold(
                onSuccess = { result ->
                    val formattedInput = formatOutputUseCase(result.output)
                    _uiState.update {
                        it.copy(
                            input = formattedInput,
                            isLoading = false,
                            errorMessage = null
                        )
                    }

                    // Fetch explanation
                    viewModelScope.launch {
                        converterRepository.explain(
                            input = currentState.output,
                            fromBase = currentState.toBase,
                            toBase = currentState.fromBase
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
                                input = currentState.output,
                                output = formattedInput,
                                fromBase = currentState.toBase,
                                toBase = currentState.fromBase,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            errorMessage = error.message ?: "Conversion failed",
                            input = "",
                            isLoading = false
                        )
                    }
                }
            )
        }
    }
}
