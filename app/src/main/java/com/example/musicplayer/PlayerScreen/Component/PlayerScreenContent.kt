package com.example.musicplayer.PlayerScreen.Component

import android.media.AudioDeviceInfo
import android.net.Uri
import android.os.Looper
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.AudioAttributes
import androidx.media3.common.AuxEffectInfo
import androidx.media3.common.DeviceInfo
import androidx.media3.common.Effect
import androidx.media3.common.Format
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.PriorityTaskManager
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.VideoSize
import androidx.media3.common.text.CueGroup
import androidx.media3.common.util.Clock
import androidx.media3.common.util.Size
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DecoderCounters
import androidx.media3.exoplayer.ExoPlaybackException
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.PlayerMessage
import androidx.media3.exoplayer.Renderer
import androidx.media3.exoplayer.SeekParameters
import androidx.media3.exoplayer.analytics.AnalyticsCollector
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.image.ImageOutput
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ShuffleOrder
import androidx.media3.exoplayer.trackselection.TrackSelectionArray
import androidx.media3.exoplayer.trackselection.TrackSelector
import androidx.media3.exoplayer.video.VideoFrameMetadataListener
import androidx.media3.exoplayer.video.spherical.CameraMotionListener
import kotlinx.coroutines.delay


@Composable
fun PlayerScreenContent(
    exoPlayer: ExoPlayer,
) {
    Column (
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        var currentPosition by remember { mutableFloatStateOf(0f) }
        var duration by remember { mutableLongStateOf(0) }
        var isPlaying by remember { mutableStateOf(false) }
        var artAlbum by remember { mutableStateOf<Uri?> (null) }
        var trackTitle by remember { mutableStateOf("unknow title") }
        var trackArtists by remember { mutableStateOf("unknow artist") }

        DisposableEffect(exoPlayer) {
            val listener = object : Player.Listener {
                override fun onIsPlayingChanged(Playing: Boolean) {
                    isPlaying = Playing
                }

                override fun onPlaybackStateChanged(p: Int) {
                    duration = exoPlayer.duration.coerceAtLeast(0L)
                }

                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    artAlbum = mediaMetadata.artworkUri
                    trackTitle = mediaMetadata.title.toString()
                    trackArtists = mediaMetadata.artist.toString()
                }

            }

            exoPlayer.addListener(listener)

            onDispose {
                exoPlayer.removeListener(listener)
            }
        }

        // Update current position periodically
        LaunchedEffect(isPlaying) {
            while (isPlaying) {
                currentPosition = exoPlayer.currentPosition.toFloat()
                delay(500) // Update every second
            }
        }

        // Update position when not playing
        LaunchedEffect(currentPosition) {
            if (!isPlaying) {
                currentPosition = exoPlayer.currentPosition.toFloat()
            }
        }

        PlayerArtworkDisplay(
            modifier = Modifier,
            artworkUri = artAlbum
        )

        Column {
            TitlePlate(
                trackName = trackTitle,
                trackArtists = trackArtists
            )
            PlayerControls(

                currentPosition = currentPosition,
                duration = duration,
                isPlaying = isPlaying,
                onPlayPause = {
                    if (isPlaying) {
                        exoPlayer.pause()
                    } else exoPlayer.play()
                },
                onNext = { exoPlayer.seekToNext() },
                onPrevious = { exoPlayer.seekToPrevious() },
                onSeek = { fraction -> exoPlayer.seekTo((fraction * duration).toLong()) }
            )
        }

    }
}

@Preview(showSystemUi = true)
@Composable
fun PlayerScreenContentPreview() {
    PlayerScreenContent(
        exoPlayer = mockExoPlayer()
    )
}

@OptIn(UnstableApi::class)
private fun mockExoPlayer(): ExoPlayer {
    return object : ExoPlayer {

        
        override fun play() {}
        override fun pause() {}
        override fun seekToNext() {}
        override fun seekToPrevious() {}
        override fun seekTo(positionMs: Long) {}
        override fun addListener(listener: Player.Listener) {}
        override fun removeListener(listener: Player.Listener) {}
        override fun setMediaItems(mediaItems: List<MediaItem>) {
            TODO("Not yet implemented")
        }

        override fun setMediaItems(
            mediaItems: List<MediaItem>,
            resetPosition: Boolean
        ) {
            TODO("Not yet implemented")
        }

        override fun setMediaItems(
            mediaItems: List<MediaItem>,
            startIndex: Int,
            startPositionMs: Long
        ) {
            TODO("Not yet implemented")
        }

        // Minimal implementations for required methods
        override fun getPlaybackState(): Int = Player.STATE_READY
        override fun getPlaybackSuppressionReason(): Int {
            TODO("Not yet implemented")
        }

        override fun isPlaying(): Boolean {
            TODO("Not yet implemented")
        }

        override fun getPlayWhenReady(): Boolean = true
        override fun getRepeatMode(): Int = Player.REPEAT_MODE_ONE
        override fun hasNext() = true
        override fun hasNextWindow(): Boolean {
            TODO("Not yet implemented")
        }

        override fun hasPrevious() = true
        override fun hasPreviousWindow(): Boolean {
            TODO("Not yet implemented")
        }

        override fun getVolume() = 1f
        override fun clearVideoSurface() {
            TODO("Not yet implemented")
        }

        override fun clearVideoSurface(surface: Surface?) {
            TODO("Not yet implemented")
        }

        override fun setVideoSurface(surface: Surface?) {
            TODO("Not yet implemented")
        }

        override fun setVideoSurfaceHolder(surfaceHolder: SurfaceHolder?) {
            TODO("Not yet implemented")
        }

        override fun clearVideoSurfaceHolder(surfaceHolder: SurfaceHolder?) {
            TODO("Not yet implemented")
        }

        override fun setVideoSurfaceView(surfaceView: SurfaceView?) {
            TODO("Not yet implemented")
        }

        override fun clearVideoSurfaceView(surfaceView: SurfaceView?) {
            TODO("Not yet implemented")
        }

        override fun setVideoTextureView(textureView: TextureView?) {
            TODO("Not yet implemented")
        }

        override fun clearVideoTextureView(textureView: TextureView?) {
            TODO("Not yet implemented")
        }

        override fun getVideoSize(): VideoSize {
            TODO("Not yet implemented")
        }

        override fun getSurfaceSize(): Size {
            TODO("Not yet implemented")
        }

        override fun getCurrentCues(): CueGroup {
            TODO("Not yet implemented")
        }

        override fun getDeviceInfo(): DeviceInfo {
            TODO("Not yet implemented")
        }

        override fun isDeviceMuted() = false
        
        // Stub implementations for remaining interface methods
        override fun getAudioAttributes() = throw NotImplementedError()
        override fun getBufferedPercentage() = throw NotImplementedError()
        override fun getTotalBufferedDuration(): Long {
            TODO("Not yet implemented")
        }

        override fun isCurrentWindowDynamic(): Boolean {
            TODO("Not yet implemented")
        }

        override fun isCurrentMediaItemDynamic(): Boolean {
            TODO("Not yet implemented")
        }

        override fun isCurrentWindowLive(): Boolean {
            TODO("Not yet implemented")
        }

        override fun isCurrentMediaItemLive(): Boolean {
            TODO("Not yet implemented")
        }

        override fun getCurrentLiveOffset(): Long {
            TODO("Not yet implemented")
        }

        override fun isCurrentWindowSeekable(): Boolean {
            TODO("Not yet implemented")
        }

        override fun isCurrentMediaItemSeekable(): Boolean {
            TODO("Not yet implemented")
        }

        override fun isPlayingAd(): Boolean {
            TODO("Not yet implemented")
        }

        override fun getCurrentAdGroupIndex(): Int {
            TODO("Not yet implemented")
        }

        override fun getCurrentAdIndexInAdGroup(): Int {
            TODO("Not yet implemented")
        }

        override fun getContentDuration(): Long {
            TODO("Not yet implemented")
        }

        override fun getContentPosition(): Long {
            TODO("Not yet implemented")
        }

        override fun getContentBufferedPosition(): Long {
            TODO("Not yet implemented")
        }

        override fun getCurrentTimeline() = throw NotImplementedError()
        override fun getCurrentMediaItem() = throw NotImplementedError()
        override fun getMediaItemCount() = throw NotImplementedError()
        override fun getMediaItemAt(index: Int) = throw NotImplementedError()
        override fun getDuration(): Long {
            TODO("Not yet implemented")
        }

        override fun getCurrentPosition(): Long {
            TODO("Not yet implemented")
        }

        override fun getBufferedPosition(): Long {
            TODO("Not yet implemented")
        }

        override fun getShuffleModeEnabled() = throw NotImplementedError()
        override fun isLoading(): Boolean {
            TODO("Not yet implemented")
        }

        override fun getDeviceVolume() = throw NotImplementedError()
        override fun setAudioAttributes(audioAttributes: AudioAttributes, handleAudioFocus: Boolean) {}
        override fun setPlayWhenReady(playWhenReady: Boolean) {}
        override fun setRepeatMode(repeatMode: Int) {}
        override fun setShuffleModeEnabled(shuffleModeEnabled: Boolean) {}
        override fun setVolume(volume: Float) {}
        override fun setDeviceVolume(volume: Int) {}
        override fun setDeviceVolume(volume: Int, flags: Int) {
            TODO("Not yet implemented")
        }

        override fun increaseDeviceVolume() {}
        override fun increaseDeviceVolume(flags: Int) {
            TODO("Not yet implemented")
        }

        override fun decreaseDeviceVolume() {}
        override fun decreaseDeviceVolume(flags: Int) {
            TODO("Not yet implemented")
        }

        override fun setDeviceMuted(muted: Boolean) {}
        override fun setDeviceMuted(muted: Boolean, flags: Int) {
            TODO("Not yet implemented")
        }

        override fun clearMediaItems() {}
        override fun setMediaItem(mediaItem: MediaItem) {}
        override fun setMediaItem(mediaItem: MediaItem, startPositionMs: Long) {}
        override fun setMediaItem(mediaItem: MediaItem, resetPosition: Boolean) {}
        override fun addMediaItem(mediaItem: MediaItem) {}
        override fun addMediaItem(index: Int, mediaItem: MediaItem) {}
        override fun addMediaItems(mediaItems: List<MediaItem>) {
            TODO("Not yet implemented")
        }

        override fun addMediaItems(
            index: Int,
            mediaItems: List<MediaItem>
        ) {
            TODO("Not yet implemented")
        }

        override fun moveMediaItem(currentIndex: Int, newIndex: Int) {
            TODO("Not yet implemented")
        }

        override fun moveMediaItems(fromIndex: Int, toIndex: Int, newIndex: Int) {}
        override fun removeMediaItem(index: Int) {}
        override fun removeMediaItems(fromIndex: Int, toIndex: Int) {}
        override fun replaceMediaItem(index: Int, mediaItem: MediaItem) {}
        override fun replaceMediaItems(
            fromIndex: Int,
            toIndex: Int,
            mediaItems: List<MediaItem>
        ) {
            TODO("Not yet implemented")
        }

        override fun setAudioSessionId(audioSessionId: Int) {
            TODO("Not yet implemented")
        }

        override fun getAudioSessionId(): Int {
            TODO("Not yet implemented")
        }

        override fun setAuxEffectInfo(auxEffectInfo: AuxEffectInfo) {
            TODO("Not yet implemented")
        }

        override fun clearAuxEffectInfo() {
            TODO("Not yet implemented")
        }

        override fun setPreferredAudioDevice(audioDeviceInfo: AudioDeviceInfo?) {
            TODO("Not yet implemented")
        }

        override fun setSkipSilenceEnabled(skipSilenceEnabled: Boolean) {
            TODO("Not yet implemented")
        }

        override fun getSkipSilenceEnabled(): Boolean {
            TODO("Not yet implemented")
        }

        override fun setVideoEffects(videoEffects: List<Effect>) {
            TODO("Not yet implemented")
        }

        override fun setVideoScalingMode(videoScalingMode: Int) {
            TODO("Not yet implemented")
        }

        override fun getVideoScalingMode(): Int {
            TODO("Not yet implemented")
        }

        override fun setVideoChangeFrameRateStrategy(videoChangeFrameRateStrategy: Int) {
            TODO("Not yet implemented")
        }

        override fun getVideoChangeFrameRateStrategy(): Int {
            TODO("Not yet implemented")
        }

        override fun setVideoFrameMetadataListener(listener: VideoFrameMetadataListener) {
            TODO("Not yet implemented")
        }

        override fun clearVideoFrameMetadataListener(listener: VideoFrameMetadataListener) {
            TODO("Not yet implemented")
        }

        override fun setCameraMotionListener(listener: CameraMotionListener) {
            TODO("Not yet implemented")
        }

        override fun clearCameraMotionListener(listener: CameraMotionListener) {
            TODO("Not yet implemented")
        }

        override fun createMessage(target: PlayerMessage.Target): PlayerMessage {
            TODO("Not yet implemented")
        }

        override fun setSeekParameters(seekParameters: SeekParameters?) {
            TODO("Not yet implemented")
        }

        override fun getSeekParameters(): SeekParameters {
            TODO("Not yet implemented")
        }

        override fun setForegroundMode(foregroundMode: Boolean) {
            TODO("Not yet implemented")
        }

        override fun setPauseAtEndOfMediaItems(pauseAtEndOfMediaItems: Boolean) {
            TODO("Not yet implemented")
        }

        override fun getPauseAtEndOfMediaItems(): Boolean {
            TODO("Not yet implemented")
        }

        override fun getAudioFormat(): Format? {
            TODO("Not yet implemented")
        }

        override fun getVideoFormat(): Format? {
            TODO("Not yet implemented")
        }

        override fun getAudioDecoderCounters(): DecoderCounters? {
            TODO("Not yet implemented")
        }

        override fun getVideoDecoderCounters(): DecoderCounters? {
            TODO("Not yet implemented")
        }

        override fun setHandleAudioBecomingNoisy(handleAudioBecomingNoisy: Boolean) {
            TODO("Not yet implemented")
        }

        override fun setWakeMode(wakeMode: Int) {
            TODO("Not yet implemented")
        }

        override fun setPriorityTaskManager(priorityTaskManager: PriorityTaskManager?) {
            TODO("Not yet implemented")
        }

        override fun isSleepingForOffload(): Boolean {
            TODO("Not yet implemented")
        }

        override fun isTunnelingEnabled(): Boolean {
            TODO("Not yet implemented")
        }

        override fun setImageOutput(imageOutput: ImageOutput) {
            TODO("Not yet implemented")
        }

        override fun prepare() {}
        override fun release() {}
        override fun stop() {}
        override fun seekToDefaultPosition() {}
        override fun seekToDefaultPosition(mediaItemIndex: Int) {}
        override fun seekTo(mediaItemIndex: Int, positionMs: Long) {}
        override fun getSeekBackIncrement(): Long {
            TODO("Not yet implemented")
        }

        override fun seekBack() {}
        override fun getSeekForwardIncrement(): Long {
            TODO("Not yet implemented")
        }

        override fun seekForward() {}
        override fun hasPreviousMediaItem() = throw NotImplementedError()
        override fun previous() {
            TODO("Not yet implemented")
        }

        override fun hasNextMediaItem() = throw NotImplementedError()
        override fun next() {
            TODO("Not yet implemented")
        }

        override fun seekToPreviousMediaItem() {}
        override fun getMaxSeekToPreviousPosition(): Long {
            TODO("Not yet implemented")
        }

        override fun seekToNextMediaItem() {}
        @Deprecated("Deprecated in Java")
        override fun seekToPreviousWindow() {}
        override fun seekToNextWindow() {}
        override fun setPlaybackParameters(playbackParameters: PlaybackParameters) {}
        override fun setPlaybackSpeed(speed: Float) {}
        override fun getPlaybackParameters() = throw NotImplementedError()
        override fun getCurrentTracks() = throw NotImplementedError()
        override fun getPlayerError(): ExoPlaybackException? {
            TODO("Not yet implemented")
        }

        override fun getAudioComponent(): ExoPlayer.AudioComponent? {
            TODO("Not yet implemented")
        }

        override fun getVideoComponent(): ExoPlayer.VideoComponent? {
            TODO("Not yet implemented")
        }

        override fun getTextComponent(): ExoPlayer.TextComponent? {
            TODO("Not yet implemented")
        }

        override fun getDeviceComponent(): ExoPlayer.DeviceComponent? {
            TODO("Not yet implemented")
        }

        override fun addAudioOffloadListener(listener: ExoPlayer.AudioOffloadListener) {
            TODO("Not yet implemented")
        }

        override fun removeAudioOffloadListener(listener: ExoPlayer.AudioOffloadListener) {
            TODO("Not yet implemented")
        }

        override fun getAnalyticsCollector(): AnalyticsCollector {
            TODO("Not yet implemented")
        }

        override fun addAnalyticsListener(listener: AnalyticsListener) {
            TODO("Not yet implemented")
        }

        override fun removeAnalyticsListener(listener: AnalyticsListener) {
            TODO("Not yet implemented")
        }

        override fun getRendererCount(): Int {
            TODO("Not yet implemented")
        }

        override fun getRendererType(index: Int): Int {
            TODO("Not yet implemented")
        }

        override fun getRenderer(index: Int): Renderer {
            TODO("Not yet implemented")
        }

        override fun getTrackSelector(): TrackSelector? {
            TODO("Not yet implemented")
        }

        override fun getCurrentTrackGroups() = throw NotImplementedError()
        override fun getCurrentTrackSelections(): TrackSelectionArray {
            TODO("Not yet implemented")
        }

        override fun getPlaybackLooper(): Looper {
            TODO("Not yet implemented")
        }

        override fun getClock(): Clock {
            TODO("Not yet implemented")
        }

        override fun prepare(mediaSource: MediaSource) {
            TODO("Not yet implemented")
        }

        override fun prepare(
            mediaSource: MediaSource,
            resetPosition: Boolean,
            resetState: Boolean
        ) {
            TODO("Not yet implemented")
        }

        override fun setMediaSources(mediaSources: List<MediaSource>) {
            TODO("Not yet implemented")
        }

        override fun setMediaSources(
            mediaSources: List<MediaSource>,
            resetPosition: Boolean
        ) {
            TODO("Not yet implemented")
        }

        override fun setMediaSources(
            mediaSources: List<MediaSource>,
            startMediaItemIndex: Int,
            startPositionMs: Long
        ) {
            TODO("Not yet implemented")
        }

        override fun setMediaSource(mediaSource: MediaSource) {
            TODO("Not yet implemented")
        }

        override fun setMediaSource(
            mediaSource: MediaSource,
            startPositionMs: Long
        ) {
            TODO("Not yet implemented")
        }

        override fun setMediaSource(
            mediaSource: MediaSource,
            resetPosition: Boolean
        ) {
            TODO("Not yet implemented")
        }

        override fun addMediaSource(mediaSource: MediaSource) {
            TODO("Not yet implemented")
        }

        override fun addMediaSource(
            index: Int,
            mediaSource: MediaSource
        ) {
            TODO("Not yet implemented")
        }

        override fun addMediaSources(mediaSources: List<MediaSource>) {
            TODO("Not yet implemented")
        }

        override fun addMediaSources(
            index: Int,
            mediaSources: List<MediaSource>
        ) {
            TODO("Not yet implemented")
        }

        override fun setShuffleOrder(shuffleOrder: ShuffleOrder) {
            TODO("Not yet implemented")
        }

        override fun getTrackSelectionParameters() = throw NotImplementedError()
        override fun setTrackSelectionParameters(parameters: TrackSelectionParameters) {}
        override fun getMediaMetadata(): MediaMetadata {
            TODO("Not yet implemented")
        }

        override fun getPlaylistMetadata() = throw NotImplementedError()
        override fun setPlaylistMetadata(mediaMetadata: MediaMetadata) {}
        override fun getCurrentManifest() = throw NotImplementedError()
        override fun getCurrentPeriodIndex() = throw NotImplementedError()
        override fun getCurrentWindowIndex(): Int {
            TODO("Not yet implemented")
        }

        override fun getCurrentMediaItemIndex() = throw NotImplementedError()
        override fun getNextWindowIndex(): Int {
            TODO("Not yet implemented")
        }

        override fun getNextMediaItemIndex() = throw NotImplementedError()
        override fun getPreviousWindowIndex(): Int {
            TODO("Not yet implemented")
        }

        override fun getPreviousMediaItemIndex() = throw NotImplementedError()
        override fun canAdvertiseSession() = throw NotImplementedError()
        override fun getApplicationLooper() = throw NotImplementedError()
        override fun isCommandAvailable(command: Int) = throw NotImplementedError()
        override fun getAvailableCommands() = throw NotImplementedError()
    }
}