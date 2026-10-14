package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.QuranData
import com.example.ui.components.FullPlayerSheet
import com.example.ui.components.MiniPlayerBar
import com.example.ui.components.QuranAppHeader
import com.example.ui.components.QuranBottomNavigationBar
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.ReciterDetailScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.QuranAppTheme
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.Screen

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
            val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
            val playerState by viewModel.playerState.collectAsStateWithLifecycle()
            val favorites by viewModel.favorites.collectAsStateWithLifecycle()
            val downloads by viewModel.downloads.collectAsStateWithLifecycle()
            val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

            val snackbarHostState = remember { SnackbarHostState() }
            var showSalawatSplash by remember { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                delay(2000)
                showSalawatSplash = false
            }

            LaunchedEffect(playerState.errorMessage) {
                playerState.errorMessage?.let { msg ->
                    snackbarHostState.showSnackbar(msg)
                    viewModel.clearErrorMessage()
                }
            }

            QuranAppTheme(darkTheme = isDarkTheme) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = if (isDarkTheme) listOf(
                                        Color(0xFF0F1B33),
                                        Color(0xFF0A0F1D),
                                        Color(0xFF070B14)
                                    ) else listOf(
                                        Color(0xFFEDF2FA),
                                        Color(0xFFF6F8FC),
                                        Color(0xFFFFFFFF)
                                    )
                                )
                            )
                    ) {
                        Scaffold(
                            containerColor = Color.Transparent,
                            snackbarHost = { SnackbarHost(snackbarHostState) },
                            bottomBar = {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    MiniPlayerBar(
                                        playerState = playerState,
                                        onPlayPauseClick = { viewModel.togglePlayPause() },
                                        onNextClick = { viewModel.playNextSurah() },
                                        onExpandClick = { viewModel.setFullPlayerVisible(true) },
                                        onToggleFavorite = { viewModel.toggleFavoriteCurrent() }
                                    )

                                    QuranBottomNavigationBar(
                                        currentScreen = currentScreen,
                                        onNavigate = { viewModel.navigateTo(it) }
                                    )
                                }
                            }
                        ) { paddingValues ->
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = paddingValues.calculateBottomPadding())
                                    .statusBarsPadding()
                            ) {
                                if (currentScreen !is Screen.ReciterDetail) {
                                    val headerTitle = when (currentScreen) {
                                        is Screen.Home -> "الرئيسية"
                                        is Screen.Search -> "بحث"
                                        is Screen.Library -> "مكتبتي"
                                        is Screen.Settings -> "الإعدادات"
                                        else -> "القرآن الكريم"
                                    }
                                    QuranAppHeader(
                                        title = headerTitle,
                                        onEqualizerClick = { viewModel.setFullPlayerVisible(true) },
                                        onPremiumClick = { viewModel.navigateTo(Screen.Library) }
                                    )
                                }

                                Box(modifier = Modifier.fillMaxSize()) {
                                    when (val screen = currentScreen) {
                                        is Screen.Home -> {
                                            HomeScreen(
                                                playerState = playerState,
                                                onReciterSelected = { reciter ->
                                                    viewModel.navigateTo(Screen.ReciterDetail(reciter))
                                                },
                                                onResumeListening = { reciter, surah ->
                                                    viewModel.playSurah(reciter, surah)
                                                },
                                                onSurahSelected = { reciter, surah ->
                                                    viewModel.playSurah(reciter, surah)
                                                },
                                                onAmbientToggle = { type ->
                                                    viewModel.toggleAmbientSound(type)
                                                },
                                                onAmbientVolumeChange = { vol ->
                                                    viewModel.setAmbientVolume(vol)
                                                },
                                                onSeeAllReciters = {
                                                    viewModel.navigateTo(Screen.ReciterDetail(QuranData.reciters[0]))
                                                }
                                            )
                                        }

                                        is Screen.ReciterDetail -> {
                                            ReciterDetailScreen(
                                                reciter = screen.reciter,
                                                playerState = playerState,
                                                onBackClick = { viewModel.navigateTo(Screen.Home) },
                                                onSurahSelected = { reciter, surah ->
                                                    viewModel.playSurah(reciter, surah)
                                                },
                                                onPlayAll = { reciter ->
                                                    viewModel.playSurah(reciter, QuranData.surahs[0])
                                                }
                                            )
                                        }

                                        is Screen.Search -> {
                                            SearchScreen(
                                                searchQuery = searchQuery,
                                                onQueryChange = { viewModel.setSearchQuery(it) },
                                                onReciterSelected = { reciter ->
                                                    viewModel.navigateTo(Screen.ReciterDetail(reciter))
                                                },
                                                onSurahSelected = { reciter, surah ->
                                                    viewModel.playSurah(reciter, surah)
                                                }
                                            )
                                        }

                                        is Screen.Library -> {
                                            LibraryScreen(
                                                favorites = favorites,
                                                downloads = downloads,
                                                playerState = playerState,
                                                onPlayFavorite = { reciter, surah ->
                                                    viewModel.playSurah(reciter, surah)
                                                },
                                                onPlayDownloaded = { downloadedSurah ->
                                                    val reciter = QuranData.reciters.find { it.id == downloadedSurah.reciterId }
                                                        ?: QuranData.reciters[0]
                                                    val surah = QuranData.surahs.find { it.number == downloadedSurah.surahNumber }
                                                        ?: QuranData.surahs[0]
                                                    viewModel.playSurah(reciter, surah)
                                                },
                                                onDeleteDownload = { reciterId, surahNumber ->
                                                    viewModel.removeDownload(reciterId, surahNumber)
                                                }
                                            )
                                        }

                                        is Screen.Settings -> {
                                            SettingsScreen(
                                                isDarkTheme = isDarkTheme,
                                                onToggleTheme = { viewModel.toggleTheme() },
                                                playerState = playerState,
                                                onAmbientVolumeChange = { viewModel.setAmbientVolume(it) }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Full Screen iOS/Spotify Player overlay
                        AnimatedVisibility(
                            visible = playerState.isFullPlayerVisible,
                            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                        ) {
                            FullPlayerSheet(
                                playerState = playerState,
                                onDismiss = { viewModel.setFullPlayerVisible(false) },
                                onPlayPause = { viewModel.togglePlayPause() },
                                onNext = { viewModel.playNextSurah() },
                                onPrevious = { viewModel.playPreviousSurah() },
                                onSeek = { pos -> viewModel.seekTo(pos) },
                                onToggleFavorite = { viewModel.toggleFavoriteCurrent() },
                                onDownload = { viewModel.downloadCurrentSurah() },
                                onAmbientSelect = { type -> viewModel.toggleAmbientSound(type) },
                                onAmbientVolumeChange = { vol -> viewModel.setAmbientVolume(vol) },
                                onToggleLoop = { viewModel.toggleLooping() },
                                onCycleSpeed = { viewModel.cyclePlaybackSpeed() },
                                onSetSleepTimer = { mins -> viewModel.setSleepTimer(mins) }
                            )
                        }

                        // 2-second Salawat launch message overlay
                        AnimatedVisibility(
                            visible = showSalawatSplash,
                            enter = fadeIn(),
                            exit = fadeOut(),
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(24.dp),
                                color = Color(0xFF0D1527).copy(alpha = 0.95f),
                                shadowElevation = 16.dp,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 22.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "ﷺ",
                                        fontSize = 32.sp,
                                        color = Color(0xFFFFD700),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "صلّ على النبي",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "اللهم صلِّ وسلِّم وبارك على نبينا محمد",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF1DB954),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
