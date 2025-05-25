package com.plcoding.bookpedia.book.data.sampleData

import com.plcoding.bookpedia.book.data.dto.MedicalQASampleInput
import com.plcoding.bookpedia.book.data.dto.Answer
import com.plcoding.bookpedia.book.data.dto.Context
import com.plcoding.bookpedia.book.data.dto.Metadata
import com.plcoding.bookpedia.book.data.dto.FollowupQuestion
import com.plcoding.bookpedia.book.data.dto.RelatedQuestion


object SampleMedicalData {
    val dataset = listOf(
        MedicalQASampleInput(
            input = "I have a 70 year-old patient with normal LV function with symptomatic paroxysmal atrial fibrillation. Comorbidities include hypertension and diabetes. Should I consider an atrial fibrillation ablation? Should I also consider a watchman device implantation?",
            answers = listOf(
                Answer(
                    answer = "Based on the 2023 guidelines, you may consider atrial fibrillation ablation for your patient...",
                    context = listOf(
                        Context(
                            metadata = Metadata(
                                year = 2023,
                                region = "United States",
                                title = "2023 Guideline for the Diagnosis and Management of Atrial Fibrillation",
                                source = "Unknown"
                            ),
                            page_content = "Atrial fibrillation ablation may be considered for patients with symptomatic paroxysmal atrial fibrillation..."
                        )
                    ),
                    followup_questions = listOf(
                        FollowupQuestion(
                            question = "What are the risks associated with atrial fibrillation ablation?",
                            answer = "The risks associated with atrial fibrillation ablation include bleeding, infection, stroke...",
                            context = listOf(
                                Context(
                                    metadata = Metadata(
                                        year = 2023,
                                        region = "United States",
                                        title = "2023 Guideline for the Diagnosis and Management of Atrial Fibrillation",
                                        source = "Unknown"
                                    ),
                                    page_content = "The risks of atrial fibrillation ablation include bleeding, infection..."
                                )
                            )
                        )
                    )
                )
            ),
            related_questions = listOf(
                RelatedQuestion(
                    input = "What are the symptoms of atrial fibrillation?",
                    answers = listOf(
                        Answer(
                            answer = "Common symptoms include palpitations, fatigue, shortness of breath...",
                            context = listOf(
                                Context(
                                    metadata = Metadata(
                                        year = 2023,
                                        title = "Atrial Fibrillation Symptoms Guide"
                                    ),
                                    page_content = "Patients with AFib often report feeling..."
                                )
                            )
                        )
                    )
                )
            )
        )
    )
}