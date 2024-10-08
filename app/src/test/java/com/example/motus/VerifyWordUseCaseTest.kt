package com.example.motus

import com.example.motus.usecases.LetterVerificationResult
import com.example.motus.usecases.VerifyWordUseCase
import com.google.common.truth.Truth
import org.junit.Test


class VerifyWordUseCaseTest {
    @Test
    fun should_return_correct_when_word_is_correct() {
        val verifyWordUseCase = VerifyWordUseCase()
        val result = verifyWordUseCase.execute("ACIDES", "ACIDES")
        Truth.assertThat(result.getOrThrow()).isEqualTo(
            listOf(
                LetterVerificationResult.CORRECT,
                LetterVerificationResult.CORRECT,
                LetterVerificationResult.CORRECT,
                LetterVerificationResult.CORRECT,
                LetterVerificationResult.CORRECT,
                LetterVerificationResult.CORRECT
            )
        )
    }

    @Test
    fun should_detekt_misplaced_letter() {
        val verifyWordUseCase = VerifyWordUseCase()
        val result = verifyWordUseCase.execute("ACIDES", "AICDSE")
        Truth.assertThat(result.getOrThrow()).isEqualTo(
            listOf(
                LetterVerificationResult.CORRECT,
                LetterVerificationResult.MISPLACED,
                LetterVerificationResult.MISPLACED,
                LetterVerificationResult.CORRECT,
                LetterVerificationResult.MISPLACED,
                LetterVerificationResult.MISPLACED
            )
        )
    }

    @Test
    fun should_detect_incorrect_letter() {
        val verifyWordUseCase = VerifyWordUseCase()
        val result = verifyWordUseCase.execute("ACIDES", "ACIDEB")
        Truth.assertThat(result.getOrThrow()).isEqualTo(
            listOf(
                LetterVerificationResult.CORRECT,
                LetterVerificationResult.CORRECT,
                LetterVerificationResult.CORRECT,
                LetterVerificationResult.CORRECT,
                LetterVerificationResult.CORRECT,
                LetterVerificationResult.INCORRECT
            )
        )
    }

    @Test
    fun should_return_error_when_word_length_is_incorrect() {
        val verifyWordUseCase = VerifyWordUseCase()
        val result = verifyWordUseCase.execute("ACIDE", "ACIDES")
        Truth.assertThat(result.exceptionOrNull())
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun should_accept_lower_case_input() {
        val verifyWordUseCase = VerifyWordUseCase()
        val result = verifyWordUseCase.execute("acides", "ACIDES")
        Truth.assertThat(result.getOrThrow()).isEqualTo(
            listOf(
                LetterVerificationResult.CORRECT,
                LetterVerificationResult.CORRECT,
                LetterVerificationResult.CORRECT,
                LetterVerificationResult.CORRECT,
                LetterVerificationResult.CORRECT,
                LetterVerificationResult.CORRECT
            )
        )
    }

    @Test
    fun should_detect_multiple_occurence_of_letter() {
        val verifyWordUseCase = VerifyWordUseCase()
        val result = verifyWordUseCase.execute(wordInput = "NAPESA", wordToGuess = "NATURE")
        Truth.assertThat(result.getOrThrow()).isEqualTo(
            listOf(
                LetterVerificationResult.CORRECT,
                LetterVerificationResult.CORRECT,
                LetterVerificationResult.INCORRECT,
                LetterVerificationResult.MISPLACED,
                LetterVerificationResult.INCORRECT,
                LetterVerificationResult.INCORRECT
            )
        )
    }

    @Test
    fun should_detect_multiple_occurence_of_letter_2() {
        val verifyWordUseCase = VerifyWordUseCase()
        val result = verifyWordUseCase.execute(wordInput = "SURALA", wordToGuess = "SURLIA")
        Truth.assertThat(result.getOrThrow()).isEqualTo(
            listOf(
                LetterVerificationResult.CORRECT,
                LetterVerificationResult.CORRECT,
                LetterVerificationResult.CORRECT,
                LetterVerificationResult.INCORRECT,
                LetterVerificationResult.MISPLACED,
                LetterVerificationResult.CORRECT
            )
        )
    }

    @Test
    fun should_detect_multiple_occurence_of_letter_3() {
        val verifyWordUseCase = VerifyWordUseCase()
        val result = verifyWordUseCase.execute(wordInput = "COOOOS", wordToGuess = "COCONS")
        Truth.assertThat(result.getOrThrow()).isEqualTo(
            listOf(
                LetterVerificationResult.CORRECT,
                LetterVerificationResult.CORRECT,
                LetterVerificationResult.INCORRECT,
                LetterVerificationResult.CORRECT,
                LetterVerificationResult.INCORRECT,
                LetterVerificationResult.CORRECT
            )
        )
    }

    @Test
    fun should_detect_multiple_occurence_of_letter_4() {
        val verifyWordUseCase = VerifyWordUseCase()
        val result = verifyWordUseCase.execute(wordInput = "BRIIII", wordToGuess = "BIHARI")
        Truth.assertThat(result.getOrThrow()).isEqualTo(
            listOf(
                LetterVerificationResult.CORRECT,
                LetterVerificationResult.MISPLACED,
                LetterVerificationResult.MISPLACED,
                LetterVerificationResult.INCORRECT,
                LetterVerificationResult.INCORRECT,
                LetterVerificationResult.CORRECT
            )
        )
    }
}