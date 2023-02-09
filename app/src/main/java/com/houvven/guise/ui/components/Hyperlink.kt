package com.houvven.guise.ui.components

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import com.houvven.guise.util.android.IntentUtils

@Composable
fun Hyperlink(
    modifier: Modifier = Modifier,
    label: String? = null,
    url: String,
    color: Color = LocalContentColor.current,
    style: TextStyle = TextStyle.Default,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {}
) {
    val string = buildAnnotatedString {
        append(label ?: url)
        addStyle(SpanStyle(color = color), 0, length)
    }
    ClickableText(
        text = string,
        modifier = modifier,
        style = style,
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = onTextLayout
    ) {
        IntentUtils.openBrowser(url)
    }
}

@Composable
fun EmailHyperLink(
    modifier: Modifier = Modifier,
    label: String? = null,
    address: String,
    color: Color = LocalContentColor.current,
    style: TextStyle = TextStyle.Default,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {}
) {
    val string = buildAnnotatedString {
        append(label ?: address)
        addStyle(SpanStyle(color = color), 0, length)
    }
    ClickableText(
        text = string,
        modifier = modifier,
        style = style,
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = onTextLayout
    ) {
        IntentUtils.openEmail(address)
    }
}