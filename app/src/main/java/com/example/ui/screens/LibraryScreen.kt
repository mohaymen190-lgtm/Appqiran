package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.QuranData
import com.example.data.local.DownloadedSurah
import com.example.data.local.FavoriteSurah
import com.example.model.Reciter
import com.example.model.Surah
import com.example.viewmodel.PlayerUiState

@Composable
fun LibraryScreen(
    favorites: List<FavoriteSurah>,
    downloads: List<DownloadedSurah>,
    playerState: PlayerUiState,
    onPlayFavorite: (Reciter, Surah) -> Unit,
    onPlayDownloaded: (DownloadedSurah) -> Unit,
    onDeleteDownload: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = Color(0xFF1DB954)
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("المفضلة (${favorites.size})", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("التحميلات (${downloads.size})", fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (selectedTab == 0) {
            if (favorites.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(top = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("لا توجد سور في المفضلة بعد", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("اضغط على أيقونة القلب أثناء الاستماع لحفظ السورة هنا", color = Color(0xFF8F9BB3), fontSize = 13.sp, textAlign = TextAlign.Center)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    items(favorites) { fav ->
                        val reciter = QuranData.reciters.find { it.id == fav.reciterId } ?: QuranData.reciters[0]
                        val surah = QuranData.surahs.find { it.number == fav.surahNumber } ?: QuranData.surahs[0]

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onPlayFavorite(reciter, surah) },
                            color = Color(0xFF131A2E).copy(alpha = 0.85f),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF1DB954))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(text = "${surah.number}", color = Color(0xFF8F9BB3), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.weight(1f))
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "سورة ${surah.nameAr}", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    Text(text = reciter.nameAr, color = Color(0xFF8F9BB3), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if (downloads.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(top = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("لا توجد سور محملة للاستماع بدون إنترنت", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("اضغط على زر التحميل في مشغل الصوت لحفظ السورة بدون إنترنت", color = Color(0xFF8F9BB3), fontSize = 13.sp, textAlign = TextAlign.Center)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    items(downloads) { down ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onPlayDownloaded(down) },
                            color = Color(0xFF131A2E).copy(alpha = 0.85f),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { onDeleteDownload(down.reciterId, down.surahNumber) }) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", tint = Color(0xFFEF4444))
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "سورة ${down.surahNameAr}", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "${down.reciterNameAr} • ${down.fileSizeFormatted}", color = Color(0xFF8F9BB3), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
