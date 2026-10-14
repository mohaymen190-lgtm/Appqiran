package com.example.repository

import android.content.Context
import android.os.Environment
import android.util.Log
import com.example.data.QuranData
import com.example.data.local.DownloadedSurah
import com.example.data.local.FavoriteSurah
import com.example.data.local.QuranDao
import com.example.model.Reciter
import com.example.model.Surah
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class QuranRepository(
    private val quranDao: QuranDao,
    private val context: Context
) {
    val favorites: Flow<List<FavoriteSurah>> = quranDao.getAllFavorites()
    val downloads: Flow<List<DownloadedSurah>> = quranDao.getAllDownloads()

    fun isFavorite(reciterId: String, surahNumber: Int): Flow<Boolean> {
        val id = "${reciterId}_$surahNumber"
        return quranDao.isFavorite(id)
    }

    suspend fun toggleFavorite(reciter: Reciter, surah: Surah, isCurrentlyFav: Boolean) {
        val id = "${reciter.id}_${surah.number}"
        if (isCurrentlyFav) {
            quranDao.deleteFavorite(id)
        } else {
            val fav = FavoriteSurah(
                id = id,
                reciterId = reciter.id,
                reciterNameAr = reciter.nameAr,
                surahNumber = surah.number,
                surahNameAr = surah.nameAr,
                versesCount = surah.versesCount
            )
            quranDao.insertFavorite(fav)
        }
    }

    fun isDownloaded(reciterId: String, surahNumber: Int): Flow<Boolean> {
        val id = "${reciterId}_$surahNumber"
        return quranDao.isDownloaded(id)
    }

    suspend fun downloadSurah(
        reciter: Reciter,
        surah: Surah,
        onProgress: (Int) -> Unit = {}
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val audioUrl = QuranData.getAudioUrl(reciter, surah.number)
            val dir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC) ?: context.filesDir
            val fileName = "${reciter.id}_${String.format("%03d", surah.number)}.mp3"
            val targetFile = File(dir, fileName)

            val url = URL(audioUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            connection.connect()

            val fileLength = connection.contentLength
            val input = connection.inputStream
            val output = FileOutputStream(targetFile)

            val data = ByteArray(4096)
            var total: Long = 0
            var count: Int

            while (input.read(data).also { count = it } != -1) {
                total += count
                if (fileLength > 0) {
                    val progress = ((total * 100) / fileLength).toInt()
                    onProgress(progress)
                }
                output.write(data, 0, count)
            }

            output.flush()
            output.close()
            input.close()

            val fileSizeMb = String.format("%.1f MB", targetFile.length() / (1024.0 * 1024.0))
            val id = "${reciter.id}_${surah.number}"

            val downloadItem = DownloadedSurah(
                id = id,
                reciterId = reciter.id,
                reciterNameAr = reciter.nameAr,
                surahNumber = surah.number,
                surahNameAr = surah.nameAr,
                localFilePath = targetFile.absolutePath,
                fileSizeFormatted = fileSizeMb
            )
            quranDao.insertDownload(downloadItem)

            Result.success(targetFile.absolutePath)
        } catch (e: Exception) {
            Log.e("QuranRepository", "Error downloading surah", e)
            Result.failure(e)
        }
    }

    suspend fun deleteDownload(reciterId: String, surahNumber: Int) {
        val id = "${reciterId}_$surahNumber"
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC) ?: context.filesDir
        val fileName = "${reciterId}_${String.format("%03d", surahNumber)}.mp3"
        val targetFile = File(dir, fileName)
        if (targetFile.exists()) {
            targetFile.delete()
        }
        quranDao.deleteDownload(id)
    }
}
