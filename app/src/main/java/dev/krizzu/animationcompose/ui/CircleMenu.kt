package dev.krizzu.animationcompose.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Constraints
import dev.krizzu.animationcompose.R
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

const val ZINDEX_MENU_BUTTON = 10f
const val ZINDEX_MENU_WRAPPER = 5f
const val ZINDEX_MENU_ITEM = 7f
const val MAX_MENU_ITEMS = 7 // what's the limit of the items in menu

// ratio of how big the circle menu should be (against the screen width)
const val MENU_SHAPE_RATIO = 1.75
const val MAX_MENU_BUTTON_SIZE = 160

// for content to be visible, we need to operate on II quadrant
const val CIRCLE_QUADRANT_OFFSET_DEG = 180
const val BUTTON_ROTATION_DEG = 135f

val MENU_BACKGROUND_COLOR = Color(0xff353E59)
val BUTTON_CLOSED_COLOR = Color(0xff487EFA)
val BUTTON_OPEN_MENU = Color(0xff3B445B)

@Composable
fun CircleMenu(
    menuItemSize: Int = 150, menuContent: @Composable (Boolean, (Boolean) -> Unit) -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    Layout(content = {
        ControlButton(isVisible) { isVisible = it }
        MenuWrapper(
            isVisible = isVisible,
            itemSize = menuItemSize,
        ) { menuContent(isVisible) { isVisible = it } }
    }) { measurables, constraints ->

        val maxWidth = constraints.maxWidth
        val maxHeight = constraints.maxHeight
        val (button, menuWrapper) = measurables
        val buttonPlaceable =
            button.measure(Constraints.fixed(MAX_MENU_BUTTON_SIZE, MAX_MENU_BUTTON_SIZE))
        val contentPlaceable = menuWrapper.measure(
            constraints.copy(
                maxWidth = (maxWidth * MENU_SHAPE_RATIO).toInt(),
                maxHeight = (maxWidth * MENU_SHAPE_RATIO).toInt(),
            )
        )

        layout(constraints.maxWidth, constraints.minHeight) {
            val buttonOffsetFromEdge = MAX_MENU_BUTTON_SIZE + MAX_MENU_BUTTON_SIZE / 2
            buttonPlaceable.place(
                maxWidth - buttonOffsetFromEdge,
                maxHeight - buttonOffsetFromEdge,
                ZINDEX_MENU_BUTTON
            )

            contentPlaceable.place(
                maxWidth - contentPlaceable.width / 2,
                maxHeight - contentPlaceable.height / 2,
                ZINDEX_MENU_WRAPPER
            )
        }
    }
}


@Composable
fun ControlButton(isVisible: Boolean, updateVisibleState: (Boolean) -> Unit) {
    val bgColor by animateColorAsState(if (isVisible) BUTTON_OPEN_MENU else BUTTON_CLOSED_COLOR)
    val image = painterResource(id = R.drawable.add)
    val rotationFloat by animateFloatAsState(
        if (isVisible) BUTTON_ROTATION_DEG else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )

    Box(
        modifier = Modifier.fillMaxSize().clip(CircleShape)
            .clickable { updateVisibleState(!isVisible) }.background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = image, contentDescription = "", modifier = Modifier.rotate(rotationFloat)
        )
    }
}

@Composable
fun MenuWrapper(
    itemSize: Int, isVisible: Boolean, content: @Composable () -> Unit
) {
    val animVal by animateFloatAsState(
        if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing)
    )

    Box(
        modifier = Modifier.fillMaxSize().alpha(animVal).scale(animVal)
            .clip(RoundedCornerShape(topStartPercent = 50)).background(MENU_BACKGROUND_COLOR)
    ) {
        Layout(content = { content() }) { measurables, constraints ->

            if (measurables.size > MAX_MENU_ITEMS) {
                throw Error("Too many Composables provided to Circle Menu - max is $MAX_MENU_ITEMS")
            }

            val maxWidth = (constraints.maxWidth / MENU_SHAPE_RATIO).toInt()
            val maxHeight = (constraints.maxHeight / MENU_SHAPE_RATIO).toInt()
            val a = maxWidth * MENU_SHAPE_RATIO
            val x0 = a / 2
            val y0 = a / 2
            val r = a / 2

            val placeables = measurables.map {
                it.measure(
                    constraints.copy(
                        maxHeight = itemSize, maxWidth = itemSize, minHeight = 0, minWidth = 0
                    )
                )
            }

            layout(maxWidth, maxHeight) {
                val rows = placeables.chunked(4)

                rows.forEachIndexed { index, placeables ->

                    val spaceFromTheEdge = itemSize / 2
                    val rowOffset = index * itemSize * 1.5 // space between each rows
                    val offsetR = r - spaceFromTheEdge - rowOffset

                    // calculate how much space each item in a row takes
                    val maxQuadrantDeg = 90 // we operate on a quarter of cull circle
                    val decreasePerRowDeg = 12 // each new row will decrease in size available
                    val availableRowSpaceDeg = maxQuadrantDeg - (decreasePerRowDeg * (index + 1))
                    val paddingOffsetDeg = maxQuadrantDeg - availableRowSpaceDeg
                    val spacePerItemDeg = availableRowSpaceDeg / placeables.size

                    var currentAngleDeg = ((spacePerItemDeg / 2) + paddingOffsetDeg / 2)

                    placeables.forEach {
                        val rad = ((currentAngleDeg + CIRCLE_QUADRANT_OFFSET_DEG) * PI) / 180
                        val x = x0 + (offsetR * cos(rad))
                        val y = y0 + (offsetR * sin(rad))
                        it.place(x.toInt(), y.toInt(), ZINDEX_MENU_ITEM)
                        currentAngleDeg += spacePerItemDeg
                    }
                }
            }
        }
    }
}