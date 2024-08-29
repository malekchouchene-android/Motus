package com.example.motus.repos

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class RepoModule {
    @Binds
    @Singleton
    abstract fun bindWordsRepository(wordsRepositoryImp: WordsRepositoryImp): WordsRepository
}