package com.xware

import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Request
import org.schabi.newpipe.extractor.downloader.Response
import java.util.concurrent.TimeUnit

/**
 * NewPipeExtractor가 필요로 하는 HTTP Downloader 구현체.
 * OkHttp를 사용하여 실제 네트워크 요청 수행.
 */
class NpeDownloader private constructor() : Downloader() {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .followRedirects(true)
        .addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .header("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/124.0.0.0 Safari/537.36")
                    .build()
            )
        }
        .build()

    companion object {
        val instance by lazy { NpeDownloader() }
    }

    override fun execute(request: Request): Response {
        val reqBuilder = okhttp3.Request.Builder().url(request.url())

        // 헤더 복사
        request.headers()?.forEach { (key, values) ->
            values?.forEach { value -> reqBuilder.addHeader(key, value) }
        }

        // HTTP 메서드
        val httpReq = when (request.httpMethod()) {
            "POST" -> {
                val body = request.dataToSend()?.toRequestBody()
                    ?: "".toRequestBody()
                reqBuilder.post(body).build()
            }
            "DELETE" -> reqBuilder.delete().build()
            else -> reqBuilder.get().build()
        }

        val okResp = client.newCall(httpReq).execute()
        val responseBody = okResp.body?.string() ?: ""

        return Response(
            okResp.code,
            okResp.message,
            okResp.headers.toMultimap(),
            responseBody,
            okResp.request.url.toString()
        )
    }
}
