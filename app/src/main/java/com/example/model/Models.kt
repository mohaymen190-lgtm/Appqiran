package com.example.model

enum class AmbientSoundType(val titleAr: String, val subtitleAr: String, val iconName: String) {
    NONE("بدون خلفية", "بدون مؤثرات صوتية", "VolumeOff"),
    OCEAN("أمواج البحر", "هدوء شاطئ البحر المتلاطم والواقعي", "Waves"),
    RAIN("صوت المطر", "زخات مطر هادئة ونقية", "WaterDrop"),
    WIND("نسيم الرياح", "حفيف رياح لطيفة ومهدئة", "Air"),
    NIGHT_BIRDS("طبيعة هادئة", "أجواء سكينة وسكون ليلي", "Nightlight")
}

data class Reciter(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val title: String,
    val serverUrl: String,
    val imageDrawableName: String,
    val isFeatured: Boolean = false,
    val location: String = ""
)

data class Surah(
    val number: Int,
    val nameAr: String,
    val nameEn: String,
    val nameTranslation: String,
    val versesCount: Int,
    val revelationType: String,
    val durationMinutes: String = "15:00"
)
