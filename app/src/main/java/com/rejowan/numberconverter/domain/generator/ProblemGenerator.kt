package com.rejowan.numberconverter.domain.generator

import com.rejowan.numberconverter.domain.model.Difficulty
import com.rejowan.numberconverter.domain.model.Exercise
import com.rejowan.numberconverter.domain.model.NumberBase
import kotlin.random.Random

class ProblemGenerator {

    fun generateProblem(
        difficulty: Difficulty,
        fromBase: NumberBase? = null,
        toBase: NumberBase? = null
    ): Exercise {
        val selectedFromBase = fromBase ?: getRandomBase()
        val selectedToBase = toBase ?: getRandomBase(exclude = selectedFromBase)

        val number = generateNumber(difficulty, selectedFromBase)
        val problem = "Convert $number from ${selectedFromBase.name} to ${selectedToBase.name}"
        val correctAnswer = convertNumber(number, selectedFromBase, selectedToBase)

        val hints = generateHints(number, selectedFromBase, selectedToBase, difficulty)
        val explanation = generateExplanation(number, selectedFromBase, selectedToBase, correctAnswer)

        return Exercise(
            id = "practice_${System.currentTimeMillis()}",
            problem = problem,
            correctAnswer = correctAnswer,
            difficulty = difficulty,
            fromBase = selectedFromBase,
            toBase = selectedToBase,
            explanation = explanation,
            hints = hints
        )
    }

    fun generateBatch(
        count: Int,
        difficulty: Difficulty,
        fromBase: NumberBase? = null,
        toBase: NumberBase? = null
    ): List<Exercise> {
        return (1..count).map { generateProblem(difficulty, fromBase, toBase) }
    }

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

    private fun generateHints(
        number: String,
        fromBase: NumberBase,
        toBase: NumberBase,
        difficulty: Difficulty
    ): List<String> {
        val hints = mutableListOf<String>()

        // Hint 1: Convert to decimal first if not direct
        if (fromBase != NumberBase.DECIMAL && toBase != NumberBase.DECIMAL) {
            val decimalValue = number.toLong(fromBase.value)
            hints.add("First convert $number (${fromBase.name}) to decimal: $decimalValue")
        }

        // Hint 2: Provide conversion method
        when (toBase) {
            NumberBase.BINARY -> hints.add("To convert to binary, repeatedly divide by 2 and track remainders")
            NumberBase.OCTAL -> hints.add("To convert to octal, repeatedly divide by 8 and track remainders")
            NumberBase.HEXADECIMAL -> hints.add("To convert to hexadecimal, repeatedly divide by 16 and track remainders (A=10, B=11, C=12, D=13, E=14, F=15)")
            NumberBase.DECIMAL -> hints.add("To convert to decimal, multiply each digit by its positional value and sum")
        }

        // Hint 3: For easy problems, provide partial answer
        if (difficulty == Difficulty.EASY) {
            val decimalValue = number.toLong(fromBase.value)
            hints.add("The decimal value is $decimalValue")
        }

        return hints
    }

    private fun generateExplanation(
        number: String,
        fromBase: NumberBase,
        toBase: NumberBase,
        correctAnswer: String
    ): String {
        val decimalValue = number.toLong(fromBase.value)

        return buildString {
            append("Converting $number (${fromBase.name}) to ${toBase.name}:\n\n")

            if (fromBase != NumberBase.DECIMAL) {
                append("Step 1: Convert to decimal\n")
                append("$number (${fromBase.name}) = $decimalValue (Decimal)\n\n")
            }

            if (toBase != NumberBase.DECIMAL) {
                append("Step 2: Convert decimal to ${toBase.name}\n")
                append("$decimalValue (Decimal) = $correctAnswer (${toBase.name})\n\n")
            }

            append("Final Answer: $correctAnswer")
        }
    }

    private fun getRandomBase(exclude: NumberBase? = null): NumberBase {
        val bases = NumberBase.entries.filter { it != exclude }
        return bases.random()
    }
}
