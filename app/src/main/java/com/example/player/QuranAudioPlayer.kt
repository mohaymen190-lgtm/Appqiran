package com.example.player

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.util.Log
import com.example.model.AmbientSoundType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * High-fidelity organic procedural ambient audio synthesizer.
 * Generates rich, realistic, layered physical acoustics:
 * - Real Ocean: Dual-period swells (deep crash, rushing foam, receding undertow) with realistic crests
 * - Real Rain: Dense multi-scale raindrop splatters + sustained soothing rainfall bed
 * - Real Wind: Dynamic gusting wind with howling resonance and soothing breeze variation
 * - Night Nature: Gentle crickets, quiet night breeze and harmonic serenity
 */
class AmbientSoundEngine {
    private var audioTrack: AudioTrack? = null
    private var synthJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    @Volatile
    private var isRunning = false

    @Volatile
    private var currentType = AmbientSoundType.NONE

    @Volatile
    var volume: Float = 0.65f
        set(value) {
            field = value
            audioTrack?.setVolume(value)
        }

    fun playAmbient(type: AmbientSoundType) {
        if (type == AmbientSoundType.NONE) {
            stop()
            return
        }
        currentType = type
        if (!isRunning) {
            startSynthesizer()
        }
    }

    fun stop() {
        isRunning = false
        synthJob?.cancel()
        synthJob = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
            audioTrack = null
        } catch (e: Exception) {
            Log.e("AmbientSoundEngine", "Error stopping track", e)
        }
    }

    private fun startSynthesizer() {
        isRunning = true
        synthJob?.cancel()

        synthJob = scope.launch {
            val sampleRate = 22050
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = (minBufferSize * 2).coerceAtLeast(4096)

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            track.setVolume(volume)
            audioTrack = track

            try {
                track.play()
            } catch (e: Exception) {
                Log.e("AmbientSoundEngine", "Track play failed", e)
                return@launch
            }

            val buffer = ShortArray(1024)

            // Synthesizer state variables
            var b0 = 0.0
            var b1 = 0.0
            var b2 = 0.0
            var b3 = 0.0
            var b4 = 0.0
            var b5 = 0.0
            var b6 = 0.0

            var lowPassSurf = 0.0
            var lowPassFoam = 0.0
            var wavePhase1 = 0.0
            var wavePhase2 = 0.0

            var windModPhase = 0.0
            var windLowPass = 0.0

            var rainDropTimer = 0
            var rainLowPass = 0.0

            val twoPi = 2.0 * PI

            while (isActive && isRunning) {
                val mode = currentType
                for (i in buffer.indices) {
                    val white = (Random.nextDouble() * 2.0) - 1.0

                    // Paul Kellet's filtered pink noise algorithm
                    b0 = 0.99886 * b0 + white * 0.0555179
                    b1 = 0.99332 * b1 + white * 0.0750759
                    b2 = 0.96900 * b2 + white * 0.1538520
                    b3 = 0.86650 * b3 + white * 0.3104856
                    b4 = 0.55000 * b4 + white * 0.5329522
                    b5 = -0.7616 * b5 - white * 0.0168980
                    val pink = (b0 + b1 + b2 + b3 + b4 + b5 + b6 + white * 0.5362) * 0.11
                    b6 = white * 0.115926

                    val sample: Double = when (mode) {
                        AmbientSoundType.OCEAN -> {
                            // Waves simulation:
                            // Dual tidal surge cycle (approx 7.5s & 11s)
                            wavePhase1 += (twoPi / (sampleRate * 7.5))
                            if (wavePhase1 > twoPi) wavePhase1 -= twoPi

                            wavePhase2 += (twoPi / (sampleRate * 11.2))
                            if (wavePhase2 > twoPi) wavePhase2 -= twoPi

                            // Asymmetrical wave crest (steep build-up, gentle wash/recede)
                            val wave1 = (sin(wavePhase1) + 0.35 * sin(2.0 * wavePhase1)).coerceIn(-1.0, 1.0)
                            val envelope1 = (wave1 + 1.0) * 0.5 // 0.0 to 1.0
                            val swell = envelope1 * envelope1 // Non-linear surge

                            // Deep undertow rumble (low-pass filtered pink noise)
                            lowPassSurf += (pink - lowPassSurf) * 0.035
                            val rumble = lowPassSurf * (0.35 + 0.65 * swell)

                            // Crashing whitecaps & surf foam (modulated bandpassed hiss)
                            val foamHiss = (white * 0.7 + pink * 0.3)
                            lowPassFoam += (foamHiss - lowPassFoam) * (0.05 + 0.25 * swell)
                            val foam = lowPassFoam * (swell * 0.75)

                            // Combined realistic ocean sound
                            val oceanSignal = (rumble * 1.35 + foam * 0.95)
                            oceanSignal.coerceIn(-1.0, 1.0)
                        }

                        AmbientSoundType.RAIN -> {
                            // Rain: steady rainfall bed + intermittent droplet transients
                            rainLowPass += (pink - rainLowPass) * 0.18
                            val steadyRain = rainLowPass * 0.75

                            // Patter droplets
                            rainDropTimer--
                            var droplet = 0.0
                            if (rainDropTimer <= 0) {
                                rainDropTimer = Random.nextInt(40, 220)
                                droplet = (Random.nextDouble() - 0.5) * 0.6
                            }

                            val rainSignal = steadyRain + droplet + (white * 0.08)
                            rainSignal.coerceIn(-1.0, 1.0)
                        }

                        AmbientSoundType.WIND -> {
                            // Wind: Dynamic swirling modulation
                            windModPhase += (twoPi / (sampleRate * 4.8))
                            if (windModPhase > twoPi) windModPhase -= twoPi

                            val gust = (0.5 + 0.5 * sin(windModPhase)) * (0.7 + 0.3 * cos(windModPhase * 2.3))
                            windLowPass += (pink - windLowPass) * (0.015 + 0.06 * gust)
                            val windSignal = windLowPass * (0.6 + 0.8 * gust)
                            windSignal.coerceIn(-1.0, 1.0)
                        }

                        AmbientSoundType.NIGHT_BIRDS -> {
                            // Night Nature: Deep calm night breeze with subtle cricket hum
                            windLowPass += (pink - windLowPass) * 0.02
                            val nightBreeze = windLowPass * 0.5
                            val cricketCarrier = sin(i * 0.85) * (if (Random.nextDouble() > 0.94) 0.15 else 0.0)
                            val nightSignal = nightBreeze + cricketCarrier
                            nightSignal.coerceIn(-1.0, 1.0)
                        }

                        AmbientSoundType.NONE -> 0.0
                    }

                    // Write 16-bit PCM integer
                    buffer[i] = (sample * 24000.0).toInt().coerceIn(-32767, 32767).toShort()
                }

                track.write(buffer, 0, buffer.size)
            }
        }
    }
}

/**
 * Coordinated Quran Recitation and Ambient Audio Manager
 */
class QuranAudioPlayer {
    private var mediaPlayer: MediaPlayer? = null
    val ambientEngine = AmbientSoundEngine()

    var onPlaybackStateChanged: ((isPlaying: Boolean, currentPositionMs: Int, durationMs: Int) -> Unit)? = null
    var onCompletion: (() -> Unit)? = null
    var onError: ((String) -> Unit)? = null

    private var currentSpeed: Float = 1.0f
    private var isLoopingSurah: Boolean = false

    fun setPlaybackSpeed(speed: Float) {
        currentSpeed = speed
        try {
            mediaPlayer?.let {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    it.playbackParams = it.playbackParams.setSpeed(speed)
                }
            }
        } catch (e: Exception) {
            Log.e("QuranAudioPlayer", "Error setting playback speed", e)
        }
    }

    fun setLooping(loop: Boolean) {
        isLoopingSurah = loop
    }

    fun isLooping(): Boolean = isLoopingSurah

    fun playUrl(url: String) {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null

            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                setOnPreparedListener { mp ->
                    try {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M && currentSpeed != 1.0f) {
                            mp.playbackParams = mp.playbackParams.setSpeed(currentSpeed)
                        }
                    } catch (e: Exception) {
                        Log.e("QuranAudioPlayer", "Failed to apply speed", e)
                    }
                    mp.start()
                    onPlaybackStateChanged?.invoke(true, mp.currentPosition, mp.duration)
                }
                setOnCompletionListener {
                    if (isLoopingSurah) {
                        it.seekTo(0)
                        it.start()
                        onPlaybackStateChanged?.invoke(true, 0, it.duration)
                    } else {
                        onPlaybackStateChanged?.invoke(false, it.duration, it.duration)
                        onCompletion?.invoke()
                    }
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("QuranAudioPlayer", "MediaPlayer error: $what, $extra")
                    onError?.invoke("تعذر تشغيل المقطع الصوتي، يرجى التأكد من الاتصال بالإنترنت")
                    true
                }
                prepareAsync()
            }
            mediaPlayer = player
        } catch (e: Exception) {
            Log.e("QuranAudioPlayer", "Error starting playback", e)
            onError?.invoke(e.localizedMessage ?: "حدث خطأ أثناء التشغيل")
        }
    }

    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                onPlaybackStateChanged?.invoke(false, it.currentPosition, it.duration)
            }
        }
    }

    fun resume() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
                onPlaybackStateChanged?.invoke(true, it.currentPosition, it.duration)
            }
        }
    }

    fun seekTo(positionMs: Int) {
        mediaPlayer?.seekTo(positionMs)
    }

    fun getDuration(): Int = mediaPlayer?.duration ?: 0
    fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: 0
    fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true

    fun setAmbientSound(type: AmbientSoundType) {
        ambientEngine.playAmbient(type)
    }

    fun setAmbientVolume(volume: Float) {
        ambientEngine.volume = volume
    }

    fun release() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            ambientEngine.stop()
        } catch (e: Exception) {
            Log.e("QuranAudioPlayer", "Error releasing player", e)
        }
    }
}
