package com.zirakusa.stone.nativeapp.ui.theme

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.zirakusa.stone.nativeapp.R

/**
 * Stone's mascots. The one chosen in Personalizado is shown inside the app (header, rail,
 * about) and as the home-screen icon. Each mascot is an `activity-alias` in the manifest with
 * its own adaptive icon; Stone keeps exactly one of them enabled.
 *
 * Alias names must never change: Android keeps the enabled state per name, so renaming or
 * removing one can leave the app with no launcher entry after an update.
 */
enum class LauncherIcon(val key: String, val alias: String, val drawable: Int, val label: Int) {
    // Since 0.5.0 the old aliases carry the new mascots (panda → red panda, fox → kitten). Since 0.5.1 the otter is the
    // default in ".Home"; ".HomeKoala" ([LEGACY], the otter in 0.5.0) only moves over to it and is switched off.
    OTTER("otter", "Home", R.drawable.mascot_otter, R.string.mascot_otter),
    LAMB("lamb", "HomeLamb", R.drawable.mascot_lamb, R.string.mascot_lamb),
    SEAL("seal", "HomeSeal", R.drawable.mascot_seal, R.string.mascot_seal),
    RED_PANDA("red_panda", "HomePanda", R.drawable.mascot_red_panda, R.string.mascot_red_panda),
    KITTEN("kitten", "HomeFox", R.drawable.mascot_kitten, R.string.mascot_kitten);

    companion object {
        /** The mascot currently in use, read from the package manager at start-up. */
        val DEFAULT = OTTER
        /** The otter's alias in 0.5.0: still in the manifest so an update never leaves Stone without an icon. */
        const val LEGACY = "HomeKoala"
        var current by mutableStateOf(DEFAULT)
            private set

        fun from(key: String) = entries.firstOrNull { it.key == key } ?: DEFAULT

        /**
         * Reads which alias is enabled. If none is (for example after updating from a build that
         * had other icons), the otter is enabled again so Stone never loses its launcher entry; the
         * old otter alias ([LEGACY]) moves over to `.Home`.
         */
        fun load(context: Context) {
            val enabled = entries.firstOrNull { it.isEnabled(context) }
            val legacy = runCatching { context.packageManager.getComponentEnabledSetting(component(context, LEGACY)) }
                .getOrDefault(PackageManager.COMPONENT_ENABLED_STATE_DISABLED) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            if (enabled == null || legacy) apply(context, enabled ?: DEFAULT) else current = enabled
        }

        /**
         * Enables [icon] and disables the others without killing the app. Launchers refresh the
         * icon on their own; some take a few seconds or need the home screen to be reopened.
         */
        fun apply(context: Context, icon: LauncherIcon) {
            val manager = context.packageManager
            // Enable the new one first so the app always has a launcher entry.
            runCatching { manager.setComponentEnabledSetting(icon.component(context),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP) }
            (entries.filter { it != icon }.map { it.alias } + LEGACY).forEach {
                runCatching { manager.setComponentEnabledSetting(component(context, it),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP) }
            }
            current = icon
        }

        private fun LauncherIcon.isEnabled(context: Context): Boolean {
            val state = runCatching { context.packageManager.getComponentEnabledSetting(component(context)) }
                .getOrDefault(PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
            // DEFAULT means "as declared in the manifest": only ".Home" (the otter) starts enabled.
            return state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED ||
                (state == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT && this == DEFAULT)
        }

        private fun LauncherIcon.component(context: Context) = component(context, alias)
        private fun component(context: Context, alias: String) = ComponentName(context, "com.zirakusa.stone.nativeapp.$alias")
    }
}
