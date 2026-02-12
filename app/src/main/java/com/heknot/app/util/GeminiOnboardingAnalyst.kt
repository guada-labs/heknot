package com.heknot.app.util

import android.content.Context
import android.util.Log
import com.heknot.app.data.local.database.entity.ActivityLevel
import com.heknot.app.data.local.database.entity.FitnessGoal
import org.json.JSONObject

/**
 * Result of the AI onboarding analysis.
 */
data class OnboardingAnalysis(
    val suggestedActivityLevel: ActivityLevel,
    val suggestedFitnessGoal: FitnessGoal,
    val healthNarrative: String
)

/**
 * Utility for analyzing user lifestyle and goals using Gemini Nano.
 * On S24 Ultra, this leverages the AICore system service.
 */
class GeminiOnboardingAnalyst(private val context: Context) {

    /**
     * Analyzes the user's free-text story to suggest activity level and fitness goal.
     */
    suspend fun analyzeLifestyle(userStory: String): OnboardingAnalysis {
        if (android.os.Build.VERSION.SDK_INT < 31) {
            Log.d("GeminiAnalyst", "AI features disabled for API < 31")
            return fallback()
        }
        if (userStory.isBlank()) return fallback()

        return try {
            val prompt = """
                Analyze this user's lifestyle and fitness story to suggest their activity level and fitness goal.
                
                STORY:
                ${userStory}
                
                ACTIVITY LEVELS (Select one):
                - SEDENTARY: Little or no exercise, desk job.
                - LIGHT: Light exercise 1-3 days/week.
                - MODERATE: Moderate exercise 3-5 days/week.
                - ACTIVE: Intense exercise 6-7 days/week.
                - VERY_ACTIVE: Very intense physical job or training 2x/day.
                
                FITNESS GOALS (Select one):
                - LOSE_WEIGHT: User wants to lose fat or reduce weight.
                - MAINTAIN_WEIGHT: User wants to stay as they are or focus on health.
                - GAIN_WEIGHT: User wants to build muscle or increase mass.
                
                Return ONLY a JSON object with:
                "activityLevel" (String, must be the EXACT enum name above),
                "fitnessGoal" (String, must be the EXACT enum name above),
                "narrative" (String, a short 2-3 sentence summary in Spanish of what you understood and why you chose these settings).
                
                RESPONSE FORMAT:
                {
                  "activityLevel": "...",
                  "fitnessGoal": "...",
                  "narrative": "..."
                }
            """.trimIndent()

            Log.d("GeminiAnalyst", "Analyzing story...")
            
            // TODO: In a real S24 Ultra environment, we would use the AICore GenerativeModel:
            // val generativeModel = GenerativeModel("gemini-nano", context)
            // val response = generativeModel.generateContent(prompt)
            // return parseJsonResponse(response.text)
            
            // Simulation for early dev/stabilization
            simulateResult(userStory)
        } catch (e: Exception) {
            Log.e("GeminiAnalyst", "Analysis failed", e)
            fallback()
        }
    }

    private fun simulateResult(story: String): OnboardingAnalysis {
        val lowerStory = story.lowercase()
        val activity = when {
            lowerStory.contains("atleta") || lowerStory.contains("fuerte") || lowerStory.contains("diario") -> ActivityLevel.ACTIVE
            lowerStory.contains("ejercicio") || lowerStory.contains("entreno") || lowerStory.contains("gym") -> ActivityLevel.MODERATE
            lowerStory.contains("camino") || lowerStory.contains("moverse") -> ActivityLevel.LIGHT
            else -> ActivityLevel.SEDENTARY
        }
        
        val goal = when {
            lowerStory.contains("perder") || lowerStory.contains("bajar") || lowerStory.contains("grasa") || lowerStory.contains("adelgazar") -> FitnessGoal.LOSE_WEIGHT
            lowerStory.contains("ganar") || lowerStory.contains("musculo") || lowerStory.contains("subir") || lowerStory.contains("volumen") -> FitnessGoal.GAIN_WEIGHT
            else -> FitnessGoal.MAINTAIN_WEIGHT
        }
        
        val narrative = "Basado en tu descripción, parece que tienes un nivel de actividad ${activity.displayName.lowercase()}. He sugerido una meta de ${goal.displayName.lowercase()} para alinearnos con tus prioridades."
        
        return OnboardingAnalysis(activity, goal, narrative)
    }

    private fun parseJsonResponse(jsonStr: String?): OnboardingAnalysis {
        if (jsonStr == null) return fallback()
        return try {
            val cleanJson = jsonStr.substringAfter("{").substringBeforeLast("}")
            val json = JSONObject("{$cleanJson}")
            
            val activityName = json.optString("activityLevel", "MODERATE")
            val goalName = json.optString("fitnessGoal", "MAINTAIN_WEIGHT")
            
            OnboardingAnalysis(
                suggestedActivityLevel = try { ActivityLevel.valueOf(activityName) } catch (e: Exception) { ActivityLevel.MODERATE },
                suggestedFitnessGoal = try { FitnessGoal.valueOf(goalName) } catch (e: Exception) { FitnessGoal.MAINTAIN_WEIGHT },
                healthNarrative = json.optString("narrative", "")
            )
        } catch (e: Exception) {
            Log.e("GeminiAnalyst", "JSON parsing failed", e)
            fallback()
        }
    }

    private fun fallback() = OnboardingAnalysis(
        suggestedActivityLevel = ActivityLevel.MODERATE,
        suggestedFitnessGoal = FitnessGoal.MAINTAIN_WEIGHT,
        healthNarrative = "No pude analizar tu descripción detalladamente. He configurado valores estándar para comenzar."
    )
}
