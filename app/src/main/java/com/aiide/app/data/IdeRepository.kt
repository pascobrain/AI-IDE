package com.aiide.app.data

import kotlinx.coroutines.flow.Flow

class IdeRepository(private val ideDao: IdeDao) {
    val allFiles: Flow<List<CodeFile>> = ideDao.getAllFiles()
    val allMessages: Flow<List<ChatMessage>> = ideDao.getAllMessages()

    suspend fun getFileById(id: Long): CodeFile? = ideDao.getFileById(id)

    suspend fun insertFile(file: CodeFile): Long {
        return ideDao.insertFile(file)
    }

    suspend fun updateFile(file: CodeFile) {
        ideDao.updateFile(file)
    }

    suspend fun deleteFile(file: CodeFile) {
        ideDao.deleteFile(file)
    }

    suspend fun insertMessage(message: ChatMessage): Long {
        return ideDao.insertMessage(message)
    }

    suspend fun clearChat() {
        ideDao.clearChat()
    }
}
