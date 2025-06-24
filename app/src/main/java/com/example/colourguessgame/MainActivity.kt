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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.colourguessgame.ui.theme.ColourGuessGameTheme
import com.example.colourguessgame.ui.theme.Typography


private const val MAX_ITEMS_IN_ROW: Int = 3

class MainActivity : ComponentActivity() {
    private var answersState = mutableStateOf(RoundVariables())
    @OptIn(ExperimentalStdlibApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ColourGuessGameTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ColorPresenter(
                        modifier = Modifier
                            .padding(innerPadding)
                            .background(
                                answersState.value.background
                            ),
                        submitAnswer = { color -> submitAnswer(color) },
                        answers = answersState.value.answers,
                        colorCode = answersState.value.correctAnswer.value.toHexString(format = HexFormat.UpperCase).substring(0, 6),
                    )
                }
            }
        }
    }

    private fun submitAnswer(answer: Color) {
        if (answer == answersState.value.correctAnswer) {
            victory()
        } else {
            answersState.value = answersState.value.copy(
                answers = answersState.value.answers.filter { it != answer }
            )
        }
    }

    private fun victory() {
        answersState.value = answersState.value.copy(
            background = Color.Green,
        )
        //startNewRound()
    }

    private fun startNewRound() {
        answersState.value = RoundVariables()
    }
}

data class RoundVariables(
    val correctAnswer: Color = Color(0xFFFFFFFF),
    val answers: List<Color> = mutableListOf(Color(0XFFFFFFFF), Color(0XFF00FFFF), Color(0XFFFF00FF), Color(0XFFFFFF00)),
    val background: Color = Color(0xFFFFFFFF)
)

@Composable
fun ColorPresenter(
    modifier: Modifier = Modifier,
    submitAnswer: (Color) -> Unit,
    answers: List<Color>,
    colorCode: String,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Guess the color",
            style = Typography.headlineLarge,
        )
        Text(
            text = colorCode,
            style = Typography.headlineMedium,
        )
        PossibleAnswers(variants = answers, submitAnswer = submitAnswer)
    }
}

@Composable
fun PossibleAnswers(
    variants: List<Color>,
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
        items(variants) { variant ->
            Card(
                shape = CardDefaults.elevatedShape,
                modifier = Modifier
                    .padding(5.dp)
                    .aspectRatio(1f),
                onClick = { submitAnswer(variant) },
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().background(variant)
                )
            }
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ColourGuessGameTheme {
        ColorPresenter()
    }
}*/
