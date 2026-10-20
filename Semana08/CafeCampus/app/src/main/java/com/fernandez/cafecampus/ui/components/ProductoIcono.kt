
package com.fernandez.cafecampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProductoIcono(
    icono: String,
    modifier: Modifier = Modifier,
    size: Dp = 70.dp
) {
    val simbolo = when (icono) {
        "cafe" -> "☕"
        "plato" -> "🍽️"
        "jugo" -> "🍹"
        "pan" -> "🥪"
        "botella" -> "🥤"
        "dulce" -> "🧁"
        else -> "🍴"
    }

    Box(
        modifier = modifier
            .size(size)
            .background(
                Color(0xFFFFEDD5),
                RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = simbolo,
            fontSize = 32.sp
        )
    }
}
