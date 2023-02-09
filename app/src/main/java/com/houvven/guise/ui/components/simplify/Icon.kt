package com.houvven.guise.ui.components.simplify


import androidx.compose.foundation.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale


@Composable
fun SimplifyIcon(
    painter: Painter,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(painter, null, modifier, tint)
}


@Composable
fun SimplifyIcon(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(imageVector, null, modifier, tint)
}


@Composable
fun SimplifyIcon(
    bitmap: ImageBitmap,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(bitmap, null, modifier, tint)
}

@Composable
fun SimplifyImage(
    painter: Painter,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null
) {
    Image(painter, null, modifier, alignment, contentScale, alpha, colorFilter)
}

@Composable
fun SimplifyImage(
    bitmap: ImageBitmap,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality
) {
    Image(bitmap, null, modifier, alignment, contentScale, alpha, colorFilter, filterQuality)
}

@Composable
fun SimplifyImage(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null
) {
    Image(imageVector, null, modifier, alignment, contentScale, alpha, colorFilter)
}