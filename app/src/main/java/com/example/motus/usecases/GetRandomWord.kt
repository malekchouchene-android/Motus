package com.example.motus.usecases

import com.example.motus.repos.WordsRepository
import com.example.motus.utils.runSuspendCatching
import timber.log.Timber
import javax.inject.Inject
import kotlin.random.Random

class GetRandomWord @Inject constructor(private val wordsRepository: WordsRepository) {
    suspend fun execute(): Result<String> {
        return runSuspendCatching {
            wordsRepository.getListOfWords().getOrThrow().filter {
                it.length == WORD_LENGTH
            }.random()
        }
    }

}
