package com.example.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import com.example.model.AmbientSoundType
import com.example.model.Reciter
import com.example.model.Surah

/**
 * Compose audio mixing state holder for Quran recitation and realistic ambient sound effects (ocean waves, rain, wind).
 */
data class AudioMixerState(
    val isPlaying: Boolean,
    val currentPositionMs: Int,
    val durationMs: Int,
    val currentSurah: Surah,
    val currentReciter: Reciter,
    val ambientType: AmbientSoundType,
    val ambientVolume: Float,
    val playbackSpeed: Float
)

/**
 * Pure Compose utility hook to bind and observe QuranAudioPlayer with automatic lifecycle clean-up.
 */
@Composable
fun rememberQuranAudioPlayer(): QuranAudioPlayer {
    val player = remember { QuranAudioPlayer() }

    DisposableEffect(player) {
        onDispose {
            player.release()
        }
    }

    return player
}
