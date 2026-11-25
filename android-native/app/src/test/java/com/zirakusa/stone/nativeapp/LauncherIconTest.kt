package com.zirakusa.stone.nativeapp

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.test.core.app.ApplicationProvider
import com.zirakusa.stone.nativeapp.ui.theme.LauncherIcon
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Stone must always have exactly one home-screen entry, whatever state Android kept. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LauncherIconTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    private fun launcherEntries(): List<String> = context.packageManager.queryIntentActivities(
        Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).setPackage(context.packageName), 0)
        .map { it.activityInfo.name.substringAfterLast('.') }

    @Test fun freshInstallShowsOnlyTheOtter() {
        assertEquals(listOf("Home"), launcherEntries())
    }

    @Test fun choosingAMascotKeepsOneEntry() {
        LauncherIcon.apply(context, LauncherIcon.KITTEN)
        assertEquals(listOf("HomeFox"), launcherEntries())                 // old alias name, new mascot
        LauncherIcon.apply(context, LauncherIcon.SEAL)
        assertEquals(listOf("HomeSeal"), launcherEntries())
        LauncherIcon.apply(context, LauncherIcon.OTTER)
        assertEquals(listOf("Home"), launcherEntries())
    }

    @Test fun startUpRestoresAnEntryWhenAllWereDisabled() {
        LauncherIcon.entries.forEach {
            context.packageManager.setComponentEnabledSetting(ComponentName(context, "com.zirakusa.stone.nativeapp.${it.alias}"),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
        }
        LauncherIcon.load(context)
        assertEquals(listOf("Home"), launcherEntries())
    }

    @Test fun fiveMascotsEachWithItsOwnEntry() {
        assertEquals(listOf("otter", "lamb", "seal", "red_panda", "kitten"), LauncherIcon.entries.map { it.key })
        assertEquals(5, LauncherIcon.entries.map { it.alias }.toSet().size)
        LauncherIcon.entries.forEach { LauncherIcon.apply(context, it); assertEquals(listOf(it.alias), launcherEntries()) }
        assertEquals(LauncherIcon.OTTER, LauncherIcon.from("penguin"))                  // gone since 0.5.1
    }

    @Test fun theOldOtterAliasMovesToHome() {
        // A phone updated from 0.5.0 with the otter chosen: .HomeKoala on, .Home off.
        val pm = context.packageManager
        pm.setComponentEnabledSetting(ComponentName(context, "com.zirakusa.stone.nativeapp.Home"), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
        pm.setComponentEnabledSetting(ComponentName(context, "com.zirakusa.stone.nativeapp.HomeKoala"), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
        LauncherIcon.load(context)
        assertEquals(LauncherIcon.OTTER, LauncherIcon.current)
        assertEquals(listOf("Home"), launcherEntries())
    }
}
