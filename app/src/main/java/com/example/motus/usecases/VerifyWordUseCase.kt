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
        val wordChars = input.toCharArray()
        // Count the number of each letter in the word to guess
        val countLetter: MutableMap<Char, Int> = mutableMapOf()
        for (correctChar in correctWord.toCharArray().distinct()) {
            countLetter[correctChar] = correctWord.count { it == correctChar }
        }
        val checkedLetter: MutableMap<Char, Int> = mutableMap()
        for (index in 0 until WORD_LENGTH) {
            if (wordChars[index] == correctWord[index]) {
                result.add(LetterVerificationResult.CORRECT)
                checkedLetter[wordChars[index]] =
                    checkedLetter.getOrDefault(wordChars[index], 0) + 1
            } else if (!correctWord.contains(wordChars[index])) {
                result.add(LetterVerificationResult.INCORRECT)
            } else {
                if (checkedLetter.getOrDefault(wordChars[index], 0) < countLetter.getOrDefault(
                        wordChars[index],
                        0
                    )
                ) {
                    result.add(LetterVerificationResult.MISPLACED)
                } else {
                    result.add(LetterVerificationResult.INCORRECT)
                }
            }
        }

        return Result.success(result)

    }

    private fun mutableMap(): MutableMap<Char, Int> = mutableMapOf()
}


enum class LetterVerificationResult {
    CORRECT,
    INCORRECT,
    MISPLACED
}