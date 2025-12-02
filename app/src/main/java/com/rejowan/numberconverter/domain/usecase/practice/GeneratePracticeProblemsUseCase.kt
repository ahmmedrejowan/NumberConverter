package com.rejowan.numberconverter.domain.usecase.practice

import com.rejowan.numberconverter.domain.generator.ProblemGenerator
import com.rejowan.numberconverter.domain.model.Difficulty
import com.rejowan.numberconverter.domain.model.Exercise
import com.rejowan.numberconverter.domain.model.NumberBase

class GeneratePracticeProblemsUseCase(
    private val problemGenerator: ProblemGenerator
) {
    operator fun invoke(
        count: Int = 10,
        difficulty: Difficulty = Difficulty.MEDIUM,
        fromBase: NumberBase? = null,
        toBase: NumberBase? = null
    ): List<Exercise> {
        return problemGenerator.generateBatch(count, difficulty, fromBase, toBase)
    }
}
