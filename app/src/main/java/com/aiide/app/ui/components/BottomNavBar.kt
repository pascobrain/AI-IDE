package com.aiide.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.aiide.app.ui.IdeTab

@Composable
fun BottomNavBar(
    currentTab: IdeTab,
    onTabSelected: (IdeTab) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentTab == IdeTab.EDITOR,
            onClick = { onTabSelected(IdeTab.EDITOR) },
            icon = { Icon(Icons.Default.EditNote, contentDescription = "Editor") },
            label = { Text("Editor") },
            modifier = Modifier.testTag("nav_editor")
        )
        NavigationBarItem(
            selected = currentTab == IdeTab.FILES,
            onClick = { onTabSelected(IdeTab.FILES) },
            icon = { Icon(Icons.Default.Folder, contentDescription = "Files") },
            label = { Text("Files") },
            modifier = Modifier.testTag("nav_files")
        )
        NavigationBarItem(
            selected = currentTab == IdeTab.CHAT,
            onClick = { onTabSelected(IdeTab.CHAT) },
            icon = { Icon(Icons.Default.Chat, contentDescription = "AI Copilot") },
            label = { Text("AI Copilot") },
            modifier = Modifier.testTag("nav_chat")
        )
        NavigationBarItem(
            selected = currentTab == IdeTab.TERMINAL,
            onClick = { onTabSelected(IdeTab.TERMINAL) },
            icon = { Icon(Icons.Default.Terminal, contentDescription = "Terminal") },
            label = { Text("Terminal") },
            modifier = Modifier.testTag("nav_terminal")
        )
        NavigationBarItem(
            selected = currentTab == IdeTab.SETTINGS,
            onClick = { onTabSelected(IdeTab.SETTINGS) },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            modifier = Modifier.testTag("nav_settings")
        )
    }
}
