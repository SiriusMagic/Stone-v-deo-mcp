package com.zirakusa.stone.nativeapp.feature.library

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class QuickActionDefinition(val id: String, val label: String, val description: String,
    val icon: ImageVector, val available: Boolean)

/** Single source of truth for home shortcuts. Unavailable actions stay out of the operational UI. */
object QuickActionRegistry {
    /** Actions that stay at the front of every preset profile. Custom can reorder or hide them. */
    val pinnedIds = listOf("sort")

    /** Actions that have a real destination or operation in the current app. */
    private val available = setOf("diagnostics", "api_mode")

    /** Stone Video plays always in Cine (0–300 %): no audio-mode shortcuts. "sort" is added by LibraryScreen. */
    val preferredIds = emptyList<String>()
    val defaultVisibleIds = preferredIds.toSet()

    val definitions = listOf(
        QuickActionDefinition("diagnostics", "Diagnóstico de rendimiento", "CPU, RAM y audio", Icons.Default.Memory, true),
        QuickActionDefinition("api_mode", "Modo MCP", "Conecta un asistente de IA", Icons.Default.Hub, true),
    )
    val operational = definitions.filter { it.available && it.id in available }
}
