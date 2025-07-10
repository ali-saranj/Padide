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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.halion.padide.ui.theme.PadideFontFamily

@Composable
fun PadideDialog(
    modifier: Modifier = Modifier,
    value: String = "",
    message: String = "Padide",
    onDismissRequest: () -> Unit = { },
    label: String = "Enter your name",
    onSubmit: (String) -> Unit = { },
    openDialog: Boolean = true,
) {
    var openDialog by remember { mutableStateOf(openDialog) }
    var valueTextFiled by remember { mutableStateOf(value) }
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
                Text(message, fontFamily = PadideFontFamily())
                Spacer(modifier = Modifier.padding(8.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = valueTextFiled,
                    singleLine = true,
                    maxLines = 1,
                    onValueChange = { valueTextFiled = it },
                    label = { Text(label, fontFamily = PadideFontFamily()) },
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        openDialog = false
                        onSubmit(valueTextFiled)
                    }
                ) {
                    Text("ثبت", fontFamily = PadideFontFamily())
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