package voice.playback.di

import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeSubcomponent
import dagger.BindsInstance
import voice.common.AppScope
import voice.playback.session.PlaybackService

@PlaybackScope
@MergeSubcomponent(
  scope = PlaybackScope::class,
)
interface PlaybackComponent {

  fun inject(target: PlaybackService)

  @MergeSubcomponent.Factory
  interface Factory {
    fun create(@BindsInstance playbackService: PlaybackService): PlaybackComponent
  }

  @ContributesTo(AppScope::class)
  interface Provider {
    val playbackComponentFactory: Factory
  }
}
