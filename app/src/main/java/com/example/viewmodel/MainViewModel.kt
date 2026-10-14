package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.QuranData
import com.example.data.local.DownloadedSurah
import com.example.data.local.FavoriteSurah
import com.example.data.local.QuranDatabase
import com.example.model.AmbientSoundType
import com.example.model.Reciter
import com.example.model.Surah
import com.example.player.QuranAudioPlayer
import com.example.repository.QuranRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

sealed class Screen {
    data object Home : Screen()
    data class ReciterDetail(val reciter: Reciter) : Screen()
    data object Search : Screen()
    data object Library : Screen()
    data object Settings : Screen()
}

data class PlayerUiState(
    val currentReciter: Reciter = QuranData.reciters[0],
    val currentSurah: Surah = QuranData.surahs[0],
    val isPlaying: Boolean = false,
    val currentPositionMs: Int = 0,
    val durationMs: Int = 0,
    val isFullPlayerVisible: Boolean = false,
    val ambientSound: AmbientSoundType = AmbientSoundType.NONE,
    val ambientVolume: Float = 0.65f,
    val isFavorite: Boolean = false,
    val isDownloaded: Boolean = false,
    val downloadProgress: Int? = null,
    val isBuffering: Boolean = false,
    val errorMessage: String? = null,
    val playbackSpeed: Float = 1.0f,
    val isLooping: Boolean = false,
    val sleepTimerMinutesLeft: Int? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = QuranDatabase.getDatabase(application)
    val repository = QuranRepository(database.quranDao(), application)
    val audioPlayer = QuranAudioPlayer()

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _playerState = MutableStateFlow(PlayerUiState())
    val playerState: StateFlow<PlayerUiState> = _playerState.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val favorites: StateFlow<List<FavoriteSurah>> = repository.favorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val downloads: StateFlow<List<DownloadedSurah>> = repository.downloads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var progressTrackingJob: Job? = null
    private var sleepTimerJob: Job? = null

    init {
        audioPlayer.onPlaybackStateChanged = { isPlaying, pos, dur ->
            _playerState.value = _playerState.value.copy(
                isPlaying = isPlaying,
                currentPositionMs = pos,
                durationMs = dur,
                isBuffering = false
            )
        }

        audioPlayer.onError = { error ->
            _playerState.value = _playerState.value.copy(
                isPlaying = false,
                isBuffering = false,
                errorMessage = error
            )
        }

        audioPlayer.onCompletion = {
            playNextSurah()
        }

        viewModelScope.launch {
            _playerState
                .map { it.currentReciter.id to it.currentSurah.number }
                .distinctUntilChanged()
                .flatMapLatest { (reciterId, surahNumber) ->
                    val favId = "${reciterId}_$surahNumber"
                    database.quranDao().isFavorite(favId)
                }
                .collect { isFav ->
                    _playerState.value = _playerState.value.copy(isFavorite = isFav)
                }
        }
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun playSurah(reciter: Reciter, surah: Surah) {
        _playerState.value = _playerState.value.copy(
            currentReciter = reciter,
            currentSurah = surah,
            isBuffering = true,
            errorMessage = null
        )

        val offlineFileName = "${reciter.id}_${String.format("%03d", surah.number)}.mp3"
        val offlineDir = getApplication<Application>().getExternalFilesDir(android.os.Environment.DIRECTORY_MUSIC)
            ?: getApplication<Application>().filesDir
        val offlineFile = File(offlineDir, offlineFileName)

        val playUrl = if (offlineFile.exists()) {
            offlineFile.absolutePath
        } else {
            QuranData.getAudioUrl(reciter, surah.number)
        }

        audioPlayer.playUrl(playUrl)
        startProgressTracking()
    }

    fun togglePlayPause() {
        if (_playerState.value.isPlaying) {
            audioPlayer.pause()
            _playerState.value = _playerState.value.copy(isPlaying = false)
        } else {
            if (_playerState.value.currentPositionMs > 0) {
                audioPlayer.resume()
                _playerState.value = _playerState.value.copy(isPlaying = true)
                startProgressTracking()
            } else {
                playSurah(_playerState.value.currentReciter, _playerState.value.currentSurah)
            }
        }
    }

    fun seekTo(positionMs: Int) {
        audioPlayer.seekTo(positionMs)
        _playerState.value = _playerState.value.copy(currentPositionMs = positionMs)
    }

    fun playNextSurah() {
        val currentIndex = QuranData.surahs.indexOfFirst { it.number == _playerState.value.currentSurah.number }
        val nextIndex = if (currentIndex in 0 until QuranData.surahs.lastIndex) currentIndex + 1 else 0
        playSurah(_playerState.value.currentReciter, QuranData.surahs[nextIndex])
    }

    fun playPreviousSurah() {
        val currentIndex = QuranData.surahs.indexOfFirst { it.number == _playerState.value.currentSurah.number }
        val prevIndex = if (currentIndex > 0) currentIndex - 1 else QuranData.surahs.lastIndex
        playSurah(_playerState.value.currentReciter, QuranData.surahs[prevIndex])
    }

    fun setFullPlayerVisible(visible: Boolean) {
        _playerState.value = _playerState.value.copy(isFullPlayerVisible = visible)
    }

    fun toggleAmbientSound(type: AmbientSoundType) {
        val nextType = if (_playerState.value.ambientSound == type) AmbientSoundType.NONE else type
        _playerState.value = _playerState.value.copy(ambientSound = nextType)
        audioPlayer.setAmbientSound(nextType)
    }

    fun setAmbientVolume(vol: Float) {
        _playerState.value = _playerState.value.copy(ambientVolume = vol)
        audioPlayer.setAmbientVolume(vol)
    }

    fun toggleFavoriteCurrent() {
        val reciter = _playerState.value.currentReciter
        val surah = _playerState.value.currentSurah
        val isFav = _playerState.value.isFavorite
        viewModelScope.launch {
            repository.toggleFavorite(reciter, surah, isFav)
            _playerState.value = _playerState.value.copy(isFavorite = !isFav)
        }
    }

    fun downloadCurrentSurah() {
        val reciter = _playerState.value.currentReciter
        val surah = _playerState.value.currentSurah
        viewModelScope.launch {
            _playerState.value = _playerState.value.copy(downloadProgress = 0)
            repository.downloadSurah(reciter, surah) { progress ->
                _playerState.value = _playerState.value.copy(downloadProgress = progress)
            }
            _playerState.value = _playerState.value.copy(downloadProgress = null, isDownloaded = true)
        }
    }

    fun removeDownload(reciterId: String, surahNumber: Int) {
        viewModelScope.launch {
            repository.deleteDownload(reciterId, surahNumber)
        }
    }

    fun clearErrorMessage() {
        _playerState.value = _playerState.value.copy(errorMessage = null)
    }

    fun toggleLooping() {
        val nextLoop = !_playerState.value.isLooping
        _playerState.value = _playerState.value.copy(isLooping = nextLoop)
        audioPlayer.setLooping(nextLoop)
    }

    fun setPlaybackSpeed(speed: Float) {
        _playerState.value = _playerState.value.copy(playbackSpeed = speed)
        audioPlayer.setPlaybackSpeed(speed)
    }

    fun cyclePlaybackSpeed() {
        val speeds = listOf(0.75f, 1.0f, 1.25f, 1.5f)
        val currentIndex = speeds.indexOf(_playerState.value.playbackSpeed).coerceAtLeast(0)
        val nextSpeed = speeds[(currentIndex + 1) % speeds.size]
        setPlaybackSpeed(nextSpeed)
    }

    fun setSleepTimer(minutes: Int?) {
        sleepTimerJob?.cancel()
        if (minutes == null || minutes <= 0) {
            _playerState.value = _playerState.value.copy(sleepTimerMinutesLeft = null)
            return
        }

        _playerState.value = _playerState.value.copy(sleepTimerMinutesLeft = minutes)
        sleepTimerJob = viewModelScope.launch {
            var remaining = minutes
            while (isActive && remaining > 0) {
                delay(60_000)
                remaining--
                if (remaining > 0) {
                    _playerState.value = _playerState.value.copy(sleepTimerMinutesLeft = remaining)
                } else {
                    // Time up - pause both Quran and Ambient Audio
                    audioPlayer.pause()
                    audioPlayer.setAmbientSound(AmbientSoundType.NONE)
                    _playerState.value = _playerState.value.copy(
                        isPlaying = false,
                        ambientSound = AmbientSoundType.NONE,
                        sleepTimerMinutesLeft = null
                    )
                }
            }
        }
    }

    private fun startProgressTracking() {
        progressTrackingJob?.cancel()
        progressTrackingJob = viewModelScope.launch {
            while (isActive && _playerState.value.isPlaying) {
                delay(1000)
                val cur = audioPlayer.getCurrentPosition()
                val dur = audioPlayer.getDuration()
                if (dur > 0) {
                    _playerState.value = _playerState.value.copy(
                        currentPositionMs = cur,
                        durationMs = dur
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }
}
