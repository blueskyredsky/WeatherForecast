package com.currentweather.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ErrorItem(
    modifier: Modifier = Modifier,
    content: @Composable (ColumnScope.() -> Unit) = {},
    action: () -> Unit,
    buttonContent: @Composable RowScope.() -> Unit
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        content()
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = action
        ) {
            buttonContent()
        }
    }
}