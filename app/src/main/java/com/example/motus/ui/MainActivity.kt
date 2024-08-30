package com.example.motus.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.example.motus.R
import com.example.motus.ui.theme.MotusTheme
import com.example.motus.ui.theme.incorrectColor
import com.example.motus.usecases.LetterVerificationResult
import com.example.motus.usecases.MAX_ATTEMPTS
import com.example.motus.usecases.WORD_LENGTH
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MotusViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val state = viewModel.state.collectAsState().value
            MotusTheme {
                if (state.win) {
                    AlertDialog(
                        onDismissRequest = { },
                        confirmButton = {
                            TextButton(onClick = { viewModel.startANewGame() }) {
                                Text(stringResource(id = R.string.restart_game))

                            }
                        },
                        title = { Text(stringResource(R.string.win_title_dialog)) },
                        text = { Text(text = stringResource(R.string.win_description_dialog)) })
                }
                if (state.lose) {
                    Timber.e("Perdu !")
                    AlertDialog(
                        onDismissRequest = { },
                        confirmButton = {
                            TextButton(onClick = { viewModel.startANewGame() }) {
                                Text(stringResource(R.string.restart_game))

                            }
                        },
                        title = { Text(stringResource(R.string.end_game_title_dialog)) },
                        text = {
                            Text(
                                text = stringResource(
                                    R.string.end_game_description_dialog,
                                    state.wordToGuess ?: ""
                                )
                            )
                        })
                }
                GameScreen(state = state,
                    restart = { viewModel.startANewGame() },
                    submitAttempt = {
                        viewModel.submitAttempt(it)
                    })
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    state: MotusState,
    restart: () -> Unit,
    submitAttempt: (String) -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)

        ) {
            if (state.hint == null && state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

            } else {
                LazyColumn(
                    Modifier
                        .fillMaxWidth()

                ) {
                    stickyHeader {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(
                                    R.string.number_of_try,
                                    state.attempts.size,
                                    MAX_ATTEMPTS
                                ),
                                style = MaterialTheme.typography.bodyLarge,
                                color = incorrectColor,
                                modifier = Modifier.padding(8.dp)
                            )
                            TextButton(
                                onClick = { restart() },
                            ) {
                                Text(text = stringResource(id = R.string.restart_game))
                            }
                        }

                    }
                    item {
                        AttemptsComposable(attempt = state.hint ?: emptyList())
                    }
                    items(state.attempts) { attempt ->
                        AttemptsComposable(attempt = attempt)
                    }
                    item {
                        InputAttempt(
                            enabled = !state.win && !state.lose,
                            firstLetter = state.wordToGuess?.first().toString(),
                            onSubmitAttempt = {
                                submitAttempt(it)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AttemptsComposable(attempt: List<LetterAttempts>) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        items(attempt) {
            LetterComposable(
                letter = it.letter,
                letterVerificationResult = it.result,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@Composable
fun LetterComposable(
    letter: String,
    letterVerificationResult: LetterVerificationResult,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .letterResult(letterVerificationResult),
        contentAlignment = Alignment.Center,
    )
    {
        Text(
            text = letter,
        )
    }

}

@Composable
fun Modifier.letterResult(letterVerificationResult: LetterVerificationResult): Modifier =
    when (letterVerificationResult) {
        LetterVerificationResult.CORRECT -> this
            .background(
                MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp)
            )
            .fillMaxSize()

        LetterVerificationResult.INCORRECT -> this.background(
            shape = RoundedCornerShape(8.dp),
            color = incorrectColor
        )

        LetterVerificationResult.MISPLACED -> this
            .background(
                MaterialTheme.colorScheme.secondary,
                shape = CircleShape
            )
            .fillMaxSize()
    }

@Composable
fun InputAttempt(
    modifier: Modifier = Modifier,
    firstLetter: String,
    onSubmitAttempt: (String) -> Unit,
    enabled: Boolean = true,
) {
    var text by rememberSaveable { mutableStateOf(firstLetter) }
    LaunchedEffect(firstLetter) {
        text = firstLetter
    }
    Row(
        modifier
            .fillMaxWidth()
            .padding(
                8.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = text,
            modifier = Modifier.weight(1.0f),
            singleLine = true,
            enabled = enabled,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                imeAction = if (text.length == WORD_LENGTH) ImeAction.Send else ImeAction.None
            ),
            onValueChange = { if (it.length <= WORD_LENGTH) text = it.uppercase() },
            keyboardActions = KeyboardActions(onSend = {
                onSubmitAttempt.invoke(text)
            }),
        )
        Button(
            onClick = {
                onSubmitAttempt(text)
                text = ""
            },
            enabled = text.length == WORD_LENGTH && enabled,
        ) {
            Text(text = "Valider")
        }

    }

}