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
        val result = Array(WORD_LENGTH) { LetterVerificationResult.INCORRECT }
        val challengeChars = input.toCharArray()
        // Count the number of each letter in the word to guess
        val countLetter: MutableMap<Char, Int> = mutableMapOf()
        for (correctChar in correctWord.toCharArray().distinct()) {
            countLetter[correctChar] = correctWord.count { it == correctChar }
        }
        val checkedLetter: MutableMap<Char, Int> = mutableMapOf()
        // Identify correct char ,
        for (i in 0 until WORD_LENGTH) {
            if (challengeChars[i] == correctWord[i]) {
                result[i] = LetterVerificationResult.CORRECT
                checkedLetter[challengeChars[i]] =
                    checkedLetter.getOrDefault(challengeChars[i], 0) + 1
            }
        }
        // Identify misplaced char and Incorrect
        for (index in 0 until WORD_LENGTH) {
            if (result[index] == LetterVerificationResult.CORRECT) {
                continue
            }
            if (!correctWord.contains(challengeChars[index])) {
                result[index] = LetterVerificationResult.INCORRECT
            } else {
                if (checkedLetter.getOrDefault(challengeChars[index], 0) < countLetter.getOrDefault(
                        challengeChars[index],
                        0
                    )
                ) {
                    result[index] = LetterVerificationResult.MISPLACED
                } else {
                    result[index] = LetterVerificationResult.INCORRECT
                }
            }
        }

        return Result.success(result.toList())

    }
}


enum class LetterVerificationResult {
    CORRECT,
    INCORRECT,
    MISPLACED
}