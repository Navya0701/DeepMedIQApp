package com.plcoding.bookpedia.book.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MedicalQASampleInput(
    val input: String,
    val answers: List<Answer>,
    val related_questions: List<RelatedQuestion>? = null
)

@Serializable
data class Answer(
    val answer: String,
    val context: List<Context>,
    val followup_questions: List<FollowupQuestion>? = null
)

@Serializable
data class Context(
    val metadata: Metadata,
    val page_content: String
)

@Serializable
data class Metadata(
    val year: Int? = null,
    val region: String? = null,
    val title: String? = null,
    val source: String? = null,
    val page: Int? = null
)

@Serializable
data class FollowupQuestion(
    val question: String,
    val answer: String,
    val context: List<Context>
)

@Serializable
data class RelatedQuestion(
    val input: String,
    val answers: List<Answer>
)

