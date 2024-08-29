package com.example.motus.repos

import android.content.Context
import com.example.motus.BackgroundDispatcher
import com.example.motus.network.WordApi
import com.example.motus.utils.runSuspendCatching
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordsRepositoryImp @Inject constructor(
    @ApplicationContext val context: Context,
    val wordApi: WordApi,
    @BackgroundDispatcher val backgroundDispatcher: CoroutineDispatcher = Dispatchers.IO,
) :
    WordsRepository {
    override suspend fun getListOfWords(): Result<List<String>> {
        return withContext(backgroundDispatcher) {
            runSuspendCatching {
                wordApi.getListOfWords()
                    .split("\n").map {
                        it.trim()
                    }
            }.recoverCatching {
                context.assets.open("words.txt").bufferedReader().readLines()
            }
        }

    }

}