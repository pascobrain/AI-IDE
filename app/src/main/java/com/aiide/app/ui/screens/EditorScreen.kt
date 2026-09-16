package com.aiide.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aiide.app.data.CodeFile
import com.aiide.app.ui.theme.EditorBackground

@Composable
fun EditorScreen(
    currentFile: CodeFile?,
    files: List<CodeFile>,
    onSelectFile: (Long) -> Unit,
    onContentChanged: (CodeFile, String) -> Unit,
    onAiAction: (String, String) -> Unit
) {
    if (currentFile == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(EditorBackground),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No File Open",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Select a file from the Files tab or create a new one to start coding.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (files.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onSelectFile(files.first().id) },
                        modifier = Modifier.testTag("open_first_file_btn")
                    ) {
                        Text("Open ${files.first().name}")
                    }
                }
            }
        }
        return
    }

    var textValue by remember(currentFile.id) { mutableStateOf(currentFile.content) }
    val verticalScrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EditorBackground)
    ) {
        // Quick Toolbar
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(
                        onClick = { onAiAction("Explain this code and suggest improvements", textValue) },
                        label = { Text("Explain", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(14.dp)) },
                        modifier = Modifier.testTag("ai_explain_chip")
                    )
                    AssistChip(
                        onClick = { onAiAction("Refactor and optimize this code for best practices and readability", textValue) },
                        label = { Text("Refactor", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(14.dp)) },
                        modifier = Modifier.testTag("ai_refactor_chip")
                    )
                    AssistChip(
                        onClick = { onAiAction("Write comprehensive unit tests for this code", textValue) },
                        label = { Text("Test", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Science, contentDescription = null, modifier = Modifier.size(14.dp)) },
                        modifier = Modifier.testTag("ai_test_chip")
                    )
                }

                Text(
                    text = currentFile.language.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Code Editor area with line numbers and text field
        Row(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            // Line numbers column
            val lineCount = textValue.count { it == '\n' } + 1
            val lineNumbersText = buildString {
                for (i in 1..lineCount) {
                    append("$i\n")
                }
            }
            Column(
                modifier = Modifier
                    .width(48.dp)
                    .fillMaxHeight()
                    .background(Color(0xFF0D1117))
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = lineNumbersText,
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = Color(0xFF484F58)
                    ),
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            // Editable Code
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .verticalScroll(verticalScrollState)
                    .padding(12.dp)
            ) {
                BasicTextField(
                    value = textValue,
                    onValueChange = {
                        textValue = it
                        onContentChanged(currentFile, it)
                    },
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = Color(0xFFE6EDF3)
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("code_editor_input")
                )
            }
        }
    }
}
