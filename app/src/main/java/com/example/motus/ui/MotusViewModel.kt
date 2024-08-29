package com.example.motus.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.motus.BackgroundDispatcher
import com.example.motus.usecases.GetRandomWordUseCase
import com.example.motus.usecases.LetterVerificationResult
import com.example.motus.usecases.MAX_ATTEMPTS
import com.example.motus.usecases.VerifyWordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MotusViewModel @Inject constructor(
    private val getRandomWord: GetRandomWordUseCase,
    private val verifyWordUseCase: VerifyWordUseCase,
    @BackgroundDispatcher private val backgroundDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {
    private val _state = MutableStateFlow(MotusState())
    val state: StateFlow<MotusState> = _state

    init {
        startANewGame()
    }

    fun startANewGame() {
        viewModelScope.launch(backgroundDispatcher) {
            _state.update {
                MotusState()
            }
            getRandomWord.execute().onSuccess { word ->
                Timber.e("Word to guess: $word")
                _state.update {
                    MotusState(
                        wordToGuess = word,
                        isLoading = false,
                        error = null,
                        attempts = emptyList(),
                        hint =
                        listOf(
                            LetterAttempts(
                                word.first().toString(),
                                LetterVerificationResult.CORRECT
                            ),
                            LetterAttempts(".", LetterVerificationResult.INCORRECT),
                            LetterAttempts(".", LetterVerificationResult.INCORRECT),
                            LetterAttempts(".", LetterVerificationResult.INCORRECT),
                            LetterAttempts(".", LetterVerificationResult.INCORRECT),
                            LetterAttempts(".", LetterVerificationResult.INCORRECT),
                        ),
                    )
                }
            }.onFailure {
                Timber.e(it)
            }
        }
    }

    fun submitAttempt(word: String) {
        if (_state.value.win || _state.value.lose || _state.value.win) return
        viewModelScope.launch(backgroundDispatcher) {
            val wordToGuess = _state.value.wordToGuess ?: return@launch
            _state.update {
                it.copy(
                    isLoading = true,
                )
            }
            verifyWordUseCase.execute(word, wordToGuess)
                .onSuccess { verificationResult ->
                    val lastAttempts = _state.value.attempts.toMutableList()
                    lastAttempts.add(word.mapIndexed { index, char ->
                        LetterAttempts(char.toString(), verificationResult[index])
                    })
                    val newAttempts = lastAttempts.toList()
                    _state.update { lastState ->
                        lastState.copy(
                            isLoading = false,
                            error = null,
                            attempts = newAttempts,
                            win = verificationResult.all { it == LetterVerificationResult.CORRECT },
                            lose = newAttempts.size == MAX_ATTEMPTS
                        )
                    }
                }.onFailure { error ->
                    Timber.e(error)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error
                        )
                    }
                }
        }
    }
}

data class MotusState(
    val wordToGuess: String? = null,
    // List<LetterAttempts> represent a row of the game
    val attempts: List<List<LetterAttempts>> = emptyList(),
    // hint is Just the First letter of the word to guess and . in each box
    val hint: List<LetterAttempts>? = null,
    val isLoading: Boolean = true,
    val win: Boolean = false,
    val lose: Boolean = false,
    val error: Throwable? = null,
)

// Represents a box with a letter and the result of the verification
data class LetterAttempts(val letter: String, val result: LetterVerificationResult)