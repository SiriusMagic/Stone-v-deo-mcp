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
import com.zirakusa.stone.nativeapp.ui.theme.StonePalette

/** Stone Video puts video first: Videos, Shorts, Carpetas (of videos), Playlists, MV; music after. */
enum class LibraryTab(val label: Int) {
    Videos(R.string.videos), Shorts(R.string.tab_shorts), Folders(R.string.folders), Playlists(R.string.playlists),
    MusicVideos(R.string.tab_music_videos), Songs(R.string.songs), Artists(R.string.artists), Albums(R.string.albums),
    Genres(R.string.genres);

    val isVideo: Boolean get() = this == Videos || this == Shorts || this == Folders || this == MusicVideos

    /** Videos and Carpetas hold every video; Shorts and MV follow [VideoKind]; the music tabs every song. */
    fun accepts(song: Song): Boolean = when (this) {
        Videos, Folders -> song.isVideo
        Shorts -> VideoKind.isShort(song)
        MusicVideos -> VideoKind.isMusicVideo(song)
        else -> !song.isVideo
    }
}

/** Big bold tab names with a short accent bar under the active one. */
@Composable fun LibraryTabs(selected: LibraryTab, landscape: Boolean = false, onSelect: (LibraryTab) -> Unit) {
    Row(Modifier.padding(top = if (landscape) 0.dp else 4.dp).height(if (landscape) 48.dp else 58.dp).fillMaxWidth()
        .horizontalScroll(rememberScrollState()).selectableGroup().padding(horizontal = if (landscape) 8.dp else 12.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        LibraryTab.entries.forEach { tab ->
            val active = tab == selected
            Column(Modifier.clip(RoundedCornerShape(10.dp))
                .selectable(active, role = Role.Tab, onClick = { onSelect(tab) })
                .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(tab.label),
                    color = if (active) StonePalette.Text else StonePalette.Secondary.copy(alpha = .75f),
                    fontSize = if (landscape) 17.sp else 22.sp,
                    fontWeight = if (active) FontWeight.ExtraBold else FontWeight.Bold, letterSpacing = 0.sp)
                Box(Modifier.padding(top = 4.dp).size(width = 26.dp, height = 4.dp).clip(RoundedCornerShape(2.dp))
                    .background(if (active) StonePalette.Yellow else Color.Transparent))
            }
        }
    }
}
