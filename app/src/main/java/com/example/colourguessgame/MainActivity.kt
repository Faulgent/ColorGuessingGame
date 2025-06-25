package com.example.colourguessgame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.colourguessgame.ui.theme.ColourGuessGameTheme
import com.example.colourguessgame.ui.theme.Typography
import java.lang.Math.random

private const val MAX_ITEMS_IN_ROW: Int = 3

class MainActivity : ComponentActivity() {
    private var gameState = mutableStateOf(GameVariables(RoundVariables()))

    @OptIn(ExperimentalStdlibApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ColourGuessGameTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GameUi(
                        modifier = Modifier
                            .padding(innerPadding),
                        submitAnswer = { color -> submitAnswer(color) },
                        gameState = gameState.value,
                        colorCode = '#' + gameState.value.roundVariables.correctAnswer.value.toHexString(
                            format = HexFormat.UpperCase
                        )
                            .substring(2, 8),
                        startNewRound = { startNewRound() },
                        startNewGame = { startNewGame() },
                    )
                }
            }
        }
    }

    private fun submitAnswer(answer: Color) {
        when {
            answer == gameState.value.roundVariables.correctAnswer -> roundVictory()
            gameState.value.lives > 1 -> wrongAnswer(answer)
            else -> gameLost()
        }
    }

    private fun gameLost() {
        gameState.value = gameState.value.copy(
            lives = gameState.value.lives - 1,
        )
    }

    private fun wrongAnswer(answer: Color) {
        gameState.value = gameState.value.copy(
            roundVariables = gameState.value.roundVariables.copy(
                answers = gameState.value.roundVariables.answers.filter { it != answer },
            ),
            lives = gameState.value.lives - 1,
        )
    }

    private fun roundVictory() {
        gameState.value = gameState.value.copy(
            roundVariables = gameState.value.roundVariables.copy(
                shouldStartNewRound = true,
            ),
            points = gameState.value.points + 1,
        )
    }

    private fun startNewRound() {
        gameState.value = gameState.value.copy(
            roundVariables = RoundVariables()
        )
    }

    private fun startNewGame() {
        gameState.value = GameVariables(RoundVariables())

    }
}

data class GameVariables(
    val roundVariables: RoundVariables,
    val lives: Int = 5,
    val points: Int = 0,
)

data class RoundVariables(
    val answers: List<Color> = generateColorList(4),
    val correctAnswer: Color = answers.random(),
    val shouldStartNewRound: Boolean = false,
)

fun generateColorList(counter: Int): List<Color> {
    val colorList = mutableListOf<Color>()
    for (i in 0 until counter) {
        colorList.add(
            Color(
                alpha = 1f,
                red = random().toFloat(),
                green = random().toFloat(),
                blue = random().toFloat()
            )
        )
    }
    return colorList
}

@Composable
fun GameUi(
    modifier: Modifier = Modifier,
    submitAnswer: (Color) -> Unit,
    gameState: GameVariables,
    colorCode: String,
    startNewRound: () -> Unit,
    startNewGame: () -> Unit,
) {
    if (gameState.lives == 0) {
        FinalScore(
            gameState = gameState,
            startNewGame = startNewGame,
        )
    } else {
        GameScreen(
            modifier = modifier,
            submitAnswer = submitAnswer,
            gameState = gameState,
            colorCode = colorCode,
            startNewRound = startNewRound,
        )
    }
}

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    submitAnswer: (Color) -> Unit,
    gameState: GameVariables,
    colorCode: String,
    startNewRound: () -> Unit,
) {
    Column(
        modifier = modifier
            .background(Color(0xFF808080))
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Life: ${gameState.lives}",
            style = Typography.headlineMedium,
        )
        Text(
            text = "Points: ${gameState.points}",
            style = Typography.headlineMedium,
        )
        Text(
            text = "Guess the color",
            style = Typography.headlineLarge,
        )
        Text(
            text = colorCode,
            style = Typography.headlineMedium,
        )
        PossibleAnswers(gameState = gameState, submitAnswer = submitAnswer)
        if (gameState.roundVariables.shouldStartNewRound) {
            Button(
                onClick = { startNewRound() },
                modifier = Modifier.padding(top = 20.dp),
            ) {
                Text(text = "Next Round!")
            }
        }
    }
}

@Composable
fun FinalScore(
    gameState: GameVariables,
    startNewGame: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .background(Color(0xFF27a567))
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Game Over",
            style = Typography.headlineLarge,
        )
        Text(
            text = "Your final score is ${gameState.points}",
            style = Typography.headlineMedium,
        )
        Button(
            onClick = startNewGame,
        ) {
            Text(text = "Start New Game")
        }
    }
}

@Composable
fun PossibleAnswers(
    gameState: GameVariables,
    submitAnswer: (Color) -> Unit,
) {
    LazyVerticalGrid(
        modifier = Modifier
            .wrapContentSize()
            .padding(15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.SpaceBetween,
        columns = GridCells.Fixed(MAX_ITEMS_IN_ROW),
    ) {
        items(gameState.roundVariables.answers) { variant ->
            Card(
                shape = CardDefaults.elevatedShape,
                modifier = Modifier
                    .padding(5.dp)
                    .aspectRatio(1f),
                onClick = if (!gameState.roundVariables.shouldStartNewRound) {
                    { submitAnswer(variant) }
                } else {
                    {}
                },
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(variant)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ColourGuessGameTheme {
        GameUi(
            submitAnswer = {},
            gameState = GameVariables(
                roundVariables = RoundVariables(),
            ),
            colorCode = "",
            startNewRound = {},
            startNewGame = {},
        )
    }
}
