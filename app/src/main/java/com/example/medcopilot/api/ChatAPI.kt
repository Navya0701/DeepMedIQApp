package com.example.medcopilot.api

import ChatListItem
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ChatAPI {

    @POST("chat")
    suspend fun getChatMessages(
        @Query("message") message: String,
        @Body requestBody: RequestBody,  // For JSON body
        @Header("Content-Type") contentType: String = "application/json",
    ) : Response<ChatListItem>


}