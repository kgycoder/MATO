package com.xware

import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.stream.AudioStream
import org.schabi.newpipe.extractor.stream.StreamInfo

class YouTubeExtractor {

    private var initialized = false

    private fun ensureInit() {
        if (!initialized) {
            try {
                NewPipe.init(NpeDownloader.instance)
                initialized = true
                android.util.Log.d("XWare/NPE", "NewPipe initialized")
            } catch (e: Exception) {
                android.util.Log.e("XWare/NPE", "init failed: ${e.message}")
            }
        }
    }

    data class StreamInfo(
        val audioUrl: String,
        val title: String,
        val duration: Long,
        val thumbnailUrl: String
    )

    fun extractAudio(videoId: String): StreamInfo? {
        ensureInit()
        return try {
            val url = "https://www.youtube.com/watch?v=$videoId"
            android.util.Log.d("XWare/NPE", "Extracting: $url")

            // ServiceList.YouTube 는 StreamingService 객체 → 직접 getInfo 에 전달
            val info = org.schabi.newpipe.extractor.stream.StreamInfo
                .getInfo(ServiceList.YouTube, url)

            android.util.Log.d("XWare/NPE",
                "Got StreamInfo: title=${info.name}, " +
                "audioStreams=${info.audioStreams.size}")

            val best = info.audioStreams
                .filter { it.url?.isNotEmpty() == true }
                .sortedWith(
                    compareByDescending<AudioStream> {
                        it.format?.mimeType?.contains("opus") == true
                    }.thenByDescending { it.bitrate }
                )
                .firstOrNull()

            if (best == null) {
                android.util.Log.w("XWare/NPE", "No audio stream found")
                return null
            }

            android.util.Log.d("XWare/NPE",
                "Best: ${best.format?.mimeType} ${best.bitrate}bps")

            StreamInfo(
                audioUrl     = best.url ?: return null,
                title        = info.name ?: "",
                duration     = info.duration,
                thumbnailUrl = info.thumbnails
                    .maxByOrNull { it.height }?.url
                    ?: "https://i.ytimg.com/vi/$videoId/hqdefault.jpg"
            )

        } catch (e: Exception) {
            android.util.Log.e("XWare/NPE", "extractAudio failed: ${e.message}")
            null
        }
    }
}
