package org.deepmediq.deepgram

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class DeepgramClient(private val apiKey: String) {
    private val client = HttpClient()

    suspend fun transcribeAudio(audioBytes: ByteArray, mimeType: String = "audio/m4a"): String {
        val response: HttpResponse = client.post("https://api.deepgram.com/v1/listen") {
            header(HttpHeaders.Authorization, "Token $apiKey")
            header(HttpHeaders.ContentType, mimeType)
            setBody(audioBytes)

            // 👇 Specify nova-3 explicitly
            url {
                parameters.append("model", "nova-3")
            }
        }
        return response.bodyAsText()
    }
}
