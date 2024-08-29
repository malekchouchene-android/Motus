package com.example.motus.repos

fun interface WordsRepository {
    suspend fun getListOfWords(): Result<List<String>>
}