package com.example.data.api

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.example.BuildConfig
import com.example.data.model.ClassRoutine
import com.example.data.model.ExamItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class GeminiRoutineParser(private val context: Context) {

  private val client = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

  /**
   * Analyzes an image of a university class routine using multimodal Gemini AI.
   * Extracts ONLY the actual classes present in the user's image.
   */
  suspend fun analyzeRoutineImage(
    bitmap: Bitmap,
    onProgress: (String) -> Unit = {}
  ): Result<List<ClassRoutine>> = withContext(Dispatchers.IO) {
    var apiKey = BuildConfig.GEMINI_API_KEY
    if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
      apiKey = System.getenv("GEMINI_API_KEY") ?: ""
    }

    if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
      return@withContext Result.failure(
        Exception("Gemini API key is not configured. Please configure your GEMINI_API_KEY in the Secrets panel.")
      )
    }

    onProgress("Preprocessing your routine image...")
    val resized = resizeBitmap(bitmap, 1200)
    val base64Image = bitmapToBase64(resized)

    val prompt = """
      You are an expert academic routine and university timetable analyzer.
      Carefully examine this image of a university class schedule / routine timetable.
      Extract every scheduled class lecture, laboratory, or seminar shown in the image.
      
      For each class entry found in the image, extract:
      - courseCode: The subject code, e.g. "CS101", "MATH201", "PHY102"
      - courseTitle: Course title or name, e.g. "Data Structures", "Calculus I"
      - dayOfWeek: Full day name: "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", or "Sunday"
      - startTime: e.g. "08:30 AM", "10:00 AM", "01:30 PM"
      - endTime: e.g. "10:00 AM", "11:30 AM", "03:00 PM"
      - room: Classroom, lecture hall, or lab
      - instructor: Professor, Lecturer, or TA name if shown
      - section: Section or batch if specified, e.g. "Sec 1", or empty string
      - notes: Any additional info (e.g. "Lab", "Lecture")
      
      Return ONLY a JSON array containing objects with these exact keys:
      [
        {
          "courseCode": "CS101",
          "courseTitle": "Data Structures",
          "dayOfWeek": "Monday",
          "startTime": "08:30 AM",
          "endTime": "10:00 AM",
          "room": "Room 302",
          "instructor": "Prof. Alan",
          "section": "Sec 1",
          "notes": "Lecture"
        }
      ]
      
      If no classes can be detected in the image, return an empty array [].
    """.trimIndent()

    val jsonBody = JSONObject().apply {
      put("contents", JSONArray().apply {
        put(JSONObject().apply {
          put("parts", JSONArray().apply {
            put(JSONObject().apply {
              put("text", prompt)
            })
            put(JSONObject().apply {
              put("inlineData", JSONObject().apply {
                put("mimeType", "image/jpeg")
                put("data", base64Image)
              })
            })
          })
        })
      })
      put("generationConfig", JSONObject().apply {
        put("responseMimeType", "application/json")
        put("temperature", 0.1)
      })
    }

    val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
    val modelsToTry = listOf("gemini-3.8-flash", "gemini-3.5-flash", "gemini-flash-latest")
    var lastError: Exception? = null

    for (model in modelsToTry) {
      try {
        onProgress("Scanning timetable with $model...")
        val request = Request.Builder()
          .url("https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey")
          .post(requestBody)
          .build()

        val response = client.newCall(request).execute()
        val responseBodyString = response.body?.string().orEmpty()

        if (response.isSuccessful) {
          onProgress("Extracting your scheduled classes...")
          val rootJson = JSONObject(responseBodyString)
          val candidates = rootJson.optJSONArray("candidates")
          val textContent = candidates
            ?.optJSONObject(0)
            ?.optJSONObject("content")
            ?.optJSONArray("parts")
            ?.optJSONObject(0)
            ?.optString("text")
            .orEmpty()

          val routines = parseRoutinesFromJson(textContent)
          if (routines.isNotEmpty()) {
            return@withContext Result.success(routines)
          } else {
            return@withContext Result.failure(
              Exception("No classes could be identified from this image. Please make sure the timetable is clear, upright, and legible.")
            )
          }
        } else {
          val errorMsg = try {
            JSONObject(responseBodyString).getJSONObject("error").getString("message")
          } catch (_: Exception) {
            "HTTP ${response.code}: $responseBodyString"
          }
          lastError = Exception(errorMsg)
        }
      } catch (e: Exception) {
        lastError = e
      }
    }

    Result.failure(lastError ?: Exception("Could not scan the timetable image. Please try again."))
  }

  /**
   * Analyzes an image of a university EXAM routine / schedule circular.
   * Extracts examination dates, times, course codes, and exam rooms.
   */
  suspend fun analyzeExamRoutineImage(
    bitmap: Bitmap,
    onProgress: (String) -> Unit = {}
  ): Result<List<ExamItem>> = withContext(Dispatchers.IO) {
    var apiKey = BuildConfig.GEMINI_API_KEY
    if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
      apiKey = System.getenv("GEMINI_API_KEY") ?: ""
    }

    if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
      return@withContext Result.failure(
        Exception("Gemini API key is not configured. Please configure your GEMINI_API_KEY in the Secrets panel.")
      )
    }

    onProgress("Preprocessing your exam routine image...")
    val resized = resizeBitmap(bitmap, 1200)
    val base64Image = bitmapToBase64(resized)

    val prompt = """
      You are an expert academic examination schedule and exam timetable analyzer.
      Carefully examine this image of a university examination routine or circular sheet.
      Extract every scheduled exam, midterm, quiz, or final exam session shown in the image.
      
      For each exam entry found in the image, extract:
      - courseCode: The subject code, e.g. "CSC 497", "MATH 201", "CSE 4383"
      - courseTitle: Course title or name, e.g. "Compiler Design", "Calculus I"
      - examDate: The scheduled exam date, e.g. "Thu, 22 Oct" or "2026-10-25"
      - time: Exam time slot, e.g. "10:00 AM - 12:00 PM", "02:00 PM - 05:00 PM"
      - room: Exam hall, classroom, or building, e.g. "Room 607", "Hall B"
      - seatNumber: Seat or roll range if indicated, or empty string
      
      Return ONLY a JSON array containing objects with these exact keys:
      [
        {
          "courseCode": "CSC 497",
          "courseTitle": "Compiler Design",
          "examDate": "Thu, 22 Oct",
          "time": "10:00 AM - 12:00 PM",
          "room": "Room 607",
          "seatNumber": ""
        }
      ]
      
      If no exams can be detected in the image, return an empty array [].
    """.trimIndent()

    val jsonBody = JSONObject().apply {
      put("contents", JSONArray().apply {
        put(JSONObject().apply {
          put("parts", JSONArray().apply {
            put(JSONObject().apply {
              put("text", prompt)
            })
            put(JSONObject().apply {
              put("inlineData", JSONObject().apply {
                put("mimeType", "image/jpeg")
                put("data", base64Image)
              })
            })
          })
        })
      })
      put("generationConfig", JSONObject().apply {
        put("responseMimeType", "application/json")
        put("temperature", 0.1)
      })
    }

    val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
    val modelsToTry = listOf("gemini-3.8-flash", "gemini-3.5-flash", "gemini-flash-latest")
    var lastError: Exception? = null

    for (model in modelsToTry) {
      try {
        onProgress("Scanning exam routine with $model...")
        val request = Request.Builder()
          .url("https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey")
          .post(requestBody)
          .build()

        val response = client.newCall(request).execute()
        val responseBodyString = response.body?.string().orEmpty()

        if (response.isSuccessful) {
          onProgress("Extracting your scheduled exams...")
          val rootJson = JSONObject(responseBodyString)
          val candidates = rootJson.optJSONArray("candidates")
          val textContent = candidates
            ?.optJSONObject(0)
            ?.optJSONObject("content")
            ?.optJSONArray("parts")
            ?.optJSONObject(0)
            ?.optString("text")
            .orEmpty()

          val exams = parseExamsFromJson(textContent)
          if (exams.isNotEmpty()) {
            return@withContext Result.success(exams)
          } else {
            return@withContext Result.failure(
              Exception("No exams could be identified from this image. Please ensure the exam schedule is clearly visible.")
            )
          }
        } else {
          val errorMsg = try {
            JSONObject(responseBodyString).getJSONObject("error").getString("message")
          } catch (_: Exception) {
            "HTTP ${response.code}: $responseBodyString"
          }
          lastError = Exception(errorMsg)
        }
      } catch (e: Exception) {
        lastError = e
      }
    }

    Result.failure(lastError ?: Exception("Could not scan the exam routine image. Please try again."))
  }

  fun parseExamRoutineFromRawText(rawText: String): List<ExamItem> {
    val lines = rawText.lines().map { it.trim() }.filter { it.isNotBlank() }
    val exams = mutableListOf<ExamItem>()
    val courseRegex = Regex("""\b([A-Za-z]{2,5}\s*\d{2,4}[A-Za-z]?)\b""")
    val timeRegex = Regex("""(\d{1,2}[:.]\d{2}\s*(?:AM|PM|am|pm)?)\s*(?:-|to)\s*(\d{1,2}[:.]\d{2}\s*(?:AM|PM|am|pm)?)""")
    val dateRegex = Regex("""(?:\b(?:Mon|Tue|Wed|Thu|Fri|Sat|Sun)[a-z]*,?\s*)?\b\d{1,2}(?:st|nd|rd|th)?\s+(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[a-z]*|\b\d{4}-\d{2}-\d{2}\b""", RegexOption.IGNORE_CASE)

    var currentDate = "Upcoming Date"

    for (line in lines) {
      val dateMatch = dateRegex.find(line)
      if (dateMatch != null && !line.contains(Regex("""\d{1,2}:\d{2}"""))) {
        currentDate = dateMatch.value
      }

      val courseMatch = courseRegex.find(line)
      if (courseMatch != null) {
        val courseCode = courseMatch.value.replace(" ", "").uppercase()
        val timeMatch = timeRegex.find(line)
        val time = timeMatch?.value ?: "10:00 AM - 12:00 PM"

        val roomRegex = Regex("""(?:Room|Hall|Lab|RM|Rm)\s*([A-Za-z0-9\-]+)""", RegexOption.IGNORE_CASE)
        val roomMatch = roomRegex.find(line)
        val room = roomMatch?.value ?: ""

        val cleaned = line
          .replace(courseMatch.value, "")
          .replace(timeMatch?.value.orEmpty(), "")
          .replace(roomMatch?.value.orEmpty(), "")
          .replace(Regex("""[-|,|;]"""), " ")
          .trim()

        val courseTitle = if (cleaned.length > 2) cleaned else "Course Exam"
        val thisDate = dateMatch?.value ?: currentDate

        exams.add(
          ExamItem(
            courseCode = courseCode,
            courseTitle = courseTitle,
            examDate = thisDate,
            time = time,
            room = room
          )
        )
      }
    }

    return exams
  }

  fun parseRoutineFromRawText(rawText: String): List<ClassRoutine> {
    val lines = rawText.lines().map { it.trim() }.filter { it.isNotBlank() }
    val routines = mutableListOf<ClassRoutine>()
    var currentDay = "Monday"
    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    val timeRegex = Regex("""(\d{1,2}[:.]\d{2}\s*(?:AM|PM|am|pm)?)\s*(?:-|to)\s*(\d{1,2}[:.]\d{2}\s*(?:AM|PM|am|pm)?)""")
    val courseRegex = Regex("""\b([A-Za-z]{2,5}\s*\d{2,4}[A-Za-z]?)\b""")

    for (line in lines) {
      val matchedDay = days.firstOrNull { line.contains(it, ignoreCase = true) }
      if (matchedDay != null) {
        currentDay = matchedDay
        continue
      }

      val timeMatch = timeRegex.find(line)
      val courseMatch = courseRegex.find(line)

      if (courseMatch != null) {
        val courseCode = courseMatch.value.replace(" ", "").uppercase()
        val startTime = timeMatch?.groupValues?.getOrNull(1) ?: "09:00 AM"
        val endTime = timeMatch?.groupValues?.getOrNull(2) ?: "10:30 AM"

        val roomRegex = Regex("""(?:Room|Hall|Lab|RM|Rm)\s*([A-Za-z0-9\-]+)""", RegexOption.IGNORE_CASE)
        val roomMatch = roomRegex.find(line)
        val room = roomMatch?.value ?: ""

        val cleaned = line
          .replace(courseMatch.value, "")
          .replace(timeMatch?.value.orEmpty(), "")
          .replace(roomMatch?.value.orEmpty(), "")
          .replace(Regex("""[-|,|;]"""), " ")
          .trim()

        val courseTitle = if (cleaned.length > 2) cleaned else "Class Lecture"

        routines.add(
          ClassRoutine(
            courseCode = courseCode,
            courseTitle = courseTitle,
            dayOfWeek = currentDay,
            startTime = startTime,
            endTime = endTime,
            room = room,
            instructor = "",
            colorHex = ClassRoutine.colorForCourse(courseCode),
            notes = ""
          )
        )
      }
    }

    return routines
  }

  private fun parseExamsFromJson(jsonString: String): List<ExamItem> {
    val list = mutableListOf<ExamItem>()
    try {
      val clean = jsonString.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
      val array = if (clean.startsWith("[")) {
        JSONArray(clean)
      } else if (clean.startsWith("{")) {
        val obj = JSONObject(clean)
        obj.optJSONArray("exams") ?: obj.optJSONArray("schedule") ?: JSONArray()
      } else {
        JSONArray()
      }

      for (i in 0 until array.length()) {
        val item = array.getJSONObject(i)
        val code = item.optString("courseCode", "").trim()
        val title = item.optString("courseTitle", "").trim()
        val date = item.optString("examDate", "").trim()
        val time = item.optString("time", "").trim()
        val room = item.optString("room", "").trim()
        val seat = item.optString("seatNumber", "").trim()

        if (code.isNotBlank() || title.isNotBlank()) {
          list.add(
            ExamItem(
              courseCode = if (code.isNotBlank()) code.uppercase() else "EXAM",
              courseTitle = if (title.isNotBlank()) title else "Examination",
              examDate = if (date.isNotBlank()) date else "Upcoming Date",
              time = if (time.isNotBlank()) time else "10:00 AM - 12:00 PM",
              room = room,
              seatNumber = seat
            )
          )
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return list
  }

  private fun parseRoutinesFromJson(jsonString: String): List<ClassRoutine> {
    val list = mutableListOf<ClassRoutine>()
    try {
      val clean = jsonString.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
      val array = if (clean.startsWith("[")) {
        JSONArray(clean)
      } else if (clean.startsWith("{")) {
        val obj = JSONObject(clean)
        obj.optJSONArray("classes") ?: obj.optJSONArray("schedule") ?: JSONArray()
      } else {
        JSONArray()
      }

      for (i in 0 until array.length()) {
        val item = array.getJSONObject(i)
        val code = item.optString("courseCode", "").trim()
        val title = item.optString("courseTitle", "").trim()
        val day = item.optString("dayOfWeek", "").trim()
        val start = item.optString("startTime", "").trim()
        val end = item.optString("endTime", "").trim()
        val room = item.optString("room", "").trim()
        val instructor = item.optString("instructor", "").trim()
        val section = item.optString("section", "").trim()
        val notes = item.optString("notes", "").trim()

        if (code.isNotBlank() || title.isNotBlank()) {
          list.add(
            ClassRoutine(
              courseCode = if (code.isNotBlank()) code.uppercase() else "CLASS",
              courseTitle = if (title.isNotBlank()) title else "Lecture",
              dayOfWeek = normalizeDay(day),
              startTime = if (start.isNotBlank()) start else "09:00 AM",
              endTime = if (end.isNotBlank()) end else "10:30 AM",
              room = room,
              instructor = instructor,
              section = section,
              colorHex = ClassRoutine.colorForCourse(code),
              notes = notes
            )
          )
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return list
  }

  private fun normalizeDay(day: String): String {
    val d = day.lowercase()
    return when {
      d.startsWith("mon") -> "Monday"
      d.startsWith("tue") -> "Tuesday"
      d.startsWith("wed") -> "Wednesday"
      d.startsWith("thu") -> "Thursday"
      d.startsWith("fri") -> "Friday"
      d.startsWith("sat") -> "Saturday"
      d.startsWith("sun") -> "Sunday"
      else -> "Monday"
    }
  }

  fun loadBitmapFromUri(uri: Uri): Bitmap? {
    return try {
      val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
      }
      context.contentResolver.openInputStream(uri)?.use { stream ->
        BitmapFactory.decodeStream(stream, null, options)
      }

      var sampleSize = 1
      val maxDim = 1600
      while (options.outWidth / sampleSize > maxDim || options.outHeight / sampleSize > maxDim) {
        sampleSize *= 2
      }

      val decodeOptions = BitmapFactory.Options().apply {
        inSampleSize = sampleSize
      }

      context.contentResolver.openInputStream(uri)?.use { stream ->
        BitmapFactory.decodeStream(stream, null, decodeOptions)
      }
    } catch (_: Exception) {
      null
    }
  }

  private fun resizeBitmap(bitmap: Bitmap, maxDim: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    if (width <= maxDim && height <= maxDim) return bitmap
    val ratio = if (width > height) maxDim.toFloat() / width else maxDim.toFloat() / height
    val newWidth = (width * ratio).toInt()
    val newHeight = (height * ratio).toInt()
    return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
  }

  private fun bitmapToBase64(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
    return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
  }

  fun loadSampleRoutineBitmap(): Bitmap? {
    return try {
      val resId = context.resources.getIdentifier("sample_routine_sheet", "drawable", context.packageName)
      if (resId != 0) {
        BitmapFactory.decodeResource(context.resources, resId)
      } else {
        null
      }
    } catch (_: Exception) {
      null
    }
  }
}
