package com.aiide.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.aiide.app.data.CodeFile
import com.aiide.app.ui.IdeTab
import com.aiide.app.ui.IdeViewModel
import com.aiide.app.ui.components.BottomNavBar
import com.aiide.app.ui.components.TopBar
import com.aiide.app.ui.screens.*
import com.aiide.app.ui.theme.AIIDETheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: IdeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AIIDETheme {
                val files by viewModel.allFiles.collectAsState()
                val currentFileId by viewModel.currentFileId.collectAsState()
                val currentTab by viewModel.currentTab.collectAsState()
                val messages by viewModel.allMessages.collectAsState()
                val apiKey by viewModel.apiKey.collectAsState()
                val isLoading by viewModel.isLoading.collectAsState()
                val terminalLogs by viewModel.terminalLogs.collectAsState()

                val coroutineScope = rememberCoroutineScope()

                // Seed initial files if empty
                LaunchedEffect(files) {
                    if (files.isEmpty()) {
                        viewModel.createNewFile(
                            name = "MainActivity.kt",
                            language = "kotlin",
                            content = """package com.aiide.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Text("Hello from AI IDE!")
        }
    }
}"""
                        )
                        viewModel.createNewFile(
                            name = "utils.kt",
                            language = "kotlin",
                            content = """package com.aiide.app.utils

// Utility functions for AI IDE
fun formatCode(code: String): String {
    return code.trim()
}"""
                        )
                    }
                }

                val currentFile = files.find { it.id == currentFileId } ?: files.firstOrNull()

                Scaffold(
                    topBar = {
                        TopBar(
                            currentFile = currentFile,
                            onBuildClick = {
                                viewModel.runProjectBuild()
                                viewModel.selectTab(IdeTab.TERMINAL)
                            },
                            onRunClick = {
                                viewModel.runProjectBuild()
                                viewModel.selectTab(IdeTab.TERMINAL)
                            },
                            onAiQuickAssist = {
                                viewModel.selectTab(IdeTab.CHAT)
                            }
                        )
                    },
                    bottomBar = {
                        BottomNavBar(
                            currentTab = currentTab,
                            onTabSelected = { viewModel.selectTab(it) }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentTab) {
                            IdeTab.EDITOR -> EditorScreen(
                                currentFile = currentFile,
                                files = files,
                                onSelectFile = { viewModel.selectFile(it) },
                                onContentChanged = { file, content ->
                                    viewModel.updateFileContent(file, content)
                                },
                                onAiAction = { prompt, code ->
                                    viewModel.selectTab(IdeTab.CHAT)
                                    viewModel.sendChatMessage(prompt, code)
                                }
                            )
                            IdeTab.FILES -> FilesScreen(
                                files = files,
                                currentFileId = currentFileId,
                                onSelectFile = { id ->
                                    viewModel.selectFile(id)
                                    viewModel.selectTab(IdeTab.EDITOR)
                                },
                                onCreateFile = { name, lang, content ->
                                    viewModel.createNewFile(name, lang, content)
                                    viewModel.selectTab(IdeTab.EDITOR)
                                },
                                onDeleteFile = { file ->
                                    viewModel.deleteFile(file)
                                }
                            )
                            IdeTab.CHAT -> ChatScreen(
                                messages = messages,
                                isLoading = isLoading,
                                onSendMessage = { prompt ->
                                    viewModel.sendChatMessage(prompt, currentFile?.content ?: "")
                                },
                                onClearChat = {
                                    viewModel.clearChat()
                                }
                            )
                            IdeTab.TERMINAL -> TerminalScreen(
                                logs = terminalLogs,
                                isLoading = isLoading,
                                onRunBuild = { viewModel.runProjectBuild() },
                                onRunTests = { viewModel.runTests() }
                            )
                            IdeTab.SETTINGS -> SettingsScreen(
                                currentApiKey = apiKey,
                                onSaveApiKey = { key ->
                                    viewModel.setApiKey(key)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
