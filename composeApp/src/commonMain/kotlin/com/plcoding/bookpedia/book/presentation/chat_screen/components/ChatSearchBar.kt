package com.plcoding.bookpedia.book.presentation.chat_screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cmp_bookpedia.composeapp.generated.resources.Res
import cmp_bookpedia.composeapp.generated.resources.ic_send
import cmp_bookpedia.composeapp.generated.resources.mic_24px
import cmp_bookpedia.composeapp.generated.resources.search_hint

import com.plcoding.bookpedia.book.data.sampleData.SampleMedicalData
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


@Composable
fun ChatSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSendClick: (String) -> Unit,
    onImeSearch: () -> Unit,
    modifier: Modifier = Modifier,
    onMicClick: () -> Unit = {},
    isRecording: Boolean = false
) {
    var showSuggestions by remember { mutableStateOf(false) }
    
    // Get all available questions from the sample data
    val suggestions by remember(searchQuery) {
        derivedStateOf {
            if (searchQuery.isBlank()) {
                emptyList()
            } else {
                val allQuestions = mutableListOf<String>()
                
                // Add main questions
                SampleMedicalData.dataset.forEach { data ->
                    allQuestions.add(data.input)
                    
                    // Add related questions
                    data.related_questions?.forEach { related ->
                        allQuestions.add(related.input)
                    }
                    
                    // Add follow-up questions
                    data.answers.forEach { answer ->
                        answer.followup_questions?.forEach { followup ->
                            allQuestions.add(followup.question)
                        }
                    }
                }
                
                // Filter and return matching questions
                allQuestions
                    .distinct() // Remove duplicates
                    .filter { it.contains(searchQuery, ignoreCase = true) }
            }
        }
    }

    Box(modifier = modifier) {
        Column {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { 
                    onSearchQueryChange(it)
                    showSuggestions = it.isNotBlank()
                },
                shape = RoundedCornerShape(100),
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = Color.Black,
                    focusedBorderColor = Color.Black
                ),
                placeholder = {
                    Text(
                        text = if (searchQuery.isEmpty()) "How can DeepMedIQ help you today.." else stringResource(
                            Res.string.search_hint)
                    )
                },
                singleLine = false,
                maxLines = 3,
                keyboardActions = KeyboardActions(
                    onSearch = { onImeSearch() }
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
                leadingIcon = {
                    IconButton(onClick = {
//                        requestAudioPermission {
//                            onMicClick()
//                        }
                    }) {
                        Image(
                            painter = painterResource(resource = Res.drawable.mic_24px),
                            contentDescription = "Mic",
                            modifier = Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(if (isRecording) Color.Red else Color.Black)
                        )
                    }
                },
                trailingIcon = {
                    AnimatedVisibility(
                        visible = searchQuery.isNotBlank()
                    ) {
                        IconButton(
                            onClick = {
                                onSendClick(searchQuery)
                            }
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.ic_send),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                modifier = Modifier
                    .background(
                        shape = RoundedCornerShape(100),
                        color = Color.White
                    )
                    .fillMaxWidth()
                    .zIndex(1f)
            )

            // Suggestions dropdown
            AnimatedVisibility(
                visible = showSuggestions && suggestions.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = 4.dp)
                    .zIndex(2f)
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .fillMaxWidth()
                ) {
                    suggestions.forEach { suggestion ->
                        Text(
                            text = suggestion,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSearchQueryChange(suggestion)
                                    showSuggestions = false
                                }
                                .padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}