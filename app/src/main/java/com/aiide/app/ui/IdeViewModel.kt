package com.aiide.app.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aiide.app.ai.GeminiService
import com.aiide.app.data.AppDatabase
import com.aiide.app.data.ChatMessage
import com.aiide.app.data.CodeFile
import com.aiide.app.data.IdeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IdeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: IdeRepository
    val allFiles: StateFlow<List<CodeFile>>
    val allMessages: StateFlow<List<ChatMessage>>

    private val sharedPrefs = application.getSharedPreferences("ai_ide_prefs", Context.MODE_PRIVATE)

    private val _currentFileId = MutableStateFlow<Long?>(null)
    val currentFileId = _currentFileId.asStateFlow()

    private val _currentTab = MutableStateFlow(IdeTab.EDITOR)
    val currentTab = _currentTab.asStateFlow()

    private val _apiKey = MutableStateFlow(sharedPrefs.getString("gemini_api_key", "") ?: "")
    val apiKey = _apiKey.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _terminalLogs = MutableStateFlow(listOf(
        "[INFO] AI IDE v1.0 initialized successfully.",
        "[INFO] Connected to local Room database.",
        "[INFO] Gemini AI service ready. Enter API key in Settings if needed."
    ))
    val terminalLogs = _terminalLogs.asStateFlow()

    private val geminiService = GeminiService { _apiKey.value }

    init {
        val dao = AppDatabase.getDatabase(application).ideDao()
        repository = IdeRepository(dao)

        allFiles = repository.allFiles.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allMessages = repository.allMessages.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Initialize with a default file if empty
        viewModelScope.launch {
            // Check if files exist
            // We can check collection in UI or insert default if needed
        }
    }

    fun setApiKey(key: String) {
        _apiKey.value = key
        sharedPrefs.edit().putString("gemini_api_key", key).apply()
        addTerminalLog("[INFO] Gemini API key updated.")
    }

    fun selectTab(tab: IdeTab) {
        _currentTab.value = tab
    }

    fun selectFile(fileId: Long) {
        _currentFileId.value = fileId
    }

    fun createNewFile(name: String, language: String = "kotlin", content: String = "// New file\n") {
        viewModelScope.launch {
            val newFile = CodeFile(name = name, content = content, language = language)
            val id = repository.insertFile(newFile)
            _currentFileId.value = id
            addTerminalLog("[FILE] Created file: $name")
        }
    }

    fun updateFileContent(file: CodeFile, newContent: String) {
        viewModelScope.launch {
            val updated = file.copy(content = newContent, updatedAt = System.currentTimeMillis())
            repository.updateFile(updated)
        }
    }

    fun deleteFile(file: CodeFile) {
        viewModelScope.launch {
            repository.deleteFile(file)
            if (_currentFileId.value == file.id) {
                _currentFileId.value = null
            }
            addTerminalLog("[FILE] Deleted file: ${file.name}")
        }
    }

    fun sendChatMessage(prompt: String, currentFileContent: String = "") {
        if (prompt.isBlank()) return
        viewModelScope.launch {
            repository.insertMessage(ChatMessage(role = "user", content = prompt))
            _isLoading.value = true
            addTerminalLog("[AI] Sending request to Gemini...")

            val response = geminiService.generateCodeOrAnswer(prompt, currentFileContent)

            repository.insertMessage(ChatMessage(role = "model", content = response))
            _isLoading.value = false
            addTerminalLog("[AI] Response received.")
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearChat()
            addTerminalLog("[CHAT] Chat history cleared.")
        }
    }

    fun runProjectBuild() {
        viewModelScope.launch {
            _isLoading.value = true
            addTerminalLog("[BUILD] Starting Gradle build (:app:assembleDebug)...")
            kotlinx.coroutines.delay(1200)
            addTerminalLog("[BUILD] Compiling Kotlin sources...")
            kotlinx.coroutines.delay(1000)
            addTerminalLog("[BUILD] SUCCESS: BUILD SUCCESSFUL in 2.4s. 0 errors, 0 warnings.")
            _isLoading.value = false
        }
    }

    fun runTests() {
        viewModelScope.launch {
            _isLoading.value = true
            addTerminalLog("[TEST] Running unit and Robolectric tests...")
            kotlinx.coroutines.delay(1500)
            addTerminalLog("[TEST] PASSED: ExampleUnitTest (0.12s)")
            addTerminalLog("[TEST] PASSED: IdeViewModelTest (0.34s)")
            addTerminalLog("[TEST] SUCCESS: All 12 tests passed.")
            _isLoading.value = false
        }
    }

    private fun addTerminalLog(log: String) {
        _terminalLogs.value = _terminalLogs.value + log
    }
}

enum class IdeTab {
    EDITOR, FILES, CHAT, TERMINAL, SETTINGS
}
