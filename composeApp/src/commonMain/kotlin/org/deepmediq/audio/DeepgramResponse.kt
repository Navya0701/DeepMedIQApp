@kotlinx.serialization.Serializable
data class DeepgramResponse(
    val metadata: Metadata,
    val results: Results
)

@kotlinx.serialization.Serializable
data class Metadata(
    val request_id: String,
    val transaction_key: String,
    val sha256: String,
    val created: String,
    val duration: Double,
    val channels: Int,
    val models: List<String>,
    val model_info: Map<String, ModelInfo>
)

@kotlinx.serialization.Serializable
data class ModelInfo(
    val name: String,
    val version: String,
    val arch: String
)

@kotlinx.serialization.Serializable
data class Results(
    val channels: List<Channel>
)

@kotlinx.serialization.Serializable
data class Channel(
    val alternatives: List<Alternative>
)

@kotlinx.serialization.Serializable
data class Alternative(
    val transcript: String,
    val confidence: Float,
    val words: List<Word>
)

@kotlinx.serialization.Serializable
data class Word(
    val word: String,
    val start: Float,
    val end: Float,
    val confidence: Float
)
