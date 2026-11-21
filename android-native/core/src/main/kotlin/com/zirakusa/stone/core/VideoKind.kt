package com.zirakusa.stone.core

/**
 * Pure rules that sort videos into Stone Video's tabs (Shorts, MV). No Android here, so they are
 * unit tested and shared by the library and the MCP tools.
 */
object VideoKind {
    /** Longest clip still counted as a short when its picture size is unknown. */
    const val SHORT_FALLBACK_MS = 60_000L
    /** A vertical video longer than this is a normal video recorded upright, not a short. */
    const val SHORT_MAX_MS = 10 * 60_000L

    private val musicWords = listOf("official video", "official music video", "video oficial", "videoclip",
        "video clip", "lyric video", "lyrics video", "video lyric", "visualizer", "(mv)", "[mv]", " m/v", "live session",
        "en vivo", "audio oficial", "official audio")
    private val musicFolders = listOf("music", "música", "musica", "mv", "videoclips", "clips musicales")

    /** Vertical (taller than wide) and not long; clips with unknown size count when they are under a minute. */
    fun isShort(song: Song): Boolean {
        if (!song.isVideo) return false
        if (song.width > 0 && song.height > 0) return song.height > song.width && song.durationMs in 1..SHORT_MAX_MS
        return song.durationMs in 1..SHORT_FALLBACK_MS
    }

    /** Music video: words like "official video" in the title, a music folder, or an artist tag on a song-length clip. */
    fun isMusicVideo(song: Song): Boolean {
        if (!song.isVideo || isShort(song)) return false
        val title = " ${song.title.lowercase()}"
        if (musicWords.any { it in title }) return true
        if (song.folder.trim().lowercase() in musicFolders) return true
        return song.artist.isNotBlank() && song.durationMs in 90_000L..12 * 60_000L
    }

    /** Width and height as seen on screen: MediaStore stores the encoded size plus a rotation. */
    fun displaySize(width: Int, height: Int, rotation: Int): Pair<Int, Int> =
        if (((rotation % 360) + 360) % 360 in setOf(90, 270)) height to width else width to height
}
