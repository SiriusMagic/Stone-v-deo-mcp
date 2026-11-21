package com.zirakusa.stone.nativeapp.feature.library.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zirakusa.stone.core.Song
import com.zirakusa.stone.core.VideoKind
import com.zirakusa.stone.nativeapp.R
import com.zirakusa.stone.nativeapp.ui.components.StoneGlyph
import com.zirakusa.stone.nativeapp.ui.components.StoneIcon
import com.zirakusa.stone.nativeapp.ui.theme.StonePalette

/**
 * Stone Video: Música first with a divider (the mirror of Stone, where Videos goes first), then the video tabs.
 * Géneros groups videos by their genre tag (terror, romance…), not music.
 */
enum class LibraryTab(val label: Int) {
    Music(R.string.tab_music), Videos(R.string.videos), Shorts(R.string.tab_shorts), Folders(R.string.folders),
    Playlists(R.string.playlists), MusicVideos(R.string.tab_music_videos), Genres(R.string.genres);

    val isVideo: Boolean get() = this != Music && this != Playlists

    /** Música holds every song; Shorts and MV follow [VideoKind]; the other video tabs hold every video. */
    fun accepts(song: Song): Boolean = when (this) {
        Music -> !song.isVideo
        Shorts -> VideoKind.isShort(song)
        MusicVideos -> VideoKind.isMusicVideo(song)
        else -> song.isVideo
    }
}

/** Big bold tab names with a short accent bar under the active one; Música carries a note and a divider. */
@Composable fun LibraryTabs(selected: LibraryTab, landscape: Boolean = false, onSelect: (LibraryTab) -> Unit) {
    Row(Modifier.padding(top = if (landscape) 0.dp else 4.dp).height(if (landscape) 48.dp else 58.dp).fillMaxWidth()
        .horizontalScroll(rememberScrollState()).selectableGroup().padding(horizontal = if (landscape) 8.dp else 12.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        LibraryTab.entries.forEach { tab ->
            val active = tab == selected
            val color = if (active) StonePalette.Text else StonePalette.Secondary.copy(alpha = .75f)
            Column(Modifier.clip(RoundedCornerShape(10.dp))
                .selectable(active, role = Role.Tab, onClick = { onSelect(tab) })
                .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (tab == LibraryTab.Music) StoneIcon(StoneGlyph.Music, color = color, size = if (landscape) 16 else 20)
                    Text(stringResource(tab.label), Modifier.padding(start = if (tab == LibraryTab.Music) 4.dp else 0.dp),
                        color = color, fontSize = if (landscape) 17.sp else 22.sp,
                        fontWeight = if (active) FontWeight.ExtraBold else FontWeight.Bold, letterSpacing = 0.sp)
                }
                Box(Modifier.padding(top = 4.dp).size(width = 26.dp, height = 4.dp).clip(RoundedCornerShape(2.dp))
                    .background(if (active) StonePalette.Yellow else Color.Transparent))
            }
            // Divider between the music tab and the video tabs.
            if (tab == LibraryTab.Music) Box(Modifier.padding(horizontal = 6.dp).width(1.5.dp).height(24.dp)
                .background(StonePalette.Muted.copy(alpha = .6f)))
        }
    }
}
