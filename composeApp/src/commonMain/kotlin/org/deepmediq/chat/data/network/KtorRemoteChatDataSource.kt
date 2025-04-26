package org.deepmediq.chat.data.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.setBody
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.deepmediq.chat.data.dto.FetchResponseDto
import org.deepmediq.chat.data.sampleData.SampleMedicalData
import org.deepmediq.core.data.safeCall
import org.deepmediq.core.domain.DataError
import org.deepmediq.core.domain.Result

private const val BASE_URL = "https://mcapisvc2.azurewebsites.net"

class KtorRemoteChatDataSource(
    private val httpClient: HttpClient
): RemoteChatDataSource {
    override suspend fun fetchChats(query: String): Result<FetchResponseDto, DataError.Remote> {
        println("came here")
        return safeCall<FetchResponseDto> {
            val jsonPayload = Json.encodeToString(SampleMedicalData.dataset)
            httpClient.post("$BASE_URL/chat") {
                contentType(ContentType.Application.Json)
                parameter("message", query)
                setBody(jsonPayload)
            }
        }
    }
}