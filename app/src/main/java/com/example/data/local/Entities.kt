package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteSurah(
    @PrimaryKey val id: String, // format: "reciterId_surahNumber"
    val reciterId: String,
    val reciterNameAr: String,
    val surahNumber: Int,
    val surahNameAr: String,
    val versesCount: Int,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "downloads")
data class DownloadedSurah(
    @PrimaryKey val id: String, // format: "reciterId_surahNumber"
    val reciterId: String,
    val reciterNameAr: String,
    val surahNumber: Int,
    val surahNameAr: String,
    val localFilePath: String,
    val fileSizeFormatted: String = "4.2 MB",
    val downloadedAt: Long = System.currentTimeMillis()
)
