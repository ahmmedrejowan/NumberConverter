package com.rejowan.numberconverter.domain.generator

import com.rejowan.numberconverter.domain.model.Difficulty
import com.rejowan.numberconverter.domain.model.Exercise
import com.rejowan.numberconverter.domain.model.NumberBase
import com.rejowan.numberconverter.domain.model.Operation
import kotlin.random.Random

class ProblemGenerator {

    // ==================== Conversion Problems ====================

    fun generateConversionProblem(
        difficulty: Difficulty,
        fromBase: NumberBase? = null,
        toBase: NumberBase? = null
    ): Exercise {
        val selectedFromBase = fromBase ?: getRandomBase()
        val selectedToBase = toBase ?: getRandomBase(exclude = selectedFromBase)

        val number = generateNumber(difficulty, selectedFromBase)
        val problem = "Convert $number (${selectedFromBase.displayName}) to ${selectedToBase.displayName}"
        val correctAnswer = convertNumber(number, selectedFromBase, selectedToBase)

        val hints = generateConversionHints(number, selectedFromBase, selectedToBase, difficulty)
        val explanation = generateConversionExplanation(number, selectedFromBase, selectedToBase, correctAnswer)

        return Exercise(
            id = "conv_${System.currentTimeMillis()}_${Random.nextInt()}",
            problem = problem,
            correctAnswer = correctAnswer,
            difficulty = difficulty,
            fromBase = selectedFromBase,
            toBase = selectedToBase,
            explanation = explanation,
            hints = hints
        )
    }

    fun generateConversionBatch(
        count: Int,
        difficulty: Difficulty,
        fromBase: NumberBase? = null,
        toBase: NumberBase? = null
    ): List<Exercise> {
        return (1..count).map { generateConversionProblem(difficulty, fromBase, toBase) }
    }

    // ==================== Calculation Problems ====================

    fun generateCalculationProblem(difficulty: Difficulty): Exercise {
        val input1Base = getRandomBase()
        val input2Base = getRandomBase()
        val outputBase = getRandomBase()
        val operation = Operation.entries.random()

        val (num1Decimal, num2Decimal) = generateCalculationNumbers(difficulty, operation)

        val num1 = convertFromDecimal(num1Decimal, input1Base)
        val num2 = convertFromDecimal(num2Decimal, input2Base)

        val resultDecimal = when (operation) {
            Operation.ADD -> num1Decimal + num2Decimal
            Operation.SUBTRACT -> num1Decimal - num2Decimal
            Operation.MULTIPLY -> num1Decimal * num2Decimal
            Operation.DIVIDE -> num1Decimal / num2Decimal
        }

        val correctAnswer = convertFromDecimal(resultDecimal, outputBase)

        val problem = "$num1 (${input1Base.displayName}) ${operation.symbol} $num2 (${input2Base.displayName}) = ? (${outputBase.displayName})"

        val hints = generateCalculationHints(num1, input1Base, num2, input2Base, operation, difficulty)
        val explanation = generateCalculationExplanation(
            num1, input1Base, num2, input2Base, operation, outputBase, resultDecimal, correctAnswer
        )

        return Exercise(
            id = "calc_${System.currentTimeMillis()}_${Random.nextInt()}",
            problem = problem,
            correctAnswer = correctAnswer,
            difficulty = difficulty,
            fromBase = input1Base,
            toBase = outputBase,
            explanation = explanation,
            hints = hints
        )
    }

    fun generateCalculationBatch(count: Int, difficulty: Difficulty): List<Exercise> {
        return (1..count).map { generateCalculationProblem(difficulty) }
    }

    private fun generateCalculationNumbers(difficulty: Difficulty, operation: Operation): Pair<Long, Long> {
        return when (difficulty) {
            Difficulty.EASY -> {
                val num1 = Random.nextLong(1, 16)
                val num2 = when (operation) {
                    Operation.DIVIDE -> {
                        val divisors = (1..num1).filter { num1 % it == 0L }
                        if (divisors.isNotEmpty()) divisors.random() else 1L
                    }
                    Operation.SUBTRACT -> Random.nextLong(1, num1 + 1)
                    else -> Random.nextLong(1, 16)
                }
                Pair(num1, num2)
            }
            Difficulty.MEDIUM -> {
                val num1 = Random.nextLong(10, 100)
                val num2 = when (operation) {
                    Operation.DIVIDE -> {
                        val divisors = (1..minOf(num1, 20)).filter { num1 % it == 0L }
                        if (divisors.isNotEmpty()) divisors.random() else 1L
                    }
                    Operation.MULTIPLY -> Random.nextLong(2, 16)
                    else -> Random.nextLong(10, 100)
                }
                Pair(num1, num2)
            }
            Difficulty.HARD -> {
                val num1 = Random.nextLong(100, 500)
                val num2 = when (operation) {
                    Operation.DIVIDE -> {
                        val divisors = (1..minOf(num1, 50)).filter { num1 % it == 0L }
                        if (divisors.isNotEmpty()) divisors.random() else 1L
                    }
                    Operation.MULTIPLY -> Random.nextLong(2, 20)
                    else -> Random.nextLong(50, 300)
                }
                Pair(num1, num2)
            }
        }
    }

    private fun generateCalculationHints(
        num1: String,
        base1: NumberBase,
        num2: String,
        base2: NumberBase,
        operation: Operation,
        difficulty: Difficulty
    ): List<String> {
        val hints = mutableListOf<String>()

        val decimal1 = num1.toLong(base1.value)
        val decimal2 = num2.toLong(base2.value)

        hints.add("First convert both numbers to decimal")

        if (difficulty == Difficulty.EASY || difficulty == Difficulty.MEDIUM) {
            hints.add("$num1 (${base1.displayName}) = $decimal1 (Decimal)")
            hints.add("$num2 (${base2.displayName}) = $decimal2 (Decimal)")
        }

        if (difficulty == Difficulty.EASY) {
            val result = when (operation) {
                Operation.ADD -> decimal1 + decimal2
                Operation.SUBTRACT -> decimal1 - decimal2
                Operation.MULTIPLY -> decimal1 * decimal2
                Operation.DIVIDE -> decimal1 / decimal2
            }
            hints.add("$decimal1 ${operation.symbol} $decimal2 = $result (Decimal)")
        }

        return hints
    }

    private fun generateCalculationExplanation(
        num1: String,
        base1: NumberBase,
        num2: String,
        base2: NumberBase,
        operation: Operation,
        outputBase: NumberBase,
        resultDecimal: Long,
        correctAnswer: String
    ): String {
        val decimal1 = num1.toLong(base1.value)
        val decimal2 = num2.toLong(base2.value)

        return buildString {
            append("Step 1: Convert to decimal\n")
            append("$num1 (${base1.displayName}) = $decimal1\n")
            append("$num2 (${base2.displayName}) = $decimal2\n\n")

            append("Step 2: Perform ${operation.displayName.lowercase()}\n")
            append("$decimal1 ${operation.symbol} $decimal2 = $resultDecimal\n\n")

            if (outputBase != NumberBase.DECIMAL) {
                append("Step 3: Convert to ${outputBase.displayName}\n")
                append("$resultDecimal (Decimal) = $correctAnswer (${outputBase.displayName})\n\n")
            }

            append("Final Answer: $correctAnswer")
        }
    }

    private fun convertFromDecimal(value: Long, base: NumberBase): String {
        val absValue = kotlin.math.abs(value)
        val converted = when (base) {
            NumberBase.BINARY -> absValue.toString(2)
            NumberBase.OCTAL -> absValue.toString(8)
            NumberBase.DECIMAL -> absValue.toString(10)
            NumberBase.HEXADECIMAL -> absValue.toString(16).uppercase()
        }
        return if (value < 0) "-$converted" else converted
    }

    // ==================== Legacy Support ====================

    @Deprecated("Use generateConversionProblem instead")
    fun generateProblem(
        difficulty: Difficulty,
        fromBase: NumberBase? = null,
        toBase: NumberBase? = null
    ): Exercise = generateConversionProblem(difficulty, fromBase, toBase)

    @Deprecated("Use generateConversionBatch instead")
    fun generateBatch(
        count: Int,
        difficulty: Difficulty,
        fromBase: NumberBase? = null,
        toBase: NumberBase? = null
    ): List<Exercise> = generateConversionBatch(count, difficulty, fromBase, toBase)

    // ==================== Private Helpers ====================

    private fun generateNumber(difficulty: Difficulty, base: NumberBase): String {
        val decimalValue = when (difficulty) {
            Difficulty.EASY -> Random.nextInt(1, 16) // 1-15
            Difficulty.MEDIUM -> Random.nextInt(16, 256) // 16-255
            Difficulty.HARD -> Random.nextInt(256, 4096) // 256-4095
        }

        return when (base) {
            NumberBase.BINARY -> decimalValue.toString(2)
            NumberBase.OCTAL -> decimalValue.toString(8)
            NumberBase.DECIMAL -> decimalValue.toString(10)
            NumberBase.HEXADECIMAL -> decimalValue.toString(16).uppercase()
        }
    }

    private fun convertNumber(number: String, fromBase: NumberBase, toBase: NumberBase): String {
        val decimalValue = number.toLong(fromBase.value)
        return when (toBase) {
            NumberBase.BINARY -> decimalValue.toString(2)
            NumberBase.OCTAL -> decimalValue.toString(8)
            NumberBase.DECIMAL -> decimalValue.toString(10)
            NumberBase.HEXADECIMAL -> decimalValue.toString(16).uppercase()
        }
    }

    private fun generateConversionHints(
        number: String,
        fromBase: NumberBase,
        toBase: NumberBase,
        difficulty: Difficulty
    ): List<String> {
        val hints = mutableListOf<String>()

        if (fromBase != NumberBase.DECIMAL && toBase != NumberBase.DECIMAL) {
            val decimalValue = number.toLong(fromBase.value)
            hints.add("First convert $number (${fromBase.displayName}) to decimal: $decimalValue")
        }

        when (toBase) {
            NumberBase.BINARY -> hints.add("To convert to binary, repeatedly divide by 2 and track remainders")
            NumberBase.OCTAL -> hints.add("To convert to octal, repeatedly divide by 8 and track remainders")
            NumberBase.HEXADECIMAL -> hints.add("To convert to hex, divide by 16 (A=10, B=11, C=12, D=13, E=14, F=15)")
            NumberBase.DECIMAL -> hints.add("Multiply each digit by its positional value and sum")
        }

        if (difficulty == Difficulty.EASY) {
            val decimalValue = number.toLong(fromBase.value)
            hints.add("The decimal value is $decimalValue")
        }

        return hints
    }

    private fun generateConversionExplanation(
        number: String,
        fromBase: NumberBase,
        toBase: NumberBase,
        correctAnswer: String
    ): String {
        val decimalValue = number.toLong(fromBase.value)

        return buildString {
            append("Converting $number (${fromBase.displayName}) to ${toBase.displayName}:\n\n")

            if (fromBase != NumberBase.DECIMAL) {
                append("Step 1: Convert to decimal\n")
                append("$number (${fromBase.displayName}) = $decimalValue (Decimal)\n\n")
            }

            if (toBase != NumberBase.DECIMAL) {
                val step = if (fromBase == NumberBase.DECIMAL) "Step 1" else "Step 2"
                append("$step: Convert to ${toBase.displayName}\n")
                append("$decimalValue (Decimal) = $correctAnswer (${toBase.displayName})\n\n")
            }

            append("Final Answer: $correctAnswer")
        }
    }

    private fun getRandomBase(exclude: NumberBase? = null): NumberBase {
        val bases = NumberBase.entries.filter { it != exclude }
        return bases.random()
    }
}
