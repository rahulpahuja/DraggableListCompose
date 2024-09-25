package com.rp.draggableviewandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.rp.draggableviewandroid.ui.theme.DraggableViewAndroidTheme


class DraggableItemActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val items = remember { mutableStateListOf("Item 1", "Item 2", "Item 3", "Item 4") }
            DraggableViewAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                   MyScreen()
                }
            }
        }
    }
}


@Composable
fun DraggableList(
    items: List<String>,
    onMove: (Int, Int) -> Unit
) {
    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffsetY by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    LazyColumn {
        itemsIndexed(items) { index, item ->
            val isBeingDragged = draggedIndex == index

            Box(
                modifier = Modifier
                    .zIndex(if (isBeingDragged) 1f else 0f) // Bring dragged item to front
                    .offset {
                        if (isBeingDragged) {
                            IntOffset(0, dragOffsetY.toInt())
                        } else {
                            IntOffset.Zero
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = {
                                draggedIndex = index
                                isDragging = true
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                dragOffsetY += dragAmount.y
                            },
                            onDragEnd = {
                                // Determine target index based on offset position
                                val targetIndex = (draggedIndex!! + (dragOffsetY / 100).toInt())
                                    .coerceIn(0, items.size - 1)

                                // Perform the item swap
                                if (targetIndex != draggedIndex) {
                                    onMove(draggedIndex!!, targetIndex)
                                }

                                // Reset state
                                dragOffsetY = 0f
                                draggedIndex = null
                                isDragging = false
                            },
                            onDragCancel = {
                                dragOffsetY = 0f
                                draggedIndex = null
                                isDragging = false
                            }
                        )
                    }
                    .background(
                        if (isBeingDragged) Color.Blue else Color.Green,
                        shape = RectangleShape
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Text(text = item)
                    Text(text = "Rahul Pahuja")
                }

            }
        }
    }
}

@Composable
fun MyScreen() {
    val items = remember { mutableStateListOf("Item 1", "Item 2", "Item 3", "Item 4") }

    DraggableList(items = items) { fromIndex, toIndex ->
        val item = items.removeAt(fromIndex)
        items.add(toIndex.coerceIn(0, items.size), item)
    }
}
