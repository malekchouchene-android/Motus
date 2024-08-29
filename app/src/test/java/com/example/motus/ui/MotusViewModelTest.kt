package com.example.motus.ui

import com.example.motus.CoroutinesTestRule
import com.example.motus.usecases.GetRandomWordUseCase
import com.example.motus.usecases.LetterVerificationResult
import com.example.motus.usecases.VerifyWordUseCase
import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class MotusViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule(testDispatcher)
    private val getRandomWordUseCase: GetRandomWordUseCase = mockk()
    private val verifyWordUseCase: VerifyWordUseCase = VerifyWordUseCase()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun should_init_return_a_right_state() {
        runTest {
            // Given
            coEvery { getRandomWordUseCase.execute() } returns Result.success("TESTUIA")

            // When
            val viewModel = MotusViewModel(
                getRandomWord = getRandomWordUseCase,
                verifyWordUseCase = verifyWordUseCase,
                backgroundDispatcher = testDispatcher
            )
            advanceUntilIdle()
            // Then
            Truth.assertThat(viewModel.state.value.wordToGuess).isEqualTo("TESTUIA")
            Truth.assertThat(viewModel.state.value.isLoading).isFalse()
            Truth.assertThat(viewModel.state.value.error).isNull()
            Truth.assertThat(viewModel.state.value.attempts).isEmpty()
            Truth.assertThat(viewModel.state.value.lose).isFalse()
            Truth.assertThat(viewModel.state.value.win).isFalse()
            Truth.assertThat(viewModel.state.value.hint).isEqualTo(
                listOf(
                    LetterAttempts("T", LetterVerificationResult.CORRECT),
                    LetterAttempts(".", LetterVerificationResult.INCORRECT),
                    LetterAttempts(".", LetterVerificationResult.INCORRECT),
                    LetterAttempts(".", LetterVerificationResult.INCORRECT),
                    LetterAttempts(".", LetterVerificationResult.INCORRECT),
                    LetterAttempts(".", LetterVerificationResult.INCORRECT),
                )
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun should_start_new_game_return_a_right_state() {
        runTest {
            // Given
            coEvery { getRandomWordUseCase.execute() } returns Result.success("TESTUIA")
            val viewModel = MotusViewModel(
                getRandomWord = getRandomWordUseCase,
                verifyWordUseCase = verifyWordUseCase,
                backgroundDispatcher = testDispatcher
            )
            advanceUntilIdle()
            coEvery { getRandomWordUseCase.execute() } returns Result.success("AESTUIA")
            viewModel.startANewGame()
            advanceUntilIdle()
            // When

            // Then
            Truth.assertThat(viewModel.state.value.wordToGuess).isEqualTo("AESTUIA")
            Truth.assertThat(viewModel.state.value.isLoading).isFalse()
            Truth.assertThat(viewModel.state.value.error).isNull()
            Truth.assertThat(viewModel.state.value.attempts).isEmpty()
            Truth.assertThat(viewModel.state.value.lose).isFalse()
            Truth.assertThat(viewModel.state.value.win).isFalse()
            Truth.assertThat(viewModel.state.value.hint).isEqualTo(
                listOf(
                    LetterAttempts("A", LetterVerificationResult.CORRECT),
                    LetterAttempts(".", LetterVerificationResult.INCORRECT),
                    LetterAttempts(".", LetterVerificationResult.INCORRECT),
                    LetterAttempts(".", LetterVerificationResult.INCORRECT),
                    LetterAttempts(".", LetterVerificationResult.INCORRECT),
                    LetterAttempts(".", LetterVerificationResult.INCORRECT),
                )
            )
        }
    }

    @Test
    fun should_submit_new_attempts_and_win_when_correct() {
        runTest {
            //GIVEN
            coEvery { getRandomWordUseCase.execute() } returns Result.success("TESTUI")
            val viewModel = MotusViewModel(
                getRandomWord = getRandomWordUseCase,
                verifyWordUseCase = verifyWordUseCase,
                backgroundDispatcher = testDispatcher
            )
            advanceUntilIdle()
            // WHEN
            viewModel.submitAttempt("TESTUI")
            advanceUntilIdle()
            // THEN
            Truth.assertThat(viewModel.state.value.win).isTrue()
            Truth.assertThat(viewModel.state.value.attempts).isEqualTo(
                listOf(
                    listOf(
                        LetterAttempts("T", LetterVerificationResult.CORRECT),
                        LetterAttempts("E", LetterVerificationResult.CORRECT),
                        LetterAttempts("S", LetterVerificationResult.CORRECT),
                        LetterAttempts("T", LetterVerificationResult.CORRECT),
                        LetterAttempts("U", LetterVerificationResult.CORRECT),
                        LetterAttempts("I", LetterVerificationResult.CORRECT),
                    )
                )
            )
        }
    }

    @Test
    fun should_end_game_when_attempts_exceed() {
        runTest {
            //GIVEN
            coEvery { getRandomWordUseCase.execute() } returns Result.success("TESTUI")
            val viewModel = MotusViewModel(
                getRandomWord = getRandomWordUseCase,
                verifyWordUseCase = verifyWordUseCase,
                backgroundDispatcher = testDispatcher
            )
            advanceUntilIdle()
            // WHEN
            viewModel.submitAttempt("TAEDSD")
            advanceUntilIdle()
            Truth.assertThat(viewModel.state.value.win).isFalse()
            Truth.assertThat(viewModel.state.value.lose).isFalse()
            viewModel.submitAttempt("TABDAD")
            advanceUntilIdle()
            viewModel.submitAttempt("TABDAD")
            advanceUntilIdle()
            Truth.assertThat(viewModel.state.value.win).isFalse()
            Truth.assertThat(viewModel.state.value.lose).isFalse()
            viewModel.submitAttempt("TABDAD")
            advanceUntilIdle()
            Truth.assertThat(viewModel.state.value.win).isFalse()
            Truth.assertThat(viewModel.state.value.lose).isFalse()
            viewModel.submitAttempt("TABDAD")
            advanceUntilIdle()
            Truth.assertThat(viewModel.state.value.win).isFalse()
            Truth.assertThat(viewModel.state.value.lose).isFalse()
            viewModel.submitAttempt("TABDAD")
            advanceUntilIdle()
            Truth.assertThat(viewModel.state.value.win).isFalse()
            Truth.assertThat(viewModel.state.value.lose).isFalse()
            viewModel.submitAttempt("TABDAD")
            advanceUntilIdle()
            Truth.assertThat(viewModel.state.value.win).isFalse()
            Truth.assertThat(viewModel.state.value.lose).isTrue()
        }
    }
}