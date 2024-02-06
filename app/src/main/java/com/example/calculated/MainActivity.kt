package com.example.calculated

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculated.ui.theme.CalculatedTheme
import com.example.calculated.ui.theme.Cyan
import com.example.calculated.ui.theme.Red

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatedTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary
                ) {

                    val calcButtons = remember {
                        mutableStateListOf(
                            CalcButton("AC", CalcButtonType.Reset),
                            CalcButton("AC", CalcButtonType.Reset),
                            CalcButton("AC", CalcButtonType.Reset),
                            CalcButton("÷", CalcButtonType.Action),

                            CalcButton("7", CalcButtonType.Normal),
                            CalcButton("8", CalcButtonType.Normal),
                            CalcButton("9", CalcButtonType.Normal),

                            CalcButton("x", CalcButtonType.Action),

                            CalcButton("4", CalcButtonType.Normal),
                            CalcButton("5", CalcButtonType.Normal),
                            CalcButton("6", CalcButtonType.Normal),

                            CalcButton("-", CalcButtonType.Action),

                            CalcButton("1", CalcButtonType.Normal),
                            CalcButton("2", CalcButtonType.Normal),
                            CalcButton("3", CalcButtonType.Normal),

                            CalcButton("+", CalcButtonType.Action),

                            CalcButton(
                                icon = Icons.Outlined.Refresh,
                                type = CalcButtonType.Reset
                            ),
                            CalcButton("0", CalcButtonType.Normal),
                            CalcButton(".", CalcButtonType.Normal),

                            CalcButton("=", CalcButtonType.Action)
                        )
                    }
                    val (uiText, setUiText) = remember {
                        mutableStateOf("0")
                    }
                    LaunchedEffect(uiText) {
                        if (uiText.startsWith("0") && uiText != "0") {
                            setUiText(uiText.substring(1))
                        }
                    }
                    val (input, setInput) = remember {
                        mutableStateOf<String?>(null)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Column {
                            Text(
                                modifier = Modifier.padding(8.dp),
                                text = uiText,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            LazyVerticalGrid(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .background(MaterialTheme.colorScheme.secondary),
                                columns = GridCells.Fixed(4),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                items(calcButtons) {
                                    CalculatorButton(
                                        button = it,
                                        onClick = {
                                            when (it.type) {
                                                CalcButtonType.Normal -> {
                                                    runCatching {
                                                        setUiText(
                                                            uiText.toInt().toString() + it.text
                                                        )
                                                    }.onFailure { throwable ->
                                                        setUiText(uiText + it.text)
                                                    }
                                                    setInput((input ?: "") + it.text)
                                                    if (viewModel.action.value.isNotEmpty()) {
                                                        if (viewModel.secondNumber.value == null) {
                                                            viewModel.setSecondNumber(it.text!!.toDouble())
                                                        } else {
                                                            if (viewModel.secondNumber.value.toString()
                                                                    .split(".")[1] == "0"
                                                            ) {
                                                                viewModel.setSecondNumber(
                                                                    (viewModel.secondNumber.value.toString()
                                                                        .split(".")
                                                                        .first() + it.text!!).toDouble()
                                                                )
                                                            } else {
                                                                viewModel.setSecondNumber((viewModel.secondNumber.value.toString() + it.text!!).toDouble())

                                                            }
                                                        }
                                                    }
                                                }

                                                CalcButtonType.Action -> {
                                                    if (it.text == "=") {
                                                        val result = viewModel.getResult()
                                                        setUiText(result.toString())
                                                        setInput(null)
                                                        viewModel.resetAll()
                                                    } else {
                                                        runCatching {
                                                            setUiText(
                                                                uiText.toInt().toString() + it.text
                                                            )
                                                        }.onFailure { throwable ->
                                                            setUiText(uiText + it.text)
                                                        }
                                                        if (input != null) {
                                                            if (viewModel.firstNumber.value == null) {
                                                                viewModel.setFirstNumber(input.toDouble())
                                                            } else {
                                                                viewModel.setSecondNumber(input.toDouble())
                                                            }
                                                            viewModel.setAction(it.text!!)
                                                            setInput(null)
                                                        }
                                                    }
                                                }

                                                CalcButtonType.Reset -> {
                                                    setUiText("")
                                                    setInput(null)
                                                    viewModel.resetAll()
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .clip(
                                    RoundedCornerShape(8.dp)
                                )
                        )
                        {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(id = R.drawable.ic_darkmode),
                                contentDescription = null,
                                tint = Color.White
                            )

                            Icon(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(id = R.drawable.ic_lightmode),
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(button: CalcButton, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primary)
            .fillMaxHeight()
            .aspectRatio(1f)
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        val contentColor =
            when (button.type) {
                CalcButtonType.Normal -> Color.White
                CalcButtonType.Action -> Red
                else -> Cyan
            }
        if (button.text != null) {
            Text(
                button.text,
                color = contentColor,
                fontWeight = FontWeight.Bold,
                fontSize = if (button.type == CalcButtonType.Action) 25.sp else 20.sp
            )
        } else {
            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = button.icon!!,
                contentDescription = null,
                tint = contentColor
            )
        }
    }
}

data class CalcButton(
    val text: String? = null,
    val type: CalcButtonType,
    val icon: ImageVector? = null
)

enum class CalcButtonType {
    Normal, Action, Reset
}