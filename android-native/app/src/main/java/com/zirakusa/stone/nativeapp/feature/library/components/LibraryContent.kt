package com.zirakusa.stone.nativeapp.feature.library.components

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zirakusa.stone.core.Song
import com.zirakusa.stone.nativeapp.R
import com.zirakusa.stone.nativeapp.feature.library.LibraryState
import com.zirakusa.stone.nativeapp.ui.components.*
import com.zirakusa.stone.nativeapp.ui.theme.StoneLook
import com.zirakusa.stone.nativeapp.ui.theme.StonePalette
import com.zirakusa.stone.nativeapp.ui.system.ImmersiveWindowEffect
import com.zirakusa.stone.nativeapp.feature.library.video.VideoLibraryContent
import com.zirakusa.stone.nativeapp.feature.library.video.VideoLibraryLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun LibraryContent(state: LibraryState, songs: List<Song>, tab: LibraryTab,
    collection: String?, select: (String?) -> Unit, searching: Boolean, canPlay: Boolean,
    currentId: String, onPlay: (Song, List<Song>) -> Unit, onFolder: () -> Unit,
    modifier: Modifier = Modifier, onCreatePlaylist: (String) -> Unit = {},
    onRemoveFromPlaylist: (String, Song) -> Unit = { _, _ -> },
    onDelete: (Song) -> Unit = {}, onAudioOnly: (Song) -> Unit = {},
    compact: Boolean = false,
    onVideoOptions: (Song) -> Unit = {},
    videoLibraryLayout: VideoLibraryLayout = VideoLibraryLayout.GRID,
    onVideoLibraryLayoutChange: (VideoLibraryLayout) -> Unit = {},
    onSetPlaylistCover: (String, Uri) -> Unit = { _, _ -> },
    onRestorePlaylistCover: (String) -> Unit = {},
    selectionActions: LibrarySelectionActions = LibrarySelectionActions()) {
    val mediaSongs = songs.filter(tab::accepts)
    if (tab.isVideo) {
        VideoLibraryContent(mediaSongs, state, currentId, modifier, onPlay, onFolder,
            compact, videoLibraryLayout, onVideoLibraryLayoutChange, onVideoOptions, onDelete, selectionActions.onTagsSaved,
            startInFolders = tab == LibraryTab.Folders || tab == LibraryTab.Genres, shorts = tab == LibraryTab.Shorts,
            byGenre = tab == LibraryTab.Genres)
        return
    }
    val visible = mediaSongs
    val songLayout = VideoLibraryLayout.fromKey(StoneLook.songLayout)
    val dense = compact || songLayout == VideoLibraryLayout.COMPACT
    var managing by remember(collection) { mutableStateOf(false) }
    var selectedIds by remember(collection) { mutableStateOf<Set<String>>(emptySet()) }
    var menuVisible by remember(collection) { mutableStateOf(false) }
    var deleteConfirmVisible by remember(collection) { mutableStateOf(false) }
    var tagSong by remember { mutableStateOf<Song?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current
    androidx.activity.compose.BackHandler(enabled = managing) { managing = false; selectedIds = emptySet() }
    Column(modifier.fillMaxWidth()) {
    LazyColumn(Modifier.weight(1f).fillMaxWidth().testTag("library-list"), contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp)) {
        run {
            if (!compact && tab == LibraryTab.Music && collection == null) item {
                Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(pluralStringResource(R.plurals.song_count, visible.size, visible.size), Modifier.weight(1f),
                        color = StonePalette.SongSubtitle, fontSize = 15.sp)
                    IconButton(onClick = onFolder, enabled = !state.scanning) {
                        Icon(Icons.Default.CreateNewFolder, stringResource(R.string.choose_folder))
                    }
                    LayoutSelector(songLayout, { StoneLook.chooseSongLayout(it.key) })
                }
            }
            if (managing) item {
                SelectionHeader(selectedIds.size, visible.size, onExit = { managing = false; selectedIds = emptySet() },
                    onToggleAll = {
                        selectedIds = if (selectedIds.size == visible.size) emptySet() else visible.map { it.id }.toSet()
                    })
            } else if (collection != null) item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StoneIconButton(StoneGlyph.Back, stringResource(R.string.back), { select(null) })
                    Text(collection, Modifier.weight(1f))
                    IconButton(onClick = { menuVisible = true }) {
                        Icon(Icons.Default.MoreVert, stringResource(R.string.more_options))
                    }
                }
            }
            if (visible.isEmpty() && !state.scanning) item {
                if (searching) Text(stringResource(R.string.no_results), Modifier.padding(vertical = 32.dp), color = StonePalette.Muted)
                else LibraryEmptyState(videos = tab.isVideo)
            }
            if (songLayout == VideoLibraryLayout.GRID && !compact) {
                // "Grande": two big covers per row.
                items(visible.chunked(2), key = { it.first().id }) { pair ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        pair.forEach { song ->
                            Box(Modifier.weight(1f)) {
                                SongGridCard(song, current = song.id == currentId,
                                    checked = if (managing) song.id in selectedIds else null,
                                    onEdit = if (managing && selectedIds.size == 1 && song.id in selectedIds) {
                                        { tagSong = song }
                                    } else null,
                                    onLongClick = { if (!managing) { managing = true; selectedIds = setOf(song.id) } },
                                    onClick = {
                                        if (managing) selectedIds = if (song.id in selectedIds) selectedIds - song.id
                                            else selectedIds + song.id
                                        else if (canPlay) onPlay(song, visible)
                                    })
                            }
                        }
                        if (pair.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            } else items(visible, key = { it.id }) { song ->
                if (managing) Row(Modifier.fillMaxWidth().clickable {
                    selectedIds = if (song.id in selectedIds) selectedIds - song.id else selectedIds + song.id
                }, verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(song.id in selectedIds, null,
                        colors = RadioButtonDefaults.colors(selectedColor = StonePalette.Blue))
                    Box(Modifier.weight(1f)) {
                        LibrarySongRow(song, song.id == currentId, false, compact = dense,
                            trailing = if (selectedIds.size == 1 && song.id in selectedIds) {
                                { EditTagsButton { tagSong = song } }
                            } else null) { }
                    }
                } else LibrarySongRow(song, song.id == currentId, canPlay, compact = dense,
                    onLongClick = { managing = true; selectedIds = setOf(song.id) }) { onPlay(song, visible) }
            }
        }
    }
    val selectedSongs = visible.filter { it.id in selectedIds }
    if (managing) SelectionActionBar(videos = false, enabled = selectedIds.isNotEmpty(),
        onPlayNext = { selectionActions.onPlayNext(selectedSongs); managing = false; selectedIds = emptySet() },
        onShare = { runCatching { com.zirakusa.stone.nativeapp.data.library.user.SongActions.shareMany(context, selectedSongs) } },
        onDelete = { deleteConfirmVisible = true })
    }
    if (menuVisible) ModalBottomSheet(onDismissRequest = { menuVisible = false }) {
        ImmersiveWindowEffect()
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
            TextButton(onClick = { menuVisible = false; managing = true }) {
                Icon(Icons.Default.CheckCircle, null); Spacer(Modifier.width(12.dp)); Text(stringResource(R.string.manage))
            }
            TextButton(onClick = {
                menuVisible = false; selectedIds = visible.map { it.id }.toSet(); managing = true; deleteConfirmVisible = true
            }) {
                Icon(Icons.Default.Delete, null); Spacer(Modifier.width(12.dp)); Text(stringResource(R.string.delete_album))
            }
        }
    }
    tagSong?.let { song ->
        com.zirakusa.stone.nativeapp.feature.player.sheets.TagEditorSheet(song, close = { tagSong = null },
            saved = { tagSong = null; managing = false; selectedIds = emptySet(); selectionActions.onTagsSaved() })
    }
    if (deleteConfirmVisible) AlertDialog(onDismissRequest = { deleteConfirmVisible = false },
        title = { ImmersiveWindowEffect(); Text(stringResource(R.string.delete)) },
        text = { Text(pluralStringResource(R.plurals.delete_selected_message, selectedIds.size, selectedIds.size)) },
        confirmButton = { TextButton(onClick = {
            visible.filter { it.id in selectedIds }.forEach(onDelete)
            deleteConfirmVisible = false; managing = false; selectedIds = emptySet()
        }) { Text(stringResource(R.string.delete)) } },
        dismissButton = { TextButton(onClick = { deleteConfirmVisible = false }) {
            Text(stringResource(R.string.cancel))
        } })
}

/** Callbacks used by long-press selection in the library lists. */
class LibrarySelectionActions(
    val onAddToPlaylist: (String, Song) -> Unit = { _, _ -> },
    val onPlayNext: (List<Song>) -> Unit = {},
    val onTagsSaved: () -> Unit = {},
)
