package com.ismartcoding.plain.ui.components.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ismartcoding.plain.extensions.formatDate
import com.ismartcoding.plain.ui.models.VChat

@Composable
fun ChatDate(
    items: List<VChat>,
    m: VChat,
    index: Int,
) {
    val dateVisible =
        remember {
            if (index == items.size - 1) {
                true
            } else {
                if (index + 1 < items.size) {
                    items[index + 1].createdAt.formatDate() != m.createdAt.formatDate()
                } else {
                    false
                }
            }
        }
    if (dateVisible) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = m.createdAt.formatDate(),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Normal),
            )
        }
    }
}
