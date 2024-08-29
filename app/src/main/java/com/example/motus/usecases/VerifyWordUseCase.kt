package com.example.motus.usecases

import javax.inject.Inject


class VerifyWordUseCase @Inject constructor() {
    fun execute(wordInput: String, wordToGuess: String): Result<List<LetterVerificationResult>> {
        if (wordInput.length != WORD_LENGTH) {
            return Result.failure(IllegalArgumentException("Word length should be $WORD_LENGTH"))
        }
        val input = wordInput.uppercase()
        val correctWord = wordToGuess.uppercase()
        if (input == correctWord) {
            return Result.success(List(WORD_LENGTH) { LetterVerificationResult.CORRECT })
        }
        val result = mutableListOf<LetterVerificationResult>()
        val wordToGuessChars = correctWord.toCharArray()
        val wordChars = input.toCharArray()
        for (index in 0..WORD_LENGTH) {
            if (wordChars[index] == correctWord[index]) {
                result.add(LetterVerificationResult.CORRECT)
            } else if (!wordToGuessChars.contains(wordChars[index])) {
                result.add(LetterVerificationResult.MISPLACED)
            } else {
                result.add(LetterVerificationResult.INCORRECT)
            }
        }
        return Result.success(result)

    }
}


enum class LetterVerificationResult {
    CORRECT,
    INCORRECT,
    MISPLACED
}