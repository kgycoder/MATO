package com.xware

import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.stream.StreamInfo
import org.schabi.newpipe.extractor.stream.AudioStream

/**
 * NewPipeExtractor 기반 YouTube 스트림 추출기.
 * Rhino JS 엔진이 내장되어 nsig 복호화를 자체 처리.
 * 서버 없이 YouTube 오디오를 직접 추출 가능.
 */
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

            val ytService  = NewPipe.getService(ServiceList.YouTube)
            val streamInfo = org.schabi.newpipe.extractor.stream.StreamInfo
                .getInfo(ytService, url)

            android.util.Log.d("XWare/NPE",
                "Got StreamInfo: title=${streamInfo.name}, " +
                "audioStreams=${streamInfo.audioStreams.size}")

            // 최고 품질 오디오 선택 (opus > aac, bitrate 높은 순)
            val best = streamInfo.audioStreams
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
                "Best audio: ${best.format?.mimeType} ${best.bitrate}bps")

            StreamInfo(
                audioUrl     = best.url ?: return null,
                title        = streamInfo.name ?: "",
                duration     = streamInfo.duration,
                thumbnailUrl = streamInfo.thumbnails
                    .maxByOrNull { it.height }?.url
                    ?: "https://i.ytimg.com/vi/$videoId/hqdefault.jpg"
            )

        } catch (e: Exception) {
            android.util.Log.e("XWare/NPE", "extractAudio failed: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}
