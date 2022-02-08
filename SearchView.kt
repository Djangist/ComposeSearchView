import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight.Companion.W600
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp

@ExperimentalUnitApi
@Composable
fun SearchView(
    modifier: Modifier = Modifier,
    value: String = "",
    hintText: String = "",
    hintColor: Color = colorResource(R.color.text_secondary),
    textStyle: TextStyle = myTypography.body1,
    contentDescription: String = "search widget",
    cornerRadius: Dp = 8.dp,
    showCancelButton: Boolean = false,
    cancelButtonText: String = stringResource(R.string.search_view_cancel_button),
    fontFamily: FontFamily? = null,
    enabled: Boolean = true,
    textChangedCallback: (String) -> Unit = {},
) {
    val text = rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(value)) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }
    val showHint = remember { mutableStateOf(true) }

    val animatedBorderColor by animateColorAsState(
        targetValue = when {
            isFocused -> colorResource(id = R.color.text_primary)
            else -> Color(0xFFDDDEE7) 
        }
    )
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            enabled = enabled,
            value = text.value.text,
            textStyle = textStyle?.copy(
                color = colorResource(R.color.text_main),
                textDecoration = TextDecoration.None,
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                fontFamily = fontFamily,
                fontWeight = W600
            ),
            singleLine = true,
            onValueChange = {
                showHint.value = it.isEmpty()
                text.value = TextFieldValue(it)
                textChangedCallback(it)
            },
            cursorBrush = SolidColor(colorResource(R.color.text_primary)),
            modifier = modifier
                .focusRequester(focusRequester)
                .onFocusChanged {
                    isFocused = it.isFocused
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        if (enabled) {
                            isFocused = true
                            focusRequester.requestFocus()
                        }
                    }
                ),
            decorationBox = { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(
                            if (isFocused) 2.dp else 1.dp,
                            animatedBorderColor,
                            shape = RoundedCornerShape(cornerRadius)
                        )
                        .defaultMinSize(minHeight = 40.dp)
                        .clipToBounds()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.action_search24),
                        contentDescription = contentDescription,
                        modifier = Modifier
                            .padding(start = 8.dp),
                        tint = colorResource(R.color.icons_secondary)
                    )
                    Spacer(modifier = Modifier.padding(start = 4.dp))
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (showHint.value) {
                            Text(
                                text = hintText,
                                style = TextStyle(fontSize = TextUnit(14.0f, TextUnitType.Sp), color = hintColor),
                                fontFamily = fontFamily,
                                fontWeight = W600
                            )
                        }
                        innerTextField()
                    }
                    if (text.value.text.isNotEmpty()) {
                        Icon(
                            modifier = Modifier
                                .clickable {
                                    text.value = TextFieldValue("")
                                    showHint.value = true
                                }
                                .padding(start = 4.dp, end = 8.dp, bottom = 8.dp, top = 8.dp)
                                .size(24.dp),
                            painter = painterResource(R.drawable.action_search_clear24),
                            contentDescription = contentDescription,
                        )
                    }
                }
            }
        )
        if (showCancelButton && isFocused) {
            Spacer(modifier = Modifier.padding(start = 6.dp))
            Text(
                text = cancelButtonText,
                color = colorResource(R.color.text_primary),
                maxLines = 1,
                modifier = Modifier.clickable {
                    focusManager.clearFocus()
                },
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                fontFamily = fontFamily,
                fontWeight = W600
            )
        }
    }
}

@ExperimentalUnitApi
@Preview
@Composable
fun SearchViewPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(colorResource(R.color.surface_main))
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(4.dp)
        ) {
            SearchView(
                hintText = "Поиск",
                modifier = Modifier.padding(4.dp),
                showCancelButton = true,
                fontFamily = myCreditFonts,
            )
        }
    }
}
