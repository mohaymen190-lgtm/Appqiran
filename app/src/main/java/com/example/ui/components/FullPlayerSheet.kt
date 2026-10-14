package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Share
import com.example.data.QuranTextData
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.AmbientSoundType
import com.example.ui.util.ResourceUtils
import com.example.viewmodel.PlayerUiState

@Composable
fun FullPlayerSheet(
    playerState: PlayerUiState,
    onDismiss: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Int) -> Unit,
    onToggleFavorite: () -> Unit,
    onDownload: () -> Unit,
    onAmbientSelect: (AmbientSoundType) -> Unit,
    onAmbientVolumeChange: (Float) -> Unit,
    onToggleLoop: () -> Unit,
    onCycleSpeed: () -> Unit,
    onSetSleepTimer: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageResId = ResourceUtils.getDrawableResId(context, playerState.currentReciter.imageDrawableName)
    var showTimerDialog by remember { mutableStateOf(false) }
    var showLiveAyatView by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFF090E1A)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top action bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "إغلاق",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "يتم الاستماع الآن",
                        fontSize = 12.sp,
                        color = Color(0xFF8F9BB3)
                    )
                    Text(
                        text = playerState.currentReciter.nameAr,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Toggle Live Ayah Lyrics / Mushaf View
                IconButton(onClick = { showLiveAyatView = !showLiveAyatView }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = "عرض الآيات والمصحف",
                        tint = if (showLiveAyatView) Color(0xFF1DB954) else Color.White
                    )
                }

                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (playerState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "المفضلة",
                        tint = if (playerState.isFavorite) Color(0xFFEF4444) else Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (showLiveAyatView) {
                // Live Quranic Verses View (Lyrics Style)
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF131A2E).copy(alpha = 0.95f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                ) {
                    val verses = QuranTextData.getVersesForSurah(
                        playerState.currentSurah.number,
                        playerState.currentSurah.nameAr
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "المصحف الشريف • سورة ${playerState.currentSurah.nameAr}",
                            color = Color(0xFF1DB954),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        verses.forEachIndexed { index, verse ->
                            Text(
                                text = verse,
                                color = if (index == 0) Color(0xFFFFD700) else Color.White,
                                fontSize = 17.sp,
                                lineHeight = 28.sp,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            } else {
                // Large Reciter Avatar Box with genuine photo
                Box(
                    modifier = Modifier
                        .size(230.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0xFF161F33)),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageResId != 0) {
                        AsyncImage(
                            model = imageResId,
                            contentDescription = playerState.currentReciter.nameAr,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color(0xFF8F9BB3),
                            modifier = Modifier.size(96.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Surah Title & Details
            Text(
                text = "سورة ${playerState.currentSurah.nameAr}",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${playerState.currentSurah.revelationType} • ${playerState.currentSurah.versesCount} آية • ${playerState.currentReciter.title}",
                fontSize = 13.sp,
                color = Color(0xFF8F9BB3)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Slider
            val maxDur = if (playerState.durationMs > 0) playerState.durationMs.toFloat() else 1f
            var sliderPos by remember(playerState.currentPositionMs) {
                mutableFloatStateOf(playerState.currentPositionMs.toFloat().coerceIn(0f, maxDur))
            }

            Slider(
                value = sliderPos,
                onValueChange = { sliderPos = it },
                onValueChangeFinished = { onSeek(sliderPos.toInt()) },
                valueRange = 0f..maxDur,
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color(0xFF1DB954),
                    inactiveTrackColor = Color(0xFF1E2842)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatMs(playerState.currentPositionMs),
                    fontSize = 12.sp,
                    color = Color(0xFF8F9BB3)
                )
                Text(
                    text = formatMs(playerState.durationMs),
                    fontSize = 12.sp,
                    color = Color(0xFF8F9BB3)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Playback controls row (Spotify style)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Loop Surah button
                IconButton(onClick = onToggleLoop, modifier = Modifier.size(46.dp)) {
                    Icon(
                        imageVector = if (playerState.isLooping) Icons.Default.RepeatOne else Icons.Default.Repeat,
                        contentDescription = "تكرار السورة",
                        tint = if (playerState.isLooping) Color(0xFF1DB954) else Color(0xFF8F9BB3)
                    )
                }

                IconButton(onClick = onPrevious, modifier = Modifier.size(54.dp)) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "السابق",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = Color(0xFF1DB954),
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .clickable { onPlayPause() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (playerState.isBuffering) {
                            CircularProgressIndicator(color = Color.Black, strokeWidth = 3.dp, modifier = Modifier.size(32.dp))
                        } else {
                            Icon(
                                imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "تشغيل",
                                tint = Color.Black,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }
                }

                IconButton(onClick = onNext, modifier = Modifier.size(54.dp)) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "التالي",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                IconButton(onClick = onDownload, modifier = Modifier.size(46.dp)) {
                    if (playerState.downloadProgress != null) {
                        CircularProgressIndicator(
                            color = Color(0xFF1DB954),
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "تحميل",
                            tint = if (playerState.isDownloaded) Color(0xFF1DB954) else Color(0xFF8F9BB3)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Extra Features Bar: Speed & Sleep Timer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Playback speed button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF161F33),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onCycleSpeed() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "السرعة",
                            tint = if (playerState.playbackSpeed != 1.0f) Color(0xFF1DB954) else Color(0xFF8F9BB3),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${playerState.playbackSpeed}x",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (playerState.playbackSpeed != 1.0f) Color(0xFF1DB954) else Color.White
                        )
                    }
                }

                // Sleep Timer button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF161F33),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showTimerDialog = !showTimerDialog }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "مؤقت النوم",
                            tint = if (playerState.sleepTimerMinutesLeft != null) Color(0xFF1DB954) else Color(0xFF8F9BB3),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (playerState.sleepTimerMinutesLeft != null)
                                "مؤقت: ${playerState.sleepTimerMinutesLeft} د"
                            else "مؤقت النوم",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (playerState.sleepTimerMinutesLeft != null) Color(0xFF1DB954) else Color.White
                        )
                    }
                }
            }

            // Sleep Timer selector drawer
            if (showTimerDialog) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF131A2E),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "إيقاف التلاوة والصوت تلقائياً بعد:",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                null to "إلغاء",
                                15 to "١٥ د",
                                30 to "٣٠ د",
                                45 to "٤٥ د",
                                60 to "٦٠ د"
                            ).forEach { (mins, label) ->
                                val isSelected = playerState.sleepTimerMinutesLeft == mins
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) Color(0xFF1DB954) else Color(0xFF1D263D),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            onSetSleepTimer(mins)
                                            showTimerDialog = false
                                        }
                                ) {
                                    Text(
                                        text = label,
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        color = if (isSelected) Color.Black else Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Realistic Ambient Sound Controls Box
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF131A2E).copy(alpha = 0.95f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (playerState.ambientSound != AmbientSoundType.NONE)
                                "مفعل: ${playerState.ambientSound.titleAr} (${playerState.ambientSound.subtitleAr})"
                            else "الخلفية الهادئة (اضغط للتشغيل مع التلاوة)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (playerState.ambientSound != AmbientSoundType.NONE) Color(0xFF1DB954) else Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            AmbientSoundType.NONE to (Icons.Default.VolumeOff to "بدون"),
                            AmbientSoundType.OCEAN to (Icons.Default.Waves to "أمواج"),
                            AmbientSoundType.RAIN to (Icons.Default.WaterDrop to "مطر"),
                            AmbientSoundType.WIND to (Icons.Default.Air to "رياح"),
                            AmbientSoundType.NIGHT_BIRDS to (Icons.Default.Nightlight to "سكينة")
                        ).forEach { (type, pair) ->
                            val isSelected = playerState.ambientSound == type
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Color(0xFF1DB954) else Color(0xFF1D263D),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onAmbientSelect(type) }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = pair.first,
                                        contentDescription = pair.second,
                                        tint = if (isSelected) Color.Black else Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = pair.second,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.Black else Color.White
                                    )
                                }
                            }
                        }
                    }

                    if (playerState.ambientSound != AmbientSoundType.NONE) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "صوت المؤثر:", fontSize = 11.sp, color = Color(0xFF8F9BB3))
                            Spacer(modifier = Modifier.width(8.dp))
                            Slider(
                                value = playerState.ambientVolume,
                                onValueChange = onAmbientVolumeChange,
                                valueRange = 0.1f..1.0f,
                                modifier = Modifier.weight(1f),
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF1DB954),
                                    activeTrackColor = Color(0xFF1DB954)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatMs(ms: Int): String {
    val totalSec = ms / 1000
    val min = totalSec / 60
    val sec = totalSec % 60
    return String.format("%02d:%02d", min, sec)
}
