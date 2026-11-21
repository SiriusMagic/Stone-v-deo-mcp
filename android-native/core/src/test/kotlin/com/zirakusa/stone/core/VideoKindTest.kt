package com.zirakusa.stone.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VideoKindTest {
    private fun video(title: String = "clip", ms: Long = 120_000, w: Int = 0, h: Int = 0, artist: String = "",
        folder: String = "Camera") = Song("content://v/$title", title, artist, "", folder, ms, 1, 1, isVideo = true,
        width = w, height = h)

    @Test fun verticalClipsAreShorts() {
        assertTrue(VideoKind.isShort(video(w = 1080, h = 1920, ms = 64_000)))
        assertTrue(VideoKind.isShort(video(w = 720, h = 1280, ms = 295_000)))
        assertFalse(VideoKind.isShort(video(w = 1920, h = 1080, ms = 30_000)))
        assertFalse(VideoKind.isShort(video(w = 1080, h = 1920, ms = 40 * 60_000)))
    }

    @Test fun unknownSizeFallsBackToDuration() {
        assertTrue(VideoKind.isShort(video(ms = 45_000)))
        assertFalse(VideoKind.isShort(video(ms = 5 * 60_000)))
        assertFalse(VideoKind.isShort(Song("content://a", "a", "", "", "", 30_000, 1, 1)))
    }

    @Test fun musicVideosByTitleFolderOrArtist() {
        assertTrue(VideoKind.isMusicVideo(video("Artista - Canción (Video Oficial)", w = 1920, h = 1080)))
        assertTrue(VideoKind.isMusicVideo(video("cualquier", folder = "Música", w = 1920, h = 1080)))
        assertTrue(VideoKind.isMusicVideo(video("tema", artist = "Shakira", ms = 200_000, w = 1920, h = 1080)))
        assertFalse(VideoKind.isMusicVideo(video("cumpleaños", w = 1920, h = 1080)))
        assertFalse(VideoKind.isMusicVideo(video("x (official video)", w = 1080, h = 1920, ms = 50_000)))
    }

    @Test fun rotationSwapsSize() {
        assertEquals(1080 to 1920, VideoKind.displaySize(1920, 1080, 90))
        assertEquals(1920 to 1080, VideoKind.displaySize(1920, 1080, 0))
        assertEquals(1080 to 1920, VideoKind.displaySize(1920, 1080, -90))
    }
}
