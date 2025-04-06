package com.example.deepmediq.models

data class MedicalQAResponse(
    val input: String,
    val answers: List<Answer>,
    val related_questions: List<RelatedQuestion>? = null
)

data class Answer(
    val answer: String,
    val context: List<Context>,
    val followup_questions: List<FollowupQuestion>? = null
)

data class Context(
    val metadata: Metadata,
    val page_content: String
)

data class Metadata(
    val year: Int? = null,
    val region: String? = null,
    val title: String? = null,
    val source: String? = null,
    val page: Int? = null
)

data class FollowupQuestion(
    val question: String,
    val answer: String,
    val context: List<Context>
)

data class RelatedQuestion(
    val input: String,
    val answers: List<Answer>
)

// For the root list structure
typealias MedicalQADataset = List<MedicalQAResponse>