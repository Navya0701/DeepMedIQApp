package com.example.deepmediq.sampleData

import com.example.deepmediq.models.Answer
import com.example.deepmediq.models.Context
import com.example.deepmediq.models.FollowupQuestion
import com.example.deepmediq.models.MedicalQAResponse
import com.example.deepmediq.models.Metadata
import com.example.deepmediq.models.RelatedQuestion

object SampleMedicalData {
    val dataset = listOf(
        MedicalQAResponse(
            input = "I have a 70 year-old patient with normal LV function with symptomatic paroxysmal atrial fibrillation. Comorbidities include hypertension and diabetes. Should I consider an atrial fibrillation ablation? Should I also consider a watchman device implantation?",
            answers = listOf(
                Answer(
                    answer = "Based on the 2023 guidelines, you may consider atrial fibrillation ablation for your patient...",
                    context = listOf(
                        Context(
                            metadata = com.example.deepmediq.models.Metadata(
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
                                    metadata = com.example.deepmediq.models.Metadata(
                                        year = 2023,
                                        region = "United States",
                                        title = "2023 Guideline for the Diagnosis and Management of Atrial Fibrillation",
                                        source = "Unknown"
                                    ),
                                    page_content = "The risks of atrial fibrillation ablation include bleeding, infection..."
                                )
                            )
                        ),
                        // Add other follow-up questions similarly
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
                                    metadata = com.example.deepmediq.models.Metadata(
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
        ),
        MedicalQAResponse(
            input = "65-year-old patient with ischemic cardiomyopathy with LV ejection fraction of 30%. Is primary prevention ICD indicated in this patient? What other factors should I consider in deciding the device type?",
            answers = listOf(
                Answer(
                    answer = "Based on the ESC guidelines, an ICD is indicated for primary prevention in this patient...",
                    context = listOf(
                        Context(
                            metadata = Metadata(
                                year = 2024,
                                region = "European Society of Cardiology (ESC)",
                                title = "ESC Guidelines for the management of patients with ventricular arrhythmias...",
                                page = 3363
                            ),
                            page_content = "The use of an implantable cardioverter-defibrillator (ICD) for primary prevention..."
                        )
                    ),
                    followup_questions = listOf(
                        // Add follow-up questions similarly
                    )
                )
            )
        ),
        // Add other medical Q&A entries similarly
    )
}