package com.example.deepmediq.backendRepository

import ChatListItem
import android.util.Log
import com.example.deepmediq.api.ChatAPI
import com.example.deepmediq.models.Context
import com.example.deepmediq.sampleData.SampleMedicalData
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import com.example.deepmediq.models.Metadata
import java.net.SocketTimeoutException
import javax.inject.Inject


class ChatRepository @Inject constructor(private val chatAPI: ChatAPI) {


    private val _chatMessages = MutableStateFlow(
        ChatListItem(
            answer = "Hello! How can I assist you today?",
            context = listOf(
               Context(
                   metadata = Metadata(
                       year = 2024,
                       title = "MedCoPilot Journal"
                   ),
                   page_content ="This is sample"
               )
            ),
            input = "Initial input"
        )
    )

    val chatMessages: StateFlow<ChatListItem> get() = _chatMessages

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    suspend fun getChatMessages(inputString: String){
        try{
            // Convert data map to JSON string
            val jsonBody = Gson().toJson(SampleMedicalData.dataset)
                .toRequestBody("application/json".toMediaTypeOrNull())

            val response = chatAPI.getChatMessages(inputString,jsonBody)
            Log.d("response", response.toString())
            Log.d("response", response.body().toString())
            if(response.isSuccessful && response.body() !=null){
                _chatMessages.emit(response.body()!!)
            }
        } catch (e: SocketTimeoutException) {
            _error.value = "Connection timeout - please check your network"
        } catch (e: IOException) {
            _error.value = "Network error - please check your connection"
        } catch (e: Exception) {
            _error.value = "Unexpected error: ${e.localizedMessage}"
        }finally{
            Log.d("error", _error.value.toString())
        }

    }

}