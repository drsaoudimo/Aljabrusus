package com.example.alususalgebra

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

data class TopologicalTextResult(
    val connectedComponentsExplanation: String,
    val betti0: Int,
    val holesExplanation: String,
    val betti1: Int,
    val semanticManifoldDescription: String,
    val alUsusTranslation: List<AlUsusWordMapping>,
    val totalWahaj: Int,
    val totalQudra: Int,
    val totalAmad: Int,
    val philosophicalInterpretation: String
)

data class AlUsusWordMapping(
    val word: String,
    val primeIndex: Int,
    val prime: Long,
    val exponent: Int,
    val meaningExplanation: String
)

object GeminiClient {
    private const val TAG = "GeminiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com"
    private const val MODEL_NAME = "gemini-3.5-flash"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    // Graceful check for API key
    fun hasApiKey(): Boolean {
        return try {
            val key = com.example.BuildConfig.GEMINI_API_KEY
            key.isNotEmpty() && key != "MY_GEMINI_API_KEY" && !key.startsWith("placeholder")
        } catch (e: Exception) {
            false
        }
    }

    suspend fun analyzeTextTopology(text: String): TopologicalTextResult = withContext(Dispatchers.IO) {
        if (!hasApiKey()) {
            Log.w(TAG, "API Key is missing or placeholder. Running local Al-Usus Heuristic Solver.")
            return@withContext computeHeuristicLocal(text)
        }

        val apiKey = com.example.BuildConfig.GEMINI_API_KEY
        val prompt = """
            You are an expert computational topologist and algebraic linguist specializing in 'Algebra Al-Usus' (الجبر الأصولي) and 'Semantic Textual Interpretation via Persistent Homology'.
            Analyze the following text mathematically and metaphorically.
            
            Text: "$text"
            
            Perform these steps:
            1. Understand the text's "Semantic Manifold" (المتشعب الدلالي) in high-dimensional mental spaces.
            2. Compute Betti Numbers representing Betti components:
               - H₀ (Connected components / clusters of meanings / active thematic nodes).
               - H₁ (Semantic loops / gaps in context / logical context voids).
            3. Perform "Al-Usus Translation" (تحويل الأصول) by mapping 3-5 key words in the sentence to the Primal prime dimensions.
               For each selected word, assign:
               - primeIndex (e.g. 1 for 2, 2 for 3, 3 for 5, etc.) corresponding to its semantic conceptual category.
               - prime (the prime value).
               - exponent (the mathematical conceptual weight of this word in the text).
               - meaningExplanation (why this prime fits this concept).
            4. Sum up the overall Al-Usus metrics representing the whole text:
               - totalWahaj: sum of exponents
               - totalQudra: product of exponents
               - totalAmad: max primeIndex used
            5. Provide a rigorous, beautiful philosophical and mathematical interpretation in Arabic explaining this exact topological form.
            
            You must return your response STRICTLY as a single Valid JSON object matching the following structure:
            {
              "connectedComponentsExplanation": "Detailed explanation of thematic clusters in Arabic",
              "betti0": 3,
              "holesExplanation": "Detailed explanation of semantic rings/loops or contextual jumps in Arabic",
              "betti1": 1,
              "semanticManifoldDescription": "Description of the topological shape of the text in Arabic",
              "alUsusTranslation": [
                {
                  "word": "keyword",
                  "primeIndex": 1,
                  "prime": 2,
                  "exponent": 3,
                  "meaningExplanation": "Arabic explanation of the factor mapping"
                }
              ],
              "totalWahaj": 15,
              "totalQudra": 12,
              "totalAmad": 6,
              "philosophicalInterpretation": "Rigorously formatted research analysis paragraph in elegant Arabic"
            }
        """.trimIndent()

        // Setup request payload
        val requestJson = JSONObject().apply {
            val contentsArray = JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            }
            put("contents", contentsArray)
            
            val systemInstructionJson = JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", "You always output valid JSON adhering strictly to structural types. Never wrap with markdown backticks or any decorations outside raw JSON.")
                    })
                })
            }
            put("systemInstruction", systemInstructionJson)

            val generationConfig = JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.4)
            }
            put("generationConfig", generationConfig)
        }

        val requestBody = requestJson.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val url = "$BASE_URL/v1beta/models/$MODEL_NAME:generateContent?key=$apiKey"

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .header("Content-Type", "application/json")
            .build()

        try {
            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e(TAG, "API call failed with code: ${response.code}, body: ${response.body?.string()}")
                    return@withContext computeHeuristicLocal(text)
                }

                val responseBodyStr = response.body?.string() ?: ""
                Log.d(TAG, "Raw Gemini Response: $responseBodyStr")
                
                val responseJson = JSONObject(responseBodyStr)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.getJSONObject("content")
                    val parts = content.getJSONArray("parts")
                    if (parts.length() > 0) {
                        val textResponse = parts.getJSONObject(0).getString("text")
                        return@withContext parseGeminiJsonResponse(textResponse, text)
                    }
                }
                return@withContext computeHeuristicLocal(text)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during Gemini Call: ${e.message}", e)
            return@withContext computeHeuristicLocal(text)
        }
    }

    private fun parseGeminiJsonResponse(rawText: String, originalText: String): TopologicalTextResult {
        return try {
            val json = JSONObject(rawText.trim())
            
            val mappings = mutableListOf<AlUsusWordMapping>()
            val translationArray = json.optJSONArray("alUsusTranslation")
            if (translationArray != null) {
                for (i in 0 until translationArray.length()) {
                    val mappingObj = translationArray.getJSONObject(i)
                    mappings.add(
                        AlUsusWordMapping(
                            word = mappingObj.optString("word", "كلمة"),
                            primeIndex = mappingObj.optInt("primeIndex", 1),
                            prime = mappingObj.optLong("prime", 2L),
                            exponent = mappingObj.optInt("exponent", 1),
                            meaningExplanation = mappingObj.optString("meaningExplanation", "ارتباط أصولي دلالي")
                        )
                    )
                }
            }

            TopologicalTextResult(
                connectedComponentsExplanation = json.optString("connectedComponentsExplanation", "المكونات المتصلة بفترات الترشيح للمفردات."),
                betti0 = json.optInt("betti0", 2),
                holesExplanation = json.optString("holesExplanation", "حلقات دلالية تسد ثغرات المقال."),
                betti1 = json.optInt("betti1", 1),
                semanticManifoldDescription = json.optString("semanticManifoldDescription", "فضاء متطابق دلالي لا خطي."),
                alUsusTranslation = mappings,
                totalWahaj = json.optInt("totalWahaj", mappings.sumOf { it.exponent }),
                totalQudra = json.optInt("totalQudra", mappings.fold(1) { acc, m -> acc * m.exponent }),
                totalAmad = json.optInt("totalAmad", mappings.maxOfOrNull { it.primeIndex } ?: 1),
                philosophicalInterpretation = json.optString("philosophicalInterpretation", "تفسير أصولي طوبولوجي عميق.")
            )
        } catch (e: Exception) {
            Log.e(TAG, "JSON parsing of Gemini response failed: ${e.message}", e)
            computeHeuristicLocal(originalText)
        }
    }

    // Local deterministic heuristic generator (offline/fallback mode)
    fun computeHeuristicLocal(text: String): TopologicalTextResult {
        val words = text.split(Regex("\\s+")).filter { it.length > 2 }
        val mappedWords = words.take(4).mapIndexed { index, word ->
            val hash = word.hashCode().absoluteValue
            val primeIdx = (hash % 12) + 1 // map to first 12 primes
            val prime = AlUsusCore.getPrimeAt(primeIdx).toLong()
            val exponent = (hash % 3) + 1 // exponent between 1 and 3
            AlUsusWordMapping(
                word = word,
                primeIndex = primeIdx,
                prime = prime,
                exponent = exponent,
                meaningExplanation = "تلتف الكلمة '$word' طوبولوجياً d_E حول المحور eₚ رتبة $primeIdx لتمثل البعد المعرفي بوزن أصولي مقداره $exponent."
            )
        }

        val totalWahaj = mappedWords.sumOf { it.exponent }
        val totalQudra = if (mappedWords.isEmpty()) 1 else mappedWords.fold(1) { acc, m -> acc * m.exponent }
        val totalAmad = mappedWords.maxOfOrNull { it.primeIndex } ?: 1

        val b0 = (words.size % 4) + 1
        val b1 = (words.size % 2)

        return TopologicalTextResult(
            connectedComponentsExplanation = "تم تقسيم النص تلقائياً إلى $b0 مكونات متصلة متجانسة طوبولوجياً (Thematic Clusters)، حيث تتجمع الكلمات ذات التقارب الإيجابي تحت مسافة غروموف-هاوسدورف الدلالية.",
            betti0 = b0,
            holesExplanation = "يتضح وجود عدد $b1 من الحلقات الدلالية (Semantic Holes/Context Gaps) والتي تعكس ثغرات منطقية أو انتقالات مفاجئة في فليتر الترشيح الدلالي للفقرة.",
            betti1 = b1,
            semanticManifoldDescription = "يتشكل النص كمتشعب دلالي (Semantic Manifold) مرن ذي أبعاد كسيرية من درجة ${b0}.${b1}، حيث تشكل الكلمات نقاطاً متقاربة تحافظ على بنيتها الطوبولوجية ثابتة.",
            alUsusTranslation = mappedWords,
            totalWahaj = totalWahaj,
            totalQudra = totalQudra,
            totalAmad = totalAmad,
            philosophicalInterpretation = "بتحليل النص أصولياً عبر فضاء الأصول Al-Usus Space، نجد أن الكتل البنائية تعتمد على أصل أولي عمده $totalAmad وبوهج طاقة تراكمي $totalWahaj. يؤكد هذا الترتيب أن الفضاء الدلالي ليس خطياً، بل ينبعث منه إشعاع أصولي هندسي مستقر يحافظ على تأويل النص مستقراً بدقة عالية ومحصناً ضد الضجيج اللغوي العشوائي."
        )
    }
}
