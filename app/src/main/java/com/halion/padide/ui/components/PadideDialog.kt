package com.halion.padide.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun PadideDialog(
    modifier: Modifier = Modifier,
    message: String = "Padide",
    onDismissRequest: () -> Unit = { },
    label: String = "Enter your name",
    onSubmit: (String) -> Unit = { },
    openDialog: Boolean = true,
) {
    var openDialog by remember { mutableStateOf(openDialog) }
    var valueTextFiled by remember { mutableStateOf("") }

    if (openDialog) {
        Dialog(onDismissRequest = {
            openDialog = false
            onDismissRequest()
        }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(message)
                Spacer(modifier = Modifier.padding(8.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = valueTextFiled,
                    onValueChange = { valueTextFiled = it },
                    label = { Text(label) }
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        openDialog = false
                        onSubmit(valueTextFiled)
                    }
                ) {
                    Text("OK")
                }
            }
        }
    }
}

@Preview(name = "PadideDialog")
@Composable
private fun PreviewPadideDialog() {
    PadideDialog()
}