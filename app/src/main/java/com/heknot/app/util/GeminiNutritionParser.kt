package com.heknot.app.util

import android.content.Context
import android.util.Log
import org.json.JSONObject

/**
 * Result of a nutritional scan.
 */
data class ScannedNutrition(
    val calories: Int = 0,
    val protein: Float = 0f,
    val carbs: Float = 0f,
    val fat: Float = 0f,
    val sugar: Float = 0f,
    val fiber: Float = 0f,
    val sodium: Float = 0f,
    val servingSize: Float = 0f, // in grams or ml
    val servingUnit: String = "g",
    val name: String? = null,
    val brand: String? = null,
    val category: String? = null
)

/**
 * Utility for parsing unstructured OCR text into nutritional data using Gemini Nano.
 * On S24 Ultra, this leverages the AICore system service.
 */
class GeminiNutritionParser(private val context: Context) {

    /**
     * Parses raw text from a nutrition label into structured data.
     * Uses a prompt-based approach for Gemini Nano.
     */
    suspend fun parseNutritionText(rawText: String): ScannedNutrition {
        if (rawText.isBlank()) return ScannedNutrition()

        return try {
            val prompt = """
                Extract nutritional values and product info from this OCR text. 
                The text might be in English or Spanish. Look for keywords like:
                - Calories: Calorías, Energía, Valor energético.
                - Protein: Proteínas.
                - Carbs: Carbohidratos, Hidratos de carbono, Azúcares.
                - Fat: Grasas, Lípidos, Grasas saturadas.
                - Serving: Porción, Tamaño de ración, Por ración, 100g.

                Return ONLY a JSON object with these keys: 
                "name" (String), "brand" (String), "category" (String),
                "calories" (Int), "protein" (Float), "carbs" (Float), "fat" (Float), 
                "sugar" (Float), "fiber" (Float), "sodium" (Float, in mg),
                "servingSize" (Float), "servingUnit" (String, e.g., "g", "ml").
                
                CRITICAL LOGIC:
                - If the text contains values for BOTH "per 100g" and "per serving/portion" (often labeled "por ración" or "por porción"), prioritize the "PER SERVING" values.
                - Only use 100g values if serving/portion values are completely missing.
                - If a value is missing, use 0 or null for strings.
                - Category should be one of: DAIRY, GRAINS, FRUITS, VEGETABLES, PROTEINS, SNACKS, BEVERAGES, OTHER.
                - Do NOT include units in the numeric values.
                
                TEXT:
                $rawText
            """.trimIndent()

            Log.d("GeminiParser", "Sending prompt to Gemini Nano...")
            
            // TODO: In a real S24 Ultra environment, we would use the AICore GenerativeModel:
            // val generativeModel = GenerativeModel("gemini-nano", context)
            // val response = generativeModel.generateContent(prompt)
            // return parseJsonResponse(response.text)

            // For now, since we are in the stabilization phase, we simulate a successful result
            // if we detect keywords in Spanish/English to show the UI works.
            simulateResultForTesting(rawText)
        } catch (e: Exception) {
            Log.e("GeminiParser", "Gemini Nano parsing failed", e)
            ScannedNutrition()
        }
    }

    /**
     * Simulation for testing when model is not available or in early dev.
     */
    private fun simulateResultForTesting(text: String): ScannedNutrition {
        val protein = Regex("(Proteinas|Protein)[^0-9]*([0-9]+[.,]?[0-9]*)", RegexOption.IGNORE_CASE)
            .find(text)?.groupValues?.get(2)?.replace(",", ".")?.toFloatOrNull() ?: 0f
        val cals = Regex("(Calorias|Calories|Energia)[^0-9]*([0-9]+)", RegexOption.IGNORE_CASE)
            .find(text)?.groupValues?.get(2)?.toIntOrNull() ?: 0
        
        return ScannedNutrition(
            name = "Prueba S24",
            calories = cals,
            protein = protein,
            carbs = (protein * 2), // Mocking some logic
            category = "OTHER"
        )
    }

    private fun parseJsonResponse(jsonStr: String?): ScannedNutrition {
        if (jsonStr == null) return ScannedNutrition()
        return try {
            // Extract the JSON block if the LLM added markdown backticks
            val cleanJson = jsonStr.substringAfter("{").substringBeforeLast("}")
            val json = JSONObject("{$cleanJson}")
            ScannedNutrition(
                calories = json.optInt("calories", 0),
                protein = json.optDouble("protein", 0.0).toFloat(),
                carbs = json.optDouble("carbs", 0.0).toFloat(),
                fat = json.optDouble("fat", 0.0).toFloat(),
                sugar = json.optDouble("sugar", 0.0).toFloat(),
                fiber = json.optDouble("fiber", 0.0).toFloat(),
                sodium = json.optDouble("sodium", 0.0).toFloat(),
                servingSize = json.optDouble("servingSize", 0.0).toFloat(),
                servingUnit = json.optString("servingUnit", "g"),
                name = json.optString("name").takeIf { it.isNotBlank() },
                brand = json.optString("brand").takeIf { it.isNotBlank() },
                category = json.optString("category").takeIf { it.isNotBlank() }
            )
        } catch (e: Exception) {
            Log.e("GeminiParser", "JSON parsing failed: $jsonStr", e)
            ScannedNutrition()
        }
    }
}
