package com.olaf.rereminder.data

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Reminder(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val scheduledTime: Long,
    val isEnabled: Boolean = true,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis(),
    val dismissedAt: Long? = null
)
