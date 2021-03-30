package dev.krizzu.animationcompose.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.krizzu.animationcompose.R
import kotlinx.coroutines.delay

data class ItemSpec(val title: String, val color: Color, val resId: Int)

val ITEM_SPECS = listOf(
    ItemSpec("Voice", Color(0xFFDFE5FF), R.drawable.icon_voice),
    ItemSpec("Notes", Color(0xFFD0E8EB), R.drawable.icon_notes),
    ItemSpec("Link", Color(0xFFA0DFBB), R.drawable.icon_link),
    ItemSpec("Template", Color(0xFFFFDFDA), R.drawable.icon_template),
    ItemSpec("Notes", Color(0xFF8DC0F8), R.drawable.icon_notes),
    ItemSpec("Scan", Color(0xFFFEEFD8), R.drawable.icon_scan),
    ItemSpec("Upload", Color(0xFFFF9770), R.drawable.icon_upload),
)

@Composable
fun MyMenuItem(index: Int, isMenuVisible: Boolean, offsets: List<Float>) {
    val animSpec = spring<Dp>(dampingRatio = 0.8f, stiffness = 100f)
    val (xOffset, yOffset) = remember { offsets }

    var animDriver by remember { mutableStateOf(isMenuVisible) }
    val spec = ITEM_SPECS[index]


    LaunchedEffect(isMenuVisible) {
        if (isMenuVisible) {
            // delay animation a bit when becoming visible
            delay((index + 1) * 15L)
        }
        animDriver = isMenuVisible
    }

    val animX by animateDpAsState(
        if (animDriver) 0.dp else xOffset.dp, animationSpec = animSpec
    )
    val animY by animateDpAsState(
        if (animDriver) 0.dp else yOffset.dp, animationSpec = animSpec
    )

    val img = painterResource(id = spec.resId)

    Column(
        Modifier.fillMaxSize().offset(x = animX, y = animY),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier.fillMaxSize(0.7f).clip(CircleShape).background(spec.color),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = img,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(0.45f),
                colorFilter = ColorFilter.tint(Color.Black)
            )
        }
        Text(
            modifier = Modifier.fillMaxSize().padding(top = 4.dp),
            text = spec.title,
            color = Color(0xFFA6AABD),
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}