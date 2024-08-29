package com.example.motus.repos

import com.example.motus.CoroutinesTestRule
import com.example.motus.network.WordApi
import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test


class WordsRepositoryImpTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule(testDispatcher)
    val api: WordApi = mockk()

    @Test
    fun should_split_words() {
        runTest {
            // GIVEN
            coEvery {
                api.getListOfWords()
            } returns "test\ntestui\nabc"

            // WHEN
            val repository = WordsRepositoryImp(
                context = mockk(),
                wordApi = api,
                backgroundDispatcher = testDispatcher
            )
            val result = repository.getListOfWords()
            // THEN
            Truth.assertThat(
                result.getOrThrow()
            ).containsExactly("test", "testui", "abc")
        }
    }
}