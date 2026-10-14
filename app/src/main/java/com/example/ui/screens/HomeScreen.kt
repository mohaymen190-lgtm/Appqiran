package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.QuranData
import com.example.model.AmbientSoundType
import com.example.model.Reciter
import com.example.model.Surah
import com.example.ui.util.ResourceUtils
import com.example.viewmodel.PlayerUiState

@Composable
fun HomeScreen(
    playerState: PlayerUiState,
    onReciterSelected: (Reciter) -> Unit,
    onResumeListening: (Reciter, Surah) -> Unit,
    onSurahSelected: (Reciter, Surah) -> Unit,
    onAmbientToggle: (AmbientSoundType) -> Unit,
    onAmbientVolumeChange: (Float) -> Unit,
    onSeeAllReciters: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // 1. Promotional Banner: 13 Reciters & 114 Surahs
        item {
            NewRecitersBanner(
                onClick = onSeeAllReciters,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        // 2. Hero Section: "تم تشغيله مؤخراً"
        item {
            RecentlyPlayedCard(
                reciter = playerState.currentReciter,
                surah = playerState.currentSurah,
                onResumeClick = {
                    onResumeListening(playerState.currentReciter, playerState.currentSurah)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // 3. Section: "المفضلة لديك"
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "عرض الكل",
                    color = Color(0xFF388AF6),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onSeeAllReciters() }
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "كبار القرّاء",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "★",
                        color = Color(0xFFFFD700),
                        fontSize = 18.sp
                    )
                }
            }
        }

        // 4. Horizontal Grid / Row of Reciter Avatars
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(QuranData.reciters) { reciter ->
                    ReciterCircleCard(
                        reciter = reciter,
                        onClick = { onReciterSelected(reciter) }
                    )
                }
            }
        }

        // 4.5. Spiritual Spotify Playlists (Feature #1)
        item {
            SpiritualPlaylistsSection(
                onPlaylistSelect = { surahNumber ->
                    val surah = QuranData.surahs.find { it.number == surahNumber } ?: QuranData.surahs[0]
                    onSurahSelected(playerState.currentReciter, surah)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            )
        }

        // 4.6. Smart Digital Tasbeeh Counter (Feature #3)
        item {
            SmartTasbeehCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        // 5. Ambient Calm Sound Controller (Real Ocean, Rain, Wind, Night Nature)
        item {
            AmbientSoundQuickSelector(
                selectedAmbient = playerState.ambientSound,
                ambientVolume = playerState.ambientVolume,
                onSelect = onAmbientToggle,
                onVolumeChange = onAmbientVolumeChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        // 6. Section: "جميع سور القرآن الكريم (١١٤ سورة)"
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "١١٤ سورة كاملة",
                    fontSize = 13.sp,
                    color = Color(0xFF1DB954),
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "سور القرآن الكريم",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Complete 114 Surahs list
        items(QuranData.surahs) { surah ->
            PopularSurahItem(
                surah = surah,
                reciter = playerState.currentReciter,
                isPlaying = playerState.isPlaying && playerState.currentSurah.number == surah.number,
                onClick = { onSurahSelected(playerState.currentReciter, surah) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
fun NewRecitersBanner(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .testTag("banner_new_reciters"),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF141926).copy(alpha = 0.95f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = null,
                tint = Color(0xFF7E8A9E),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "١٣ قارئاً وجميع السور (١١٤ سورة)",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "تلاوات كاملة مع صوت أمواج ومؤثرات واقعية",
                    color = Color(0xFF8F9BB3),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Surface(
                shape = CircleShape,
                color = Color(0xFF2979FF),
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "١١٤", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun RecentlyPlayedCard(
    reciter: Reciter,
    surah: Surah,
    onResumeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageResId = ResourceUtils.getDrawableResId(context, reciter.imageDrawableName)

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF161F33).copy(alpha = 0.95f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "تم تشغيله مؤخراً",
                    color = Color(0xFF9EABC2),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = reciter.nameAr,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "سورة ${surah.nameAr}",
                    color = Color(0xFF1DB954),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    modifier = Modifier.clickable { onResumeClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "◀", color = Color.Black, fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "تابع الاستماع", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.width(18.dp))

            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E283D)),
                contentAlignment = Alignment.Center
            ) {
                if (imageResId != 0) {
                    AsyncImage(
                        model = imageResId,
                        contentDescription = reciter.nameAr,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFF8F9BB3),
                        modifier = Modifier.size(54.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ReciterCircleCard(
    reciter: Reciter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageResId = ResourceUtils.getDrawableResId(context, reciter.imageDrawableName)

    Column(
        modifier = modifier
            .width(120.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(Color(0xFF1C253B)),
            contentAlignment = Alignment.Center
        ) {
            if (imageResId != 0) {
                AsyncImage(
                    model = imageResId,
                    contentDescription = reciter.nameAr,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color(0xFF8F9BB3),
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = reciter.nameAr,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun AmbientSoundQuickSelector(
    selectedAmbient: AmbientSoundType,
    ambientVolume: Float,
    onSelect: (AmbientSoundType) -> Unit,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141926).copy(alpha = 0.95f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedAmbient != AmbientSoundType.NONE) {
                    Text(
                        text = "مفعل: ${selectedAmbient.titleAr}",
                        color = Color(0xFF1DB954),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "خلفيات طبيعية واقعية (أمواج، مطر، رياح)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = Color(0xFF2979FF),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    AmbientSoundType.NONE to "بدون",
                    AmbientSoundType.OCEAN to "أمواج البحر",
                    AmbientSoundType.RAIN to "المطر",
                    AmbientSoundType.WIND to "الرياح",
                    AmbientSoundType.NIGHT_BIRDS to "سكينة"
                ).forEach { (type, label) ->
                    val isSelected = selectedAmbient == type
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onSelect(type) },
                        color = if (isSelected) Color(0xFF1DB954) else Color(0xFF1B2338),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = label,
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.Black else Color.White
                        )
                    }
                }
            }

            if (selectedAmbient != AmbientSoundType.NONE) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = null,
                        tint = Color(0xFF1DB954),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "قوة صوت الأمواج/الطبيعة:",
                        fontSize = 11.sp,
                        color = Color(0xFF8F9BB3)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Slider(
                        value = ambientVolume,
                        onValueChange = onVolumeChange,
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

@Composable
fun PopularSurahItem(
    surah: Surah,
    reciter: Reciter,
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        color = if (isPlaying) Color(0xFF1C273D) else Color(0xFF121724).copy(alpha = 0.7f),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (isPlaying) Color(0xFF1DB954) else Color(0xFF1B2338),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.GraphicEq else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = if (isPlaying) Color.Black else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "${surah.number}",
                fontSize = 12.sp,
                color = Color(0xFF8F9BB3),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "سورة ${surah.nameAr}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPlaying) Color(0xFF1DB954) else Color.White
                )
                Text(
                    text = "${surah.revelationType} • ${surah.versesCount} آية",
                    fontSize = 12.sp,
                    color = Color(0xFF8F9BB3)
                )
            }
        }
    }
}

/**
 * Feature #1: Spiritual Spotify-style Playlists for Quran Recitations
 */
@Composable
fun SpiritualPlaylistsSection(
    onPlaylistSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "مختارات خاصة",
                color = Color(0xFF1DB954),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "قوائم التشغيل الروحانية",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val playlists = listOf(
                Triple("سورة الكهف • نور الجمعة", "بركة ونور ما بين الجمعتين", 18),
                Triple("سورة يس • قلب القرآن", "سكينة وطمأنينة للقلوب", 36),
                Triple("سورة الرحمن • عروس القرآن", "فبأي آلاء ربكما تكذبان", 55),
                Triple("سورة الملك • المنجية من عذاب القبر", "تبارك الذي بيده الملك", 67),
                Triple("المعوذتان والإخلاص • حصن المسلم", "تحصين وسكينة قبل النوم", 112)
            )

            items(playlists) { (title, subtitle, surahNumber) ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF161F33).copy(alpha = 0.95f),
                    modifier = Modifier
                        .width(220.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onPlaylistSelect(surahNumber) }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF1DB954),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "تشغيل",
                                        tint = Color.Black,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Text(
                                text = "Spotify Playlist",
                                color = Color(0xFF8F9BB3),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = title,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = subtitle,
                            color = Color(0xFF8F9BB3),
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

/**
 * Feature #3: Smart Digital Tasbeeh Counter with Haptic Tap and Dhikr Switcher
 */
@Composable
fun SmartTasbeehCard(
    modifier: Modifier = Modifier
) {
    var count by remember { mutableIntStateOf(0) }
    var currentDhikrIndex by remember { mutableIntStateOf(0) }
    val adhkar = listOf(
        "سُبْحَانَ اللَّهِ",
        "الْحَمْدُ لِلَّهِ",
        "لَا إِلَٰهَ إِلَّا اللَّهُ",
        "اللَّهُ أَكْبَرُ",
        "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ",
        "اللَّهُمَّ صَلِّ عَلَى نَبِيِّنَا مُحَمَّدٍ"
    )

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF131A2E).copy(alpha = 0.95f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { count = 0 }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "إعادة التصفير",
                        tint = Color(0xFF8F9BB3),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "المسبحة الإلكترونية الذكية",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = {
                        currentDhikrIndex = (currentDhikrIndex + 1) % adhkar.size
                        count = 0
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = "تغيير الذكر",
                        tint = Color(0xFF1DB954),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = adhkar[currentDhikrIndex],
                color = Color(0xFFFFD700),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF1E2842),
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .clickable { count++ }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 36.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$count",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = "اضغط للتسبيح",
                        color = Color(0xFF1DB954),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

