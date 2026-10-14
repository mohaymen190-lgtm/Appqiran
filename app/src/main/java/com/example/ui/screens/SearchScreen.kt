package com.example.ui.screens

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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.QuranData
import com.example.model.Reciter
import com.example.model.Surah
import com.example.ui.util.ResourceUtils
import androidx.compose.foundation.background

@Composable
fun SearchScreen(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onReciterSelected: (Reciter) -> Unit,
    onSurahSelected: (Reciter, Surah) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val filteredReciters = QuranData.reciters.filter {
        it.nameAr.contains(searchQuery.trim(), ignoreCase = true) ||
        it.nameEn.contains(searchQuery.trim(), ignoreCase = true)
    }

    val filteredSurahs = QuranData.surahs.filter {
        it.nameAr.contains(searchQuery.trim(), ignoreCase = true) ||
        it.nameEn.contains(searchQuery.trim(), ignoreCase = true) ||
        it.number.toString() == searchQuery.trim()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            placeholder = { Text("ابحث عن أي سورة (١١٤ سورة) أو قارئ...", color = Color(0xFF7E8A9E)) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color(0xFF1DB954))
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1DB954),
                unfocusedBorderColor = Color(0xFF1E2842),
                focusedContainerColor = Color(0xFF131A2E),
                unfocusedContainerColor = Color(0xFF131A2E),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            if (filteredReciters.isNotEmpty()) {
                item {
                    Text(
                        text = "القرّاء (${filteredReciters.size})",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(bottom = 12.dp)
                    ) {
                        items(filteredReciters) { reciter ->
                            val imageResId = ResourceUtils.getDrawableResId(context, reciter.imageDrawableName)
                            Column(
                                modifier = Modifier
                                    .width(100.dp)
                                    .clickable { onReciterSelected(reciter) },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
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
                                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color.White)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = reciter.nameAr,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "السور (${filteredSurahs.size} من ١١٤)",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(filteredSurahs) { surah ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onSurahSelected(QuranData.reciters[0], surah) },
                    color = Color(0xFF131A2E).copy(alpha = 0.8f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF1DB954))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "${surah.number}", color = Color(0xFF8F9BB3), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.weight(1f))
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "سورة ${surah.nameAr}", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(text = "${surah.revelationType} • ${surah.versesCount} آية", color = Color(0xFF8F9BB3), fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
