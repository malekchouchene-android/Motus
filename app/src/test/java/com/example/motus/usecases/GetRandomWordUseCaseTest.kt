package com.example.motus.usecases

import com.example.motus.CoroutinesTestRule
import com.example.motus.repos.WordsRepository
import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test


class GetRandomWordUseCaseTest {
    val repository: WordsRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule(testDispatcher)

    @Test
    fun should_return_a_word_respect_the_length() {
        runTest {
            // Given
            coEvery { repository.getListOfWords() } returns Result.success(
                listOf(
                    "TESTUIA",
                    "TESTUI",
                    "TESTUIC"
                )
            )
            // When
            val useCase = GetRandomWordUseCase(repository)
            val result = useCase.execute()
            // Then
            Truth.assertThat(result.getOrNull()).isEqualTo("TESTUI")
        }

    }
}