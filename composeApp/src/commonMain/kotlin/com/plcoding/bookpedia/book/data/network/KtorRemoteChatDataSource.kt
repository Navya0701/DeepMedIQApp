package com.plcoding.bookpedia.book.data.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.setBody
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.plcoding.bookpedia.book.data.dto.FetchResponseDto
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.book.data.sampleData.SampleMedicalData
import com.plcoding.bookpedia.core.data.safeCall
import com.plcoding.bookpedia.core.domain.Result

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