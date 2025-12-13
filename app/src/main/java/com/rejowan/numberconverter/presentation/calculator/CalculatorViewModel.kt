package com.rejowan.numberconverter.presentation.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejowan.numberconverter.data.converter.BaseConverter
import com.rejowan.numberconverter.data.converter.CalculatorExplanationGenerator
import com.rejowan.numberconverter.data.local.datastore.PreferencesManager
import com.rejowan.numberconverter.domain.model.NumberBase
import com.rejowan.numberconverter.domain.model.Operation
import com.rejowan.numberconverter.domain.usecase.calculator.CalculateUseCase
import com.rejowan.numberconverter.domain.usecase.converter.ValidateInputUseCase
import com.rejowan.numberconverter.presentation.calculator.state.CalculatorUiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class CalculatorViewModel(
    private val calculateUseCase: CalculateUseCase,
    private val validateInputUseCase: ValidateInputUseCase,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    private val _calculationTrigger = MutableStateFlow(0L)

    init {
        viewModelScope.launch {
            _calculationTrigger
                .debounce(300)
                .collect {
                    if (_uiState.value.input1.isNotEmpty() && _uiState.value.input2.isNotEmpty()) {
                        calculate()
                    }
                }
        }
    }

    fun onInput1Changed(input: String) {
        val filtered = filterInputForBase(input, _uiState.value.input1Base)
        _uiState.update { it.copy(input1 = filtered, validation1Error = null, explanation = null) }
        triggerCalculation()
    }

    fun onInput2Changed(input: String) {
        val filtered = filterInputForBase(input, _uiState.value.input2Base)
        _uiState.update { it.copy(input2 = filtered, validation2Error = null, explanation = null) }
        triggerCalculation()
    }

    fun onInput1BaseChanged(base: NumberBase) {
        _uiState.update { it.copy(input1Base = base, validation1Error = null, explanation = null) }
        triggerCalculation()
    }

    fun onInput2BaseChanged(base: NumberBase) {
        _uiState.update { it.copy(input2Base = base, validation2Error = null, explanation = null) }
        triggerCalculation()
    }

    fun onOutputBaseChanged(base: NumberBase) {
        _uiState.update { it.copy(outputBase = base, explanation = null) }
        triggerCalculation()
    }

    fun onOperationChanged(operation: Operation) {
        _uiState.update { it.copy(operation = operation, explanation = null) }
        triggerCalculation()
    }

    fun clearAll() {
        _uiState.update {
            CalculatorUiState()
        }
    }

    fun swapInputs() {
        _uiState.update {
            it.copy(
                input1 = it.input2,
                input2 = it.input1,
                input1Base = it.input2Base,
                input2Base = it.input1Base,
                validation1Error = null,
                validation2Error = null
            )
        }
        triggerCalculation()
    }

    private fun triggerCalculation() {
        _calculationTrigger.value = System.currentTimeMillis()
    }

    private fun calculate() {
        val currentState = _uiState.value

        if (currentState.input1.isEmpty() || currentState.input2.isEmpty()) {
            _uiState.update { it.copy(output = "", errorMessage = null) }
            return
        }

        // Validate input 1
        if (!BaseConverter.isValidInput(currentState.input1, currentState.input1Base)) {
            _uiState.update {
                it.copy(
                    validation1Error = "Invalid ${currentState.input1Base.displayName} number",
                    output = ""
                )
            }
            return
        }

        // Validate input 2
        if (!BaseConverter.isValidInput(currentState.input2, currentState.input2Base)) {
            _uiState.update {
                it.copy(
                    validation2Error = "Invalid ${currentState.input2Base.displayName} number",
                    output = ""
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val decimalPlaces = preferencesManager.decimalPlaces.first()

            calculateUseCase(
                input1 = currentState.input1,
                input1Base = currentState.input1Base,
                input2 = currentState.input2,
                input2Base = currentState.input2Base,
                operation = currentState.operation,
                outputBase = currentState.outputBase,
                decimalPlaces = decimalPlaces
            ).fold(
                onSuccess = { result ->
                    // Generate explanation
                    val explanation = try {
                        CalculatorExplanationGenerator.generate(
                            input1 = currentState.input1,
                            input1Base = currentState.input1Base,
                            input2 = currentState.input2,
                            input2Base = currentState.input2Base,
                            operation = currentState.operation,
                            outputBase = currentState.outputBase,
                            result = result
                        )
                    } catch (e: Exception) {
                        null
                    }

                    _uiState.update {
                        it.copy(
                            output = result,
                            isLoading = false,
                            errorMessage = null,
                            explanation = explanation
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            output = "",
                            isLoading = false,
                            errorMessage = error.message ?: "Calculation failed",
                            explanation = null
                        )
                    }
                }
            )
        }
    }

    private fun filterInputForBase(input: String, base: NumberBase): String {
        val allowedChars = when (base) {
            NumberBase.BINARY -> "[01.]"
            NumberBase.OCTAL -> "[0-7.]"
            NumberBase.DECIMAL -> "[0-9.]"
            NumberBase.HEXADECIMAL -> "[0-9a-fA-F.]"
        }
        return input.filter { it.toString().matches(Regex(allowedChars)) }
    }
}
