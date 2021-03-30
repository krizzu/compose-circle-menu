package dev.krizzu.animationcompose.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

// https://dribbble.com/shots/6978977-Menu

@Composable
fun MainContent() {
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {

        val menuItemSize = 160f
        val maxMenuItems = 7

        CircleMenu(menuItemSize = menuItemSize.toInt()) { isVisible, _ ->

            // ideally, should be calculated based on menu size and position
            // good for now as for preview
            val offsets = listOf(
                listOf(menuItemSize * 2f, menuItemSize * 0.5f),
                listOf(menuItemSize * 1.5f, menuItemSize),
                listOf(menuItemSize, menuItemSize * 1.5f),
                listOf(menuItemSize * 0.5f, menuItemSize * 2f),
                listOf(menuItemSize * 1.5f, menuItemSize * 0.5f),
                listOf(menuItemSize, menuItemSize),
                listOf(menuItemSize * 0.5f, menuItemSize * 1.5f)
            )

            for (i in 0 until maxMenuItems) {
                MyMenuItem(index = i, isMenuVisible = isVisible, offsets = offsets[i])
            }
        }
    }
}


@Preview
@Composable
fun ContentPreview() {
    MainContent()
}